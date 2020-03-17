package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDragonFly;
import thebetweenlands.common.entity.mobs.EntityDragonFly;

@SideOnly(Side.CLIENT)
public class RenderDragonFly extends RenderLiving<EntityDragonFly> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/dragon_fly.png");

    public RenderDragonFly(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelDragonFly(), 0.5F);
    }

    @Override
    protected void preRenderCallback(EntityDragonFly entitylivingbaseIn, float partialTickTime) {
        scaleDragonFly(entitylivingbaseIn, partialTickTime);
        
        GlStateManager.rotate(entitylivingbaseIn.rotationPitch, 1, 0, 0);
    }

    protected void scaleDragonFly(EntityDragonFly dragonFly, float partialTickTime) {
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDragonFly entity) {
        return TEXTURE;
    }
}