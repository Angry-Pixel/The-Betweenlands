package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.models.entity.ModelMireSnailEgg;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;

public class RenderMireSnailEgg extends RenderLiving<EntityMireSnailEgg> {
    private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/mireSnailEgg.png");

    public RenderMireSnailEgg(RenderManager manager) {
        super(manager, new ModelMireSnailEgg(), 0.1F);
    }

    @Override
    protected void preRenderCallback(EntityMireSnailEgg entitylivingbaseIn, float partialTickTime) {
        EntityMireSnailEgg egg = entitylivingbaseIn;
        GL11.glScalef(2.5F + egg.pulseFloat, 2.5F + egg.pulseFloat, 2.5F + egg.pulseFloat);
    }


    @Override
    protected ResourceLocation getEntityTexture(EntityMireSnailEgg entity) {
        return texture;
    }
}