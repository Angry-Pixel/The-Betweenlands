package thebetweenlands.common.item.armor.extension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SmallSpiritTreeFaceModel;
import thebetweenlands.common.TheBetweenlands;

public class SmallSpiritTreeMaskRenderer extends MaskRenderer {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/misc/small_spirit_tree_mask.png");
	private static final ResourceLocation SIDE_TEXTURE = TheBetweenlands.prefix("textures/misc/small_spirit_tree_mask_side.png");

	@Override
	public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
		return new HumanoidArmorModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(BLModelLayers.SMALL_SPIRIT_TREE_MASK));
	}

	@Override
	public ResourceLocation getOverlayTexture() {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getSideOverlayTexture(boolean left) {
		return SIDE_TEXTURE;
	}
}
