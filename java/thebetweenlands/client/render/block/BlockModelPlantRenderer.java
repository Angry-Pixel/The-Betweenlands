package thebetweenlands.client.render.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelBlackHatMushroom;
import thebetweenlands.client.model.block.ModelBlackHatMushroom2;
import thebetweenlands.client.model.block.ModelBlackHatMushroom3;
import thebetweenlands.client.model.block.ModelBulbCappedMushroom;
import thebetweenlands.client.model.block.ModelFlatHeadMushroom;
import thebetweenlands.client.model.block.ModelFlatHeadMushroom2;
import thebetweenlands.client.model.block.ModelRegularPlant;
import thebetweenlands.client.model.block.ModelSundew;
import thebetweenlands.client.model.block.ModelTubePlant;
import thebetweenlands.client.model.block.ModelVenusFlyTrap;
import thebetweenlands.client.model.block.ModelVolarpad;
import thebetweenlands.client.model.block.ModelWeepingBlue;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.utils.ModelConverter;
import thebetweenlands.utils.ModelConverter.Box;
import thebetweenlands.utils.ModelConverter.Model;
import thebetweenlands.utils.ModelConverter.TextureMap;
import thebetweenlands.utils.ModelConverter.Vec3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockModelPlantRenderer implements ISimpleBlockRenderingHandler {
	public static ModelConverter plantModelInvFlatHead;
	public static ModelConverter plantModelInvBlackHat;
	public static ModelConverter plantModelInvRegularPlant;
	public static ModelConverter plandModelInvVolarpad;
	public static ModelConverter plantModelInvTubePlant;
	public static ModelConverter plantModelInvWeepingBlue;
	public static ModelConverter plantModelInvVenusFlyTrap;
	public static ModelConverter plantModelInvBulbCappedMushroom;
	public static ModelConverter plantModelInvSundew;

	public static ModelConverter plantModelVolarpad1;
	public static ModelConverter plantModelVolarpad2;
	public static ModelConverter plantModelVolarpad3;
	public static ModelConverter plantModelFlatHead;
	public static ModelConverter plantModelBlackHat1;
	public static ModelConverter plantModelBlackHat2;
	public static ModelConverter plantModelBlackHat3;
	public static ModelConverter plantModelFlatHead1;
	public static ModelConverter plantModelFlatHead2;
	public static ModelConverter plantModelVenusFlyTrap;
	public static ModelConverter plantModelVenusFlyTrapBlooming;
	public static ModelConverter plantModelTubePlant;
	public static ModelConverter plantModelRegularPlant;
	public static ModelConverter plantModelWeepingBlue;
	public static ModelConverter plantModelBulbCappedMushroom;
	public static ModelConverter plantModelSundew;

	public static ModelBlackHatMushroom modelBlackHatMushroom1 = new ModelBlackHatMushroom();
	public static ModelBlackHatMushroom2 modelBlackHatMushroom2 = new ModelBlackHatMushroom2();
	public static ModelBlackHatMushroom3 modelBlackHatMushroom3 = new ModelBlackHatMushroom3();
	public static ModelFlatHeadMushroom modelFlatHeadMushroom1 = new ModelFlatHeadMushroom();
	public static ModelFlatHeadMushroom2 modelFlatHeadMushroom2 = new ModelFlatHeadMushroom2();
	public static ModelRegularPlant modelRegularPlant = new ModelRegularPlant();
	public static ModelTubePlant modelTubePlant = new ModelTubePlant();
	public static ModelWeepingBlue modelWeepingBlue = new ModelWeepingBlue();
	public static ModelVolarpad modelVolarpad = new ModelVolarpad();
	public static ModelBulbCappedMushroom modelBulbCappedMushroom = new ModelBulbCappedMushroom();
	public static ModelSundew modelSundew = new ModelSundew();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}

		tessellator.addTranslation(0.5F, 1.5F, 0.5F);

		tessellator.startDrawingQuads();

		if(block == BLBlockRegistry.blackHatMushroom) {
			if(plantModelInvBlackHat == null) {
				plantModelInvBlackHat = new ModelConverter(
						new ModelBlackHatMushroom2(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.blackHatMushroom.modelTexture2),
						true);
			}
			plantModelInvBlackHat.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.flatHeadMushroom) {
			if(plantModelInvFlatHead == null) {
				plantModelInvFlatHead = new ModelConverter(
						new ModelFlatHeadMushroom(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.flatHeadMushroom.modelTexture1),
						true);
			}
			plantModelInvFlatHead.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.pitcherPlant) {
			if(plantModelInvTubePlant == null) {
				plantModelInvTubePlant = new ModelConverter(
						new ModelTubePlant(),
						0.065D / 1.5D,
						new TextureMap(128, 128, BLBlockRegistry.pitcherPlant.modelTexture1),
						true);
				plantModelInvTubePlant.offsetWS(new Vec3(0, -0.7, 0));
			}
			plantModelInvTubePlant.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.swampPlant) {
			if(plantModelInvRegularPlant == null) {
				plantModelInvRegularPlant = new ModelConverter(
						new ModelRegularPlant(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.swampPlant.modelTexture1),
						true);
			}
			plantModelInvRegularPlant.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.bulbCappedMushroom) {
			if(plantModelInvBulbCappedMushroom == null) {
				plantModelInvBulbCappedMushroom = new ModelConverter(
						new ModelBulbCappedMushroom(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.bulbCappedMushroom.modelTexture1),
						true);
			}
			plantModelInvBulbCappedMushroom.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.venusFlyTrap) {
			if(plantModelInvVenusFlyTrap == null) {
				plantModelInvVenusFlyTrap = new ModelConverter(
						new ModelVenusFlyTrap(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.venusFlyTrap.modelTexture),
						true);
			}
			plantModelInvVenusFlyTrap.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.volarpad) {
			if(plandModelInvVolarpad == null) {
				plandModelInvVolarpad = new ModelConverter(
						new ModelVolarpad(),
						0.065D / 3.0D,
						new TextureMap(256, 256, BLBlockRegistry.volarpad.modelTexture1),
						true);
				plandModelInvVolarpad.offsetWS(new Vec3(0, -1.2, 0));
			}
			plandModelInvVolarpad.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.weepingBlue) {
			if(plantModelInvWeepingBlue == null) {
				plantModelInvWeepingBlue = new ModelConverter(
						modelWeepingBlue,
						0.065D / 1.5D,
						new TextureMap(64, 64, BLBlockRegistry.weepingBlue.modelTexture1),
						true);
				plantModelInvWeepingBlue.offsetWS(new Vec3(0, -0.75, 0));
			}
			plantModelInvWeepingBlue.renderWithTessellator(tessellator);
		}

		if(block == BLBlockRegistry.sundew) {
			if(plantModelInvSundew == null) {
				plantModelInvSundew = new ModelConverter(
						modelSundew,
						0.065D / 1.5D,
						new TextureMap(128, 128, BLBlockRegistry.sundew.topIcon),
						true);
				plantModelInvSundew.offsetWS(new Vec3(0, -0.75, 0));
			}
			plantModelInvSundew.renderWithTessellator(tessellator);
		}

		tessellator.draw();

		tessellator.addTranslation(-0.5F, -1.5F, -0.5F);

		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;

		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		tessellator.setColorOpaque(255, 255, 255);
		tessellator.addTranslation(x + 0.5F, y + 1.6F, z + 0.5F);

		Random rnd = new Random();
        long seed = x * 0x2FC20FL ^ y * 0x6EBFFF5L ^ z;
		rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);
		if(block == BLBlockRegistry.blackHatMushroom) {
			if(plantModelBlackHat1 == null) {
				plantModelBlackHat1 = new ModelConverter(
						modelBlackHatMushroom1,
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.blackHatMushroom.modelTexture1),
						true);
			}
			if(plantModelBlackHat2 == null) {
				plantModelBlackHat2 = new ModelConverter(
						modelBlackHatMushroom2,
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.blackHatMushroom.modelTexture2),
						true);
			}
			if(plantModelBlackHat3 == null) {
				plantModelBlackHat3 = new ModelConverter(
						modelBlackHatMushroom3,
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.blackHatMushroom.modelTexture3),
						true);
			}
			int randInt = rnd.nextInt(3);
			if(randInt == 0) {
				plantModelBlackHat1.getModel().rotate(1.0F, rnd.nextFloat() * 40 - 20, rnd.nextFloat() * 180, 0.0F, new Vec3(0, 0, 0)).renderWithTessellator(tessellator);
			} else if(randInt == 1) {
				plantModelBlackHat2.getModel().rotate(1.0F, rnd.nextFloat() * 40 - 20, rnd.nextFloat() * 180, 0.0F, new Vec3(0, 0, 0)).renderWithTessellator(tessellator);
			} else {
				plantModelBlackHat3.getModel().rotate(1.0F, rnd.nextFloat() * 40 - 20, rnd.nextFloat() * 180, 0.0F, new Vec3(0, 0, 0)).renderWithTessellator(tessellator);
			}
		} else if(block == BLBlockRegistry.flatHeadMushroom) {
			if(plantModelFlatHead1 == null) {
				plantModelFlatHead1 = new ModelConverter(
						modelFlatHeadMushroom1,
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.flatHeadMushroom.modelTexture1),
						true);
			}
			if(plantModelFlatHead2 == null) {
				plantModelFlatHead2 = new ModelConverter(
						modelFlatHeadMushroom2,
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.flatHeadMushroom.modelTexture2),
						true);
			}
			if(rnd.nextInt(2) == 0) {
				plantModelFlatHead1.getModel().rotate(1.0F, rnd.nextFloat() * 40 - 20, rnd.nextFloat() * 180, 0.0F, new Vec3(0, 0, 0)).renderWithTessellator(tessellator);
			} else {
				plantModelFlatHead2.getModel().rotate(1.0F, rnd.nextFloat() * 40 - 20, rnd.nextFloat() * 180, 0.0F, new Vec3(0, 0, 0)).renderWithTessellator(tessellator);
			}
		} else if(block == BLBlockRegistry.pitcherPlant && world.getBlock(x, y+1, z) == BLBlockRegistry.pitcherPlant) {
			if(plantModelTubePlant == null) {
				plantModelTubePlant = new ModelConverter(
						new ModelTubePlant(),
						0.065D,
						new TextureMap(128, 128, BLBlockRegistry.pitcherPlant.modelTexture1),
						true);
			}
			float yScale = rnd.nextFloat() / 3.0F + 0.75F;
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, yScale * 1.5F - 1.5F, rnd.nextFloat()/2.0F - 0.25F);
			plantModelTubePlant.getModel().scale(1, yScale, 1).
			rotate(rnd.nextFloat() * 360.0F, 0, 1, 0, new Vec3(0, 0, 0)).
			offsetWS(offset).renderWithTessellator(tessellator);
		} else if(block == BLBlockRegistry.swampPlant) {
			if(plantModelRegularPlant == null) {
				plantModelRegularPlant = new ModelConverter(
						new ModelRegularPlant(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.swampPlant.modelTexture1),
						true);
			}

			float yScale = rnd.nextFloat() / 1.5F + 1.0F;
			float xzScale = rnd.nextFloat() / 4.0F + 1.0F;
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, 1.5F * (yScale - 1.0F), rnd.nextFloat()/2.0F - 0.25F);
			float rotYaw = rnd.nextFloat() * 360.0F;
			plantModelRegularPlant.getModel().rotate(rotYaw, 0.0F, 1.0F, 0.0F, new Vec3(0.0F, 0.0F, 0.0F)).scale(xzScale, yScale, xzScale).
			offsetWS(offset).renderWithTessellator(tessellator);
		} else if(block == BLBlockRegistry.bulbCappedMushroom) {
			if(plantModelBulbCappedMushroom == null) {
				plantModelBulbCappedMushroom = new ModelConverter(
						new ModelBulbCappedMushroom(),
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.bulbCappedMushroom.modelTexture1),
						true);
			}
			
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, 0.0F, rnd.nextFloat()/2.0F - 0.25F);
			plantModelBulbCappedMushroom.getModel().offsetWS(offset).renderWithTessellator(tessellator);

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
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, 0.0F, rnd.nextFloat()/2.0F - 0.25F);
			if(meta == 0) {
				plantModelVenusFlyTrap.getModel().rotate(rnd.nextFloat() * 360.0F, 0, 1, 0, new Vec3(0, 0, 0))
				.offsetWS(offset).renderWithTessellator(tessellator);
			} else {
				plantModelVenusFlyTrapBlooming.getModel().rotate(rnd.nextFloat() * 360.0F, 0, 1, 0, new Vec3(0, 0, 0))
				.offsetWS(offset).renderWithTessellator(tessellator);
			}
		} else if(block == BLBlockRegistry.volarpad) {
			if(plantModelVolarpad1 == null) {
				plantModelVolarpad1 = new ModelConverter(
						modelVolarpad,
						0.065D,
						new TextureMap(256, 256, BLBlockRegistry.volarpad.modelTexture1),
						true);
			}
			if(plantModelVolarpad2 == null) {
				plantModelVolarpad2 = new ModelConverter(
						modelVolarpad,
						0.065D,
						new TextureMap(256, 256, BLBlockRegistry.volarpad.modelTexture2),
						true);
			}
			if(plantModelVolarpad3 == null) {
				plantModelVolarpad3 = new ModelConverter(
						modelVolarpad,
						0.065D,
						new TextureMap(256, 256, BLBlockRegistry.volarpad.modelTexture3),
						true);
			}
			float plantHeightOffset = -rnd.nextFloat()/1.3F;
			float padRotation = rnd.nextFloat() * 360.0F;
			int randNum = rnd.nextInt(3);
			if(randNum == 0) {
				Model convertedModel = plantModelVolarpad1.getModel();
				for(Box box : convertedModel.getBoxes()) {
					ModelRenderer mr = box.getModelRenderer();
					if(mr == modelVolarpad.pad1) {
						box.rotate(padRotation, 0, 1, 0, new Vec3(mr.rotationPointX*0.065D+0.025, 0, mr.rotationPointY*0.065D+0.15));
						break;
					}
				}
				convertedModel.rotate(rnd.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F, new Vec3(0, 0, 0)).
				offsetWS(new Vec3(rnd.nextFloat()/2.0F - 0.25F, plantHeightOffset, rnd.nextFloat()/2.0F - 0.25F));
				convertedModel.renderWithTessellator(tessellator);
			} else if(randNum == 1) {
				Model convertedModel = plantModelVolarpad2.getModel();
				for(Box box : convertedModel.getBoxes()) {
					ModelRenderer mr = box.getModelRenderer();
					if(mr == modelVolarpad.pad1) {
						box.rotate(padRotation, 0, 1, 0, new Vec3(mr.rotationPointX*0.065D+0.025, 0, mr.rotationPointY*0.065D+0.15));
						break;
					}
				}
				convertedModel.rotate(rnd.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F, new Vec3(0, 0, 0)).
				offsetWS(new Vec3(rnd.nextFloat()/2.0F - 0.25F, plantHeightOffset, rnd.nextFloat()/2.0F - 0.25F));
				convertedModel.renderWithTessellator(tessellator);
			} else {
				Model convertedModel = plantModelVolarpad3.getModel();
				for(Box box : convertedModel.getBoxes()) {
					ModelRenderer mr = box.getModelRenderer();
					if(mr == modelVolarpad.pad1) {
						box.rotate(padRotation, 0, 1, 0, new Vec3(mr.rotationPointX*0.065D+0.025, 0, mr.rotationPointY*0.065D+0.15));
						break;
					}
				}
				convertedModel.rotate(rnd.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F, new Vec3(0, 0, 0)).
				offsetWS(new Vec3(rnd.nextFloat()/2.0F - 0.25F, plantHeightOffset, rnd.nextFloat()/2.0F - 0.25F));
				convertedModel.renderWithTessellator(tessellator);
			}
		} else if(block == BLBlockRegistry.weepingBlue && world.getBlock(x, y+1, z) == BLBlockRegistry.weepingBlue) {
			if(plantModelWeepingBlue == null) {
				plantModelWeepingBlue = new ModelConverter(
						modelWeepingBlue,
						0.065D,
						new TextureMap(64, 64, BLBlockRegistry.weepingBlue.modelTexture1),
						true);
			}
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, 0.0F, rnd.nextFloat()/2.0F - 0.25F);
			plantModelWeepingBlue.getModel().offsetWS(offset).renderWithTessellator(tessellator);
		} else if(block == BLBlockRegistry.sundew && world.getBlock(x, y+1, z) == BLBlockRegistry.sundew) {
			if(plantModelSundew == null) {
				plantModelSundew = new ModelConverter(
						modelSundew,
						0.065D,
						new TextureMap(128, 128, BLBlockRegistry.sundew.topIcon),
						true);
			}
			double scale = rnd.nextFloat() * 0.4D + 0.6D;
			Vec3 offset = new Vec3(rnd.nextFloat()/2.0F - 0.25F, -1.65F + scale/0.65F, rnd.nextFloat()/2.0F - 0.25F);
			plantModelSundew.getModel().scale(scale, scale, scale).rotate(rnd.nextFloat() * 360.0F, 0, 1, 0, new Vec3(0, 0, 0))
			.offsetWS(offset).renderWithTessellator(tessellator);
		}

		tessellator.addTranslation(-x - 0.5F, -y - 1.6F, -z - 0.5F);

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