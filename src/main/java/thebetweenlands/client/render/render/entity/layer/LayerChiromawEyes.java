package thebetweenlands.client.render.render.entity.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.render.entity.render.RenderChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.util.LightingUtil;

public class LayerChiromawEyes<T extends EntityChiromaw> implements LayerRenderer<T> {
    private final ResourceLocation GLOW_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw_glow.png");
    private RenderChiromaw renderChiromaw;

    public LayerChiromawEyes(RenderChiromaw renderChiromaw) {
        this.renderChiromaw = renderChiromaw;
    }

    @Override
    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        float flap = MathHelper.sin((entitylivingbaseIn.ticksExisted + partialTicks) * 0.5F) * 0.6F;
        GlStateManager.pushMatrix();

        GlStateManager.translate(0.0F, 0F - flap * 0.5F, 0.0F);
        GlStateManager.popMatrix();

        renderChiromaw.bindTexture(GLOW_TEXTURE);
        float var4 = 1.0F;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        LightingUtil.INSTANCE.setLighting(255);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, var4);
        LightingUtil.INSTANCE.revert();
        GlStateManager.disableBlend();

    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
