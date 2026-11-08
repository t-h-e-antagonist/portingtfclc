package com.vomiter.tfclc.worldgen.postprocess.street;

public final class TFCLCStreetVoidContext {

    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private TFCLCStreetVoidContext() {
    }

    public static void push() {
        DEPTH.set(DEPTH.get() + 1);
    }

    public static void pop() {
        int depth = DEPTH.get();
        if (depth <= 1) {
            DEPTH.set(0);
        } else {
            DEPTH.set(depth - 1);
        }
    }

    public static boolean isActive() {
        return DEPTH.get() > 0;
    }

    public static void clear() {
        DEPTH.set(0);
    }
}