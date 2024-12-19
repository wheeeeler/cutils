package net.wheel.cutils.impl.fml.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.api.util.ASMUtil;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class CutilsClassTransformer implements IClassTransformer {
    public static PatchManager PATCH_MANAGER = null;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (PATCH_MANAGER == null)
            return basicClass;

        try {
            final ClassPatch patch = PATCH_MANAGER.findClassPatch(name);

            if (patch != null) {
                final ClassNode classNode = ASMUtil.getNode(basicClass);

                if (classNode != null) {
                    if (patch.isDebug()) {
                        crack.INSTANCE.getLogger().log(Level.INFO, "Methods for class " + classNode.name);
                        for (FieldNode fieldNode : classNode.fields) {
                            crack.INSTANCE.getLogger().log(Level.INFO,
                                    "Field " + fieldNode.access + " " + fieldNode.name + " " + fieldNode.desc);
                        }
                        for (MethodNode method : classNode.methods) {
                            crack.INSTANCE.getLogger().log(Level.INFO,
                                    "Method " + method.access + " " + method.name + " " + method.desc);
                        }
                    }

                    if (patch.getAccessPatch() != null) {
                        final InputStream stream = this.getClass()
                                .getResourceAsStream("/" + patch.getAccessPatch().getFile());
                        if (stream != null) {
                            String line = "";
                            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                            while ((line = reader.readLine()) != null) {
                                final String[] split = line.split(" ");

                                for (FieldNode field : classNode.fields) {
                                    if (field.name.equals(split[0]) && field.desc.equals(split[1])) {
                                        field.access = 1;
                                    }
                                }

                                for (MethodNode method : classNode.methods) {
                                    if (method.name.equals(split[0]) && split.length > 0) {
                                        if (split[1] != null && method.desc.equals(split[1])) {
                                            method.access = 1;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (Method method : patch.getClass().getDeclaredMethods()) {
                        if (method.isAnnotationPresent(MethodPatch.class) && method.getParameterCount() > 0) {
                            final MethodPatch methodPatch = method.getAnnotation(MethodPatch.class);
                            if (methodPatch != null) {
                                String methodName = methodPatch.mcpName();
                                String methodDesc = methodPatch.mcpDesc();

                                if (PATCH_MANAGER.getEnv() == PatchManager.Environment.RELEASE) {
                                    if (methodPatch.notchName().length() > 0) {
                                        methodName = methodPatch.notchName();
                                    }
                                    if (methodPatch.notchDesc().length() > 0) {
                                        methodDesc = methodPatch.notchDesc();
                                    }
                                }

                                final MethodNode methodNode = ASMUtil.findMethod(classNode, methodName, methodDesc);

                                if (methodNode != null) {
                                    if (!method.isAccessible()) {
                                        method.setAccessible(true);
                                    }
                                    try {
                                        method.invoke(patch, methodNode, PATCH_MANAGER.getEnv());
                                    } catch (Exception e) {
                                        System.out.println(
                                                "Failed to patch method: " + name + "." + methodName + methodDesc);
                                    }
                                } else {
                                    System.out.println("Method not found: " + name + "." + methodName + methodDesc);
                                }
                            }
                        }
                    }

                    return ASMUtil.toBytes(classNode);
                } else {
                    System.out.println("ClassNode is null for class: " + name);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to patch class: " + name);
        }
        return basicClass;
    }
}
