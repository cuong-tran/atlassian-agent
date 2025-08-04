//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.zhile.crack.atlassian.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Objects;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class KeyTransformer implements ClassFileTransformer {
    private static final String CN_KEY_MANAGER = "com/atlassian/extras/keymanager/KeyManager";
    private static final String LICENSE_DECODER_PATH = "com/atlassian/extras/decoder/v2/Version2LicenseDecoder";
    private static final String LICENSE_DECODER_CLASS = "com.atlassian.extras.decoder.v2.Version2LicenseDecoder";

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return classfileBuffer;
        } else if (className.equals("com/atlassian/extras/keymanager/KeyManager")) {
            System.out.println("============================== Transforming KeyManager class ==============================");
            return this.handleKeyManager();
        } else if (className.equals("com/atlassian/extras/decoder/v2/Version2LicenseDecoder")) {
            System.out.println("============================== Transforming LicenseDecoder class ==============================");
            return this.handleLicenseDecoder();
        } else {
            return classfileBuffer;
        }
    }

    private byte[] handleKeyManager() throws IllegalClassFormatException {
        try {
            System.out.println("============================== Starting handleKeyManager ==============================");
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get("com/atlassian/extras/keymanager/KeyManager".replace('/', '.'));
            CtMethod resetMethod = cc.getDeclaredMethod("reset");
            String newMethodBody = "{\n    this.privateKeys.clear();\n    this.publicKeys.clear();\n    java.util.List keys = new java.util.ArrayList();\n\n    for(java.util.Iterator iter = this.env.entrySet().iterator(); iter.hasNext();) {\n        java.util.Map.Entry envVar = (java.util.Map.Entry) iter.next();\n        String envVarKey = (String)envVar.getKey();\n        if (envVarKey.startsWith(\"ATLAS_LICENSE_PRIVATE_KEY_\")) {\n            keys.add(new com.atlassian.extras.keymanager.Key((String)envVar.getValue(), extractVersion(envVarKey), com.atlassian.extras.keymanager.Key.Type.PRIVATE));\n        }\n\n        if (envVarKey.startsWith(\"ATLAS_LICENSE_PUBLIC_KEY_\")) {\n            keys.add(new com.atlassian.extras.keymanager.Key((String)envVar.getValue(), extractVersion(envVarKey), com.atlassian.extras.keymanager.Key.Type.PUBLIC));\n        }\n    }\n\n    for(java.util.Iterator it = keys.iterator(); it.hasNext();) {\n        com.atlassian.extras.keymanager.Key key = (com.atlassian.extras.keymanager.Key)it.next();\n        this.loadKey(key);\n    }\n\n    // 使用替换后的公钥\n    System.out.println(\"============================== agent working: replacing public keys ==============================\");\n    this.loadKey(new com.atlassian.extras.keymanager.Key(\"MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAO0DidNibJHhtgxAnM9NszURYU25CVLAlwFdOWhiUkjrjOY459ObRZDVd35hQmN/cCLkDox7y2InJE6PDWfbx9BsgPmPvH75yKgPs3B8pClQVkgIpJp08R59hoZabYuvm7mxCyDGTl2lbrOi0a3j4vM5OoCWKQjIEZ28OpjTyCr3\", \"LICENSE_STRING_KEY_V2\", com.atlassian.extras.keymanager.Key.Type.PUBLIC));\n    this.loadKey(new com.atlassian.extras.keymanager.Key(\"MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGALZHuJwQzgGnYm/X9BkMcewYQnWjMIGWHd9Yom5Qw7cVIdiZkqpiSzSKurO/WAHHLN31obg7NgGkitWUysECRE3zuJVbKGhx9xjVMnP6z5SwI89vB7Gn7UWxoCvT0JZgcMyQobXeVBtM9J3EgzkdDx/+Dck7uz/l1y+HDNdRzW00=\", \"1600708331\", com.atlassian.extras.keymanager.Key.Type.PUBLIC));\n}";
            resetMethod.setBody(newMethodBody);
            System.out.println("============================== KeyManager transformation completed ==============================");
            return cc.toBytecode();
        } catch (Exception e) {
            System.err.println("============================== Error in handleKeyManager ==============================");
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

    private byte[] handleLicenseDecoder() throws IllegalClassFormatException {
        try {
            File libs = new File("/home/atlassian/apps/atlassian-jira-software-9.12.25-standalone/atlassian-jira/WEB-INF/lib");
            ClassPool cp = ClassPool.getDefault();
            Arrays.stream((File[])Objects.requireNonNull(libs.listFiles())).map(File::getAbsolutePath).forEach((it) -> {
                try {
                    cp.insertClassPath(it);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            cp.importPackage("com.atlassian.extras.common.LicenseException");
            cp.importPackage("com.atlassian.extras.common.org.springframework.util.DefaultPropertiesPersister");
            cp.importPackage("com.atlassian.extras.decoder.api.AbstractLicenseDecoder");
            cp.importPackage("com.atlassian.extras.decoder.api.LicenseVerificationException");
            cp.importPackage("com.atlassian.extras.keymanager.KeyManager");
            cp.importPackage("com.atlassian.extras.keymanager.SortedProperties");
            cp.importPackage("java.io.ByteArrayInputStream");
            cp.importPackage("java.io.ByteArrayOutputStream");
            cp.importPackage("java.io.DataInputStream");
            cp.importPackage("java.io.DataOutputStream");
            cp.importPackage("java.io.IOException");
            cp.importPackage("java.io.InputStream");
            cp.importPackage("java.io.InputStreamReader");
            cp.importPackage("java.io.OutputStream");
            cp.importPackage("java.io.Reader");
            cp.importPackage("java.io.StringWriter");
            cp.importPackage("java.io.Writer");
            cp.importPackage("java.nio.charset.Charset");
            cp.importPackage("java.nio.charset.StandardCharsets");
            cp.importPackage("java.text.SimpleDateFormat");
            cp.importPackage("java.util.Date");
            cp.importPackage("java.util.Map");
            cp.importPackage("java.util.Properties");
            cp.importPackage("java.util.zip.Inflater");
            cp.importPackage("java.util.zip.InflaterInputStream");
            cp.importPackage("org.apache.commons.codec.binary.Base64");
            CtClass target = cp.getCtClass("com.atlassian.extras.decoder.v2.Version2LicenseDecoder");
            CtMethod verifyLicenseHash = target.getDeclaredMethod("verifyLicenseHash");
            verifyLicenseHash.setBody("{System.out.println(\"atlassian-agent: skip hash check\");}");
            return target.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }
}
