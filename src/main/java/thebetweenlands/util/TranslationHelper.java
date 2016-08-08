package thebetweenlands.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.resources.I18n;

public class TranslationHelper {
	private static final List<String> UNLOCALIZED_STRINGS = new ArrayList<>();

	/**
	 * Use this function if you want the string to be logged in debug mode
	 *
	 * @param unlocalizedString unlocalized string
	 * @param params additional params
	 * @return localized string
	 */
	public static String translateToLocal(String unlocalizedString, Object... params) {
		if (I18n.hasKey(unlocalizedString))
			return I18n.format(unlocalizedString, params);
		else {
			if (UNLOCALIZED_STRINGS.size() < 100 && !UNLOCALIZED_STRINGS.contains(unlocalizedString))
				UNLOCALIZED_STRINGS.add(unlocalizedString);
			return unlocalizedString;
		}
	}

	/**
	 * Use this function if you want the string to be logged in debug mode
	 *
	 * @param unlocalizedString unlocalized string
	 * @return if the string can be localized
	 */
	public static boolean canTranslate(String unlocalizedString) {
		if (I18n.hasKey(unlocalizedString))
			return true;
		else {
			if (UNLOCALIZED_STRINGS.size() < 100 && !UNLOCALIZED_STRINGS.contains(unlocalizedString))
				UNLOCALIZED_STRINGS.add(unlocalizedString);
			return false;
		}
	}
	
	public static void addUnlocalizedString(String unlocalizedString) {
		if (UNLOCALIZED_STRINGS.size() < 100 && !UNLOCALIZED_STRINGS.contains(unlocalizedString))
			UNLOCALIZED_STRINGS.add(unlocalizedString);
	}
	
	public static ImmutableList getUnlocalizedStrings() {
		return ImmutableList.copyOf(UNLOCALIZED_STRINGS);
	}
}
