package thebetweenlands.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.gui.entries.IManualEntryItem;
import thebetweenlands.recipes.BLMaterials;

public class ItemBoneArmor extends ItemArmor implements IManualEntryItem {

    public ItemBoneArmor(int armorType) {
        super(BLMaterials.armorBone, 2, armorType);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (stack.getItem() == BLItemRegistry.boneLeggings)
            return "thebetweenlands:textures/armour/bone2.png";
        else
            return "thebetweenlands:textures/armour/bone1.png";
    }

    @Override
    public boolean getIsRepairable(ItemStack armour, ItemStack material) {
        return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.SLIMY_BONE.ordinal();
    }

    @Override
    public String manualName(int meta) {
        return "boneArmor";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{2};
    }

    @Override
    public int metas() {
        return 0;
    }
}