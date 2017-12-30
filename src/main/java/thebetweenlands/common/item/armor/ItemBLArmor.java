package thebetweenlands.common.item.armor;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.lib.ModInfo;

public class ItemBLArmor extends ItemArmor implements IAnimatorRepairable {
	protected final String armorTexture1, armorTexture2;
	protected final String gemArmorTextures[][] = new String[CircleGemType.values().length][2];
	protected final String armorName;

	public ItemBLArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String armorName) {
		super(materialIn, renderIndexIn, equipmentSlotIn);

		this.setCreativeTab(BLCreativeTabs.GEARS);

		this.armorName = armorName;

		this.armorTexture1 = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png";
		this.armorTexture2 = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png";

		CircleGemHelper.addGemPropertyOverrides(this);
	}

	/**
	 * Adds an armor texture override for the specified gem
	 * @param type
	 * @param topHalf
	 * @param bottomHalf
	 * @return
	 */
	public ItemBLArmor setGemArmorTextureOverride(CircleGemType type, String armorName) {
		this.gemArmorTextures[type.ordinal()][0] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png";
		this.gemArmorTextures[type.ordinal()][1] = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png";
		return this;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		String texture1 = this.armorTexture1;
		String texture2 = this.armorTexture2;

		CircleGemType gem = CircleGemHelper.getGem(stack);

		if(this.gemArmorTextures[gem.ordinal()][0] != null) {
			texture1 = this.gemArmorTextures[gem.ordinal()][0];
		}
		if(this.gemArmorTextures[gem.ordinal()][1] != null) {
			texture2 = this.gemArmorTextures[gem.ordinal()][1];
		}

		if(slot == EntityEquipmentSlot.LEGS) {
			return texture2;
		} else {
			return texture1;
		}
	}
	
	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairFuelCost(this.getArmorMaterial());
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairFuelCost(this.getArmorMaterial());
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getMinRepairLifeCost(this.getArmorMaterial());
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return BLMaterialRegistry.getFullRepairLifeCost(this.getArmorMaterial());
	}
}
