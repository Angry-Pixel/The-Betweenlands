package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.models.entity.ModelSwampHag;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.util.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderSwampHag extends RenderLiving<EntitySwampHag> {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/swampHag.png");
    private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/swampHagEyes.png");

    public RenderSwampHag(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelSwampHag(), 0.5F);
        //setRenderPassModel(new ModelSwampHag());
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySwampHag entity) {
        return texture;
    }

    protected int setSwampHagEyeBrightness(EntitySwampHag entity, int pass, float partialTickTime) {
        if (pass == 1) {
            bindTexture(eyeTexture);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            LightingUtil.INSTANCE.setLighting(255);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return 1;
        } else if (pass == 2) {
            LightingUtil.INSTANCE.revert();
            GL11.glDisable(GL11.GL_BLEND);
        }

        return -1;
    }

    @Override
    protected void preRenderCallback(EntitySwampHag entitylivingbaseIn, float partialTickTime) {
        GL11.glScalef(0.74F, 0.74F, 0.74F);
    }

    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        return setSwampHagEyeBrightness((EntitySwampHag) entity, pass, partialTickTime);
    }

}
