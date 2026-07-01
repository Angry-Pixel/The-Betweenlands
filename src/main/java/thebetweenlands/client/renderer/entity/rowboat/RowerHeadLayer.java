package thebetweenlands.client.renderer.entity.rowboat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import thebetweenlands.client.model.entity.rowboat.HumanoidRowerModel;

public class RowerHeadLayer<T extends LivingEntity, M extends HumanoidRowerModel<T>> extends CustomHeadLayer<T, M> {

	private final ItemInHandRenderer itemInHandRenderer;

	public RowerHeadLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet, ItemInHandRenderer itemInHandRenderer) {
		super(renderer, modelSet, itemInHandRenderer);
		this.itemInHandRenderer = itemInHandRenderer;
	}

	//[VanillaCopy] CustomHeadLayer.render, adding extra transforms to actually attach to the head
	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
		if (!itemstack.isEmpty()) {
			Item item = itemstack.getItem();
			poseStack.pushPose();
			boolean flag = livingEntity instanceof Villager || livingEntity instanceof ZombieVillager;
			if (livingEntity.isBaby() && !(livingEntity instanceof Villager)) {
				poseStack.translate(0.0F, 0.03125F, 0.0F);
				poseStack.scale(0.7F, 0.7F, 0.7F);
				poseStack.translate(0.0F, 1.0F, 0.0F);
			}

			this.getParentModel().body.translateAndRotate(poseStack);
			this.getParentModel().getHead().translateAndRotate(poseStack);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				poseStack.scale(1.1875F, -1.1875F, -1.1875F);
				if (flag) {
					poseStack.translate(0.0F, 0.0625F, 0.0F);
				}

				ResolvableProfile resolvableprofile = itemstack.get(DataComponents.PROFILE);
				poseStack.translate(-0.5, 0.0, -0.5);
				SkullBlock.Type skullblock$type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
				SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
				RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, resolvableprofile);
				WalkAnimationState walkanimationstate;
				if (livingEntity.getVehicle() instanceof LivingEntity livingentity) {
					walkanimationstate = livingentity.walkAnimation;
				} else {
					walkanimationstate = livingEntity.walkAnimation;
				}

				float f3 = walkanimationstate.position(partialTicks);
				SkullBlockRenderer.renderSkull(null, 180.0F, f3, poseStack, buffer, packedLight, skullmodelbase, rendertype);
			} else if (!(item instanceof ArmorItem armoritem) || armoritem.getEquipmentSlot() != EquipmentSlot.HEAD) {
				translateToHead(poseStack, flag);
				this.itemInHandRenderer.renderItem(livingEntity, itemstack, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
			}

			poseStack.popPose();
		}
	}
}
