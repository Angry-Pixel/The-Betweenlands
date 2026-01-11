package thebetweenlands.client.item.armor.extension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.item.armor.extension.util.BLSimpleCustomArmorRenderer;
import thebetweenlands.client.model.armor.BoneArmorModel;

public class BoneArmorRenderer extends BLSimpleCustomArmorRenderer implements IClientItemExtensions {
	public static final BoneArmorRenderer INSTANCE = new BoneArmorRenderer();

	@Override
	protected HumanoidModel<?> createNewArmorModel(EquipmentSlot slot) {
		return new BoneArmorModel(slot, Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.BONE_ARMOR));
	}

}
