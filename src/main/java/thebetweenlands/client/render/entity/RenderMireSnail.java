package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnail;

@OnlyIn(Dist.CLIENT)
public class RenderMireSnail extends RenderLiving<EntityMireSnail> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/mire_snail.png");

    public RenderMireSnail(RenderManager manager) {
        super(manager, new ModelMireSnail(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMireSnail entity) {
        return TEXTURE;
    }
}