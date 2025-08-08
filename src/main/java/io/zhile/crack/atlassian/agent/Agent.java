package io.zhile.crack.atlassian.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author pengzhile
 * @link <a href="https://zhile.io">zhile.io</a>
 * @version 1.0
 */
public class Agent {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("============================== Agent loaded ==============================");
        System.out.println("Agent args: " + args);
        try {
            inst.addTransformer(new KeyTransformer());
            System.out.println("============================== KeyTransformer added ==============================");
        } catch (Exception e) {
            System.err.println("============================== Agent failed ==============================");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
