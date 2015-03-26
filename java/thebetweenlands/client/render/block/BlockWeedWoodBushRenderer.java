package thebetweenlands.client.render.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.BlockWeedWoodBush;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockWeedWoodBushRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		this.renderWorldBlock(null, 0, 0, 0, block, modelID, renderer);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		float mini = 0F, minj = 0F, mink = 0F, maxi = 0.0F, maxj = 0.0F, maxk = 0.0F;

		if(world != null) {
			if (world.getBlock(x - 1, y, z) instanceof BlockWeedWoodBush)
				mini = -0.25F;
			if (world.getBlock(x + 1, y, z) instanceof BlockWeedWoodBush)
				maxi = 0.25F;
			if (world.getBlock(x, y - 1, z) instanceof BlockWeedWoodBush)
				minj = -0.25F;
			if (world.getBlock(x, y + 1, z) instanceof BlockWeedWoodBush)
				maxj = 0.25F;
			if (world.getBlock(x, y, z - 1) instanceof BlockWeedWoodBush)
				mink = -0.25F;
			if (world.getBlock(x, y, z + 1) instanceof BlockWeedWoodBush)
				maxk = 0.25F;
		}

		IIcon icon = ((BlockWeedWoodBush)block).iconFast;
		if (Minecraft.isFancyGraphicsEnabled()) icon = ((BlockWeedWoodBush)block).iconFancy;
		double umin = icon.getMinU();
		double vmin = icon.getMinV();
		double umax = icon.getMaxU();
		double vmax = icon.getMaxV();

		Tessellator tessellator = Tessellator.instance;

		tessellator.setColorOpaque_F(1, 1, 1);
		if(world != null) {
			tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		} else {
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.theWorld != null && mc.thePlayer != null) {
				tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
						(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
			}
		}

		tessellator.addTranslation(x, y, z);

		// Right Side
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.25 + mink, umax, vmin);

		// Right-Top Side
		tessellator.addVertexWithUV(0, 0.75, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.25, 1, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.25, 1, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0, 0.75, 0.25 + mink, umax, vmin);

		// Right-Bottom Side
		tessellator.addVertexWithUV(0.25, 0.0, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.0, 0.25, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25 + mink, umax, vmin);

		// Left Side
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.25 + mink, umin, vmin);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.25 + mink, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.75 + maxk, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.75 + maxk, umax, vmin);

		// Left-Top Side
		tessellator.addVertexWithUV(0.75, 1, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(1, 0.75, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.75, 1, 0.25 + mink, umax, vmin);

		// Left-Bottom Side
		tessellator.addVertexWithUV(1, 0.25, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25, 0.25 + mink, umax, vmin);

		// Front Side
		tessellator.addVertexWithUV(0.25 + mini, 0.25 + minj, 0, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 0.75 + maxj, 0, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75 + maxj, 0, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25 + minj, 0, umax, vmin);

		// Front-Right Side
		tessellator.addVertexWithUV(0.0, 0.25 + minj, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.75 + maxj, 0.25, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.75 + maxj, 0, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.25 + minj, 0, umax, vmin);

		// Front-Left Side
		tessellator.addVertexWithUV(0.75, 0.25 + minj, 0.0, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.75 + maxj, 0.0, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.25, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.25, umax, vmin);

		// Front-Top Side
		tessellator.addVertexWithUV(0.25 + mini, 0.75, 0, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.25, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75, 0, umax, vmin);

		// Front-Bottom Side
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 0.25, 0.0, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25, 0.0, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.25, umax, vmin);

		// Back Side
		tessellator.addVertexWithUV(0.75 + maxi, 0.25 + minj, 1, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75 + maxj, 1, umin, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.75 + maxj, 1, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.25 + minj, 1, umax, vmin);

		// Back-Top Side
		tessellator.addVertexWithUV(0.25 + mini, 0.75, 1, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75, 1, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.75, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.75, umax, vmin);

		// Back-Left Side
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.75, umin, vmin);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.75, 0.75 + maxj, 1, umax, vmax);
		tessellator.addVertexWithUV(0.75, 0.25 + minj, 1, umax, vmin);

		// Back-Right Side
		tessellator.addVertexWithUV(0.25, 0.25 + minj, 1, umin, vmin);
		tessellator.addVertexWithUV(0.25, 0.75 + maxj, 1, umin, vmax);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.75, umax, vmax);
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.75, umax, vmin);

		// Back-Bottom Side
		tessellator.addVertexWithUV(0.25 + mini, 0, 0.75, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 0, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25, 1, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.25, 1, umax, vmin);

		// Top Side
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.25 + mink, umax, vmin);

		// Bottom Side
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.25 + mink, umax, vmin);

		// Corners
		tessellator.addVertexWithUV(1, 0.25, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, umin, vmax);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, umax, vmax);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, umax, vmin);

		tessellator.addVertexWithUV(0.75, 0.25, 1, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75, umin, vmax);
		tessellator.addVertexWithUV(1, 0.25, 0.75, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25, 0.75, umax, vmin);

		tessellator.addVertexWithUV(0.75, 1, 0.75, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.75, 1, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.75, umax, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.75, umax, vmin);

		tessellator.addVertexWithUV(0.75, 0.75, 0, umin, vmin);
		tessellator.addVertexWithUV(0.75, 1, 0.25, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.75, 0.75, 0, umax, vmin);

		tessellator.addVertexWithUV(0.0, 0.25, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.25, 0.25, 0.0, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, umax, vmin);

		tessellator.addVertexWithUV(0.25, 1, 0.75, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.75, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.75, 1, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.75, 1, umax, vmin);

		tessellator.addVertexWithUV(0.25, 0.25, 1, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, umax, vmin);

		tessellator.addVertexWithUV(0.25, 1, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.25, 0.75, 0.0, umin, vmax);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, umax, vmin);

		if (Minecraft.isFancyGraphicsEnabled()) {
			tessellator.addVertexWithUV(0.1, 0.5, -0.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.9, 0.5, 1.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.9, 0.5, 1.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.1, 0.5, -0.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.1, 0.5, 0.7, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.9, 0.5, 0.3, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.9, 0.5, 0.3, umin, vmin);
			tessellator.addVertexWithUV(0.3, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.1, 0.5, 0.7, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.3, 0.5, 1.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.9, 0.5, -0.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.9, 0.5, -0.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.3, 0.5, 1.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			IIcon iconStick = ((BlockWeedWoodBush)block).iconStick;
			double uminStick = iconStick.getMinU();
			double vminStick = iconStick.getMinV();
			double umaxStick = iconStick.getMaxU();
			double vmaxStick = iconStick.getMaxV();
			int cSticks = 5;
			tessellator.addTranslation(0.5f, 0.0f, 0.5f);
			Random rnd = new Random();
			if(world != null) {
				rnd.setSeed(x << 16 | y << 8 | z);
			} else {
				rnd.setSeed(0);
			}
			for(int i = 0; i < cSticks; i++) {
				double rotation = Math.PI * 2.0f / (float)cSticks * (float)i;
				double xp1 = Math.sin(rotation) * 0.4f;
				double zp1 = Math.cos(rotation) * 0.4f;
				double xp2 = Math.sin(rotation+Math.PI/2.0f) * 0.4f;
				double zp2 = Math.cos(rotation+Math.PI/2.0f) * 0.4f;
				double xp3 = Math.sin(rotation+Math.PI) * 0.4f;
				double zp3 = Math.cos(rotation+Math.PI) * 0.4f;
				double xp4 = Math.sin(rotation+Math.PI+Math.PI/2.0f) * 0.4f;
				double zp4 = Math.cos(rotation+Math.PI+Math.PI/2.0f) * 0.4f;
				float xOff = (rnd.nextFloat() * 2.0f - 1.0f) * 0.4f;
				float yOff = (rnd.nextFloat() * 2.0f - 1.0f) * 0.4f;
				float zOff = (rnd.nextFloat() * 2.0f - 1.0f) * 0.4f;

				tessellator.addVertexWithUV(xp1+xOff, 0.8+yOff, zp1+zOff, uminStick, vminStick);
				tessellator.addVertexWithUV(xp2+xOff, 0.8+yOff, zp2+zOff, umaxStick, vminStick);
				tessellator.addVertexWithUV(xp3+xOff, 0.2+yOff, zp3+zOff, umaxStick, vmaxStick);
				tessellator.addVertexWithUV(xp4+xOff, 0.2+yOff, zp4+zOff, uminStick, vmaxStick);

				tessellator.addVertexWithUV(xp1+xOff, 0.8+yOff, zp1+zOff, uminStick, vminStick);
				tessellator.addVertexWithUV(xp4+xOff, 0.2+yOff, zp4+zOff, uminStick, vmaxStick);
				tessellator.addVertexWithUV(xp3+xOff, 0.2+yOff, zp3+zOff, umaxStick, vmaxStick);
				tessellator.addVertexWithUV(xp2+xOff, 0.8+yOff, zp2+zOff, umaxStick, vminStick);
			}
			tessellator.addTranslation(-0.5f, 0.0f, -0.5f);
		}

		tessellator.addTranslation(-x, -y, -z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.WEEDWOOD_BUSH.id();
	}
}