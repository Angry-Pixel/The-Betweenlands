package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelMireSnailEgg;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;

public class RenderMireSnailEgg extends RenderLiving<EntityMireSnailEgg> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/mire_snail_egg.png");

    public RenderMireSnailEgg(RenderManager manager) {
        super(manager, new ModelMireSnailEgg(), 0.1F);
    }

    @Override
    protected void preRenderCallback(EntityMireSnailEgg entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(2.5F + entitylivingbaseIn.pulseFloat, 2.5F + entitylivingbaseIn.pulseFloat, 2.5F + entitylivingbaseIn.pulseFloat);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMireSnailEgg entity) {
        return TEXTURE;
    }
}