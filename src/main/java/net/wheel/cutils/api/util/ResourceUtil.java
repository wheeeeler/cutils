package net.wheel.cutils.api.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ResourceUtil {

    public static Set<String> getResourceListing(Class clazz, String path, boolean recurse)
            throws URISyntaxException, IOException {
        String classPath = clazz.getName().replace(".", "/");

        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            int lastSlash = classPath.lastIndexOf('/');
            if (lastSlash != -1) {
                path = "/" + classPath.substring(0, lastSlash + 1) + path;
            } else {
                path = "/" + path;
            }

            return listFiles(new HashSet<String>(), new File(dirURL.toURI()), path, recurse);
        }

        if (dirURL == null) {
            String me = classPath + ".class";
            dirURL = clazz.getClassLoader().getResource(me);

            if (path.startsWith("/")) {
                path = path.substring(1);
            } else {
                int lastSlash = classPath.lastIndexOf('/');
                if (lastSlash != -1) {
                    path = classPath.substring(0, lastSlash + 1) + path;
                }
            }
        }

        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries();
            Set<String> result = new HashSet<String>();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path) && !name.equals(path)) {

                    if (!recurse) {
                        int nextSlash = name.indexOf('/', path.length());
                        if (nextSlash != -1) {
                            name = name.substring(0, nextSlash + 1);
                        }
                    }
                    result.add("/" + name);
                }
            }

            return result;
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    private static HashSet<String> listFiles(HashSet<String> output, File dir, String prefix, boolean recurse) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String name = prefix + file.getName() + "/";
                output.add(name);

                if (recurse) {
                    listFiles(output, file, name, true);
                }
            } else {
                output.add(prefix + file.getName());
            }
        }

        return output;
    }
}
