package thebetweenlands.util;

import net.minecraft.client.resources.I18n;
import thebetweenlands.common.TheBetweenlands;

public class TranslationHelper {

    /**
     * Use this function if you want the string to be loged in debug mode
     *
     * @param unlocalizedString unlocalized string
     * @return localized string
     */
    public static String translateToLocal(String unlocalizedString) {
        if (I18n.hasKey(unlocalizedString))
            return I18n.format(unlocalizedString);
        else {
            if (!TheBetweenlands.unlocalizedNames.contains(unlocalizedString))
                TheBetweenlands.unlocalizedNames.add(unlocalizedString);
            return unlocalizedString;
        }
    }

    /**
     * Use this function if you want the string to be loged in debug mode
     *
     * @param unlocalizedString unlocalized string
     * @return if the string can be localized
     */
    public static boolean canTranslate(String unlocalizedString) {
        if (I18n.hasKey(unlocalizedString))
            return true;
        else {
            if (!TheBetweenlands.unlocalizedNames.contains(unlocalizedString))
                TheBetweenlands.unlocalizedNames.add(unlocalizedString);
            return false;
        }
    }
}
