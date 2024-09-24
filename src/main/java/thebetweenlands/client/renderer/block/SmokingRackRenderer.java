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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.SmokingRackBlock;
import thebetweenlands.common.block.entity.SmokingRackBlockEntity;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.fishing.anadia.AnadiaParts;
import thebetweenlands.common.item.misc.AnadiaMobItem;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class SmokingRackRenderer implements BlockEntityRenderer<SmokingRackBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/smoking_rack.png"));
	private static final RenderType ACTIVE_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/smoking_rack_smoked.png"));
	private final ModelPart rack;
	private final ItemRenderer itemRenderer;

	private static final Vec3[] ITEM_OFFSETS = new Vec3[]{
		new Vec3(0.0F, 1.55F, 0.0F),
		new Vec3(0.0F, 0.7F, 0.4F),
		new Vec3(0.0F, 0.7F, -0.4F)};

	private static final Vec3[] ANADIA_OFFSETS = new Vec3[]{
		new Vec3(0.0F, 1.0F, 0.0F),
		new Vec3(0.0F, 0.125F, 0.4F),
		new Vec3(0.0F, 0.125F, -0.4F)};

	public SmokingRackRenderer(BlockEntityRendererProvider.Context context) {
		this.rack = context.bakeLayer(BLModelLayers.SMOKING_RACK);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(SmokingRackBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(SmokingRackBlock.FACING).toYRot()));
		stack.pushPose();
		stack.scale(1.0F, -1.0F, -1.0F);
		this.rack.render(stack, source.getBuffer(entity.getBlockState().getValue(SmokingRackBlock.HEATED) ? ACTIVE_TEXTURE : TEXTURE), light, overlay);
		stack.popPose();

		if (entity.getLevel() != null) {
			if (!entity.getItem(0).isEmpty()) {
				this.renderItemInSlot(stack, source, entity.getItem(0), Vec3.ZERO, 1.0F, light, overlay);
			}

			for (int i = 1; i < entity.getContainerSize(); i++) {
				if (!entity.getItem(i).isEmpty() && (i > 3 || entity.getItem(i + 3).isEmpty())) {
					if (this.shouldRenderAsEntity(entity, i) && entity.getRenderEntity(entity.getLevel(), 1) != null) {
						this.renderAnadiaInSlot(stack, source, entity.getItem(i), entity.getRenderEntity(entity.getLevel(), i), ANADIA_OFFSETS[((i - 1) % 3)], light);
					} else {
						this.renderItemInSlot(stack, source, entity.getItem(i), ITEM_OFFSETS[((i - 1) % 3)], 0.5F, light, overlay);
					}
				}
			}
		}
		stack.popPose();
	}

	private boolean shouldRenderAsEntity(SmokingRackBlockEntity entity, int slot) {
		return entity.getItem(slot).is(ItemRegistry.ANADIA) && !((MobItem<?>) entity.getItem(slot).getItem()).getEntityData(entity.getItem(slot)).isEmpty();
	}

	public void renderAnadiaInSlot(PoseStack stack, MultiBufferSource source, ItemStack item, @Nullable Entity renderEntity, Vec3 offset, int light) {
		if (renderEntity != null) {
			if (item.getItem() instanceof AnadiaMobItem anadia && anadia.isRotten(renderEntity.level(), item)) {
				((Anadia) renderEntity).setFishColor(AnadiaParts.AnadiaColor.ROTTEN);
			}
			renderEntity.setXRot(renderEntity.xRotO = 0);
			renderEntity.setYRot(renderEntity.yRotO = 0);
			float scale2 = 1.0F / ((Anadia) renderEntity).getFishSize() * 0.475F;
			stack.pushPose();
			stack.translate(0.0F, -0.5F, 0.0F);
			stack.mulPose(Axis.XN.rotationDegrees(90.0F));
			stack.mulPose(Axis.ZP.rotationDegrees(90.0F));
			stack.translate(0.0F, -0.2F, 0.75F);
			stack.translate(offset.z(), offset.x(), offset.y()); //dont ask
			stack.scale(scale2, scale2, scale2);
			EntityRenderer<? super Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(renderEntity);
			renderer.render(renderEntity, 0, 0, stack, source, light);
			stack.popPose();
		}
	}

	public void renderItemInSlot(PoseStack stack, MultiBufferSource source, ItemStack item, Vec3 offset, float scale, int light, int overlay) {
		if (!item.isEmpty()) {
			stack.pushPose();
			stack.translate(offset.x(), offset.y(), offset.z());
			stack.scale(scale, scale, scale);

			if (item.is(BlockRegistry.FALLEN_LEAVES.asItem())) {
				stack.mulPose(Axis.XP.rotationDegrees(90.0F));
			} else {
				stack.mulPose(Axis.ZP.rotationDegrees(45.0F));
			}

			this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(SmokingRackBlockEntity entity) {
		return new AABB(entity.getBlockPos()).expandTowards(0.0D, 1.0D, 0.0D);
	}
}
