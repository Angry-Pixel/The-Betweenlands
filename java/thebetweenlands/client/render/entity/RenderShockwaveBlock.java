package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.EntityShockwaveBlock;

public class RenderShockwaveBlock extends Render {

	private final RenderBlocks blockRenderer = new RenderBlocks();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
		renderShockwaveBlock((EntityShockwaveBlock) entity, x, y, z, yaw, tick);
	}

	public void renderShockwaveBlock(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		bindTexture(TextureMap.locationBlocksTexture);
		blockRenderer.renderBlockAsItem(entity.blockID, entity.blockMeta, 1.0F);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}