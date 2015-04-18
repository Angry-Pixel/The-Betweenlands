package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockDoorRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) != 0) {
			if (world.getBlock(x, y - 1, z) != block)
				return false;
		} else if (world.getBlock(x, y + 1, z) != block)
			return false;

		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		tessellator.setBrightness(renderer.renderMinY > 0.0D ? brightness : block.getMixedBrightnessForBlock(world, x, y - 1, z));
		tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
		renderer.renderFaceYNeg(block, x, y, z, renderer.getBlockIcon(block, world, x, y, z, 0));

		if ((meta & 8) != 0) {
			tessellator.setBrightness(renderer.renderMaxY < 1.0D ? brightness : block.getMixedBrightnessForBlock(world, x, y + 1, z));
			tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
			renderer.uvRotateTop = 2;
			renderer.renderFaceYPos(block, x, y, z, renderer.getBlockIcon(block, world, x, y, z, 1));
			renderer.uvRotateTop = 0;
		}

		tessellator.setBrightness(renderer.renderMinZ > 0.0D ? brightness : block.getMixedBrightnessForBlock(world, x, y, z - 1));
		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		renderer.renderFaceZNeg(block, x, y, z, renderer.getBlockIcon(block, world, x, y, z, 2));

		renderer.flipTexture = false;
		tessellator.setBrightness(renderer.renderMaxZ < 1.0D ? brightness : block.getMixedBrightnessForBlock(world, x, y, z + 1));
		tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
		renderer.renderFaceZPos(block, x, y, z, renderer.getBlockIcon(block, world, x, y, z, 3));

		renderer.flipTexture = false;
		tessellator.setBrightness(renderer.renderMinX > 0.0D ? brightness : block.getMixedBrightnessForBlock(world, x - 1, y, z));
		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		renderer.renderFaceXNeg(block, x, y, z, renderer.getBlockIcon(block, world, x, y, z, 4));

		renderer.flipTexture = false;
		tessellator.setBrightness(renderer.renderMaxX < 1.0D ? brightness : block.getMixedBrightnessForBlock(world, x + 1, y, z));
		tessellator.setColorOpaque_F(0.6F, 0.6F, 0.6F);
		renderer.renderFaceXPos(block, x, y, z, renderer.getBlockIcon(block, world, x, y, z, 5));

		renderer.flipTexture = false;
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.DOOR.id();
	}
}