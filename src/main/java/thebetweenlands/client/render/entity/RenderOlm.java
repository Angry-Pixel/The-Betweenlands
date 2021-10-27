package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelOlm;
import thebetweenlands.common.entity.mobs.EntityOlm;

@SideOnly(Side.CLIENT)
public class RenderOlm extends RenderLiving<EntityOlm> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/olm.png");

    public RenderOlm(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelOlm(), 0.2f);
    }

    @Override
    protected void preRenderCallback(EntityOlm entity, float partialTickTime) {
    	GlStateManager.scale(0.75F, 0.75F, 0.75F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityOlm entity) {
        return TEXTURE;
    }
}
