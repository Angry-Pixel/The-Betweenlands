package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelWight;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityWight;

public class RenderWight extends RenderLiving<EntityWight> {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/wight.png");

    private static final ModelWight model = new ModelWight();
    private static final ModelWight modelHeadOnly = new ModelWight().setRenderHeadOnly(true);

    public RenderWight(RenderManager rendermanagerIn) {
        super(rendermanagerIn, model, 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWight entity) {
        return texture;
    }

    @Override
    protected void preRenderCallback(EntityWight entitylivingbaseIn, float partialTickTime) {
        if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
            //TODO ShaderHelper.INSTANCE.addDynLight(new LightSource(entityliving.posX, entityliving.posY, entityliving.posZ, 10.0f, -1, -1, -1));
        }

        GlStateManager.color(0, 0, 0, 0);
    }

    /*TODO this
    @Override
    protected int shouldRenderPass(EntityLivingBase entityliving, int pass, float partialTickTime) {
        EntityWight wight = (EntityWight) entityliving;

        GlStateManager.depthMask(true);

        if (wight.isVolatile()) {
            this.setRenderPassModel(this.modelHeadOnly);
            if (pass == 0) {
                GlStateManager.scale(0.9F, 0.9F, 0.9F);
                GlStateManager.disableBlend();
                GlStateManager.colorMask(false, false, false, false);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 1.65D, 0);
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                if (wight.getRidingEntity() != null) {
                    GlStateManager.rotate((float) ((wight.ticksExisted + partialTickTime) / 30.0D * 360.0D, 0, 1, 0);
                    GlStateManager.rotate(180, 0, 1, 0);
                    GlStateManager.scale(0, 0, 0.8D);
                }
                LightingUtil.INSTANCE.setLighting(255);
                return 1;
            } else if (pass == 1) {
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1F, 1F, 1F, 0.4F);
                return 1;
            } else if (pass == 2) {
                GlStateManager.popMatrix();
                LightingUtil.INSTANCE.revert();
            }

            return -1;
        }

        this.setRenderPassModel(this.model);

        if (pass == 0) {
            GlStateManager.scale(0.9F, 0.9F, 0.9F);
            GlStateManager.translate(0, 0.175D, 0);
            GlStateManager.disableBlend();
            GlStateManager.colorMask(false, false, false, false);
            GlStateManager.color(1, 1, 1, 1);
            return 1;
        } else if (pass == 1) {
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1F, 1F, 1F, 1F - wight.getAnimation() * 0.5F);
            return 1;
        }

        return -1;
    }*/

}
