package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.models.entity.ModelSwampHag;
import thebetweenlands.client.render.render.entity.layer.LayerSwampHagEyes;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

@SideOnly(Side.CLIENT)
public class RenderSwampHag extends RenderLiving<EntitySwampHag> {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/swamp_hag.png");

    public RenderSwampHag(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelSwampHag(), 0.5F);
        addLayer(new LayerSwampHagEyes<EntitySwampHag>(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySwampHag entity) {
        return texture;
    }


    @Override
    protected void preRenderCallback(EntitySwampHag entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.74F, 0.74F, 0.74F);
    }
}
