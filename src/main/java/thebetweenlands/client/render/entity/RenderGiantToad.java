package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelGiantToad;
import thebetweenlands.common.entity.mobs.EntityGiantToad;

@OnlyIn(Dist.CLIENT)
public class RenderGiantToad extends RenderLiving<EntityGiantToad> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/giant_toad.png");

    public RenderGiantToad(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelGiantToad(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGiantToad entity) {
        return TEXTURE;
    }
}
