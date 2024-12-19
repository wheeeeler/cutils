package net.wheel.cutils.api.util.shader;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL20.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

import net.wheel.cutils.api.value.Value;
import net.wheel.cutils.crack;

public class ShaderProgram {
    public static final String TEXTURE_UNIFORM = "texture";
    public static final String LIGHTMAP_UNIFORM = "lightmap";
    public static final String ANIMATE_UNIFORM = "animate";
    public static final String DEPTH_UNIFORM = "depth";
    public static final String DEPTHDIMS_UNIFORM = "depthdims";
    public static final String ENTITYBRIGHTNESS_UNIFORM = "entitybrightness";
    public static final File shadersFsDir;
    public static final String SHADER_FS_PATH = "crack/Shaders/";
    public static final String SHADER_RES_PATH = "/assets/cutils/shaders/";
    public static final String SHADER_RES_PATH_FORMATTED = "resource://" + SHADER_RES_PATH;
    private static final LinkedList<ShaderProgram> programStack = new LinkedList();
    private static int depthTextureCounter = 0;
    private static int programBeforeGrab = 0;

    static {
        shadersFsDir = new File(SHADER_FS_PATH);
        if (!shadersFsDir.exists()) {
            shadersFsDir.mkdirs();
        }
    }

    public final Map<String, Value> userUniforms = new HashMap<String, Value>();
    private final Map<String, Integer> files = new HashMap<String, Integer>();
    private final Map<String, Integer> locations = new HashMap<String, Integer>();
    private final Map<Integer, UniformUtil.UValue> uniformQueue = new HashMap();
    private boolean boundDepth = false;
    private boolean triedCompiling = false;
    private boolean valid = false;
    private int program = 0;
    private String name;

    public ShaderProgram(String name) {
        this.name = name;
    }

    private static RuntimeException jsonException(String message) {
        return new RuntimeException("Invalid shader program JSON: " + message);
    }

    private static RuntimeException jsonTypeException(String subject, String targetType, Object obj) {
        String objType;
        if (obj == null) {
            objType = "null";
        } else {
            objType = obj.getClass().getSimpleName();
        }

        return jsonException(subject + " must be " + targetType + " (is " + objType + ")");
    }

    public static ShaderProgram loadFromJSON(String filename) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getInputStream(filename), StandardCharsets.UTF_8))) {
            final JSONParser parser = new JSONParser();
            final Object objRaw = parser.parse(reader);

            if (!(objRaw instanceof JSONObject)) {
                throw jsonTypeException("JSON root value", "a JSON object", objRaw);
            }

            final JSONObject obj = (JSONObject) objRaw;

            ShaderProgram shader;
            final Object name = obj.get("name");
            if (name == null) {
                shader = new ShaderProgram(filename);
            } else if (name instanceof String) {
                shader = new ShaderProgram((String) name);
            } else {
                throw jsonTypeException("name property", "a string or missing", name);
            }

            final Object files = obj.get("files");
            if (files == null) {
                throw jsonException("files array missing");
            } else if (files instanceof JSONArray) {
                for (final Object file : (JSONArray) files) {
                    if (file instanceof String) {
                        shader.addShaderFile((String) file);
                    } else {
                        throw jsonTypeException("a member of the files array", "a string", file);
                    }
                }
            } else {
                throw jsonTypeException("files property", "an array", files);
            }

            final Object uniforms = obj.get("uniforms");
            if (uniforms instanceof JSONObject) {
                for (HashMap.Entry<String, Object> entry : ((HashMap<String, Object>) uniforms).entrySet()) {
                    final String uniform = entry.getKey();
                    if (isUniformReserved(uniform)) {
                        throw jsonException("the uniform name '" + uniform + "' is reserved");
                    }

                    final Object uniformObjRaw = entry.getValue();
                    if (!(uniformObjRaw instanceof JSONObject)) {
                        throw jsonTypeException("a uniform entry", "JSON object", uniformObjRaw);
                    }

                    final JSONObject uniformObj = (JSONObject) uniformObjRaw;

                    String uName;
                    final Object displayName = uniformObj.get("displayname");
                    if (displayName instanceof String) {
                        uName = (String) displayName;
                    } else if (displayName == null) {
                        uName = uniform;
                    } else {
                        throw jsonTypeException("uniform display name", "a string or missing", displayName);
                    }

                    String[] uAlias;
                    final Object aliases = uniformObj.get("alias");
                    if (aliases instanceof JSONArray) {
                        for (final Object alias : (JSONArray) aliases) {
                            if (!(alias instanceof String)) {
                                throw jsonTypeException("a member of the uniform alias array", "a string", alias);
                            }
                        }

                        uAlias = ((ArrayList<String>) aliases)
                                .toArray(new String[((ArrayList<String>) aliases).size()]);
                    } else if (aliases == null) {
                        uAlias = new String[] { uniform };
                    } else {
                        throw jsonTypeException("uniform alias", "an array of strings or missing", aliases);
                    }

                    String uDesc;
                    final Object description = uniformObj.get("description");
                    if (description instanceof String) {
                        uDesc = (String) description;
                    } else if (description == null) {
                        uDesc = null;
                    } else {
                        throw jsonTypeException("uniform description", "a string or missing", description);
                    }

                    Object uDefault = uniformObj.get("default");
                    final Object uMin = uniformObj.get("min");
                    final Object uMax = uniformObj.get("max");
                    Object uIncrements = uniformObj.get("increments");

                    final Object uType = uniformObj.get("type");
                    if (!(uType instanceof String)) {
                        if (uType == null) {
                            throw jsonException("uniform type missing");
                        } else {
                            throw jsonTypeException("uniform type", "a string", uType);
                        }
                    }

                    Value finalValue;
                    switch ((String) uType) {
                        case "int":
                        case "float":
                            if (uDefault == null) {
                                uDefault = uMin;
                            }
                            if (uIncrements == null) {
                                uIncrements = new Long(0);
                            }

                            if (!(uMin instanceof Number)) {
                                throw jsonTypeException("uniform min value type mismatched;", "a number", uMin);
                            } else if (!(uMax instanceof Number)) {
                                throw jsonTypeException("uniform max value type mismatched;", "a number", uMax);
                            } else if (!(uIncrements instanceof Number)) {
                                throw jsonTypeException("uniform increments value type mismatched;",
                                        "a number or missing", uIncrements);
                            } else if (!(uDefault instanceof Number)) {
                                throw jsonTypeException("uniform default value type mismatched;", "a number or missing",
                                        uDefault);
                            }

                            if (uType.equals("int")) {
                                if (!(uMin instanceof Long)) {
                                    throw jsonException("uniform min value is an integer and must therefore be whole");
                                } else if (!(uMax instanceof Long)) {
                                    throw jsonException("uniform max value is an integer and must therefore be whole");
                                } else if (!(uIncrements instanceof Long)) {
                                    throw jsonException(
                                            "uniform increments value is an integer and must therefore be whole");
                                } else if (!(uDefault instanceof Long)) {
                                    throw jsonException(
                                            "uniform default value is an integer and must therefore be whole");
                                }

                                if (((Number) uMin).intValue() > ((Number) uMax).intValue()) {
                                    throw jsonException("uniform min must not be greater than max");
                                }

                                finalValue = new Value<Integer>(uName, uAlias, uDesc, ((Number) uDefault).intValue(),
                                        ((Number) uMin).intValue(), ((Number) uMax).intValue(),
                                        ((Number) uIncrements).intValue());
                            } else {
                                if (((Number) uMin).floatValue() > ((Number) uMax).floatValue()) {
                                    throw jsonException("uniform min must not be greater than max");
                                }

                                finalValue = new Value<Float>(uName, uAlias, uDesc, ((Number) uDefault).floatValue(),
                                        ((Number) uMin).floatValue(), ((Number) uMax).floatValue(),
                                        ((Number) uIncrements).floatValue());
                            }
                            break;
                        case "bool":
                            if (uMin != null) {
                                throw jsonException("boolean uniforms must not have a minimum value");
                            } else if (uMax != null) {
                                throw jsonException("boolean uniforms must not have a maximum value");
                            } else if (uIncrements != null) {
                                throw jsonException("boolean uniforms must not have increments");
                            } else if (uDefault == null) {
                                uDefault = new Boolean(false);
                            } else if (!(uDefault instanceof Boolean)) {
                                throw jsonTypeException("uniform default value type mismatched;",
                                        "a boolean or missing", uDefault);
                            }

                            finalValue = new Value<Boolean>(uName, uAlias, uDesc, (Boolean) uDefault);
                            break;
                        default:
                            throw jsonException("unsupported uniform type '" + uType + "'");
                    }

                    shader.addUserUniform(uniform, finalValue);
                }
            } else if (uniforms != null) {
                throw jsonTypeException("uniforms property", "an object or missing", uniforms);
            }

            return shader;
        }
    }

    public static ShaderProgram loadFromJSONNoThrow(String filename) {
        try {
            return loadFromJSON(filename);
        } catch (Exception e) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Failed to load shader program from json (see stack trace)");
            e.printStackTrace();
            return null;
        }
    }

    protected static InputStream getInputStream(String filename) throws IOException {
        if (filename.startsWith("resource://")) {
            final String resourcePath = filename.substring(11);
            final InputStream is = ShaderProgram.class.getResourceAsStream(resourcePath);

            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            return is;
        } else {
            return new FileInputStream(new File(shadersFsDir, filename));
        }
    }

    public static Iterator<ShaderProgram> getProgramsInUse() {
        return programStack.iterator();
    }

    public static boolean isUniformReserved(String name) {
        switch (name) {
            case TEXTURE_UNIFORM:
            case LIGHTMAP_UNIFORM:
            case ANIMATE_UNIFORM:
            case DEPTH_UNIFORM:
            case DEPTHDIMS_UNIFORM:
            case ENTITYBRIGHTNESS_UNIFORM:
                return true;
            default:
                return false;
        }
    }

    protected int attachShaderFile(String filename, int shaderType) throws IOException {
        int shader = this.compileShaderFile(filename, shaderType);
        OpenGlHelper.glAttachShader(this.program, shader);
        return shader;
    }

    public boolean make() {
        if (this.triedCompiling) {
            return this.valid;
        }

        this.triedCompiling = true;
        final List<Integer> shaders = new ArrayList<Integer>();

        try {
            if (!OpenGlHelper.shadersSupported) {
                throw new RuntimeException(
                        "Shaders not supported; You need at least OpenGL 2.1 or ARB shader extension support");
            }

            this.program = OpenGlHelper.glCreateProgram();

            if (this.program == 0) {
                throw new RuntimeException("glCreateProgram returned 0. This should never happen");
            }

            boolean hasVertex = false;
            boolean hasFragment = false;
            for (Map.Entry<String, Integer> entry : files.entrySet()) {
                int shaderType = entry.getValue();
                if (shaderType == OpenGlHelper.GL_VERTEX_SHADER) {
                    hasVertex = true;
                } else if (shaderType == OpenGlHelper.GL_FRAGMENT_SHADER) {
                    hasFragment = true;
                }

                shaders.add(this.attachShaderFile(entry.getKey(), shaderType));
            }

            if (!hasVertex) {
                shaders.add(this.attachShaderFile("resource:///assets/cutils/shaders/default.vert", GL_VERTEX_SHADER));
            }

            if (!hasFragment) {
                shaders.add(this.attachShaderFile("resource:///assets/cutils/shaders/func_applyColor_default.frag",
                        GL_FRAGMENT_SHADER));
                shaders.add(this.attachShaderFile("resource:///assets/cutils/shaders/func_applyTexture_default.frag",
                        GL_FRAGMENT_SHADER));
                shaders.add(this.attachShaderFile("resource:///assets/cutils/shaders/func_applyLighting_default.frag",
                        GL_FRAGMENT_SHADER));
                shaders.add(this.attachShaderFile("resource:///assets/cutils/shaders/func_applyShading_default.frag",
                        GL_FRAGMENT_SHADER));
                shaders.add(
                        this.attachShaderFile("resource:///assets/cutils/shaders/default.frag", GL_FRAGMENT_SHADER));
            }

            OpenGlHelper.glLinkProgram(this.program);
            if (OpenGlHelper.glGetProgrami(this.program, OpenGlHelper.GL_LINK_STATUS) == 0) {
                throw new RuntimeException("Error occurred while linking shader program: "
                        + OpenGlHelper.glGetProgramInfoLog(this.program, 32768));
            }
        } catch (Exception e) {
            if (this.program != 0) {
                OpenGlHelper.glDeleteProgram(this.program);
                this.program = 0;
            }

            crack.INSTANCE.getLogger().log(Level.WARNING,
                    "Failed to make shader program '" + this.name + "' (see stack trace)");
            e.printStackTrace();
            return false;
        } finally {
            for (Integer shader : shaders) {
                OpenGlHelper.glDeleteShader(shader);
            }
        }

        this.valid = true;
        return true;
    }

    public void destroy() {
        if (this.valid) {
            while (this.release(true))
                ;
            OpenGlHelper.glDeleteProgram(this.program);
            this.program = 0;
            this.valid = false;
        }

        this.files.clear();
        this.locations.clear();
        this.uniformQueue.clear();

        this.triedCompiling = true;

        this.name += " (destroyed)";
    }

    public boolean use(boolean setDefaultUniforms) {
        if (this.make()) {
            if (programStack.isEmpty()) {
                programBeforeGrab = glGetInteger(GL_CURRENT_PROGRAM);
            }
            programStack.push(this);

            OpenGlHelper.glUseProgram(this.program);

            if (setDefaultUniforms) {
                this.setTextureUniform();
                this.setLightmapUniform();
                this.setAnimateUniform();
                this.setDepthUniformAndBindTexture();
                this.setEntityBrightnessUniform(0.0f, 0.0f, 0.0f, 0.0f);
            }

            for (Map.Entry<String, Value> entry : this.userUniforms.entrySet()) {
                final String uniformName = entry.getKey();
                final Object val = entry.getValue().getValue();
                if (val instanceof Integer) {
                    this.setUniform(uniformName, (Integer) val);
                } else if (val instanceof Float) {
                    this.setUniform(uniformName, (Float) val);
                } else if (val instanceof Boolean) {
                    this.setUniform(uniformName, (Boolean) val);
                }
            }

            this.flushUniformQueue();

            return true;
        }

        return false;
    }

    public boolean use() {
        return this.use(true);
    }

    private boolean release(boolean noThrow) {
        if (this.valid) {
            int stackIndex = programStack.lastIndexOf(this);
            if (stackIndex == -1) {
                if (noThrow) {

                    return false;
                } else {
                    throw new RuntimeException(
                            "ShaderProgram.release called but the program is not in the program stack; there's a bug somewhere, report this. Make sure to only call release once after calling use");
                }
            }

            this.unbindDepthTexture();
            programStack.remove(stackIndex);

            if (programStack.isEmpty()) {
                OpenGlHelper.glUseProgram(programBeforeGrab);
            } else if (programStack.size() == stackIndex) {
                ShaderProgram newProgram = programStack.peekLast();
                OpenGlHelper.glUseProgram(newProgram.getProgram());
                newProgram.flushUniformQueue();
            }

            return true;
        } else {
            return false;
        }
    }

    public void release() {
        this.release(false);
    }

    public int getUniformLocation(String name) {
        if (!this.make()) {
            return -1;
        }

        final Integer cached = locations.get(name);
        if (cached == null) {
            int fetched = OpenGlHelper.glGetUniformLocation(this.program, name);
            locations.put(name, new Integer(fetched));
            return fetched;
        } else {
            return cached;
        }
    }

    public void flushUniformQueue() {
        if (this.make()) {
            for (Map.Entry<Integer, UniformUtil.UValue> entry : this.uniformQueue.entrySet()) {
                entry.getValue().set(entry.getKey());
            }
        }

        this.uniformQueue.clear();
    }

    private boolean uniformNeedsQueue() {
        return programStack.isEmpty() || programStack.peekLast() != this;
    }

    public void setUniform(int location, float v0) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0));
        } else {
            UniformUtil.set(location, v0);
        }
    }

    public void setUniform(int location, float v0, float v1) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0, v1));
        } else {
            UniformUtil.set(location, v0, v1);
        }
    }

    public void setUniform(int location, float v0, float v1, float v2) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0, v1, v2));
        } else {
            UniformUtil.set(location, v0, v1, v2);
        }
    }

    public void setUniform(int location, float v0, float v1, float v2, float v3) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0, v1, v2, v3));
        } else {
            UniformUtil.set(location, v0, v1, v2, v3);
        }
    }

    public void setUniform(int location, int v0) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0));
        } else {
            UniformUtil.set(location, v0);
        }
    }

    public void setUniform(int location, int v0, int v1) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0, v1));
        } else {
            UniformUtil.set(location, v0, v1);
        }
    }

    public void setUniform(int location, int v0, int v1, int v2) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0, v1, v2));
        } else {
            UniformUtil.set(location, v0, v1, v2);
        }
    }

    public void setUniform(int location, int v0, int v1, int v2, int v3) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(v0, v1, v2, v3));
        } else {
            UniformUtil.set(location, v0, v1, v2, v3);
        }
    }

    public void setUniform(int location, boolean v0) {
        this.setUniform(location, v0 ? 1 : 0);
    }

    public void setUniform(int location, boolean v0, boolean v1) {
        this.setUniform(location, v0 ? 1 : 0, v1 ? 1 : 0);
    }

    public void setUniform(int location, boolean v0, boolean v1, boolean v2) {
        this.setUniform(location, v0 ? 1 : 0, v1 ? 1 : 0, v2 ? 1 : 0);
    }

    public void setUniform(int location, boolean v0, boolean v1, boolean v2, boolean v3) {
        this.setUniform(location, v0 ? 1 : 0, v1 ? 1 : 0, v2 ? 1 : 0, v3 ? 1 : 0);
    }

    public void setUniform(int location, int vecSize, FloatBuffer val) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(vecSize, val));
        } else {
            UniformUtil.set(location, vecSize, val);
        }
    }

    public void setUniform(int location, int vecSize, IntBuffer val) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(vecSize, val));
        } else {
            UniformUtil.set(location, vecSize, val);
        }
    }

    public void setUniform(int location, int matrixSize, boolean transpose, FloatBuffer val) {
        if (location == -1 || !this.make()) {
            return;
        } else if (this.uniformNeedsQueue()) {
            this.uniformQueue.put(location, UniformUtil.wrap(matrixSize, transpose, val));
        } else {
            UniformUtil.set(location, matrixSize, transpose, val);
        }
    }

    public void setColorUniform(int location, int color) {
        this.setUniform(
                location,
                (float) (color >> 16 & 0xFF) * 0.003921569f,
                (float) (color >> 8 & 0xFF) * 0.003921569f,
                (float) (color & 0xFF) * 0.003921569f,
                (float) (color >> 24 & 0xFF) * 0.003921569f);
    }

    public void setUniform(String name, float v0) {
        this.setUniform(this.getUniformLocation(name), v0);
    }

    public void setUniform(String name, float v0, float v1) {
        this.setUniform(this.getUniformLocation(name), v0, v1);
    }

    public void setUniform(String name, float v0, float v1, float v2) {
        this.setUniform(this.getUniformLocation(name), v0, v1, v2);
    }

    public void setUniform(String name, float v0, float v1, float v2, float v3) {
        this.setUniform(this.getUniformLocation(name), v0, v1, v2, v3);
    }

    public void setUniform(String name, int v0) {
        this.setUniform(this.getUniformLocation(name), v0);
    }

    public void setUniform(String name, int v0, int v1) {
        this.setUniform(this.getUniformLocation(name), v0, v1);
    }

    public void setUniform(String name, int v0, int v1, int v2) {
        this.setUniform(this.getUniformLocation(name), v0, v1, v2);
    }

    public void setUniform(String name, int v0, int v1, int v2, int v3) {
        this.setUniform(this.getUniformLocation(name), v0, v1, v2, v3);
    }

    public void setUniform(String name, boolean v0) {
        this.setUniform(this.getUniformLocation(name), v0);
    }

    public void setUniform(String name, boolean v0, boolean v1) {
        this.setUniform(this.getUniformLocation(name), v0, v1);
    }

    public void setUniform(String name, boolean v0, boolean v1, boolean v2) {
        this.setUniform(this.getUniformLocation(name), v0, v1, v2);
    }

    public void setUniform(String name, boolean v0, boolean v1, boolean v2, boolean v3) {
        this.setUniform(this.getUniformLocation(name), v0, v1, v2, v3);
    }

    public void setUniform(String name, int vecSize, FloatBuffer val) {
        this.setUniform(this.getUniformLocation(name), vecSize, val);
    }

    public void setUniform(String name, int vecSize, IntBuffer val) {
        this.setUniform(this.getUniformLocation(name), vecSize, val);
    }

    public void setUniform(String name, int vecSize, boolean transpose, FloatBuffer val) {
        this.setUniform(this.getUniformLocation(name), vecSize, transpose, val);
    }

    public void setColorUniform(String name, int color) {
        this.setColorUniform(this.getUniformLocation(name), color);
    }

    public void setTextureUniform() {
        this.setUniform(TEXTURE_UNIFORM, 0);
    }

    public void setLightmapUniform() {
        this.setUniform(LIGHTMAP_UNIFORM, 1);
    }

    public void setAnimateUniform() {
        this.setUniform(ANIMATE_UNIFORM, (float) (System.currentTimeMillis() % 1000) / 1000.0f);
    }

    public void setDepthUniformAndBindTexture() {
        final int depthUniform = this.getUniformLocation(DEPTH_UNIFORM);
        if (depthUniform != -1 && !this.boundDepth) {
            this.boundDepth = true;
            depthTextureCounter++;

            if (depthTextureCounter == 1) {
                GlStateManager.setActiveTexture(GL_TEXTURE3);
                GlStateManager.enableTexture2D();
                FramebufferUtil.bindDepthTexture();
            }

            this.setUniform(depthUniform, 3);
            this.setUniform(DEPTHDIMS_UNIFORM, (float) FramebufferUtil.getWidth(), (float) FramebufferUtil.getHeight());

            if (depthTextureCounter == 1) {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
    }

    public void setEntityBrightnessUniform(float red, float green, float blue, float alpha) {
        this.setUniform(ENTITYBRIGHTNESS_UNIFORM, red, green, blue, alpha);
    }

    private void unbindDepthTexture() {
        if (this.boundDepth) {
            this.boundDepth = false;
            depthTextureCounter--;

            if (depthTextureCounter < 0) {
                throw new RuntimeException("Too many depth texture unbinds; there's a bug somewhere, report this");
            } else if (depthTextureCounter == 0) {
                GlStateManager.setActiveTexture(GL_TEXTURE3);
                GlStateManager.bindTexture(0);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
    }

    protected ByteBuffer getShaderFileContents(String filename) throws IOException {
        try (InputStream is = getInputStream(filename)) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                while (is.available() > 0) {
                    os.write(is.read());
                }

                final ByteBuffer buf = ByteBuffer.allocateDirect(os.size());
                buf.put(os.toByteArray(), 0, os.size());
                buf.position(0);
                return buf;
            }
        }
    }

    protected int compileShaderFile(String filename, int shaderType) throws IOException, RuntimeException {
        int shader = 0;
        try {
            shader = OpenGlHelper.glCreateShader(shaderType);

            if (shader == 0) {
                throw new RuntimeException("glCreateShader returned 0");
            }

            OpenGlHelper.glShaderSource(shader, this.getShaderFileContents(filename));
            OpenGlHelper.glCompileShader(shader);

            if (OpenGlHelper.glGetShaderi(shader, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
                throw new RuntimeException("Error occurred while compiling shader file '" + filename + "': "
                        + OpenGlHelper.glGetShaderInfoLog(shader, 32768));
            }

            return shader;
        } catch (Exception e) {
            OpenGlHelper.glDeleteShader(shader);
            throw e;
        }
    }

    public ShaderProgram addShaderFile(String filename, int shaderType) {
        if (this.triedCompiling) {
            crack.INSTANCE.getLogger().log(Level.WARNING,
                    "Already tried compiling before adding a shader file; skipped");
        } else if (this.files.containsKey(filename)) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Already added shader file '" + filename + "'; skipped");
        } else {
            this.files.put(filename, shaderType);
        }

        return this;
    }

    public ShaderProgram addShaderFile(String filename) {
        if (filename.endsWith(".vert")) {
            return this.addVertexShaderFile(filename);
        } else if (filename.endsWith(".frag")) {
            return this.addFragmentShaderFile(filename);
        } else {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Unknown file extension for shader file '" + filename
                    + "'; could not auto-detect shader type, skipped");
            return this;
        }
    }

    public ShaderProgram addVertexShaderFile(String filename) {
        return this.addShaderFile(filename, OpenGlHelper.GL_VERTEX_SHADER);
    }

    public ShaderProgram addFragmentShaderFile(String filename) {
        return this.addShaderFile(filename, OpenGlHelper.GL_FRAGMENT_SHADER);
    }

    public ShaderProgram addUserUniform(String name, Value value) {
        if (this.userUniforms.containsKey(name)) {
            crack.INSTANCE.getLogger().log(Level.WARNING, "Already added user uniform '" + name + "'; skipped");
        } else {
            this.userUniforms.put(name, value);
        }

        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getProgram() {
        return program;
    }
}
