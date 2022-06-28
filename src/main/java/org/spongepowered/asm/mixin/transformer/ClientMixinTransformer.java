package org.spongepowered.asm.mixin.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.service.mixinclient.BytecodeProvider;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.service.MixinService;

public class ClientMixinTransformer {

    private final MixinTransformer mixinTransformer;

    private final MixinEnvironment environment;

    public ClientMixinTransformer() {
        MixinBootstrap.init();
        this.mixinTransformer = new MixinTransformer();
        this.environment = MixinEnvironment.getDefaultEnvironment();
        SyntheticClassRegistry syntheticClassRegistry = new SyntheticClassRegistry();
        Extensions extensions = new Extensions(syntheticClassRegistry);
        MixinCoprocessorNestHost mixinCoprocessorNestHost = new MixinCoprocessorNestHost();

        DefaultExtensions.create(environment, extensions, syntheticClassRegistry, mixinCoprocessorNestHost);
    }

    public byte[] transform(String name, byte[] data) {
        if (data.length > 0) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(data);
            classReader.accept(classNode, 0);

            BytecodeProvider bytecodeProvider = (BytecodeProvider) MixinService.getService().getBytecodeProvider();

            if(!bytecodeProvider.getIgnore().contains(name)) {
                if (this.mixinTransformer.transformClass(environment, name, classNode)) {
                    ClassWriter classWriter = new ClassWriter(0);
                    classNode.accept(classWriter);
                    return classWriter.toByteArray();
                }
            } else {
                bytecodeProvider.getIgnore().remove(name);
            }
        }

        return data;
    }

}
