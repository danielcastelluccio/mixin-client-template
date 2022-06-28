package org.spongepowered.asm.util;

import java.io.File;

public abstract class Constants {

    public static final String CTOR = "<init>";
    public static final String CLINIT = "<clinit>";
    public static final String IMAGINARY_SUPER = "super$";
    public static final String DEBUG_OUTPUT_PATH = ".mixin.out";
    public static final String MIXIN_PACKAGE = "org.spongepowered.asm.mixin";
    public static final String MIXIN_PACKAGE_REF = Constants.MIXIN_PACKAGE.replace('.', '/');

    public static final String STRING = "java/lang/String";
    public static final String OBJECT = "java/lang/Object";
    public static final String CLASS = "java/lang/Class";

    public static final String STRING_DESC = "L" + Constants.STRING + ";";
    public static final String OBJECT_DESC = "L" + Constants.OBJECT + ";";
    public static final String CLASS_DESC = "L" + Constants.CLASS + ";";

    public static final String SYNTHETIC_PACKAGE = "org.spongepowered.asm.synthetic";
    public static final char UNICODE_SNOWMAN = '\u2603';

    public static final File DEBUG_OUTPUT_DIR = new File(Constants.DEBUG_OUTPUT_PATH);

    public static final String SIDE_DEDICATEDSERVER = "DEDICATEDSERVER";
    public static final String SIDE_SERVER = "SERVER";
    public static final String SIDE_CLIENT = "CLIENT";
    public static final String SIDE_UNKNOWN = "UNKNOWN";

    private Constants() {}

    /**
     * Shared Jar Manifest Attributes
     */
    public abstract static class ManifestAttributes {

        public static final String TWEAKER = "TweakClass";
        public static final String MAINCLASS = "Main-Class";
        public static final String MIXINCONFIGS = "MixinConfigs";
        public static final String TOKENPROVIDERS = "MixinTokenProviders";
        public static final String MIXINCONNECTOR = "MixinConnector";

        @Deprecated
        public static final String COMPATIBILITY = "MixinCompatibilityLevel";

        private ManifestAttributes() {}
    }
}