package thebetweenlands.client.item.armor.extension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.item.armor.extension.util.BLSimpleCustomArmorRenderer;
import thebetweenlands.client.model.armor.AmphibiousArmorModel;

public class AmphibiousArmorRenderer extends BLSimpleCustomArmorRenderer implements IClientItemExtensions {
	public static final AmphibiousArmorRenderer INSTANCE = new AmphibiousArmorRenderer();

	@Override
	protected HumanoidModel<?> createNewArmorModel(EquipmentSlot slot) {
		return new AmphibiousArmorModel(slot, Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.AMPHIBIOUS_ARMOR));
	}

	@Override
	public void setupModelAnimations(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot, Model model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		if (model instanceof AmphibiousArmorModel armor) {
			armor.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}
