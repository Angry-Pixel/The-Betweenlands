package thebetweenlands.client.render.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.models.entity.ModelChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.util.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderChiromaw extends RenderLiving<EntityChiromaw> {
    private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw.png");
    private final ResourceLocation GLOW_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromawGlow.png");

    public RenderChiromaw(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelChiromaw(), 0.5F);
    }

    @Override
    protected void preRenderCallback(EntityChiromaw entitylivingbaseIn, float partialTickTime) {
        EntityChiromaw chiromaw = entitylivingbaseIn;
        if (!chiromaw.getIsHanging()) {
            float flap = MathHelper.sin((entitylivingbaseIn.ticksExisted + partialTickTime) * 0.5F) * 0.6F;
            GL11.glTranslatef(0.0F, 0F - flap * 0.5F, 0.0F);
        }
    }

    protected int setMobTextureGlow(EntityChiromaw entity, int pass, float partialTickTime) {
        float flap = MathHelper.sin((entity.ticksExisted + partialTickTime) * 0.5F) * 0.6F;
        GL11.glPushMatrix();

        GL11.glTranslatef(0.0F, 0F - flap * 0.5F, 0.0F);
        GL11.glPopMatrix();
        if (pass == 1) {
            bindTexture(GLOW_TEXTURE);
            float var4 = 1.0F;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            LightingUtil.INSTANCE.setLighting(255);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var4);
            return 1;
        } else if (pass == 2) {
            LightingUtil.INSTANCE.revert();
            GL11.glDisable(GL11.GL_BLEND);
        }

        return -1;
    }

    protected int shouldRenderPass(EntityChiromaw entity, int pass, float partialTickTime) {
        return setMobTextureGlow(entity, pass, partialTickTime);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityChiromaw entity) {
        return TEXTURE;
    }
}
