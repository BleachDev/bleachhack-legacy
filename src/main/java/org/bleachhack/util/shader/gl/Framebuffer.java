package org.bleachhack.util.shader.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.TextureUtil;

public class Framebuffer {

	// hack since 1.6 mc doesn't use its own designated framebuffer
	public static Framebuffer main;
	
	private static FloatBuffer projMatrix;
	private static FloatBuffer modelMatrix;

	public int textureWidth;
	public int textureHeight;
	public int viewportWidth;
	public int viewportHeight;
	public int fbo;
	public boolean useDepthAttachment;
	public int colorAttachment;
	public int depthAttachment;

	public Framebuffer(int width, int height, boolean useDepth) {
		this.useDepthAttachment = useDepth;
		this.fbo = -1;
		this.colorAttachment = -1;
		this.depthAttachment = -1;
		this.resize(width, height);
	}

	public void resize(int width, int height) {
		if (!GLX.supportsFbo()) {
			this.viewportWidth = width;
			this.viewportHeight = height;
			return;
		}
		GL11.glEnable(2929);
		if (this.fbo >= 0) {
			this.delete();
		}
		this.initFbo(width, height);
		checkFramebufferStatus();
		GLX.advancedBindFramebuffer(GLX.framebuffer, 0);
	}

	public void delete() {
		if (!GLX.supportsFbo()) {
			return;
		}
		this.endRead();
		this.endWrite();
		if (this.depthAttachment > -1) {
			GLX.advancedDeleteRenderBuffers(this.depthAttachment);
			this.depthAttachment = -1;
		}
		if (this.colorAttachment > -1) {
			GL11.glDeleteTextures(this.colorAttachment);
			this.colorAttachment = -1;
		}
		if (this.fbo > -1) {
			GLX.advancedBindFramebuffer(GLX.framebuffer, 0);
			GLX.advancedDeleteFrameBuffers(this.fbo);
			this.fbo = -1;
		}
	}

	public void initFbo(int width, int height) {
		this.viewportWidth = width;
		this.viewportHeight = height;
		this.textureWidth = width;
		this.textureHeight = height;
		if (!GLX.supportsFbo()) {
			this.clear();
			return;
		}
		this.fbo = GLX.advancedGenFrameBuffers();
		this.colorAttachment = TextureUtil.getTexLevelParameter();
		if (this.useDepthAttachment) {
			this.depthAttachment = GLX.advancedGenRenderBuffers();
		}
		this.setTexFilter(9728);
		GL11.glBindTexture(3553, this.colorAttachment);
		GL11.glTexImage2D(3553, 0, 32856, this.textureWidth, this.textureHeight, 0, 6408, 5121, (ByteBuffer)null);
		GLX.advancedBindFramebuffer(GLX.framebuffer, this.fbo);
		GLX.advancedFrameBufferTexture2D(GLX.framebuffer, GLX.colorAttachment, 3553, this.colorAttachment, 0);
		if (this.useDepthAttachment) {
			GLX.advancedBindRenderBuffer(GLX.renderbuffer, this.depthAttachment);
			GLX.advancedRenderBufferStorage(GLX.renderbuffer, 33190, this.textureWidth, this.textureHeight);
			GLX.advancedFramebufferRenderbuffer(GLX.framebuffer, GLX.depthAttachment, GLX.renderbuffer, this.depthAttachment);
		}
		this.clear();
		this.endRead();
	}

	public void setTexFilter(int texFilter) {
		if (GLX.supportsFbo()) {
			GL11.glBindTexture(3553, this.colorAttachment);
			GL11.glTexParameterf(3553, 10241, (float)texFilter);
			GL11.glTexParameterf(3553, 10240, (float)texFilter);
			GL11.glTexParameterf(3553, 10242, 10496.0f);
			GL11.glTexParameterf(3553, 10243, 10496.0f);
			GL11.glBindTexture(3553, 0);
		}
	}

	public static void checkFramebufferStatus() {
		int integer2 = GLX.advancedCheckFrameBufferStatus(GLX.framebuffer);
		if (integer2 == GLX.completeFramebuffer) {
			return;
		}
		if (integer2 == GLX.incompleteFramebufferAttachment) {
			throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
		}
		if (integer2 == GLX.incompleteFramebufferAttachmentMiss) {
			throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
		}
		if (integer2 == GLX.incompleteFramebufferAttachmentDraw) {
			throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
		}
		if (integer2 == GLX.incompleteFramebufferAttachmentRead) {
			throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
		}
		throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + integer2);
	}

	public void beginRead() {
		if (GLX.supportsFbo()) {
			GL11.glBindTexture(3553, this.colorAttachment);
		}
	}

	public void endRead() {
		if (GLX.supportsFbo()) {
			GL11.glBindTexture(3553, 0);
		}
	}

	public void bind(boolean viewPort) {
		if (GLX.supportsFbo()) {
			GLX.advancedBindFramebuffer(GLX.framebuffer, this.fbo);
			if (viewPort) {
				GL11.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
			}
		}
	}

	public void endWrite() {
		if (GLX.supportsFbo()) {
			GLX.advancedBindFramebuffer(GLX.framebuffer, 0);
		}
	}

	public void draw(int width, int height) {
		if (!GLX.supportsFbo()) {
			return;
		}
		GL11.glColorMask(true, true, true, false);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, (double)width, (double)height, 0.0, 1000.0, 3000.0);
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
		GL11.glViewport(0, 0, width, height);
		GL11.glEnable(3553);
		GL11.glDisable(2896);
		GL11.glDisable(3008);
		GL11.glDisable(3042);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(2903);
		this.beginRead();
		float float4 = (float)width;
		float float5 = (float)height;
		float float6 = this.viewportWidth / (float)this.textureWidth;
		float float7 = this.viewportHeight / (float)this.textureHeight;
		Tessellator tessellator8 = Tessellator.INSTANCE;
		tessellator8.method_1405();
		tessellator8.method_1413(-1);
		tessellator8.method_1399(0.0, float5, 0.0, 0.0, 0.0);
		tessellator8.method_1399(float4, float5, 0.0, float6, 0.0);
		tessellator8.method_1399(float4, 0.0, 0.0, float6, float7);
		tessellator8.method_1399(0.0, 0.0, 0.0, 0.0, float7);
		tessellator8.method_1396();
		this.endRead();
		GL11.glDepthMask(true);
		GL11.glColorMask(true, true, true, true);
	}

	public void drawSimple(int width, int height) {
		enable2D(width, height, true);

		this.beginRead();
		float float4 = (float)width;
		float float5 = (float)height;
		float float6 = this.viewportWidth / (float)this.textureWidth;
		float float7 = this.viewportHeight / (float)this.textureHeight;
		Tessellator tessellator8 = Tessellator.INSTANCE;
		tessellator8.method_1405();
		tessellator8.method_1413(-1);
		tessellator8.method_1399(0.0, float5, 0.0, 0.0, 0.0);
		tessellator8.method_1399(float4, float5, 0.0, float6, 0.0);
		tessellator8.method_1399(float4, 0.0, 0.0, float6, float7);
		tessellator8.method_1399(0.0, 0.0, 0.0, 0.0, float7);
		tessellator8.method_1396();
		this.endRead();

		disable2D(width, height, true);
	}

	public void clear() {
		this.bind(true);
		GL11.glClearColor(0f, 0f, 0f, 0f);
		int integer2 = 16384;
		if (this.useDepthAttachment) {
			GL11.glClearDepth(1.0);
			integer2 |= 0x100;
		}
		GL11.glClear(integer2);
		this.endWrite();
	}
	
	public static void enable2D(int width, int height, boolean blend) {
		if (projMatrix != null || modelMatrix != null)
			throw new IllegalStateException("2D Mode can't be enabled because it isn't disabled");
		
		if (blend) {
			GL11.glEnable(GL11.GL_BLEND);
			GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
		}
		
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		projMatrix = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projMatrix);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1000, 3000);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		modelMatrix = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}
	
	public static void disable2D(int width, int height, boolean blend) {
		if (projMatrix == null || modelMatrix == null)
			throw new IllegalStateException("2D Mode can't be disabled because it isn't enabled");

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrix(projMatrix);
		projMatrix = null;

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadMatrix(modelMatrix);
		modelMatrix = null;
		
		GL11.glEnable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		if (blend)
			GL11.glDisable(GL11.GL_BLEND);
	}
}
