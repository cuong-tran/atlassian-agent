package io.zhile.crack.atlassian.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author pengzhile
 * @link <a href="https://zhile.io">zhile.io</a>
 * @version 1.0
 */
public class KeyTransformer implements ClassFileTransformer {
    private static final String CN_KEY_SPEC = "java/security/spec/EncodedKeySpec";

    private static final String LICENSE_DECODER_PATH = "com/atlassian/extras/decoder/v2/Version2LicenseDecoder";
    private static final String LICENSE_DECODER_CLASS = "com.atlassian.extras.decoder.v2.Version2LicenseDecoder";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        }

        if (className.equals(CN_KEY_SPEC)) {
            // If it falls here, license will work but there's the warning: `The product license you're using is not legitimate.`
            System.out.println("============================== Transforming KeySpec class ==============================");
            return handleKeySpec();
        } else if (className.equals(LICENSE_DECODER_PATH)) {
            // Atlassian will try this (LICENSE_DECODER_PATH) first, if failed then trying CN_KEY_SPEC.
            System.out.println("============================== Transforming LicenseDecoder class ==============================");
            return handleLicenseDecoder();
        }

        return classfileBuffer;
    }

    private byte[] handleKeySpec() throws IllegalClassFormatException {
        System.out.println("============================== Starting handleKeySpec ==============================");
        try {
            String b64f;
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get(CN_KEY_SPEC.replace('/', '.'));

            cp.importPackage("java.util.Arrays");
            try {
                Class.forName("java.util.Base64");
                cp.importPackage("java.util.Base64");
                b64f = "Base64.getDecoder().decode";
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("javax.xml.bind.DatatypeConverter");
                    cp.importPackage("javax.xml.bind.DatatypeConverter");
                    b64f = "DatatypeConverter.parseBase64Binary";
                } catch (ClassNotFoundException e1) {
                    throw new RuntimeException(e1);
                }
            }
            cc.addField(CtField.make("private static final byte[] __h_ok1=" + b64f + "(\"MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAIvfweZvmGo5otwawI3no7Udanxal3hX2haw962KL/nHQrnC4FG2PvUFf34OecSK1KtHDPQoSQ+DHrfdf6vKUJphw0Kn3gXm4LS8VK/LrY7on/wh2iUobS2XlhuIqEc5mLAUu9Hd+1qxsQkQ50d0lzKrnDqPsM0WA9htkdJJw2nS\");", cc));
            cc.addField(CtField.make("private static final byte[] __h_ok2=" + b64f + "(\"MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGALZHuJwQzgGnYm/X9BkMcewYQnWjMIGWHd9Yom5Qw7cVIdiZkqpiSzSKurO/WAHHLN31obg7NgGkitWUysECRE3zuJVbKGhx9xjVMnP6z5SwI89vB7Gn7UWxoCvT0JZgcMyQobXeVBtM9J3EgzkdDx/+Dck7uz/l1y+HDNdRzW00=\");", cc));
            cc.addField(CtField.make("private static final byte[] __h_nk=" + b64f + "(\"MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAO0DidNibJHhtgxAnM9NszURYU25CVLAlwFdOWhiUkjrjOY459ObRZDVd35hQmN/cCLkDox7y2InJE6PDWfbx9BsgPmPvH75yKgPs3B8pClQVkgIpJp08R59hoZabYuvm7mxCyDGTl2lbrOi0a3j4vM5OoCWKQjIEZ28OpjTyCr3\");", cc));
            CtConstructor cm = cc.getConstructor("([B)V");
            cm.insertBefore("if(Arrays.equals($1,__h_ok1)||Arrays.equals($1,__h_ok2)){$1=__h_nk;System.out.println(\"============================== agent working ==============================\");}");
            System.out.println("============================== KeySpec transformation completed ==============================");

            return cc.toBytecode();
        } catch (Exception e) {
            System.err.println("============================== Error in handleKeySpec ==============================");
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }


    /**
     * 移除用于验证哈希的方法: <code>com.atlassian.extras.decoder.v2.Version2LicenseDecoder#verifyLicenseHash</code>
     *
     * @return 修改过的类的字节码
     * @throws IllegalClassFormatException 当某些地方出问题了就会抛出这个异常
     */
    private byte[] handleLicenseDecoder() throws IllegalClassFormatException {
        try {
            Map<String, String> osEnv = System.getenv();
            String atlassianDir = osEnv.get("ATLASSIAN_DIR");
            if (atlassianDir == null || atlassianDir.isEmpty()) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                URL path = classLoader.getResource("");
                URI uri = Objects.requireNonNull(path).toURI();
                File libsDir = new File(Path.of(uri).toFile().getParent(), "lib");
                atlassianDir = libsDir.getAbsolutePath();
            }
            System.out.println("============ agent: the ATLASSIAN_DIR is: " + atlassianDir + " ============");
            File libs = new File(atlassianDir);
            ClassPool cp = ClassPool.getDefault();

            Arrays.stream(Objects.requireNonNull(libs.listFiles())).map(File::getAbsolutePath).forEach((it) -> {
                try {
                    cp.insertClassPath(it);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            cp.importPackage("com.atlassian.extras.common");
            cp.importPackage("com.atlassian.extras.decoder.api");
            cp.importPackage("com.atlassian.extras.keymanager");
            cp.importPackage("java.io");
            cp.importPackage("java.nio.charset");
            cp.importPackage("java.text");
            cp.importPackage("java.util");
            cp.importPackage("org.apache.commons.codec.binary");

            CtClass target = cp.getCtClass(LICENSE_DECODER_CLASS);
            CtMethod verifyLicenseHash = target.getDeclaredMethod("verifyLicenseHash");
            verifyLicenseHash.setBody("{System.out.println(\"=============== agent: skip license hash check ===============\");}");

            CtMethod checkAndGetLicenseText = target.getDeclaredMethod("checkAndGetLicenseText");
            checkAndGetLicenseText.setBody("        try {\n" +
                    "            byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64($1.getBytes(StandardCharsets.UTF_8));\n" +
                    "            ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);\n" +
                    "            DataInputStream dIn = new DataInputStream(in);\n" +
                    "            int textLength = dIn.readInt();\n" +
                    "            byte[] licenseText = new byte[textLength];\n" +
                    "            dIn.read(licenseText);\n" +
                    "            System.out.println(\"=============== agent working: skip verify the license. ===============\");\n" +
                    "            return licenseText;\n" +
                    "        } catch (Exception var10) {\n" +
                    "            throw new LicenseException(var10);\n" +
                    "        }");
            return target.toBytecode();
        } catch (Exception e) {
            System.err.println("============================== Error in handleLicenseDecoder ==============================");
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }
}
