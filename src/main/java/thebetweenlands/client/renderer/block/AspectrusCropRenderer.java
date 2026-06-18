package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.WorldShader;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.block.farming.DecayableCropBlock;

public class AspectrusCropRenderer implements BlockEntityRenderer<AspectrusCropBlockEntity> {

    public AspectrusCropRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AspectrusCropBlockEntity tile, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (tile.getBlockState().getValue(DecayableCropBlock.STAGE) < 4) return;
        if (tile.getBlockState().getValue(DecayableCropBlock.DECAYED)) return;

        Aspect aspect = tile.getAspect();
        if (aspect == null) return;
        ShaderHelper.INSTANCE.getWorldShader();
        if (!ShaderHelper.INSTANCE.isWorldShaderActive() || ShaderHelper.INSTANCE.getWorldShader().getLightSourcesAmount() >= WorldShader.MAX_LIGHT_SOURCES_PER_PASS - 4) return;

        float time = tile.getLevel().getGameTime() + partialTick;
        BlockPos pos = tile.getBlockPos();
        float brightness = wave(time, pos.getX(), pos.getZ());
        if (brightness < .4) return;

        int color = aspect.type().value().color();
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        ShaderHelper.INSTANCE.require();
        ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(
            tile.getBlockPos().getX() + 0.5,
            tile.getBlockPos().getY() + 0.5,
            tile.getBlockPos().getZ() + 0.5,
            brightness * brightness * 4.0F,
            brightness * brightness * r * 2.5F,
            brightness * brightness * g * 2.5F,
            brightness * brightness * b * 2.5F
        ));
    }

    public float wave(float time, int x, int z) {
        time = ((time + (x * 73856093 ^ z * 83492791) % 200 + 200) % 200);
        return ((float)Math.sin((time) / 15.0F) * (float)Math.cos((time + 4) / 80.0F) + 1.0F) / 2.0F;
    }
}