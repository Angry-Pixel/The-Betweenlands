package thebetweenlands.client.render.render.entity.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.render.entity.render.RenderSwampHag;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.util.LightingUtil;

public class LayerSwampHagEyes<T extends EntitySwampHag> implements LayerRenderer<T> {
    private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/swamp_hag_eyes.png");
    private final RenderSwampHag renderSwampHag;

    public LayerSwampHagEyes(RenderSwampHag renderSwampHag) {
        this.renderSwampHag = renderSwampHag;
    }

    @Override
    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderSwampHag.bindTexture(eyeTexture);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        LightingUtil.INSTANCE.setLighting(255);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        LightingUtil.INSTANCE.revert();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
