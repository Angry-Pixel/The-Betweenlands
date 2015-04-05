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
import thebetweenlands.client.model.block.ModelRegularPlant;
import thebetweenlands.client.model.block.ModelTubePlant;
import thebetweenlands.client.model.block.ModelVenusFlyTrap;
import thebetweenlands.client.model.block.ModelVolarpad;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.utils.ModelConverter;
import thebetweenlands.utils.ModelConverter.TextureMap;
import thebetweenlands.utils.ModelConverter.Vec3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockModelPlantRenderer implements ISimpleBlockRenderingHandler {
	public static ModelConverter plantModelInvFlatHead;
	public static ModelConverter plantModelInvBlackHead;
	public static ModelConverter plantModelInvRegularPlant;
	public static ModelConverter plandModelInvVolarpad;
	public static ModelConverter plantModelInvTubePlant;
	
	public static ModelConverter plantModelVenusFlyTrap;
	public static ModelConverter plantModelVenusFlyTrapBlooming;
	
	public static ModelConverter plantModelTubePlant;
	
	public static ModelConverter plantModelRegularPlant;

	public static ModelBlackHatMushroom modelBlackHatMushroom1 = new ModelBlackHatMushroom();
	public static ModelBlackHatMushroom2 modelBlackHatMushroom2 = new ModelBlackHatMushroom2();
	public static ModelBlackHatMushroom3 modelBlackHatMushroom3 = new ModelBlackHatMushroom3();

	public static ModelFlatHeadMushroom modelFlatHeadMushroom1 = new ModelFlatHeadMushroom();
	public static ModelFlatHeadMushroom2 modelFlatHeadMushroom2 = new ModelFlatHeadMushroom2();
	
	public static ModelRegularPlant modelRegularPlant = new ModelRegularPlant();
	
	public static ModelTubePlant modelTubePlant = new ModelTubePlant();
	
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
		
		if(block == BLBlockRegistry.tubePlant) {
			if(plantModelInvTubePlant == null) {
				plantModelInvTubePlant = new ModelConverter(
						new ModelTubePlant(),
						0.065D,
						new TextureMap(128, 128, BLBlockRegistry.tubePlant.modelTexture1),
						true);
			}
			plantModelInvTubePlant.renderWithTessellator(Tessellator.instance);
		}
		
		if(block == BLBlockRegistry.tubePlant) {
			if(plantModelInvTubePlant == null) {
				plantModelInvTubePlant = new ModelConverter(
						new ModelTubePlant(),
						0.065D / 1.5D,
						new TextureMap(128, 128, BLBlockRegistry.tubePlant.modelTexture1),
						true);
				plantModelInvTubePlant.offsetWS(new Vec3(0, -0.7, 0));
			}
			plantModelInvTubePlant.renderWithTessellator(Tessellator.instance);
		}
		
		if(block == BLBlockRegistry.regularPlant) {
			if(plantModelInvRegularPlant == null) {
				plantModelInvRegularPlant = new ModelConverter(
						new ModelRegularPlant(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.regularPlant.modelTexture1),
						true);
			}
			plantModelInvRegularPlant.renderWithTessellator(Tessellator.instance);
		}
		
		if(block == BLBlockRegistry.venusFlyTrap) {
			if(plantModelVenusFlyTrap == null) {
				plantModelVenusFlyTrap = new ModelConverter(
						new ModelVenusFlyTrap(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.venusFlyTrap.modelTexture),
						true);
			}
			plantModelVenusFlyTrap.renderWithTessellator(Tessellator.instance);
		}
		
		if(block == BLBlockRegistry.volarpad) {
			if(plandModelInvVolarpad == null) {
				plandModelInvVolarpad = new ModelConverter(
						new ModelVolarpad(),
						0.065D / 3.0D,
						new TextureMap(256, 256, BLBlockRegistry.volarpad.modelTexture),
						true);
				plandModelInvVolarpad.offsetWS(new Vec3(0, -1.2, 0));
			}
			plandModelInvVolarpad.renderWithTessellator(Tessellator.instance);
		}
		
		tessellator.draw();

		Tessellator.instance.addTranslation(-0.5F, -1.5F, -0.5F);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
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
		} else if(block == BLBlockRegistry.flatHeadMushroom) {
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
		} else if(block == BLBlockRegistry.tubePlant) {
			if(plantModelTubePlant == null) {
				plantModelTubePlant = new ModelConverter(
						new ModelTubePlant(),
						0.065D,
						new TextureMap(128, 128, BLBlockRegistry.tubePlant.modelTexture1),
						true);
			}
			plantModelTubePlant.renderWithTessellator(Tessellator.instance);
		} else if(block == BLBlockRegistry.regularPlant) {
			if(plantModelRegularPlant == null) {
				plantModelRegularPlant = new ModelConverter(
						new ModelRegularPlant(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.regularPlant.modelTexture1),
						true);
			}
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, 0.0F, rnd.nextFloat()/2.0F - 0.25F);
			float rotYaw = rnd.nextFloat() * 360.0F;
			//TODO: Not sure if that's better for performance than reloading the model. I'll do some testing later on
			plantModelRegularPlant.rotate(rotYaw, 0.0F, 1.0F, 0.0F, new Vec3(0.0F, 0.0F, 0.0F));
			plantModelRegularPlant.offsetWS(offset);
			plantModelRegularPlant.renderWithTessellator(Tessellator.instance);
			plantModelRegularPlant.offsetWS(offset.neg());
			plantModelRegularPlant.rotate(-rotYaw, 0.0F, 1.0F, 0.0F, new Vec3(0.0F, 0.0F, 0.0F));
		} else if(block == BLBlockRegistry.venusFlyTrap) {
			if(plantModelVenusFlyTrap == null) {
				plantModelVenusFlyTrap = new ModelConverter(
						new ModelVenusFlyTrap(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.venusFlyTrap.modelTexture),
						true);
			}
			if(plantModelVenusFlyTrapBlooming == null) {
				plantModelVenusFlyTrapBlooming = new ModelConverter(
						new ModelVenusFlyTrap(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.venusFlyTrap.modelTextureBlooming),
						true);
			}
			int meta = world.getBlockMetadata(x, y, z);
			if(meta == 0) {
				plantModelVenusFlyTrap.renderWithTessellator(Tessellator.instance);
			} else {
				plantModelVenusFlyTrapBlooming.renderWithTessellator(Tessellator.instance);
			}
		} else if(block == BLBlockRegistry.volarpad) {
			ModelConverter worldModel = new ModelConverter(
					new ModelVolarpad(),
					0.065D,
					new TextureMap(256, 256, BLBlockRegistry.volarpad.modelTexture),
					true);

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
		return BlockRenderIDs.MODEL_PLANT.id();
	}
}