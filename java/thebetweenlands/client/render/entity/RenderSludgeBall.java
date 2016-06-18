package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelSludgeBall;
import thebetweenlands.entities.mobs.EntitySludgeBall;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSludgeBall extends Render {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/sludge.png");
	private final ModelSludgeBall model;

	public RenderSludgeBall() {
		model = new ModelSludgeBall();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderSludgeBall((EntitySludgeBall) entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderSludgeBall(EntitySludgeBall sludgeball, double x, double y, double z, float rotationYaw, float partialTickTime) {
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(texture);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(true);
		model.render(0.0625F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
