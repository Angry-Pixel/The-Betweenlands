package thebetweenlands.items.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.recipes.BLMaterial;

public class ItemBoneArmor extends ItemArmorBL implements IManualEntryItem {

	public ItemBoneArmor(int armorType) {
		super(BLMaterial.armorBone, 2, armorType, "thebetweenlands:textures/armour/bone1.png", "thebetweenlands:textures/armour/bone2.png");
		this.setGemTextures(CircleGem.AQUA, "thebetweenlands:textures/armour/bone1AquaGem.png", "thebetweenlands:textures/armour/bone2AquaGem.png");
		this.setGemTextures(CircleGem.CRIMSON, "thebetweenlands:textures/armour/bone1CrimsonGem.png", "thebetweenlands:textures/armour/bone2CrimsonGem.png");
		this.setGemTextures(CircleGem.GREEN, "thebetweenlands:textures/armour/bone1GreenGem.png", "thebetweenlands:textures/armour/bone2GreenGem.png");
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.SLIMY_BONE.id;
	}

	@Override
	protected boolean isLeggings(ItemStack stack) {
		return stack.getItem() == BLItemRegistry.boneLeggings;
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