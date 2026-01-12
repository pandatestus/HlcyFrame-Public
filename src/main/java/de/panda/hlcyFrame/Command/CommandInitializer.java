package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.HlcyFrame;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommandInitializer {

    public static void scanForCommands(HlcyFrame hlcyFrame) {
        try {
            String jarPath = hlcyFrame.getClass().getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

            File jarFile = new File(jarPath);
            if (!jarFile.isFile()) {
                hlcyFrame.getLogger().warning("Not running from a JAR file – command scan skipped.");
                return;
            }

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName()
                                .replace('/', '.')
                                .replace('\\', '.')
                                .substring(0, entry.getName().length() - 6);

                        try {
                            Class<?> clazz = Class.forName(className);
                            executeAnnotatedMethod(clazz);
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void executeAnnotatedMethod(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(HlcyCMD.class)) {
                    method.setAccessible(true);
                    method.invoke(instance);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}