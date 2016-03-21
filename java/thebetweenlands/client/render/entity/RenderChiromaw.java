package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelChiromaw;
import thebetweenlands.entities.mobs.EntityChiromaw;
import thebetweenlands.utils.LightingUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChiromaw extends RenderLiving {
	private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw.png");
	private final ResourceLocation GLOW_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromawGlow.png");

	public RenderChiromaw() {
		super(new ModelChiromaw(), 0.5F);
		setRenderPassModel(new ModelChiromaw());
	}

	protected int setMobTextureGlow(EntityChiromaw entity, int pass, float partialTickTime) {
		if(pass == 1) {
			bindTexture(GLOW_TEXTURE);
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
	}

	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
		return setMobTextureGlow((EntityChiromaw) entity, pass, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

}
