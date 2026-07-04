package thebetweenlands.client.extensions.item.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.extensions.item.armor.util.BLSimpleCustomArmorRenderer;
import thebetweenlands.client.model.armor.SilkMaskModel;

public class SilkMaskRenderer extends BLSimpleCustomArmorRenderer implements IClientItemExtensions {
	public static final SilkMaskRenderer INSTANCE = new SilkMaskRenderer();

	@Override
	protected HumanoidModel<?> createNewArmorModel(EquipmentSlot slot) {
		return new SilkMaskModel(Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.SILK_MASK));
	}
}
