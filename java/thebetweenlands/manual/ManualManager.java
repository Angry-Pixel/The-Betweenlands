package thebetweenlands.manual;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.entities.property.BLEntityPropertiesRegistry;

import java.util.ArrayList;

/**
 * Created by Bart on 21/11/2015.
 */
public class ManualManager {
    public static String getCurrentManualPage(EntityPlayer player) {
        EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(player, BLEntityPropertiesRegistry.MANUAL);
        if (property != null && player != null) {
            return property.currentPage;
        }
        return null;
    }

    public static void setCurrentManualPage(EntityPlayer player, String pageName) {
        if (pageName != null && player != null) {
            BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesManual>getProperties(player, BLEntityPropertiesRegistry.MANUAL).currentPage = pageName;
        }
    }

    public static void findPage(EntityPlayer player, String pageName) {
        if (pageName != null && player != null) {
            BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesManual>getProperties(player, BLEntityPropertiesRegistry.MANUAL).foundPages.add(pageName.toLowerCase());
        }
    }

    public static ArrayList<String> getFoundPages(EntityPlayer player) {
        if (player != null) {
            return BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesManual>getProperties(player, BLEntityPropertiesRegistry.MANUAL).foundPages;
        }
        return null;
    }

    public static boolean hasFoundPage(EntityPlayer player, String page) {
        if (player != null) {
            System.out.println(getFoundPages(player).size());
            for (String s : getFoundPages(player)) {
                System.out.println("comparing: " + s.toLowerCase() + ", " + page.toLowerCase());
                if (s.toLowerCase().equals(page.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
