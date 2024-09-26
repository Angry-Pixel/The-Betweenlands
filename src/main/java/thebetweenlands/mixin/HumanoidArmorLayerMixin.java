package thebetweenlands.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.client.CircleGemTextureManager;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.registries.DataComponentRegistry;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {

	@Shadow(remap = false)
	protected abstract boolean usesInnerModel(EquipmentSlot slot);

	@Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z", shift = At.Shift.BEFORE), remap = false)
	public void renderGems(PoseStack stack, MultiBufferSource source, T entity, EquipmentSlot slot, int light, A model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		ItemStack itemstack = entity.getItemBySlot(slot);
		CircleGemType gem = itemstack.get(DataComponentRegistry.CIRCLE_GEM);
		if (gem != null) {
			VertexConsumer vertexconsumer = source.getBuffer(RenderType.armorCutoutNoCull(CircleGemTextureManager.getForMaterial(((ArmorItem)itemstack.getItem()).getMaterial(), gem, this.usesInnerModel(slot))));
			model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
		}
	}
}
