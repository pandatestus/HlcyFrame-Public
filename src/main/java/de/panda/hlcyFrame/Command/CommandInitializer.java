package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.HlcyFrame;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommandInitializer {

    private final ClassLoader classLoader;

    public CommandInitializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void executeAnnotatedMethod(Class<?> clazz) {
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

    public void init(String packageName) {
        try {
            for (Class<?> classes : getClassesFromJar(packageName)) {
                executeAnnotatedMethod(classes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Class<?>> getClassesFromJar(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();

            if ("jar".equals(protocol)) {
                String jarPath = resource.getPath();
                if (jarPath.startsWith("file:")) {
                    jarPath = jarPath.substring(5);
                }
                int bangIndex = jarPath.indexOf('!');
                if (bangIndex != -1) {
                    jarPath = jarPath.substring(0, bangIndex);
                }
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                        String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                        classes.add(Class.forName(className, true, classLoader));
                    }
                }
                jar.close();
            } else if ("file".equals(protocol)) {
                File directory = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                if (directory.exists() && directory.isDirectory()) {
                    findClassesInDirectory(packageName, directory, classes);
                }
            }
        }
        return classes;
    }

    private void findClassesInDirectory(String packageName, File directory, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findClassesInDirectory(packageName + "." + file.getName(), file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(className, true, classLoader));
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }
}