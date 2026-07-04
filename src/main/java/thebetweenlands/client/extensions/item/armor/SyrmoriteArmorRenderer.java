package thebetweenlands.client.extensions.item.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.extensions.item.armor.util.BLSimpleCustomArmorRenderer;
import thebetweenlands.client.model.armor.SyrmoriteArmorModel;

public class SyrmoriteArmorRenderer extends BLSimpleCustomArmorRenderer implements IClientItemExtensions {
	public static final SyrmoriteArmorRenderer INSTANCE = new SyrmoriteArmorRenderer();

	@Override
	protected HumanoidModel<?> createNewArmorModel(EquipmentSlot slot) {
		return new SyrmoriteArmorModel(slot, Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.SYRMORITE_ARMOR));
	}
}
