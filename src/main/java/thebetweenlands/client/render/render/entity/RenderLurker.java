package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.models.entity.ModelLurker;
import thebetweenlands.common.entity.mobs.EntityLurker;

public class RenderLurker extends RenderLiving<EntityLurker> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/lurker.png");

    public RenderLurker(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelLurker(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLurker entity) {
        return TEXTURE;
    }

    @Override
    protected void rotateCorpse(EntityLurker lurker, float headPitch, float yaw, float partialTicks) {
        super.rotateCorpse(lurker, headPitch, yaw, partialTicks);
        GL11.glRotatef((lurker).getRotationPitch(partialTicks), 1, 0, 0);
    }
}
