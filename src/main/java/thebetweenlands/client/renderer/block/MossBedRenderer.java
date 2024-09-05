package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.MossBedBlockEntity;

public class MossBedRenderer implements BlockEntityRenderer<MossBedBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/bed/moss_bed.png"));

	private final ModelPart bed;

	public MossBedRenderer(BlockEntityRendererProvider.Context context) {
		this.bed = context.bakeLayer(BLModelLayers.MOSS_BED);
	}

	@Override
	public void render(MossBedBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (entity.getBlockState().getValue(BedBlock.PART) == BedPart.FOOT) {
			stack.pushPose();
			stack.translate(0.5F, 0.0F, 0.5F);
			if (entity.getBlockState().getValue(BedBlock.FACING).getAxis() == Direction.Axis.Z) {
				stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(BedBlock.FACING).toYRot() + 180));
			} else {
				stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(BedBlock.FACING).toYRot()));
			}
			stack.scale(-1.0F, -1.0F, -1.0F);
			this.bed.render(stack, source.getBuffer(TEXTURE), light, overlay);
			stack.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(MossBedBlockEntity entity) {
		if (entity.getBlockState().getValue(BedBlock.PART) == BedPart.FOOT) {
			Direction dir = entity.getBlockState().getValue(BedBlock.FACING);
			return new AABB(entity.getBlockPos()).expandTowards(dir.getStepX() * 1.1F, 0.0D, dir.getStepZ() * 1.1F);
		}
		return BlockEntityRenderer.super.getRenderBoundingBox(entity);
	}
}
