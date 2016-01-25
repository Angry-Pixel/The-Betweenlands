package thebetweenlands.items.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.recipes.BLMaterial;

public class ItemValoniteArmor extends ItemArmorBL implements IManualEntryItem {

	public ItemValoniteArmor(int armorType) {
		super(BLMaterial.armorValonite, 2, armorType, "thebetweenlands:textures/armour/valonite1.png", "thebetweenlands:textures/armour/valonite2.png");
		this.setGemTextures(CircleGem.AQUA, "thebetweenlands:textures/armour/valonite1AquaGem.png", "thebetweenlands:textures/armour/valonite2AquaGem.png");
		this.setGemTextures(CircleGem.CRIMSON, "thebetweenlands:textures/armour/valonite1CrimsonGem.png", "thebetweenlands:textures/armour/valonite2CrimsonGem.png");
		this.setGemTextures(CircleGem.GREEN, "thebetweenlands:textures/armour/valonite1GreenGem.png", "thebetweenlands:textures/armour/valonite2GreenGem.png");
	}

	@Override
	protected boolean isLeggings(ItemStack stack) {
		return stack.getItem() == BLItemRegistry.valoniteLeggings;
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.VALONITE_SHARD.id;
	}

	@Override
	public String manualName(int meta) {
		return "valoniteArmor";
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