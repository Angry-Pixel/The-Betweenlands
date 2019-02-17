package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelLeech;
import thebetweenlands.common.entity.mobs.EntityLeech;

@OnlyIn(Dist.CLIENT)
public class RenderLeech extends RenderLiving<EntityLeech> {
    private static final ResourceLocation texture =  new ResourceLocation("thebetweenlands:textures/entity/leech_hungry.png");

    public RenderLeech(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelLeech(), 0.3F);
    }

    @Override
    protected void preRenderCallback(EntityLeech leech, float partialTickTime) {
        if(!leech.isRiding())
            GlStateManager.scale(1 + leech.getBloodConsumed() * 0.1F, 1 + leech.getBloodConsumed() * 0.1F, (leech.moveProgress + 3F) / 3F);

        if(leech.isRiding()) {
            GlStateManager.scale(leech.moveProgress*leech.moveProgress/2 + 0.5F, leech.moveProgress*leech.moveProgress/2 + 0.5F, 1F);
            GlStateManager.rotate(180, 0, 1, 0);
            GlStateManager.translate(0, 0, 0.5F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLeech entity) {
        return texture;
    }
}
