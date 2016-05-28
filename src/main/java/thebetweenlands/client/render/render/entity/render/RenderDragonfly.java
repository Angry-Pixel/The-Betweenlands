package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.models.entity.ModelDragonfly;
import thebetweenlands.common.entity.mobs.EntityDragonfly;

@SideOnly(Side.CLIENT)
public class RenderDragonfly extends RenderLiving<EntityDragonfly> {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/dragon_fly.png");

    public RenderDragonfly(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelDragonfly(), 0.5F);
    }

    @Override
    protected void preRenderCallback(EntityDragonfly entitylivingbaseIn, float partialTickTime) {
        scaleDragonFly(entitylivingbaseIn, partialTickTime);
    }

    protected void scaleDragonFly(EntityDragonfly dragonFly, float partialTickTime) {
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDragonfly entity) {
        return texture;
    }
}
