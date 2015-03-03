package thebetweenlands.client.render.entity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import thebetweenlands.client.model.entity.ModelDarkDruid;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDarkDruid extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/darkDruid.png");
	private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/darkDruidGlow.png");
	private ModelBase model = new ModelDarkDruid();

	public RenderDarkDruid() {
		super(new ModelDarkDruid(), 0.7F);
		setRenderPassModel(new ModelDarkDruid());
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entity, float f) {
		preRenderCallbackDruid((EntityDarkDruid) entity, f);
	}

	protected void preRenderCallbackDruid(EntityDarkDruid entity, float f) {
		if (entity.getDataWatcher().getWatchableObjectInt(21) == 1) {
			for (float z = 1F; z <= entity.getEntityDistance() - 6F; z += 1.0F) {
				GL11.glPushMatrix();
				GL11.glDepthMask(false);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glColor4f(1F, 1F, 1F, 0.2F - z * 0.01F * 1.0F);
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glScalef(1F, 1F, 1F);
				GL11.glTranslatef(0F, -1.51F, z);
				bindTexture(texture);
				model.render(entity, 0F, 0F, 0F, 0F, 0F, 0.0625F);
				GL11.glColor4f(1F, 1F, 1F, 1.0F);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glPopMatrix();
			}
		}
	}

	protected int setDruidEyeBrightness(EntityDarkDruid entity, int pass, float partialTickTime) {
		if (pass == 1) {
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
		} else if (pass == 2) {
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
