package thebetweenlands.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.recipes.BLMaterials;

/**
 * Created by Bart on 11-9-2015.
 */
public class ItemSkullMask extends ItemArmor {
    public ItemSkullMask() {
        super(BLMaterials.armorBone, 2, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "thebetweenlands:textures/armour/skullMask.png";
    }

    @Override
    public boolean getIsRepairable(ItemStack armour, ItemStack material) {
        return material.getItem() == BLItemRegistry.materialsBL && material.getItemDamage() == ItemMaterialsBL.EnumMaterialsBL.SLIMY_BONE.ordinal();
    }
}
