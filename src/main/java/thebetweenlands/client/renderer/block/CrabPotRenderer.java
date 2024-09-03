package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BubblerCrabModel;
import thebetweenlands.client.model.entity.SiltCrabModel;
import thebetweenlands.client.renderer.entity.BubblerCrabRenderer;
import thebetweenlands.client.renderer.entity.SiltCrabRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.AlembicBlock;
import thebetweenlands.common.block.CrabPotBlock;
import thebetweenlands.common.block.entity.CrabPotBlockEntity;
import thebetweenlands.common.entities.fishing.BubblerCrab;
import thebetweenlands.common.entities.fishing.SiltCrab;
import thebetweenlands.common.items.MobItem;

public class CrabPotRenderer implements BlockEntityRenderer<CrabPotBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/crab_pot.png"));
	private final ModelPart pot;

	private final BubblerCrabModel bubbler;
	private final SiltCrabModel silt;

	public CrabPotRenderer(BlockEntityRendererProvider.Context context) {
		this.pot = context.bakeLayer(BLModelLayers.CRAB_POT);
		this.bubbler = new BubblerCrabModel(context.bakeLayer(BLModelLayers.BUBBLER_CRAB));
		this.silt = new SiltCrabModel(context.bakeLayer(BLModelLayers.SILT_CRAB));
	}

	@Override
	public void render(CrabPotBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(AlembicBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.pot.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (entity.getLevel() != null) {
			// inputs
			if (!entity.getItem(0).isEmpty()) {
				if (this.isSafeMobItem(entity) && entity.getEntity(entity.getLevel()) != null) {
					if (entity.fallCounter > 0) {
						float smoothed = (float) entity.fallCounter * 0.03125F + ((float) entity.fallCounter * 0.03125F - (float) entity.fallCounterPrev * 0.03125F) * partialTicks;
						float smoothedTumble = (float) entity.fallCounter + ((float) entity.fallCounter - (float) entity.fallCounterPrev) * partialTicks;
						this.renderMobInSlot(stack, source, entity.getEntity(entity.getLevel()), 0.0F, 0.0625F + smoothed, 0.0F, smoothedTumble, light);
					}
					if (entity.fallCounter <= 0) {
						stack.pushPose();
						stack.translate(0.0F, 1.5F, 0.0F);
						if (!entity.animate) {
							stack.mulPose(Axis.YP.rotationDegrees((float) (90.0F - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y())));
						} else {
							stack.mulPose(Axis.YP.rotationDegrees(-90.0F + entity.getBlockState().getValue(CrabPotBlock.FACING).toYRot()));
						}
						float animationTicks = entity.prevAnimationTicks + (entity.animationTicks - entity.prevAnimationTicks) * partialTicks;

						Entity renderEntity = entity.getEntity(entity.getLevel());

						if (renderEntity != null) {
							if (renderEntity.getClass() == SiltCrab.class) {
								stack.pushPose();
								stack.scale(0.95F, -0.95F, -0.95F);
								if (entity.animate)
									this.silt.renderCrabEating(stack, source.getBuffer(RenderType.entityCutoutNoCull(SiltCrabRenderer.TEXTURE)), light, overlay, animationTicks);
								else
									this.silt.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(SiltCrabRenderer.TEXTURE)), light, overlay);
								stack.popPose();
							}

							if (renderEntity.getClass() == BubblerCrab.class) {
								stack.pushPose();
								stack.scale(0.95F, -0.95F, -0.95F);
								if (entity.animate)
									this.bubbler.renderCrabEating(stack, source.getBuffer(RenderType.entityCutoutNoCull(BubblerCrabRenderer.TEXTURE)), light, overlay, animationTicks);
								else
									this.bubbler.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(BubblerCrabRenderer.TEXTURE)), light, overlay);
								stack.popPose();
							}
						}

						stack.popPose();
					}
				} else {
					this.renderItemInSlot(stack, source, entity.getItem(0), 0.0F, 0.5F, 0.0F, 0.5F, light, overlay);
				}
			}
		}
	}

	public boolean isSafeMobItem(CrabPotBlockEntity entity) {
		return entity.getItem(0).getItem() instanceof MobItem && (entity.getEntity(entity.getLevel()) instanceof SiltCrab || entity.getEntity(entity.getLevel()) instanceof BubblerCrab);
	}

	public void renderMobInSlot(PoseStack stack, MultiBufferSource source, Entity entity, float x, float y, float z, float rotation, int light) {
		if (entity != null) {
			float scale = 0.95F;
			float tumble = rotation * 11.25F;
			float offsetRotation = 90.0F;
			float offsetY = 0.0625F;
			stack.pushPose();
			stack.translate(x, y + offsetY, z);
			stack.scale(scale, scale, scale);
			if (tumble > 0F) {
				stack.mulPose(Axis.XP.rotationDegrees(tumble));
			} else {
				stack.mulPose(Axis.XP.rotationDegrees(0.0F));
			}
			stack.mulPose(Axis.YP.rotationDegrees((float) (offsetRotation - Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y())));
			EntityRenderer<? super Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
			renderer.render(entity, 0, 0, stack, source, light);
			stack.popPose();
		}
	}

	public void renderItemInSlot(PoseStack stack, MultiBufferSource source, ItemStack item, float x, float y, float z, float scale, int light, int overlay) {
		if (!item.isEmpty()) {
			stack.pushPose();
			stack.translate(x, y, z);
			stack.scale(scale, scale, scale);
			stack.mulPose(Axis.YP.rotationDegrees((float) Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y()));
			Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}
}
