package org.bleachhack.gui.window.widget;

import org.bleachhack.mixin.AccessorTextFieldWidget;

import net.minecraft.client.gui.widget.TextFieldWidget;

public class WindowTextFieldWidget extends WindowWidget {

	public TextFieldWidget textField;

	public WindowTextFieldWidget(int x, int y, int width, int height, String text) {
		super(x, y, x + width, y + height);
		this.textField = new TextFieldWidget(mc.textRenderer, x, y, width, height);
		this.textField.setText(text);
		this.textField.setMaxLength(32767);
	}

	protected WindowTextFieldWidget(int x, int y, int width, int height) {
		super(x, y, x + width, y + height);
	}

	@Override
	public void render(int windowX, int windowY, int mouseX, int mouseY) {
			((AccessorTextFieldWidget) textField).setX(windowX + x1);
			((AccessorTextFieldWidget) textField).setX(windowY + y1);

		textField.render();

		super.render(windowX, windowY, mouseX, mouseY);
	}

	@Override
	public void mouseClicked(int windowX, int windowY, int mouseX, int mouseY, int button) {
		super.mouseClicked(windowX, windowY, mouseX, mouseY, button);

		textField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		super.tick();

		textField.tick();
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		super.keyPressed(keyCode, scanCode, modifiers);

		textField.keyPressed((char) keyCode, scanCode);
	}
}
