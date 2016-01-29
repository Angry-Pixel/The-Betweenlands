package thebetweenlands.items.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.recipes.BLMaterial;

public class ItemSyrmoriteArmor extends ItemArmorBL implements IManualEntryItem {

	public ItemSyrmoriteArmor(int armorType) {
		super(BLMaterial.armorOctine, 2, armorType, "thebetweenlands:textures/armour/syrmorite1.png", "thebetweenlands:textures/armour/syrmorite2.png");
		String itemTexture;
		switch(armorType) {
		default:
		case 0:
			itemTexture = "thebetweenlands:syrmoriteHelmet";
			break;
		case 1:
			itemTexture = "thebetweenlands:syrmoriteChestplate";
			break;
		case 2:
			itemTexture = "thebetweenlands:syrmoriteLeggings";
			break;
		case 3:
			itemTexture = "thebetweenlands:syrmoriteBoots";
			break;
		}
		this.setTextureName(itemTexture);
		this.setGemTextures(CircleGem.AQUA, itemTexture + "AquaGem", "thebetweenlands:textures/armour/syrmorite1AquaGem.png", "thebetweenlands:textures/armour/syrmorite2AquaGem.png");
		this.setGemTextures(CircleGem.CRIMSON, itemTexture + "CrimsonGem", "thebetweenlands:textures/armour/syrmorite1CrimsonGem.png", "thebetweenlands:textures/armour/syrmorite2CrimsonGem.png");
		this.setGemTextures(CircleGem.GREEN, itemTexture + "GreenGem", "thebetweenlands:textures/armour/syrmorite1GreenGem.png", "thebetweenlands:textures/armour/syrmorite2GreenGem.png");
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.SYRMORITE_INGOT.id;
	}

	@Override
	protected boolean isLeggings(ItemStack stack) {
		return stack.getItem() == BLItemRegistry.syrmoriteLeggings;
	}

	@Override
	public String manualName(int meta) {
		return "syrmoriteArmor";
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