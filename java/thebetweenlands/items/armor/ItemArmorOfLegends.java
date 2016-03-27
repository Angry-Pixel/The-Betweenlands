package thebetweenlands.items.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.BLMaterial;
import thebetweenlands.manual.IManualEntryItem;

/**
 * Created by Bart on 11-9-2015.
 */
public class ItemArmorOfLegends extends ItemArmorBL implements IManualEntryItem {
    public ItemArmorOfLegends(int armorType) {
        super(BLMaterial.armorOfLegends, 2, armorType, "thebetweenlands:textures/armour/legendary1.png", "thebetweenlands:textures/armour/legendary2.png");
        this.setCreativeTab(null);
    }

    @Override
	protected boolean isLeggings(ItemStack stack) {
		return stack.getItem() == BLItemRegistry.legendaryLeggings;
	}
    
    @Override
    public String manualName(int meta) {
        return "armorOfLegends";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{6};
    }

    @Override
    public int metas() {
        return 0;
    }
}
