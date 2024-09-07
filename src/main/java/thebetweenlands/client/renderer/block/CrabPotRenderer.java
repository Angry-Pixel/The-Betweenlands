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
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.model.entity.BubblerCrabModel;
import thebetweenlands.client.model.entity.SiltCrabModel;
import thebetweenlands.client.renderer.entity.BubblerCrabRenderer;
import thebetweenlands.client.renderer.entity.SiltCrabRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.AlembicBlock;
import thebetweenlands.common.block.CrabPotBlock;
import thebetweenlands.common.block.entity.CrabPotBlockEntity;
import thebetweenlands.common.items.MobItem;
import thebetweenlands.common.registries.EntityRegistry;

public class CrabPotRenderer implements BlockEntityRenderer<CrabPotBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/crab_pot.png"));
	private final ModelPart pot;

	private final BubblerCrabModel bubbler;
	private final SiltCrabModel silt;
	private final ItemRenderer itemRenderer;

	public CrabPotRenderer(BlockEntityRendererProvider.Context context) {
		this.pot = context.bakeLayer(BLModelLayers.CRAB_POT);
		this.bubbler = new BubblerCrabModel(context.bakeLayer(BLModelLayers.BUBBLER_CRAB));
		this.silt = new SiltCrabModel(context.bakeLayer(BLModelLayers.SILT_CRAB));
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(CrabPotBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(CrabPotBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.pot.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (entity.getLevel() != null) {
			// inputs
			if (!entity.getTheItem().isEmpty()) {
				if (this.isSafeMobItem(entity) && entity.getEntity() != null) {
					if (entity.fallCounter > 0) {
						float smoothed = (float) entity.fallCounter * 0.03125F + ((float) entity.fallCounter * 0.03125F - (float) entity.fallCounterPrev * 0.03125F) * partialTicks;
						float smoothedTumble = (float) entity.fallCounter + ((float) entity.fallCounter - (float) entity.fallCounterPrev) * partialTicks;
						this.renderMobInSlot(stack, source, entity.getLevel(), entity.getEntity(), 0.0F, 0.0625F + smoothed, 0.0F, smoothedTumble, light);
					}
					if (entity.fallCounter <= 0) {
						stack.pushPose();
						stack.translate(0.0F, 1.5F, 0.0F);
						if (!entity.animate) {
							double d0 = BetweenlandsClient.getClientPlayer().getX() - entity.getBlockPos().getX() - 0.5D;
							double d1 = BetweenlandsClient.getClientPlayer().getZ() - entity.getBlockPos().getZ() - 0.5D;
							stack.mulPose(Axis.YP.rotation((float) -Mth.atan2(d1, d0)));
						} else {
							stack.mulPose(Axis.YP.rotationDegrees(-90.0F + entity.getBlockState().getValue(CrabPotBlock.FACING).toYRot()));
						}
						float animationTicks = entity.prevAnimationTicks + (entity.animationTicks - entity.prevAnimationTicks) * partialTicks;

						EntityType<?> renderEntity = entity.getEntity();

						if (renderEntity != null) {
							if (renderEntity == EntityRegistry.SILT_CRAB.get()) {
								stack.pushPose();
								stack.scale(0.95F, -0.95F, -0.95F);
								if (entity.animate) {
									this.silt.renderCrabEating(stack, source.getBuffer(RenderType.entityCutoutNoCull(SiltCrabRenderer.TEXTURE)), light, overlay, animationTicks);
								} else {
									this.silt.root().getAllParts().forEach(ModelPart::resetPose);
									this.silt.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(SiltCrabRenderer.TEXTURE)), light, overlay);
								}
								stack.popPose();
							}

							if (renderEntity == EntityRegistry.BUBBLER_CRAB.get()) {
								stack.pushPose();
								stack.scale(0.95F, -0.95F, -0.95F);
								if (entity.animate) {
									this.bubbler.renderCrabEating(stack, source.getBuffer(RenderType.entityCutoutNoCull(BubblerCrabRenderer.TEXTURE)), light, overlay, animationTicks);
								} else {
									this.bubbler.root().getAllParts().forEach(ModelPart::resetPose);
									this.bubbler.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(BubblerCrabRenderer.TEXTURE)), light, overlay);
								}
								stack.popPose();
							}
						}

						stack.popPose();
					}
				} else {
					this.renderItemInSlot(stack, source, entity.getTheItem(), 0.0F, 0.5F, 0.0F, 0.5F, light, overlay);
				}
			}
		}
		stack.popPose();
	}

	public boolean isSafeMobItem(CrabPotBlockEntity entity) {
		return entity.getTheItem().getItem() instanceof MobItem && (entity.getEntity() == EntityRegistry.SILT_CRAB.get() || entity.getEntity()  == EntityRegistry.BUBBLER_CRAB.get());
	}

	public void renderMobInSlot(PoseStack stack, MultiBufferSource source, Level level, EntityType<?> type, float x, float y, float z, float rotation, int light) {
		Entity entity = type.create(level);
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
			this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}
}
