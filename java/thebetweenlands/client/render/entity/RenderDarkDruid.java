package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelDarkDruid;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDarkDruid
        extends RenderLiving
{
    private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/darkDruid.png");
    private static final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/darkDruidGlow.png");

    public RenderDarkDruid() {
        super(new ModelDarkDruid(), 0.7F);
        setRenderPassModel(new ModelDarkDruid());
    }

    protected int setDruidEyeBrightness(EntityDarkDruid entity, int pass, float partialTickTime) {
        if( pass == 1 ) {
            bindTexture(eyeTexture);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GL11.glDisable(GL11.GL_LIGHTING);
            char var5 = 61680;
            int var6 = var5 % 65536;
            int var7 = var5 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6 / 1.0F, var7 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return 1;
        } else if( pass == 2 ) {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }

        return -1;
    }

    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        return setDruidEyeBrightness((EntityDarkDruid) entity, pass, partialTickTime);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
