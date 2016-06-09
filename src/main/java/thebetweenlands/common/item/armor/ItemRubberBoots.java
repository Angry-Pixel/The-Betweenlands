package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.BLMaterial;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.registries.ItemRegistry;


public class ItemRubberBoots extends ItemBLArmor /*implements IManualEntryItem*/ {
    private static final int MAX_WALK_TICKS = 40;

    public ItemRubberBoots() {
        super(BLMaterial.ARMOR_RUBBER, 2, EntityEquipmentSlot.FEET, "rubber_boots");
    }

    @Override
    public boolean getIsRepairable(ItemStack armour, ItemStack material) {
        return material.getItem() == ItemRegistry.ITEMS_GENERIC && material.getItemDamage() == ItemGeneric.EnumItemGeneric.RUBBER_BALL.ordinal();
    }

    /*@Override
    public String manualName(int meta) {
        return "RUBBER_BOOTS";
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
