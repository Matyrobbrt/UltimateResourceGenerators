package com.matyrobbrt.urg.util;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Utils {

	public static String fromLang(String langName, String... args) {
		String toReturn = new TranslationTextComponent(langName).getString();
		for (int i = 0; i < args.length; i++) {
			toReturn = toReturn.replace("$" + i, args[i]);
		}
		return toReturn;
	}

	public static void appendShiftTooltip(List<ITextComponent> tooltips, List<ITextComponent> components) {
		if (Screen.hasShiftDown()) {
			tooltips.addAll(components);
		} else {
			tooltips.add(new TranslationTextComponent("tooltip.urg.hold_shift"));
		}
	}

	public static <T> List<T> conditionedList(Consumer<List<T>> addData) {
		List<T> list = Lists.newArrayList();
		addData.accept(list);
		return list;
	}

}
