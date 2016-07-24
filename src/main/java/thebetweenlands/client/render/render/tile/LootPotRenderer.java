package thebetweenlands.client.render.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelLootPot1;
import thebetweenlands.client.render.model.tile.ModelLootPot2;
import thebetweenlands.client.render.model.tile.ModelLootPot3;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.tile.TileEntityLootPot;

public class LootPotRenderer extends TileEntitySpecialRenderer<TileEntityLootPot> {

	private final ModelLootPot1 LOOT_POT = new ModelLootPot1();
	private final ModelLootPot2 LOOT_POT_2 = new ModelLootPot2();
	private final ModelLootPot3 LOOT_POT_3 = new ModelLootPot3();

	private final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/loot_pot_1.png");
	private final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/loot_pot_2.png");
	private final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/loot_pot_3.png");

	@Override
	public void renderTileEntityAt(TileEntityLootPot te, double x, double y, double z, float partialTicks, int destroyStage) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		EnumLootPot type = state.getValue(BlockLootPot.VARIANT);
		EnumFacing rotation = state.getValue(BlockLootPot.FACING);
		int offset = te.getModelRotationOffset();

		switch (type){
		default:
		case POT_1: {
			bindTexture(TEXTURE_1);
			break;
		}
		case POT_2: {
			bindTexture(TEXTURE_2);
			break;
		}
		case POT_3: {
			bindTexture(TEXTURE_3);
			break;
		}
		}
		switch (rotation) {
		default:
		case NORTH:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case EAST:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 90.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case SOUTH:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 180.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		case WEST:
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.5F, z + 0.5D);
			GlStateManager.scale(1F, -1F, -1F);
			GlStateManager.rotate(offset + 270.0F, 0.0F, 1F, 0F);
			renderType(type);
			GlStateManager.popMatrix();
			break;
		}
	}

	private void renderType(EnumLootPot type){
		switch (type){
		case POT_1: {
			LOOT_POT.render();
			break;
		}
		case POT_2: {
			LOOT_POT_2.render();
			break;
		}
		case POT_3: {
			LOOT_POT_3.render();
			break;
		}
		}
	}

}
