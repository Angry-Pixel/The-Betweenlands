package thebetweenlands.client.render.render.entity.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.render.entity.render.RenderSporeling;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.util.LightingUtil;

public class LayerSporelingEyes<T extends EntitySporeling> implements LayerRenderer<T> {
    private static final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/sporeling_glow.png");
    private final RenderSporeling renderSporeling;

    public LayerSporelingEyes(RenderSporeling renderSporeling) {
        this.renderSporeling = renderSporeling;
    }

    @Override
    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderSporeling.bindTexture(eyeTexture);
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
