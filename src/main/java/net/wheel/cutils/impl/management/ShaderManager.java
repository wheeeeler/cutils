package net.wheel.cutils.impl.management;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import net.wheel.cutils.api.util.ResourceUtil;
import net.wheel.cutils.api.util.shader.ShaderProgram;
import net.wheel.cutils.crack;

public final class ShaderManager {
    private final Map<String, ShaderProgram> shaderList = new HashMap<String, ShaderProgram>();
    private final Map<ShaderProgram, String> programToID = new HashMap<ShaderProgram, String>();

    public ShaderManager() {
        this.loadShaders();
    }

    private void loadShaders() {
        this.destroyAll();

        try {
            this.loadShadersFilesystem(ShaderProgram.shadersFsDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.loadShadersResources(ShaderProgram.SHADER_RES_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Iterator<String> it = this.getShaderList(); it.hasNext();) {
            crack.INSTANCE.getLogger().log(Level.INFO, it.next());
        }
    }

    public void unload() {
        this.destroyAll();
    }

    public void reload() {
        this.loadShaders();
    }

    private void destroyAll() {
        for (Iterator<String> it = this.getShaderList(); it.hasNext();) {
            this.getShader(it.next()).destroy();
        }

        this.shaderList.clear();
        this.programToID.clear();
    }

    private void loadShadersFilesystem(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                this.loadShadersFilesystem(file);
            } else if (file.getName().endsWith(".json")) {
                final String path = file.getPath().substring(ShaderProgram.SHADER_FS_PATH.length());
                final ShaderProgram shader = ShaderProgram.loadFromJSONNoThrow(path);
                if (shader != null) {
                    this.shaderList.put(path, shader);
                    this.programToID.put(shader, path);
                }
            }
        }
    }

    private void loadShadersResources(String path) {
        Set<String> listings;
        try {
            listings = ResourceUtil.getResourceListing(ShaderManager.class, path, true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (String listing : listings) {
            if (listing.endsWith(".json")) {
                String resourcePath = "resource://" + listing;
                final ShaderProgram shader = ShaderProgram.loadFromJSONNoThrow(resourcePath);
                if (shader != null) {
                    this.shaderList.put(resourcePath, shader);
                    this.programToID.put(shader, resourcePath);
                }
            }
        }
    }

    public ShaderProgram getShader(String shaderPath) {
        return this.shaderList.get(shaderPath);
    }

    public String getShaderID(ShaderProgram shaderProgram) {
        return this.programToID.get(shaderProgram);
    }

    public Iterator<String> getShaderList() {
        return shaderList.keySet().iterator();
    }
}
