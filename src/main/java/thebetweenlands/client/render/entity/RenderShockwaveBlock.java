package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityShockwaveBlock;

@SideOnly(Side.CLIENT)
public class RenderShockwaveBlock extends Render<EntityShockwaveBlock> {

	public RenderShockwaveBlock(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		renderShockwaveBlock(entity, x, y, z, yaw, tick);
	}

	public void renderShockwaveBlock(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glTranslatef(0.0F, 0.5F, 0.0F);
		GL11.glRotated(-90, 0, 1, 0);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(entity.blockID.getStateFromMeta(entity.blockMeta), 1.0F);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShockwaveBlock entity) {
		return null;
	}
}