package net.wheel.cutils.api.value;

import javax.annotation.Nullable;

import net.wheel.cutils.api.util.shader.ShaderProgram;
import net.wheel.cutils.crack;

public class Shader {
    private String id;

    public Shader(String id) {
        this.setShaderID(id);
    }

    public Shader() {
        this("");
    }

    public String getShaderID() {
        return this.id;
    }

    public void setShaderID(String id) {
        this.id = id;
    }

    @Nullable
    public ShaderProgram getShaderProgram() {
        if (this.id.equals("")) {
            return null;
        }

        return crack.INSTANCE.getShaderManager().getShader(this.id);
    }

    @Override
    public String toString() {
        if (this.id.equals("")) {
            return "no shader picked";
        } else {
            ShaderProgram sp = this.getShaderProgram();
            if (sp == null) {
                return "missing shader (" + this.id + ")";
            } else {
                return sp.getName();
            }
        }
    }
}
