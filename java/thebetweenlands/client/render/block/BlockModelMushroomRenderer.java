package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import scala.util.Random;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelBlackHatMushroom;
import thebetweenlands.client.model.block.ModelBlackHatMushroom2;
import thebetweenlands.client.model.block.ModelBlackHatMushroom3;
import thebetweenlands.client.model.block.ModelFlatHeadMushroom;
import thebetweenlands.client.model.block.ModelFlatHeadMushroom2;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.utils.ModelConverter;
import thebetweenlands.utils.ModelConverter.TextureMap;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockModelMushroomRenderer implements ISimpleBlockRenderingHandler {
	public static ModelConverter plantModelInvFlatHead;
	public static ModelConverter plantModelInvBlackHead;

	public static ModelBlackHatMushroom modelBlackHatMushroom1 = new ModelBlackHatMushroom();
	public static ModelBlackHatMushroom2 modelBlackHatMushroom2 = new ModelBlackHatMushroom2();
	public static ModelBlackHatMushroom3 modelBlackHatMushroom3 = new ModelBlackHatMushroom3();

	public static ModelFlatHeadMushroom modelFlatHeadMushroom1 = new ModelFlatHeadMushroom();
	public static ModelFlatHeadMushroom2 modelFlatHeadMushroom2 = new ModelFlatHeadMushroom2();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}

		Tessellator.instance.addTranslation(0.5F, 1.5F, 0.5F);

		tessellator.startDrawingQuads();

		if(block == BLBlockRegistry.blackHatMushroom) {
			if(plantModelInvBlackHead == null) {
				plantModelInvBlackHead = new ModelConverter(
						new ModelBlackHatMushroom2(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.blackHatMushroom.modelTexture2),
						true);
			}
			plantModelInvBlackHead.renderWithTessellator(Tessellator.instance);
		}

		if(block == BLBlockRegistry.flatHeadMushroom) {
			if(plantModelInvFlatHead == null) {
				plantModelInvFlatHead = new ModelConverter(
						new ModelFlatHeadMushroom(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.flatHeadMushroom.modelTexture1),
						true);
			}
			plantModelInvFlatHead.renderWithTessellator(Tessellator.instance);
		}

		tessellator.draw();

		Tessellator.instance.addTranslation(-0.5F, -1.5F, -0.5F);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		Tessellator.instance.setColorOpaque(255, 255, 255);
		Tessellator.instance.addTranslation(x + 0.5F, y + 1.6F, z + 0.5F);

		Random rnd = new Random();
		rnd.setSeed(x * y * z);
		ModelBase model = null;
		IIcon modelTexture = null;
		if(block == BLBlockRegistry.blackHatMushroom) {
			if(rnd.nextInt(30) <= 10) {
				model = modelBlackHatMushroom1;
				modelTexture = BLBlockRegistry.blackHatMushroom.modelTexture1;
			} else if(rnd.nextInt(30) <= 20) {
				model = modelBlackHatMushroom2;
				modelTexture = BLBlockRegistry.blackHatMushroom.modelTexture2;
			} else {
				model = modelBlackHatMushroom3;
				modelTexture = BLBlockRegistry.blackHatMushroom.modelTexture3;
			}
			ModelConverter worldModel = new ModelConverter(
					model,
					0.065D,
					new TextureMap(64, 64, modelTexture),
					true, 
					rnd.nextFloat() * 40 - 20, 
					rnd.nextFloat() * 180,
					0.0F);
			worldModel.renderWithTessellator(Tessellator.instance);
		} else {
			if(rnd.nextInt(20) <= 10) {
				model = modelFlatHeadMushroom1;
				modelTexture = BLBlockRegistry.flatHeadMushroom.modelTexture1;
			} else {
				model = modelFlatHeadMushroom2;
				modelTexture = BLBlockRegistry.flatHeadMushroom.modelTexture2;
			}
			ModelConverter worldModel = new ModelConverter(
					model,
					0.065D,
					new TextureMap(64, 64, modelTexture),
					true, 
					rnd.nextFloat() * 40 - 20, 
					rnd.nextFloat() * 180,
					0.0F);

			worldModel.renderWithTessellator(Tessellator.instance);
		}

		Tessellator.instance.addTranslation(-x - 0.5F, -y - 1.6F, -z - 0.5F);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.MODEL_MUSHROOM.id();
	}
}