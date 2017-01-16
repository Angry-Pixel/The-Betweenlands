package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSyrmoriteArmor extends ItemBLArmor {
	public ItemSyrmoriteArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_SYRMORITE, 3, slot, "syrmorite");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "syrmorite_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "syrmorite_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "syrmorite_green");
	}
}
