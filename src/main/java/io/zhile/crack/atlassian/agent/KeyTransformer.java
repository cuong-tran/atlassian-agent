package io.zhile.crack.atlassian.agent;

import javassist.*;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author pengzhile
 * @link https://zhile.io
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
            return handleKeySpec();
        } else if(className.equals(LICENSE_DECODER_PATH)) {
            return handleLicenseDecoder();
        }

        return classfileBuffer;
    }

    private byte[] handleKeySpec() throws IllegalClassFormatException {
        String parseMethod = "Base64.getDecoder().decode";

        try {
            ClassPool cp = ClassPool.getDefault();
            cp.importPackage("java.util");
            cp.importPackage("javax.xml.bind");

            int mod = Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
            CtClass cc = cp.get(CN_KEY_SPEC.replace('/', '.'));
            CtClass cb = cp.get("byte[]");
            CtField cfOld1 = new CtField(cb, "__h_ok1", cc);
            CtField cfOld2 = new CtField(cb, "__h_ok2", cc);
            CtField cfNew = new CtField(cb, "__h_nk", cc);
            cfOld1.setModifiers(mod);
            cfOld2.setModifiers(mod);
            cfNew.setModifiers(mod);
            cc.addField(cfOld1, parseMethod + "(\"MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAIvfweZvmGo5otwawI3no7Udanxal3hX2haw962KL/nHQrnC4FG2PvUFf34OecSK1KtHDPQoSQ+DHrfdf6vKUJphw0Kn3gXm4LS8VK/LrY7on/wh2iUobS2XlhuIqEc5mLAUu9Hd+1qxsQkQ50d0lzKrnDqPsM0WA9htkdJJw2nS\");");
            cc.addField(cfOld2, parseMethod + "(\"MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGALZHuJwQzgGnYm/X9BkMcewYQnWjMIGWHd9Yom5Qw7cVIdiZkqpiSzSKurO/WAHHLN31obg7NgGkitWUysECRE3zuJVbKGhx9xjVMnP6z5SwI89vB7Gn7UWxoCvT0JZgcMyQobXeVBtM9J3EgzkdDx/+Dck7uz/l1y+HDNdRzW00=\")");
            cc.addField(cfNew, parseMethod + "(\"MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAO0DidNibJHhtgxAnM9NszURYU25CVLAlwFdOWhiUkjrjOY459ObRZDVd35hQmN/cCLkDox7y2InJE6PDWfbx9BsgPmPvH75yKgPs3B8pClQVkgIpJp08R59hoZabYuvm7mxCyDGTl2lbrOi0a3j4vM5OoCWKQjIEZ28OpjTyCr3\");");
            CtConstructor cm = cc.getConstructor("([B)V");
            cm.insertBefore("if(Arrays.equals($1,__h_ok1) || Arrays.equals($1,__h_ok2)){" +
                        "$1=__h_nk;" +
                        "System.out.println(\"============================== agent working ==============================\");" +
                    "}");

            cc.writeFile(new File("").getAbsolutePath());

            return cc.toBytecode();
        } catch (Exception e) {
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
            // 我不知道怎么从 com.atlassian.bitbucket.internal.launcher.BitbucketServerLauncher 读取这个路径，所以我直接 HARD CODE
            // Forgive me pls...
            //File libs = new File("/opt/atlassian/bitbucket/7.21.0/app/WEB-INF/lib");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL path = classLoader.getResource("");
            System.out.println("==========================pppppppppppppppppp==============================");
            System.out.println(path);
            System.out.println("==========================pppppppppppppppppp==============================");
            ClassPool cp = ClassPool.getDefault();

            URI uri = Objects.requireNonNull(path).toURI();
            File libs = new File(Path.of(uri).toFile().getParent(), "lib");
            System.out.println("==========================qqqqqqqqqqqqqqqqqq==============================");
            System.out.println(libs.getAbsolutePath());
            System.out.println("==========================qqqqqqqqqqqqqqqqqq==============================");

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
            verifyLicenseHash.setBody("{System.out.println(\"===================atlassian-agent: skip hash check===========================\");}");

            target.writeFile(new File("").getAbsolutePath());

            return target.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalClassFormatException(e.getMessage());
        }
    }

}
