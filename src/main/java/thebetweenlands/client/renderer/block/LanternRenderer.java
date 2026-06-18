package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.LanternBlock;
import thebetweenlands.client.renderer.entity.FireflyRenderer;
import thebetweenlands.common.block.entity.LanternBlockEntity;
import thebetweenlands.common.config.BetweenlandsConfig;

public class LanternRenderer implements BlockEntityRenderer<LanternBlockEntity> {

    public LanternRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LanternBlockEntity tile, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!BetweenlandsConfig.Shader.lanternsUseShaders) return;
        BlockPos pos = tile.getBlockPos();

        double x = pos.getX() + 0.5;
        double y = pos.getY() + (tile.getBlockState().getValue(LanternBlock.HANGING) ? 0.55 : 0.3);
        double z = pos.getZ() + 0.5;

        float glowStrength = getGlow(pos, tile.getLevel().getGameTime() + partialTick);
        float radius = glowStrength * 7.0F;
        if (radius > 0.1F) {
            FireflyRenderer.addFireflyLight(x, y, z, radius);
        }
    }

    private static float getGlow(BlockPos pos, float time) {
        float phase = (pos.hashCode() & 0xFF) / 255.0F * Mth.TWO_PI; //makes it so they slightly fade in and out, but not all at the same time
        return 0.6F + 0.4F * ((Mth.sin(time / 20.0F + phase) + 1.0F) * 0.5F);
    }
    
}