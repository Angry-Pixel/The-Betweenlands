package thebetweenlands.items.loot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.recipes.BLMaterials;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemExplorerHat extends ItemArmor {
    public ItemExplorerHat() {
        super(ArmorMaterial.CLOTH, 2, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity event, int slot, String type) {
        return "thebetweenlands:textures/armour/explorerHat.png";
    }
}
