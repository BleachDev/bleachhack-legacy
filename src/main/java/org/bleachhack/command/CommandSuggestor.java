package org.bleachhack.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventKeyPress;
import org.bleachhack.event.events.EventOpenScreen;
import org.bleachhack.event.events.EventRenderInGameHud;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorChatScreen;
import org.bleachhack.mixin.AccessorTextFieldWidget;
import org.bleachhack.setting.option.Option;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class CommandSuggestor {

	private static CommandSuggestor INSTANCE;

	private String curText = "";
	private List<String> suggestions = new ArrayList<>();
	private int selected = -1;
	private int scroll;

	public static CommandSuggestor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CommandSuggestor();
		}

		return INSTANCE;
	}

	public static void start() {
		BleachHack.eventBus.subscribe(getInstance());
	}

	public static void stop() {
		getInstance().reset();
		BleachHack.eventBus.unsubscribe(getInstance());
	}

	@BleachSubscribe
	public void onDrawOverlay(EventRenderInGameHud event) {
		if (!Option.CHAT_SHOW_SUGGESTIONS.getValue())
			return;

		Screen screen = MinecraftClient.getInstance().currentScreen;

		if (screen instanceof ChatScreen) {
			TextFieldWidget field = ((AccessorChatScreen) screen).getChatField();
			String text = field.getText();

			if (!text.equals(curText)) {
				suggestions.clear();
				curText = text;

				if (text.startsWith(Command.getPrefix())) {
					suggestions.addAll(CommandManager.getSuggestionProvider().getSuggestions(text.substring(Command.getPrefix().length()).split(" ", -1)));
				}

				selected = 0;
				scroll = 0;
			}

			if (selected >= 0 && selected < suggestions.size()) {
				String[] split = field.getText().split(" ", -1);
				int offset = split[split.length - 1].length() - (split.length == 1 ? Command.getPrefix().length() : 0);

				if (offset <= suggestions.get(selected).length()) {
					MinecraftClient.getInstance().textRenderer.method_956(suggestions.get(selected).substring(offset),
							((AccessorTextFieldWidget) field).getX() + MinecraftClient.getInstance().textRenderer.getStringWidth(text) + 1,
							((AccessorTextFieldWidget) field).getY(), 0xb0b0b0);
				}
			}

			if (!suggestions.isEmpty()) {
				int length = suggestions.stream()
						.map(s -> MinecraftClient.getInstance().textRenderer.getStringWidth(s))
						.min(Comparator.reverseOrder()).orElse(0);

				int startX = MinecraftClient.getInstance().textRenderer.getStringWidth(
						field.getText().replaceFirst("[^ ]*$", "") + (!field.getText().contains(" ") ? Command.getPrefix() : "")) + 3;
				int startY = screen.height - Math.min(suggestions.size(), 10) * 12 - 15;
				for (int i = scroll; i < suggestions.size() && i < scroll + 10; i++) {
					String suggestion = suggestions.get(i);

					DrawableHelper.fill(startX, startY, startX + length + 2, startY + 12, 0xd0000000);
					MinecraftClient.getInstance().textRenderer.method_956(
							suggestion, startX + 1, startY + 2, i == selected ? 0xffff00: 0xb0b0b0);

					startY += 12;
				}
			}
		}
	}

	@BleachSubscribe
	public void onKeyPressGlobal(EventKeyPress.InWorld event) {
		
	}
	
	@BleachSubscribe
	public void onKeyPressChat(EventKeyPress.InChat event) {
		TextFieldWidget field = ((AccessorChatScreen) MinecraftClient.getInstance().currentScreen).getChatField();
		if (!suggestions.isEmpty() && !curText.isEmpty()) {
			if (event.getKey() == Keyboard.KEY_DOWN) {
				selected = selected >= suggestions.size() - 1 ? 0 : selected + 1;
				updateScroll();
			} else if (event.getKey() == Keyboard.KEY_UP) {
				selected = selected <= 0 ? suggestions.size() - 1 : selected - 1;
				updateScroll();
			} else if (event.getKey() == Keyboard.KEY_SPACE || event.getKey() == Keyboard.KEY_TAB) {
				if (selected >= 0 && selected < suggestions.size()) {
					String[] split = field.getText().split(" ", -1);
					int offset = split[split.length - 1].length() - (split.length == 1 ? Command.getPrefix().length() : 0);

					if (offset < suggestions.get(selected).length() && !suggestions.get(selected).matches("^<.*>$")) {
						field.write(suggestions.get(selected).substring(offset));
					}
				}
			}
		}
		
		if (field.getText().startsWith(Command.getPrefix())
				&& (event.getKey() == Keyboard.KEY_TAB || event.getKey() == Keyboard.KEY_UP || event.getKey() == Keyboard.KEY_DOWN)) {
			event.setCancelled(true);
		}
	}

	private void updateScroll() {
		if (scroll > selected) {
			scroll = Math.max(selected, 0);
		} else if (scroll + 10 <= selected) {
			scroll = Math.min(suggestions.size(), selected - 9);
		}
	}

	@BleachSubscribe
	public void onOpenScreen(EventOpenScreen event) {
		if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
			reset();
		}
	}

	public void reset() {
		curText = "";
		suggestions.clear();
		selected = 0;
		scroll = 0;
	}
}
