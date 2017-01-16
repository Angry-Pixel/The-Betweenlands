package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemBoneArmor extends ItemBLArmor {
	public ItemBoneArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_BONE, 3, slot, "bone");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "bone_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "bone_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "bone_green");
	}
}
