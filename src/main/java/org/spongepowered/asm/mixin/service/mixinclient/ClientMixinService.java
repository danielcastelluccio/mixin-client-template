package org.spongepowered.asm.mixin.service.mixinclient;

import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterConsole;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.Constants;
import org.spongepowered.asm.util.ReEntranceLock;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ClientMixinService implements IMixinService {

    private final ReEntranceLock lock = new ReEntranceLock(1);
    private final BytecodeProvider bytecodeProvider = new BytecodeProvider();

    @Override
    public String getName() {
        return "Glass";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void prepare() {

    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return null;
    }

    @Override
    public void offer(IMixinInternal internal) {

    }

    @Override
    public void init() {

    }

    @Override
    public void beginPhase() {

    }

    @Override
    public void checkEnv(Object bootSource) {

    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return this.lock;
    }

    @Override
    public IClassProvider getClassProvider() {
        return null;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this.bytecodeProvider;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return new ArrayList<>();
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return new ContainerHandleVirtual(this.getName());
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return new ArrayList<>();
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return ClientMixinService.class.getClassLoader().getResourceAsStream(name);
    }

    @Override
    public String getSideName() {
        return Constants.SIDE_CLIENT;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return null;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return null;
    }

    private final ILogger logger = new LoggerAdapterConsole("GlassMixin");

    @Override
    public ILogger getLogger(String name) {
        return logger;
    }

}
