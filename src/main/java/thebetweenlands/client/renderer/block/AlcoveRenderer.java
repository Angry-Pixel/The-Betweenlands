package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.MudBrickAlcoveBlock;
import thebetweenlands.common.block.entity.MudBrickAlcoveBlockEntity;

public class AlcoveRenderer implements BlockEntityRenderer<MudBrickAlcoveBlockEntity> {

	private static final RenderType[] TEXTURES = new RenderType[]{
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mud_brick_alcove_0.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mud_brick_alcove_1.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mud_brick_alcove_2.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mud_brick_alcove_3.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/mud_brick_alcove_4.png"))
	};
	private static final RenderType[] URN_TEXTURES = new RenderType[]{
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/loot_urn_1.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/loot_urn_2.png")),
		RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/loot_urn_3.png"))
	};
	private final ModelPart alcove;
	private final ModelPart smallCandle;
	private final ModelPart largeCandle;
	private final ModelPart cobweb1;
	private final ModelPart cobweb2;
	private final ModelPart[] urns;

	public AlcoveRenderer(BlockEntityRendererProvider.Context context) {
		var root = context.bakeLayer(BLModelLayers.ALCOVE);
		this.alcove = root.getChild("back_wall");
		//this.outcrop = root.getChild("outcrop_a"); these dont seem to be used in 1.12?
		this.smallCandle = root.getChild("candle_1");
		this.largeCandle = root.getChild("candle_2");
		this.cobweb1 = root.getChild("cobweb_1");
		this.cobweb2 = root.getChild("cobweb_2");
		this.urns = new ModelPart[]{context.bakeLayer(BLModelLayers.LOOT_URN_1), context.bakeLayer(BLModelLayers.LOOT_URN_2), context.bakeLayer(BLModelLayers.LOOT_URN_3)};
	}

	@Override
	public void render(MudBrickAlcoveBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		VertexConsumer consumer = source.getBuffer(TEXTURES[entity.getBlockState().getValue(MudBrickAlcoveBlock.MUD_LEVEL)]);
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(MudBrickAlcoveBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.alcove.render(stack, consumer, light, overlay);

		if (entity.getLevel() != null) {
			RandomSource random = RandomSource.create(entity.getBlockPos().asLong());
			if (random.nextBoolean()) {
				this.smallCandle.render(stack, consumer, light, overlay);
			}
			if (random.nextBoolean()) {
				this.largeCandle.render(stack, consumer, light, overlay);
			}
			if (random.nextBoolean()) {
				this.cobweb1.render(stack, consumer, light, overlay);
			}
			if (random.nextBoolean()) {
				this.cobweb2.render(stack, consumer, light, overlay);
			}
			if (entity.getBlockState().getValue(MudBrickAlcoveBlock.HAS_URN)) {
				stack.pushPose();
				stack.mulPose(Axis.YP.rotationDegrees(random.nextInt(41) - 20));
				stack.translate(-0.1F, 0.0F, -0.25F);
				stack.scale(-0.65F, 0.65F, 0.65F);
				int type = random.nextInt(3);
				this.urns[type].render(stack, source.getBuffer(URN_TEXTURES[type]), light, overlay);
				stack.popPose();
			}
		} else {
			//render all decorations as an item
			this.smallCandle.render(stack, consumer, light, overlay);
			this.largeCandle.render(stack, consumer, light, overlay);
			this.cobweb1.render(stack, consumer, light, overlay);
			this.cobweb2.render(stack, consumer, light, overlay);
		}

		stack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 128;
	}
}
