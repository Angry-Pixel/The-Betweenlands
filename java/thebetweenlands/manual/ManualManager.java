package thebetweenlands.manual;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.entities.property.BLEntityPropertiesRegistry;

/**
 * Created by Bart on 21/11/2015.
 */
public class ManualManager {
    public static String getCurrentManualPage(EntityPlayer player) {
        EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(player, BLEntityPropertiesRegistry.MANUAL);
        if (property != null) {
            return property.currentPage;
        }
        return null;
    }

    public static void setCurrentManualPage(EntityPlayer player, String pageName) {
        if (pageName != null) {
            BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesManual>getProperties(player, BLEntityPropertiesRegistry.MANUAL).currentPage = pageName;
        }
    }
}
