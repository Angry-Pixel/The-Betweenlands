package thebetweenlands.common.item.armor;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.BLMaterial;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.registries.ItemRegistry;


public class ItemRubberBoots extends ItemBLArmor /*implements IManualEntryItem*/ {
    private static final int MAX_WALK_TICKS = 40;

    public ItemRubberBoots() {
        super(BLMaterial.ARMOR_RUBBER, 2, EntityEquipmentSlot.FEET, "rubberBoots");
    }

    @Override
    public boolean getIsRepairable(ItemStack armour, ItemStack material) {
        return material.getItem() == ItemRegistry.itemsGeneric && material.getItemDamage() == ItemGeneric.EnumItemGeneric.RUBBER_BALL.ordinal();
    }

    /*@Override
    public String manualName(int meta) {
        return "rubberBoots";
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
    }*/
}
