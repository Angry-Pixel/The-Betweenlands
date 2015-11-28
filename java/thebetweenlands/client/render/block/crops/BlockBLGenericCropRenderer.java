package thebetweenlands.client.render.block.crops;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.plants.crops.BlockBLGenericCrop;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.utils.ModelConverter;
import thebetweenlands.utils.ModelConverter.Vec3;

@SideOnly(Side.CLIENT)
public class BlockBLGenericCropRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		BlockBLGenericCrop crop = (BlockBLGenericCrop) block;

		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}

		tessellator.startDrawingQuads();
		this.renderCrop(crop, 0.5F, 1.5F, 0.5F, 6);
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockBLGenericCrop crop = (BlockBLGenericCrop) block;

		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		tessellator.setColorOpaque(255, 255, 255);

		this.renderCrop(crop, x + 0.5D, y + 1.6D, z + 0.5D, world.getBlockMetadata(x, y, z));
		return true;
	}

	public void renderCrop(BlockBLGenericCrop crop, double x, double y, double z, int meta) {
		IIcon icon = crop.getIcon(0, meta);
		ModelConverter model = crop.getCropRenderer().getCropModel(meta);
		int[] textureDimensions = crop.getCropRenderer().getTextureDimensions(meta);

		Tessellator tessellator = Tessellator.instance;

		tessellator.addTranslation((float)x, (float)y, (float)z);
		Random rnd = new Random();
		long seed = (int)x * 0x2FC20FL ^ (int)y * 0x6EBFFF5L ^ (int)z;
		rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);
		float yScale = rnd.nextFloat() * 0.2F + 1.0F;
		float xzScale = rnd.nextFloat() * 0.2F + 1.0F;
		Vec3 offset = new Vec3((rnd.nextFloat()/2.0F - 0.25F) * 0.8F, 1.5F * (yScale - 1.0F), (rnd.nextFloat()/2.0F - 0.25F) * 0.8F);
		float rotYaw = rnd.nextFloat() * 360.0F;
		model.getModel().rotate(rotYaw, 0.0F, 1.0F, 0.0F, new Vec3(0.0F, 0.0F, 0.0F)).scale(xzScale, yScale, xzScale).
		offsetWS(offset).renderWithTessellator(tessellator, textureDimensions[0], textureDimensions[1], icon);
		tessellator.addTranslation(-(float)x, -(float)y, -(float)z);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.CROP.id();
	}
}