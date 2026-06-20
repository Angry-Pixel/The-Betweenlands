package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.AspectVialBlock;
import thebetweenlands.common.block.entity.AspectVialBlockEntity;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.IAspectVial;

public class AspectVialRenderer implements BlockEntityRenderer<AspectVialBlockEntity> {
    private static RenderType vialRenderType(String name, ResourceLocation texture, boolean depthNotColor) {
        return RenderType.create(
			name,
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(RenderStateShard.LIGHTMAP)
				.setOverlayState(RenderStateShard.OVERLAY)
				.setWriteMaskState(depthNotColor ? RenderStateShard.DEPTH_WRITE : RenderStateShard.COLOR_WRITE)
				.createCompositeState(true)
		);
    }

    private static final RenderType GREEN_COLOR = vialRenderType("thebetweenlands:green_aspect_vial_color", TheBetweenlands.prefix("textures/entity/block/vial_block_green.png"), false);
    private static final RenderType ORANGE_COLOR = vialRenderType("thebetweenlands:orange_aspect_vial_color", TheBetweenlands.prefix("textures/entity/block/vial_block_orange.png"), false);

    private static final RenderType GREEN_DEPTH = vialRenderType("thebetweenlands:green_aspect_vial_color", TheBetweenlands.prefix("textures/entity/block/vial_block_green.png"), true);
    private static final RenderType ORANGE_DEPTH = vialRenderType("thebetweenlands:orange_aspect_vial_depth", TheBetweenlands.prefix("textures/entity/block/vial_block_orange.png"), true);

    private static final RenderType LIQUID = RenderType.create(
        "thebetweenlands:aspect_vial_liquid",
        DefaultVertexFormat.NEW_ENTITY,
        VertexFormat.Mode.QUADS,
        256,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(TheBetweenlands.prefix("textures/entity/block/fluid.png"),false, false))
            .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OVERLAY)
            .createCompositeState(true)
    );
	private final ModelPart jar;
	private final ModelPart jarLiquid;

	public AspectVialRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(BLModelLayers.ALEMBIC);
		this.jar = root.getChild("alembic").getChild("jar");
		this.jarLiquid = root.getChild("jar_liquid");
	}

    @Override
    public void render(AspectVialBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {

        BlockPos pos = entity.getBlockPos();

        float randX = 0;
		float randZ = 0;

        if(entity.getBlockState().getValue(AspectVialBlock.RANDOM_POSITION)) {
			long posRand = (long)(pos.getY() * 224856) ^ (pos.getX() * 3129871) ^ (long)pos.getZ() * 116129781L;
			posRand = posRand * posRand * 42317861L + posRand * 11L;
			randX = (((float)(posRand >> 16 & 15L) / 15.0F) - 0.5F) * 0.45F;
			randZ = (((float)(posRand >> 24 & 15L) / 15.0F) - 0.5F) * 0.45F;
		}

        stack.pushPose();
		stack.translate(0.2F + randX, 0.0F, 0.25F + randZ);
		stack.scale(1.0F, -1.0F, -1.0F);

        this.jar.render(stack, source.getBuffer(entity.type().equals(IAspectVial.VialType.GREEN) ? GREEN_COLOR : ORANGE_COLOR), light, overlay);

		if (entity.getLevel() != null && entity.getAspect() instanceof Aspect aspect && aspect.amount() > 0) {
			int color = aspect.type().value().color();
            float[] colors = new float[] {((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F};
            float amount = aspect.amount() / (float) Amounts.VIAL;
            stack.pushPose();
            stack.translate(0.0F, amount * 0.1F - 0.05F, 0.0F);
            stack.scale(1, amount, 1);
            this.jarLiquid.render(stack, source.getBuffer(LIQUID), 15728880, overlay, FastColor.ARGB32.colorFromFloat(.98f, colors[0], colors[1], colors[2]));
            stack.popPose();
		}

        this.jar.render(stack, source.getBuffer(entity.type().equals(IAspectVial.VialType.GREEN) ? GREEN_DEPTH : ORANGE_DEPTH), light, overlay);

		stack.popPose();
    }
}
