package thebetweenlands.common.item.tools;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.ItemBLArmor;

public class ItemScout extends ItemBLArmor {

    public ItemScout() {
        super(BLMaterialRegistry.ARMOR_SYRMORITE, 2, EntityEquipmentSlot.HEAD, "scout");
        this.setCreativeTab(BLCreativeTabs.SPECIALS);
    }

}
