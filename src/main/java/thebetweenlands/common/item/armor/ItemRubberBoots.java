package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;


public class ItemRubberBoots extends ItemBLArmor /*implements IManualEntryItem*/ {
	private static final int MAX_WALK_TICKS = 40;

	public ItemRubberBoots() {
		super(BLMaterialRegistry.ARMOR_RUBBER, 2, EntityEquipmentSlot.FEET, "rubber_boots");
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return EnumItemMisc.RUBBER_BALL.isItemOf(material);
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
