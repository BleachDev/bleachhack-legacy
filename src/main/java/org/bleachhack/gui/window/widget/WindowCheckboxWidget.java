package org.bleachhack.gui.window.widget;

import org.bleachhack.gui.window.Window;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class WindowCheckboxWidget extends WindowWidget {

	public boolean checked;
	public String text;

	public WindowCheckboxWidget(int x, int y, String text, boolean pressed) {
		super(x, y, 10 + MinecraftClient.getInstance().textRenderer.getStringWidth(text), 10);
		this.checked = pressed;
		this.text = text;
	}

	@Override
	public void render(int windowX, int windowY, int mouseX, int mouseY) {
		super.render(windowX, windowY, mouseX, mouseY);

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

		int x = windowX + x1;
		int y = windowY + y1;
		int color = mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 10 ? 0x906060ff : 0x9040409f;

		Window.fill(x, y, x + 11, y + 11, color);

		if (checked) {
			textRenderer.draw("\u2714", x + 2, y + 2, 0xffeeff);
			//fill(matrix, x + 3, y + 3, x + 7, y + 7, 0xffffffff);
		}

		textRenderer.draw(text, x + 15, y + 2, 0xc0c0c0, true);
	}

	@Override
	public void mouseClicked(int windowX, int windowY, int mouseX, int mouseY, int button) {
		super.mouseClicked(windowX, windowY, mouseX, mouseY, button);

		if (mouseX >= windowX + x1 && mouseX <= windowX + x1 + 10 && mouseY >= windowY + y1 && mouseY <= windowY + y1 + 10) {
			checked = !checked;
		}
	}
}
