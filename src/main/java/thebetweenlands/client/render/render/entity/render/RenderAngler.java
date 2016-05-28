package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.models.entity.ModelAngler;
import thebetweenlands.client.render.render.entity.layer.LayerAnglerEyes;
import thebetweenlands.common.entity.mobs.EntityAngler;

@SideOnly(Side.CLIENT)
public class RenderAngler extends RenderLiving<EntityAngler> {
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/angler.png");

    public RenderAngler(RenderManager manager) {
        super(manager, new ModelAngler(), 0.5F);
        addLayer(new LayerAnglerEyes<EntityAngler>(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAngler entity) {
        return texture;
    }


    /*protected int setAnglerEyeBrightness(EntityAngler entity, int pass, float partialTickTime) {
        if(pass == 1) {
            if(ShaderHelper.INSTANCE.canUseShaders()) {
                double xOff = Math.sin(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
                double zOff = Math.cos(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
                ShaderHelper.INSTANCE.addDynLight(new LightSource(entity.posX + xOff, entity.posY + 0.95f, entity.posZ + zOff,
                        1.6f,
                        10.0f / 255.0f * 13.0F,
                        30.0f / 255.0f * 13.0F,
                        20.0f / 255.0f * 13.0F));
            }

            bindTexture(eyeTexture);
            float var4 = 1.0F;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            LightingUtil.INSTANCE.setLighting(255);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var4);
            return 1;
        } else if(pass == 2) {
            LightingUtil.INSTANCE.revert();
            GL11.glDisable(GL11.GL_BLEND);
        }
        return -1;
    }*/


    /*@Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        return setAnglerEyeBrightness((EntityAngler) entity, pass, partialTickTime);
    }*/

    @Override
    protected void preRenderCallback(EntityAngler entityliving, float f) {
        GL11.glTranslatef(0F, 0.5F, 0F);
        EntityAngler angler = entityliving;
        if (angler.isGrounded() && !angler.isLeaping()) {
            GL11.glRotatef(90F, 0F, 0F, 1F);
            GL11.glTranslatef(-0.7F, 0.7F, 0F);
        }
    }

}
