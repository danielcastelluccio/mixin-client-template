package me.danterus.mixinclient.bootstrap;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
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

        this.mixinTransformer = this.loadClass("org.spongepowered.asm.mixin.transformer.ClientMixinTransformer").getConstructor().newInstance();
        this.transformMethod = this.loadClass("org.spongepowered.asm.mixin.transformer.ClientMixinTransformer").getMethod("transform", String.class, byte[].class);
    }

    private final Map<String, Class<?>> cache = new HashMap<>();

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
        if (clazz == null) {
            clazz = cache.get(name);
        }

        if(clazz == null) {
            byte[] data = this.getModifiedBytes(name);
            clazz = super.defineClass(name, data, 0, data.length);
            cache.put(name, clazz);
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

        if (name.equals("com.google.common.base.Objects")) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(data);
            classReader.accept(classNode, 0);

            if (classNode.methods.stream().noneMatch(method -> method.name.equals("firstNonNull") && method.desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))) {
                MethodNode methodNode = new MethodNode();
                methodNode.name = "firstNonNull";
                methodNode.desc = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Alternate.class.getName().replace(".", "/"), "firstNonNull", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
                insnList.add(new InsnNode(Opcodes.ARETURN));
                methodNode.instructions.add(insnList);
                methodNode.maxLocals = 3;
                methodNode.maxStack = 3;
                methodNode.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
                classNode.methods.add(methodNode);

                ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);
                data = classWriter.toByteArray();
            }
        }

        if (name.equals("com.google.common.collect.Iterators")) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(data);
            classReader.accept(classNode, 0);

            classNode.methods.stream()
                    .filter(method -> method.name.equals("emptyIterator") && method.desc.equals("()Lcom/google/common/collect/UnmodifiableIterator;"))
                    .forEach(method -> method.access += Opcodes.ACC_PUBLIC);

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            data = classWriter.toByteArray();
        }

        if (name.equals("com.google.common.util.concurrent.Futures")) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(data);
            classReader.accept(classNode, 0);

            MethodNode methodNode = new MethodNode();
            methodNode.name = "addCallback";
            methodNode.desc = "(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/FutureCallback;)V";
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Alternate.class.getName().replace(".", "/"), "addCallback", "(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/FutureCallback;)V"));
            insnList.add(new InsnNode(Opcodes.RETURN));
            methodNode.instructions.add(insnList);
            methodNode.maxLocals = 3;
            methodNode.maxStack = 3;
            methodNode.access = Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC;
            classNode.methods.add(methodNode);

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            data = classWriter.toByteArray();
        }

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
