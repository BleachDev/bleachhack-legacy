package org.bleachhack.gui.window.widget;

import org.lwjgl.opengl.GL11;

public class WindowTextWidget extends WindowWidget {

	private String text;
	private float scale;
	public boolean shadow;
	public int color;
	public TextAlign align;

	public WindowTextWidget(String text, boolean shadow, int x, int y, int color) {
		this(text, shadow, TextAlign.LEFT, x, y, color);
	}

	public WindowTextWidget(String text, boolean shadow, TextAlign align, int x, int y, int color) {
		this(text, shadow, align, 1f, x, y, color);
	}

	public WindowTextWidget(String text, boolean shadow, TextAlign align, float scale, int x, int y, int color) {
		super(x, y, x + mc.textRenderer.getStringWidth(text), (int) (y + 10 * scale));
		this.text = text;
		this.shadow = shadow;
		this.color = color;
		this.align = align;
		this.scale = scale;
	}

	@Override
	public void render(int windowX, int windowY, int mouseX, int mouseY) {
		super.render(windowX, windowY, mouseX, mouseY);

		float offset = mc.textRenderer.getStringWidth(text) * align.offset * scale;

		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, 1);
		mc.textRenderer.method_956(text, (int) ((windowX + x1 - offset) / scale), (int) ((windowY + y1) / scale), color);
		GL11.glPopMatrix();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.x2 = x1 + mc.textRenderer.getStringWidth(text);
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		this.x2 = (int) (x1 + mc.textRenderer.getStringWidth(text) * scale);
		this.y2 = (int) (y1 + 10 * scale);
	}

	public enum TextAlign {
		LEFT(0f),
		MIDDLE(0.5f),
		RIGHT(1f);

		public final float offset;

		TextAlign(float offset) {
			this.offset = offset;
		}
	}

}
