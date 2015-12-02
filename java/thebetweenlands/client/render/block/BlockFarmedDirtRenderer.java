package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.connectedtexture.ConnectedTexture;

public class BlockFarmedDirtRenderer implements ISimpleBlockRenderingHandler {
	private static final ConnectedTexture farmedDirtTextureHelper = new ConnectedTexture(64, 18, 1);

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		renderer.setRenderBounds(0D, -0.1D, 0D, 1D, 0.9D, 1D);

		Tessellator.instance.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			Tessellator.instance.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		Tessellator.instance.startDrawingQuads();
		BlockRenderHelper.renderSimpleBlock(block, metadata, renderer);
		if(metadata != BlockFarmedDirt.PURE_SWAMP_DIRT) {
			IIcon icon = BLBlockRegistry.farmedDirt.getIcon(1, metadata);
			Tessellator.instance.setNormal(0, 1, 0);
			for(int bsx = 0; bsx <= 1; bsx++) {
				for(int bsz = 0; bsz <= 1; bsz++) {
					int quadrant = bsx + bsz * 2;

					float rxOffset = 0.5F * bsx;
					float rzOffset = 0.5F * bsz;

					float[][] relIconUVs = farmedDirtTextureHelper.getUVs(0, quadrant);

					Tessellator.instance.addTranslation(rxOffset, 0.901F, rzOffset);
					Tessellator.instance.addVertexWithUV(-0.0D, 0.0D, 0.0D, icon.getInterpolatedU(relIconUVs[0][0] * 16.0D), icon.getInterpolatedV(relIconUVs[0][1] * 16.0D));
					Tessellator.instance.addVertexWithUV(-0.0D, 0.0D, 0.5D, icon.getInterpolatedU(relIconUVs[0][0] * 16.0D), icon.getInterpolatedV(relIconUVs[1][1] * 16.0D));
					Tessellator.instance.addVertexWithUV(0.5D, 0.0D, 0.5D, icon.getInterpolatedU(relIconUVs[1][0] * 16.0D), icon.getInterpolatedV(relIconUVs[1][1] * 16.0D));
					Tessellator.instance.addVertexWithUV(0.5D, 0.0D, 0.0D, icon.getInterpolatedU(relIconUVs[1][0] * 16.0D), icon.getInterpolatedV(relIconUVs[0][1] * 16.0D));
					Tessellator.instance.addTranslation(-rxOffset, -0.901F, -rzOffset);
				}
			}
		}
		Tessellator.instance.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);

		Tessellator tessellator = Tessellator.instance;

		if(meta == BlockFarmedDirt.PURE_SWAMP_DIRT) {
			renderer.renderAllFaces = true;
		}
		renderer.renderStandardBlock(block, x, y, z);
		renderer.renderAllFaces = false;

		IIcon icon = BLBlockRegistry.farmedDirt.getIcon(1, meta);;

		tessellator.setNormal(0, 1, 0);
		tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z));
		tessellator.setColorOpaque_F(1, 1, 1);

		float ry = (float) y + 1.0F;
		tessellator.addTranslation(x, ry, z);

		if(meta != BlockFarmedDirt.PURE_SWAMP_DIRT) {
			boolean blockArray[] = new boolean[9];

			boolean isPure = meta == BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MIN || meta == BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MID || meta == BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MAX;

			for(int xo = -1; xo <= 1; xo++) {
				for(int zo = -1; zo <= 1; zo++) {
					int currentMeta = world.getBlockMetadata(x+xo, y, z+zo);
					boolean isCurrentPure = currentMeta == BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MIN || currentMeta == BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MID || currentMeta == BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MAX;
					blockArray[ConnectedTexture.getIndex(xo+1, zo+1, 3)] = world.getBlock(x+xo, y, z+zo) == BLBlockRegistry.farmedDirt && ((isCurrentPure && isPure) || (currentMeta == meta));
				}
			}

			float[][][] blockRelIconUVs = farmedDirtTextureHelper.getFaceUVs(blockArray);

			for(int bsx = 0; bsx <= 1; bsx++) {
				for(int bsz = 0; bsz <= 1; bsz++) {
					float rxOffset = 0.5F * bsx;
					float rzOffset = 0.5F * bsz;

					float[][] quadrantRelIconUVs = blockRelIconUVs[ConnectedTexture.getIndex(bsx, bsz, 2)];

					tessellator.addTranslation(rxOffset, 0, rzOffset);
					tessellator.addVertexWithUV(-0.0D, 0.0D, 0.0D, icon.getInterpolatedU(quadrantRelIconUVs[0][0] * 16.0D), icon.getInterpolatedV(quadrantRelIconUVs[0][1] * 16.0D));
					tessellator.addVertexWithUV(-0.0D, 0.0D, 0.5D, icon.getInterpolatedU(quadrantRelIconUVs[0][0] * 16.0D), icon.getInterpolatedV(quadrantRelIconUVs[1][1] * 16.0D));
					tessellator.addVertexWithUV(0.5D, 0.0D, 0.5D, icon.getInterpolatedU(quadrantRelIconUVs[1][0] * 16.0D), icon.getInterpolatedV(quadrantRelIconUVs[1][1] * 16.0D));
					tessellator.addVertexWithUV(0.5D, 0.0D, 0.0D, icon.getInterpolatedU(quadrantRelIconUVs[1][0] * 16.0D), icon.getInterpolatedV(quadrantRelIconUVs[0][1] * 16.0D));
					tessellator.addTranslation(-rxOffset, -0, -rzOffset);
				}
			}
		}

		tessellator.addTranslation(-x, -ry, -z);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.BlockRenderIDs.FARMED_DIRT.id();
	}
}
