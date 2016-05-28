package thebetweenlands.client.render.render.entity.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.render.entity.render.RenderAngler;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.util.LightingUtil;

public class LayerAnglerEyes<T extends EntityAngler> implements LayerRenderer<T> {

    private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/angler_glow.png");
    private RenderAngler renderAngler;

    public LayerAnglerEyes(RenderAngler renderAngler) {
        this.renderAngler = renderAngler;
    }

    @Override
    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        /*TODO add shader helper
        if (ShaderHelper.INSTANCE.canUseShaders()) {
            double xOff = Math.sin(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
            double zOff = Math.cos(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
            ShaderHelper.INSTANCE.addDynLight(new LightSource(entity.posX + xOff, entity.posY + 0.95f, entity.posZ + zOff,
                    1.6f,
                    10.0f / 255.0f * 13.0F,
                    30.0f / 255.0f * 13.0F,
                    20.0f / 255.0f * 13.0F));
        }*/

        renderAngler.bindTexture(eyeTexture);
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
