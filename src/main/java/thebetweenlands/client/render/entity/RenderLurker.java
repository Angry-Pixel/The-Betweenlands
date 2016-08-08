package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelLurker;
import thebetweenlands.common.entity.mobs.EntityLurker;

public class RenderLurker extends RenderLiving<EntityLurker> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/lurker.png");

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
        GlStateManager.rotate((lurker).getRotationPitch(partialTicks), 1, 0, 0);
    }
}
