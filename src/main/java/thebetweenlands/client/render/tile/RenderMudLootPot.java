package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelSunkenLootPot1;
import thebetweenlands.client.render.model.tile.ModelSunkenLootPot2;
import thebetweenlands.client.render.model.tile.ModelSunkenLootPot3;
import thebetweenlands.common.block.container.BlockLootPot;
import thebetweenlands.common.block.container.BlockLootPot.EnumLootPot;
import thebetweenlands.common.block.container.BlockMudLootPot;
import thebetweenlands.common.tile.TileEntityLootPot;
import thebetweenlands.util.StatePropertyHelper;

public abstract class RenderMudLootPot extends TileEntitySpecialRenderer<TileEntityLootPot> {
	private static final ModelSunkenLootPot1 LOOT_POT = new ModelSunkenLootPot1();
	private static final ModelSunkenLootPot2 LOOT_POT_2 = new ModelSunkenLootPot2();
	private static final ModelSunkenLootPot3 LOOT_POT_3 = new ModelSunkenLootPot3();

	private static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/mud_loot_pot_1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/mud_loot_pot_2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/mud_loot_pot_3.png");

	public abstract EnumLootPot getType();

	@Override
	public void render(TileEntityLootPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EnumLootPot type = this.getType();
		EnumFacing rotation = StatePropertyHelper.getStatePropertySafely(te, BlockMudLootPot.class, BlockLootPot.FACING, EnumFacing.NORTH);
		int offset = 0;

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		switch (type) {
		default:
		case POT_1:
			bindTexture(TEXTURE_1);
			break;
		case POT_2:
			bindTexture(TEXTURE_2);
			break;
		case POT_3:
			bindTexture(TEXTURE_3);
			break;

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
		case POT_1:
			LOOT_POT.render();
			break;
		case POT_2:
			LOOT_POT_2.render();
			break;
		case POT_3:
			LOOT_POT_3.render();
			break;
		}
	}
}
