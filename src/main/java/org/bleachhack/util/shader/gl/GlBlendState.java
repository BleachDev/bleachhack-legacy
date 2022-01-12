package org.bleachhack.util.shader.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.google.gson.JsonObject;

public class GlBlendState {
    private static GlBlendState activeBlendState = null;
    private final int srcRgb;
    private final int srcAlpha;
    private final int dstRgb;
    private final int dstAlpha;
    private final int func;
    private final boolean separateBlend;
    private final boolean blendDisabled;

    private GlBlendState(boolean separateBlend, boolean blendDisabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
        this.separateBlend = separateBlend;
        this.srcRgb = srcRgb;
        this.dstRgb = dstRgb;
        this.srcAlpha = srcAlpha;
        this.dstAlpha = dstAlpha;
        this.blendDisabled = blendDisabled;
        this.func = func;
    }

    public GlBlendState() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public GlBlendState(int srcRgb, int dstRgb, int func) {
        this(false, false, srcRgb, dstRgb, srcRgb, dstRgb, func);
    }

    public GlBlendState(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
        this(true, false, srcRgb, dstRgb, srcAlpha, dstAlpha, func);
    }

    public void enable() {
        if (this.equals(activeBlendState)) {
            return;
        }
        if (activeBlendState == null || this.blendDisabled != activeBlendState.isBlendDisabled()) {
            activeBlendState = this;
            if (this.blendDisabled) {
                GL11.glDisable((int)3042);
                return;
            }
            GL11.glEnable((int)3042);
        }
        GL14.glBlendEquation((int)this.func);
        if (this.separateBlend) {
            GL14.glBlendFuncSeparate((int)this.srcRgb, (int)this.dstRgb, (int)this.srcAlpha, (int)this.dstAlpha);
        } else {
            GL11.glBlendFunc((int)this.srcRgb, (int)this.dstRgb);
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof GlBlendState)) {
            return false;
        }
        GlBlendState class_18692 = (GlBlendState)object;
        if (this.func != class_18692.func) {
            return false;
        }
        if (this.dstAlpha != class_18692.dstAlpha) {
            return false;
        }
        if (this.dstRgb != class_18692.dstRgb) {
            return false;
        }
        if (this.blendDisabled != class_18692.blendDisabled) {
            return false;
        }
        if (this.separateBlend != class_18692.separateBlend) {
            return false;
        }
        if (this.srcAlpha != class_18692.srcAlpha) {
            return false;
        }
        return this.srcRgb == class_18692.srcRgb;
    }

    public int hashCode() {
        int n = this.srcRgb;
        n = 31 * n + this.srcAlpha;
        n = 31 * n + this.dstRgb;
        n = 31 * n + this.dstAlpha;
        n = 31 * n + this.func;
        n = 31 * n + (this.separateBlend ? 1 : 0);
        n = 31 * n + (this.blendDisabled ? 1 : 0);
        return n;
    }

    public boolean isBlendDisabled() {
        return this.blendDisabled;
    }

    public static GlBlendState method_6927(JsonObject jsonObject) {
        if (jsonObject == null) {
            return new GlBlendState();
        }
        int n = 32774;
        int n2 = 1;
        int n3 = 0;
        int n4 = 1;
        int n5 = 0;
        boolean bl = true;
        boolean bl2 = false;
        if (JsonHelper.hasString(jsonObject, "func") && (n = GlBlendState.method_6928(jsonObject.get("func").getAsString())) != 32774) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "srcrgb") && (n2 = GlBlendState.method_6930(jsonObject.get("srcrgb").getAsString())) != 1) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "dstrgb") && (n3 = GlBlendState.method_6930(jsonObject.get("dstrgb").getAsString())) != 0) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "srcalpha")) {
            n4 = GlBlendState.method_6930(jsonObject.get("srcalpha").getAsString());
            if (n4 != 1) {
                bl = false;
            }
            bl2 = true;
        }
        if (JsonHelper.hasString(jsonObject, "dstalpha")) {
            n5 = GlBlendState.method_6930(jsonObject.get("dstalpha").getAsString());
            if (n5 != 0) {
                bl = false;
            }
            bl2 = true;
        }
        if (bl) {
            return new GlBlendState();
        }
        if (bl2) {
            return new GlBlendState(n2, n3, n4, n5, n);
        }
        return new GlBlendState(n2, n3, n);
    }

    private static int method_6928(String string) {
        String string2 = string.trim().toLowerCase();
        if (string2.equals("add")) {
            return 32774;
        }
        if (string2.equals("subtract")) {
            return 32778;
        }
        if (string2.equals("reversesubtract")) {
            return 32779;
        }
        if (string2.equals("reverse_subtract")) {
            return 32779;
        }
        if (string2.equals("min")) {
            return 32775;
        }
        if (string2.equals("max")) {
            return 32776;
        }
        return 32774;
    }

    private static int method_6930(String string) {
        String string2 = string.trim().toLowerCase();
        string2 = string2.replaceAll("_", "");
        string2 = string2.replaceAll("one", "1");
        string2 = string2.replaceAll("zero", "0");
        if ((string2 = string2.replaceAll("minus", "-")).equals("0")) {
            return 0;
        }
        if (string2.equals("1")) {
            return 1;
        }
        if (string2.equals("srccolor")) {
            return 768;
        }
        if (string2.equals("1-srccolor")) {
            return 769;
        }
        if (string2.equals("dstcolor")) {
            return 774;
        }
        if (string2.equals("1-dstcolor")) {
            return 775;
        }
        if (string2.equals("srcalpha")) {
            return 770;
        }
        if (string2.equals("1-srcalpha")) {
            return 771;
        }
        if (string2.equals("dstalpha")) {
            return 772;
        }
        if (string2.equals("1-dstalpha")) {
            return 773;
        }
        return -1;
    }
}
