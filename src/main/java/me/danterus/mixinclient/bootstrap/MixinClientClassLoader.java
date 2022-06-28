package me.danterus.mixinclient.bootstrap;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class MixinClientClassLoader extends URLClassLoader {

    private final Object mixinTransformer;
    private final Method transformMethod;

    private final List<String> exclusions = new ArrayList<>();

    private final List<URL> urls = new ArrayList<>();

    private final ClassLoader parent = MixinClientClassLoader.class.getClassLoader();

    public MixinClientClassLoader() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        super(new URL[0], null);

        this.exclusions.add("java.");
        this.exclusions.add("jdk.");
        this.exclusions.add("javax.");

        this.exclusions.add("sun.");
        this.exclusions.add("com.sun.");
        this.exclusions.add("org.xml.");
        this.exclusions.add("org.w3c.");

        this.exclusions.add("org.apache.");
        this.exclusions.add("org.slf4j.");
        this.exclusions.add("com.mojang.blocklist.");

        this.exclusions.add("com.github.glassmc.loader.api.loader.Transformer");
        this.exclusions.add("com.github.glassmc.loader.api.ClassDefinition");

        this.mixinTransformer = this.loadClass("org.spongepowered.asm.mixin.transformer.ClientMixinTransformer").getConstructor().newInstance();
        this.transformMethod = this.loadClass("org.spongepowered.asm.mixin.transformer.ClientMixinTransformer").getMethod("transform", String.class, byte[].class);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (String exclusion : this.exclusions) {
            if (name.startsWith(exclusion)) {
                try {
                    return parent.loadClass(name);
                } catch (ClassNotFoundException ignored) {

                }
            }
        }

        Class<?> clazz = super.findLoadedClass(name);
        if(clazz == null) {
            byte[] data = this.getModifiedBytes(name);
            clazz = super.defineClass(name, data, 0, data.length);
        }
        return clazz;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = this.loadClass(name);
        if (resolve) {
            this.resolveClass(clazz);
        }
        return clazz;
    }

    public byte[] getModifiedBytes(String name) throws ClassNotFoundException {
        byte[] data = loadClassData(name);

        try {
            name = name.replace(".", "/");
            if (this.transformMethod != null && !name.startsWith("org/objectweb/") && !name.startsWith("org/spongepowered/") && !name.startsWith("com/google/")) {
                data = (byte[]) this.transformMethod.invoke(this.mixinTransformer, name, data);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (data.length == 0) {
            throw new ClassNotFoundException(name);
        }

        return data;
    }

    private byte[] loadClassData(String className) {
        try {
            Enumeration<URL> resources = this.getResources(className.replace(".", "/") + ".class");

            List<String> locations = new ArrayList<>();
            List<byte[]> datas = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                locations.add(resource.toString());
                datas.add(IOUtils.toByteArray(resource));
            }

            byte[][] result;
            if (datas.size() > 0) {
                result = datas.toArray(new byte[0][]);
            } else {
                result = new byte[][] { new byte[0] };
            }

            if (className.contains("io.Closeables")) {
                for (int i = 0; i < result.length; i++) {
                    ClassReader classReader = new ClassReader(result[i]);
                    ClassNode classNode = new ClassNode();
                    classReader.accept(classNode, 0);

                    boolean hasCloseQuietly = classNode.methods.stream().anyMatch(method -> method.name.equals("closeQuietly") && method.desc.equals("(Ljava/io/Reader;)V"));
                    if (hasCloseQuietly) {
                        byte[] first = result[0];
                        result[0] = result[i];
                        result[i] = first;
                    }
                }
            }

            return result[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> parentResources = Collections.list(parent.getResources(name));

        List<URL> filteredURLs = new ArrayList<>(parentResources);

        for (URL pathUrl : Collections.list(this.findResources(name))) {
            for (URL url : this.urls) {
                if (pathUrl.getFile().contains(url.getFile())) {
                    filteredURLs.add(pathUrl);
                }
            }
        }

        return Collections.enumeration(filteredURLs);
    }

    @Override
    public URL getResource(String name) {
        try {
            Enumeration<URL> resources = this.getResources(name);
            if(resources.hasMoreElements()) {
                return resources.nextElement();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return parent.getResource(name);
    }

    public void addURL(URL url) {
        this.urls.add(url);
        super.addURL(url);
    }

    @SuppressWarnings("unused")
    public void removeURL(URL url) {
        this.urls.remove(url);
    }

}
