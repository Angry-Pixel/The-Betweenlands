package thebetweenlands.items.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.BLMaterial;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;

public class ItemBoneArmor extends ItemArmorBL implements IManualEntryItem {

	public ItemBoneArmor(int armorType) {
		super(BLMaterial.armorBone, 2, armorType, "thebetweenlands:textures/armour/bone1.png", "thebetweenlands:textures/armour/bone2.png");
		String itemTexture;
		switch(armorType) {
		default:
		case 0:
			itemTexture = "thebetweenlands:boneHelmet";
			break;
		case 1:
			itemTexture = "thebetweenlands:boneChestplate";
			break;
		case 2:
			itemTexture = "thebetweenlands:boneLeggings";
			break;
		case 3:
			itemTexture = "thebetweenlands:boneBoots";
			break;
		}
		this.setTextureName(itemTexture);
		this.setGemTextures(CircleGem.AQUA, itemTexture + "AquaGem", "thebetweenlands:textures/armour/bone1AquaGem.png", "thebetweenlands:textures/armour/bone2AquaGem.png");
		this.setGemTextures(CircleGem.CRIMSON, itemTexture + "CrimsonGem", "thebetweenlands:textures/armour/bone1CrimsonGem.png", "thebetweenlands:textures/armour/bone2CrimsonGem.png");
		this.setGemTextures(CircleGem.GREEN, itemTexture + "GreenGem", "thebetweenlands:textures/armour/bone1GreenGem.png", "thebetweenlands:textures/armour/bone2GreenGem.png");
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