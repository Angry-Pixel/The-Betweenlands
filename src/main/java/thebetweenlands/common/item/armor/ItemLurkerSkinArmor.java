package thebetweenlands.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemLurkerSkinArmor extends ItemBLArmor {
	public ItemLurkerSkinArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_LURKER_SKIN, 3, slot, "lurker_skin");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "lurker_skin_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "lurker_skin_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "lurker_skin_green");
	}
}
