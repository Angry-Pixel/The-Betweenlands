package thebetweenlands.common.item.armor.extension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.armor.ExplorersHatModel;
import thebetweenlands.client.model.armor.SilkMaskModel;

public class SilkMaskRenderer implements IClientItemExtensions {

	@Override
	public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
		return new SilkMaskModel(Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.SILK_MASK));
	}
}
