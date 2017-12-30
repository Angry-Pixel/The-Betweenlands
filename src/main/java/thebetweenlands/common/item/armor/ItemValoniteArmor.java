package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemValoniteArmor extends ItemBLArmor {
	public ItemValoniteArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_VALONITE, 3, slot, "valonite");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "valonite_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "valonite_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "valonite_green");
	}
}
