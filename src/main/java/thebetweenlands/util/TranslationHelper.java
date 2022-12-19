package thebetweenlands.util;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.text.translation.I18n;

public class TranslationHelper {
	private static final Set<String> UNLOCALIZED_STRINGS = new HashSet<>();

	/**
	 * Use this function if you want the string to be logged in debug mode
	 *
	 * @param unlocalizedString unlocalized string
	 * @param params additional params
	 * @return localized string
	 */
	public static String translateToLocal(String unlocalizedString, Object... params) {
		//Needs to be old deprecated I18n for now because Item#getItemStackDisplayName is not @SideOnly(Side.CLIENT)!
		if (I18n.canTranslate(unlocalizedString))
			return I18n.translateToLocalFormatted(unlocalizedString, params);
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
		if (I18n.canTranslate(unlocalizedString))
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
