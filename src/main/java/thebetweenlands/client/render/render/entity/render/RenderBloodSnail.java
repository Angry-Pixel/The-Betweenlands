package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.models.entity.ModelBloodSnail;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;

public class RenderBloodSnail extends RenderLiving<EntityBloodSnail> {
    private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/bloodSnail.png");

    public RenderBloodSnail(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBloodSnail(), 0.2F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBloodSnail entity) {
        return texture;
    }
}
