package de.panda.hlcyFrame.Command;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
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
    private final Plugin plugin;

    public CommandInitializer(Plugin plugin) {
        this.plugin = plugin;
        this.classLoader = plugin.getClass().getClassLoader();
    }

    public void init(String packageName) {
        try {
            List<Class<?>> classes = getClassesFromJar(packageName);

            for (Class<?> clazz : classes) {
                executeAnnotatedMethod(clazz);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void executeAnnotatedMethod(Class<?> clazz) {
        try {
            if(JavaPlugin.class.isAssignableFrom(clazz)) return;

            if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) return;


            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length == 0) {
                return;
            }
            Constructor<?> ctor = null;
            for (Constructor<?> c : constructors) {
                if (c.getParameterCount() == 0) {
                    ctor = c;
                    break;
                }
            }

            if(ctor == null) return;

            ctor.setAccessible(true);
            Object instance = clazz.newInstance();

            for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(HlcyCMD.class)) {
                    method.setAccessible(true);
                    method.invoke(instance);
                    break;
                }
            }
        } catch (Throwable e) {
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

                            if (entryName.startsWith(path) && entryName.endsWith(".class")) {
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