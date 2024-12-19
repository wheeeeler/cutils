package net.wheel.cutils.api.patch.access;

public class AccessPatch {

    private String file;

    public AccessPatch(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
