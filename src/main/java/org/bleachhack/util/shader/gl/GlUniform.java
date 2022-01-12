package org.bleachhack.util.shader.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class GlUniform {
    private static final Logger LOGGER = LogManager.getLogger();
    public int loc;
    private final int count;
    private final int dataType;
    private final IntBuffer intData;
    private final FloatBuffer floatData;
    private final String name;
    private boolean stateDirty;
    private final JsonGlProgram program;
    
    public GlUniform(String name, int datatype, int count, JsonGlProgram program) {
        this.name = name;
        this.count = count;
        this.dataType = datatype;
        this.program = program;
        if (datatype <= 3) {
            this.intData = BufferUtils.createIntBuffer(count);
            this.floatData = null;
        }
        else {
            this.intData = null;
            this.floatData = BufferUtils.createFloatBuffer(count);
        }
        this.loc = -1;
        this.markStateDirty();
    }
    
    private void markStateDirty() {
        this.stateDirty = true;
        if (this.program != null) {
            this.program.method_6939();
        }
    }
    
    public static int getTypeIndex(String typeName) {
        int integer2 = -1;
        if (typeName.equals("int")) {
            integer2 = 0;
        }
        else if (typeName.equals("float")) {
            integer2 = 4;
        }
        else if (typeName.startsWith("matrix")) {
            if (typeName.endsWith("2x2")) {
                integer2 = 8;
            }
            else if (typeName.endsWith("3x3")) {
                integer2 = 9;
            }
            else if (typeName.endsWith("4x4")) {
                integer2 = 10;
            }
        }
        return integer2;
    }
    
    public void setLoc(int integer) {
        this.loc = integer;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void method_6976(float float2) {
        this.floatData.position(0);
        this.floatData.put(0, float2);
        this.markStateDirty();
    }
    
    public void method_6977(float float2, float float3) {
        this.floatData.position(0);
        this.floatData.put(0, float2);
        this.floatData.put(1, float3);
        this.markStateDirty();
    }
    
    public void method_6978(float float2, float float3, float float4) {
        this.floatData.position(0);
        this.floatData.put(0, float2);
        this.floatData.put(1, float3);
        this.floatData.put(2, float4);
        this.markStateDirty();
    }
    
    public void method_6979(float float2, float float3, float float4, float float5) {
        this.floatData.position(0);
        this.floatData.put(float2);
        this.floatData.put(float3);
        this.floatData.put(float4);
        this.floatData.put(float5);
        this.floatData.flip();
        this.markStateDirty();
    }
    
    public void method_6986(float float2, float float3, float float4, float float5) {
        this.floatData.position(0);
        if (this.dataType >= 4) {
            this.floatData.put(0, float2);
        }
        if (this.dataType >= 5) {
            this.floatData.put(1, float3);
        }
        if (this.dataType >= 6) {
            this.floatData.put(2, float4);
        }
        if (this.dataType >= 7) {
            this.floatData.put(3, float5);
        }
        this.markStateDirty();
    }
    
    public void method_6981(int integer2, int integer3, int integer4, int integer5) {
        this.intData.position(0);
        if (this.dataType >= 0) {
            this.intData.put(0, integer2);
        }
        if (this.dataType >= 1) {
            this.intData.put(1, integer3);
        }
        if (this.dataType >= 2) {
            this.intData.put(2, integer4);
        }
        if (this.dataType >= 3) {
            this.intData.put(3, integer5);
        }
        this.markStateDirty();
    }
    
    public void method_6984(float[] arr) {
        if (arr.length < this.count) {
            GlUniform.LOGGER.warn("Uniform.set called with a too-small value array (expected " + this.count + ", got " + arr.length + "). Ignoring.");
            return;
        }
        this.floatData.position(0);
        this.floatData.put(arr);
        this.floatData.position(0);
        this.markStateDirty();
    }
    
    public void method_6980(float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, float float11, float float12, float float13, float float14, float float15, float float16, float float17) {
        this.floatData.position(0);
        this.floatData.put(0, float2);
        this.floatData.put(1, float3);
        this.floatData.put(2, float4);
        this.floatData.put(3, float5);
        this.floatData.put(4, float6);
        this.floatData.put(5, float7);
        this.floatData.put(6, float8);
        this.floatData.put(7, float9);
        this.floatData.put(8, float10);
        this.floatData.put(9, float11);
        this.floatData.put(10, float12);
        this.floatData.put(11, float13);
        this.floatData.put(12, float14);
        this.floatData.put(13, float15);
        this.floatData.put(14, float16);
        this.floatData.put(15, float17);
        this.markStateDirty();
    }
    
    public void method_6983(Matrix4f matrix4f) {
        this.method_6980(matrix4f.m00, matrix4f.m01, matrix4f.m02, matrix4f.m03, matrix4f.m10, matrix4f.m11, matrix4f.m12, matrix4f.m13, matrix4f.m20, matrix4f.m21, matrix4f.m22, matrix4f.m23, matrix4f.m30, matrix4f.m31, matrix4f.m32, matrix4f.m33);
    }
    
    public void upload() {
        if (!this.stateDirty) {}
        this.stateDirty = false;
        if (this.dataType <= 3) {
            this.uploadInts();
        }
        else if (this.dataType <= 7) {
            this.uploadFloats();
        }
        else {
            if (this.dataType > 10) {
                GlUniform.LOGGER.warn("Uniform.upload called, but type value (" + this.dataType + ") is not " + "a valid type. Ignoring.");
                return;
            }
            this.uploadMatrix();
        }
    }
    
    private void uploadInts() {
        switch (this.dataType) {
            case 0: {
                GLX.gl20Uniform1(this.loc, this.intData);
                break;
            }
            case 1: {
                GLX.gl20Uniform2(this.loc, this.intData);
                break;
            }
            case 2: {
                GLX.gl20Uniform3(this.loc, this.intData);
                break;
            }
            case 3: {
                GLX.gl20Uniform4(this.loc, this.intData);
                break;
            }
            default: {
                GlUniform.LOGGER.warn("Uniform.upload called, but count value (" + this.count + ") is " + " not in the range of 1 to 4. Ignoring.");
                break;
            }
        }
    }
    
    private void uploadFloats() {
        switch (this.dataType) {
            case 4: {
                GLX.gl20Uniform(this.loc, this.floatData);
                break;
            }
            case 5: {
                GLX.gl20Uniform2(this.loc, this.floatData);
                break;
            }
            case 6: {
                GLX.gl20Uniform3(this.loc, this.floatData);
                break;
            }
            case 7: {
                GLX.gl20Uniform4(this.loc, this.floatData);
                break;
            }
            default: {
                GlUniform.LOGGER.warn("Uniform.upload called, but count value (" + this.count + ") is " + "not in the range of 1 to 4. Ignoring.");
                break;
            }
        }
    }
    
    private void uploadMatrix() {
        switch (this.dataType) {
            case 8: {
                GLX.gl20UniformMatrix2(this.loc, true, this.floatData);
                break;
            }
            case 9: {
                GLX.gl20UniformMatrix3(this.loc, true, this.floatData);
                break;
            }
            case 10: {
                GLX.gl20UniformMatrix4(this.loc, true, this.floatData);
                break;
            }
        }
    }
}
