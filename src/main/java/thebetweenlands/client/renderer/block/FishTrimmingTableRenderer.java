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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.FishTrimmingTableBlock;
import thebetweenlands.common.block.entity.FishTrimmingTableBlockEntity;
import thebetweenlands.common.items.AnadiaMobItem;
import thebetweenlands.common.items.MobItem;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class FishTrimmingTableRenderer implements BlockEntityRenderer<FishTrimmingTableBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/fish_trimming_table.png"));
	private static final RenderType USED_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/fish_trimming_table_used.png"));
	private final ModelPart table;
	private final ModelPart cleaver;
	private final ModelPart blood;

	private static final Vec3[] ITEM_OFFSETS = new Vec3[]{
		new Vec3(0.0F, -0.75F, 0.0F),
		new Vec3(-0.25F, -0.75F, 0.0F),
		new Vec3(0.0F, -0.75F, 0.125F),
		new Vec3(0.25F, -0.75F, 0.0F),
		new Vec3(-0.25F, -0.75F, 0.25F)};

	public FishTrimmingTableRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(BLModelLayers.FISH_TRIMMING_TABLE);
		this.table = root.getChild("base");
		this.cleaver = root.getChild("cleaver_blade");
		this.blood = root.getChild("blood");
	}

	@Override
	public void render(FishTrimmingTableBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(FishTrimmingTableBlock.FACING).toYRot()));
		this.table.render(stack, source.getBuffer(!entity.getItem(4).isEmpty() ? USED_TEXTURE : TEXTURE), light, overlay);
		if (!entity.getItem(5).isEmpty()) {
			this.cleaver.render(stack, source.getBuffer(!entity.getItem(4).isEmpty() ? USED_TEXTURE : TEXTURE), light, overlay);
			this.blood.render(stack, source.getBuffer(!entity.getItem(4).isEmpty() ? USED_TEXTURE : TEXTURE), light, overlay);
		}

		if (entity.getLevel() != null) {
			RandomSource random = RandomSource.create(entity.getBlockPos().asLong());

			if (!entity.getItem(0).isEmpty()) {
				if (this.shouldRenderAsEntity(entity, 0) && entity.getInputEntity(entity.getLevel()) != null)
					this.renderMobInSlot(stack, source, entity.getItem(0), entity.getInputEntity(entity.getLevel()), new Vec3(0.0F, -0.8F, 0.0F), light);
				else
					this.renderItemInSlot(stack, source, entity.getItem(0), ITEM_OFFSETS[0], 0.5F, 0.0F, light, overlay);
			}

			for (int i = 1; i < 5; i++) {
				renderItemInSlot(stack, source, entity.getItem(i), ITEM_OFFSETS[i], 0.25F, (float)random.nextDouble() * 60.0F - 30.0F, light, overlay);
			}
		}

		stack.popPose();
	}

	public boolean shouldRenderAsEntity(FishTrimmingTableBlockEntity entity, int slot) {
		if(entity.getItem(slot).is(ItemRegistry.ANADIA) && ((MobItem) entity.getItem(slot).getItem()).hasEntityData(entity.getItem(slot)))
			return true;
		return entity.getItem(slot).is(ItemRegistry.SILT_CRAB) || entity.getItem(slot).is(ItemRegistry.BUBBLER_CRAB);
	}

	public void renderMobInSlot(PoseStack stack, MultiBufferSource source, ItemStack item, @Nullable Entity renderEntity, Vec3 offset, int light) {
		if (renderEntity != null) {
//			if (item.getItem() instanceof AnadiaMobItem anadia && anadia.isRotten(entity.getLevel(), item)) {
//				((Anadia) renderEntity).setColor(EnumAnadiaColor.ROTTEN);
//			}
			float scale2 = 1F / renderEntity.getBbWidth() * 0.5F;
			if (item.getItem() instanceof AnadiaMobItem) {
//				scale2 = 1F / ((Anadia) renderEntity).getFishSize() * 0.5F;
			}
			stack.pushPose();
			stack.translate(offset.x(), offset.y(), offset.z());
			if (item.getItem() instanceof AnadiaMobItem) {
				stack.translate(0.1875F, 0F, -0.0625F);
				stack.mulPose(Axis.XP.rotationDegrees(45.0F));
				stack.mulPose(Axis.ZP.rotationDegrees(90.0F));
			} else {
				stack.translate(0F, -0.1875F, 0F);
				stack.mulPose(Axis.YN.rotationDegrees(90.0F));
			}
			stack.scale(scale2, scale2, scale2);
			EntityRenderer<? super Entity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(renderEntity);
			renderer.render(renderEntity, 90, 0, stack, source, light);
			stack.popPose();
		}
	}

	public void renderItemInSlot(PoseStack stack, MultiBufferSource source, ItemStack item, Vec3 offset, float scale, float rotation, int light, int overlay) {
		if (!item.isEmpty()) {
			stack.pushPose();
			stack.translate(offset.x(), offset.y(), offset.z());
			stack.scale(scale, scale, scale);
			stack.mulPose(Axis.XN.rotationDegrees(90.0F));
			stack.mulPose(Axis.ZP.rotationDegrees(rotation + 180));
			Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}
}
