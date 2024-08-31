package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.FastColor;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.AlembicBlock;
import thebetweenlands.common.block.CenserBlock;
import thebetweenlands.common.block.entity.AlembicBlockEntity;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;

public class AlembicRenderer implements BlockEntityRenderer<AlembicBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/alembic.png"));
	private static final RenderType LIQUID = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/fluid.png"));
	private final ModelPart alembic;
	private final ModelPart alembicLiquid;
	private final ModelPart jarLiquid;

	public AlembicRenderer(BlockEntityRendererProvider.Context context) {
		var root = context.bakeLayer(BLModelLayers.ALEMBIC);
		this.alembic = root.getChild("alembic");
		this.alembicLiquid = root.getChild("alembic_liquid");
		this.jarLiquid = root.getChild("jar_liquid");
	}

	@Override
	public void render(AlembicBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(AlembicBlock.FACING).toYRot() + 90));
		stack.scale(-1.0F, 1.0F, 1.0F);

		if (entity.getLevel() != null && entity.isFull()) {
			float[] colors = ElixirRecipe.getInfusionColor(entity.getElixirRecipe(), entity.getInfusionTime());
			float progress = entity.getProgress();
			if (1.0F - progress > 0.0F) {
				stack.pushPose();
				stack.scale(1, 1.0F - progress, 1);
				this.alembicLiquid.render(stack, source.getBuffer(LIQUID), light, overlay, FastColor.ARGB32.colorFromFloat(0.8F, colors[0], colors[1], colors[2]));
				stack.popPose();
			}

			if (progress != 0.0F) {
				stack.pushPose();
				stack.scale(1, progress, 1);
				this.jarLiquid.render(stack, source.getBuffer(LIQUID), light, overlay, FastColor.ARGB32.colorFromFloat(0.8F, colors[0], colors[1], colors[2]));
				stack.popPose();
			}
		}
		this.alembic.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();
	}
}
