package thebetweenlands.client.render.block;

import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.model.block.ModelBlockBucket;
import thebetweenlands.client.model.block.ModelWalkway;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;

public class BlockRubberTapRenderer implements ISimpleBlockRenderingHandler {
	public static ModelBlockBucket modelBlockBucket = new ModelBlockBucket();

	public static ModelConverter modelConverterBlockBucket = null;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if(modelConverterBlockBucket == null) {
			modelConverterBlockBucket = new ModelConverter(
					modelBlockBucket,
					0.065D,
					new ModelConverter.TextureMap(128, 128, BLBlockRegistry.rubberTap.icon),
					true
					);
		}

		Tessellator tessellator = Tessellator.instance;
		Minecraft mc = Minecraft.getMinecraft();
		World world = Minecraft.getMinecraft().theWorld;
		if(world != null && mc.thePlayer != null) {
			Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		Tessellator.instance.addTranslation(0, 1.2F, 0);
		modelConverterBlockBucket.renderWithTessellator(Tessellator.instance);
		Tessellator.instance.addTranslation(0, -1.2F, 0);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(world == null)
			return false;
		if(modelConverterBlockBucket == null) {
			modelConverterBlockBucket = new ModelConverter(
					modelBlockBucket,
					0.065D,
					new ModelConverter.TextureMap(128, 128, BLBlockRegistry.rubberTap.icon),
					true
					);
		}

		Tessellator.instance.setColorRGBA_F(1,1,1,1);
		Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		Tessellator.instance.addTranslation(x + 0.5F, y + 1.5F, z +0.5F);
		
		ModelConverter.Model model = modelConverterBlockBucket.getModel().rotate(90, 0.0f, 1.0f, 0.0F, new ModelConverter.Vec3(0, 0, 0));
		
		int rotation = 0;
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
		case 1:
		case 5:
			rotation = 0;
			break;
		case 2:
		case 6:
			rotation = 180;
			break;
		case 3:
		case 7:
			rotation = -90;
			break;
		case 4:
		case 8:
			rotation = -270;
			break;
		}
		
		model.rotate(rotation, 0, 1, 0, new ModelConverter.Vec3(0.0, 0, 0.0));
		
		model.renderWithTessellator(Tessellator.instance);
		
		if(meta >= 5) {
			IIcon waterIcon = ((BlockSwampWater)BLBlockRegistry.swampWater).getWaterIcon(1);
			float tx = 0.0F;
			float tz = 0.0F;
			switch(meta) {
			case 1:
			case 5:
				tx -= 0.8F;
				tz -= 0.5F;
				break;
			case 2:
			case 6:
				tx -= 0.2F;
				tz -= 0.5F;
				break;
			case 3:
			case 7:
				tx -= 0.5F;
				tz -= 0.8F;
				break;
			case 4:
			case 8:
				tx -= 0.5F;
				tz -= 0.2F;
				break;
			}
			Tessellator.instance.addTranslation(tx, -0.8F, tz);
			Tessellator.instance.setColorRGBA_F(0.8F, 0.6F, 0.4F, 2.0F);
			Tessellator.instance.addVertexWithUV(0.2, 0, 0.2, waterIcon.getMinU(), waterIcon.getMinV());
			Tessellator.instance.addVertexWithUV(0.2, 0, 0.8, waterIcon.getMinU(), waterIcon.getMaxV());
			Tessellator.instance.addVertexWithUV(0.8, 0, 0.8, waterIcon.getMaxU(), waterIcon.getMaxV());
			Tessellator.instance.addVertexWithUV(0.8, 0, 0.2, waterIcon.getMaxU(), waterIcon.getMinV());
			Tessellator.instance.addTranslation(-tx, 0.8F, -tz);
		}
		
		Tessellator.instance.addTranslation(-x-0.5F, -y-1.5F, -z-0.5F);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.BlockRenderIDs.RUBBER_TAP.id();
	}
}
