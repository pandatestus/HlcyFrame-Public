package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.HlcyFrame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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
    private final Plugin plugin; // Für besseres Logging

    public CommandInitializer(Plugin plugin) {
        this.plugin = plugin;
        this.classLoader = plugin.getClass().getClassLoader();
    }

    public void init(String packageName) {
        try {
            plugin.getLogger().info("Scanning package for @HlcyCMD: " + packageName);
            List<Class<?>> classes = getClassesFromJar(packageName);
            plugin.getLogger().info("Found " + classes.size() + " candidate class(es) in package '" + packageName + "'");

            for (Class<?> clazz : classes) {
                plugin.getLogger().info("Processing class: " + clazz.getName());
                executeAnnotatedMethod(clazz);
            }
        } catch (Throwable t) { // ⚠️ Unbedingt Throwable!
            plugin.getLogger().severe("FATAL ERROR during command initialization in package: " + packageName);
            t.printStackTrace(); // Direkt ins Log
            // Optional: Plugin stoppen, damit du den Fehler nicht übersehen kannst
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void executeAnnotatedMethod(Class<?> clazz) {
        try {
            // Prüfen, ob die Klasse überhaupt instanziierbar ist
            if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                plugin.getLogger().warning("Skipping abstract/interface class: " + clazz.getName());
                return;
            }

            Object instance = clazz.getDeclaredConstructor().newInstance();

            boolean found = false;
            for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(HlcyCMD.class)) {
                    plugin.getLogger().info("Invoking @HlcyCMD method: " + method.getName() + " in " + clazz.getSimpleName());
                    method.setAccessible(true);
                    method.invoke(instance);
                    found = true;
                    break;
                }
            }

            if (!found) {
                plugin.getLogger().warning("No @HlcyCMD method found in class: " + clazz.getName());
            }
        } catch (Throwable e) {
            plugin.getLogger().severe("Failed to instantiate or invoke @HlcyCMD in class: " + clazz.getName());
            e.printStackTrace();
            throw new RuntimeException("Command class failed: " + clazz.getName(), e);
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
                String fullPath = resource.toString();
                // Beispiel: jar:file:/plugins/MeinPlugin.jar!/de/panda/commands
                if (fullPath.startsWith("jar:file:")) {
                    String jarPath = fullPath.substring("jar:file:".length());
                    int bangIndex = jarPath.indexOf('!');
                    if (bangIndex > 0) {
                        jarPath = jarPath.substring(0, bangIndex);
                    }
                    jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

                    try (JarFile jar = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String entryName = entry.getName();

                            // Nur .class-Dateien im gewünschten Package
                            if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                                // Skip innere Klassen (MyClass$1.class etc.)
                                if (entryName.contains("$")) continue;

                                String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                                try {
                                    Class<?> clazz = Class.forName(className, false, classLoader);
                                    classes.add(clazz);
                                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                                    plugin.getLogger().warning("Could not load class: " + className + " - " + ex.getMessage());
                                }
                            }
                        }
                    }
                }
            } else if ("file".equals(protocol)) {
                // Entwicklungsumgebung (IDE)
                File dir = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                if (dir.exists() && dir.isDirectory()) {
                    findClassesInDirectory(packageName, dir, classes);
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
                String simpleName = file.getName().substring(0, file.getName().length() - 6);
                if (simpleName.contains("$")) continue; // skip inner classes

                String className = packageName + '.' + simpleName;
                try {
                    classes.add(Class.forName(className, false, classLoader));
                } catch (ClassNotFoundException ignored) {
                    plugin.getLogger().warning("Class not found in dev mode: " + className);
                }
            }
        }
    }
}