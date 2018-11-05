package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelDungeonDoorRunesLayer;
import thebetweenlands.common.block.structure.BlockDungeonDoorCombination;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityDungeonDoorCombination;

@SideOnly(Side.CLIENT)
public class RenderDungeonDoorCombination extends TileEntitySpecialRenderer<TileEntityDungeonDoorCombination> {

	private static final ModelDungeonDoorRunesLayer RUNE_BLOCK_LAYER = new ModelDungeonDoorRunesLayer();

	private static final ResourceLocation TEXTURE_1 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_3.png");
	private static final ResourceLocation TEXTURE_4 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_4.png");
	private static final ResourceLocation TEXTURE_5 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_5.png");
	private static final ResourceLocation TEXTURE_6 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_6.png");
	private static final ResourceLocation TEXTURE_7 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_7.png");
	private static final ResourceLocation TEXTURE_8 = new ResourceLocation(ModInfo.ID, "textures/tiles/rune_overlay_8.png");

	private static final ResourceLocation TEXTURE_RUNE_GLOW = new ResourceLocation(ModInfo.ID, "textures/tiles/dungeon_runes_glow.png");
	
    @Override
    public void render (TileEntityDungeonDoorCombination tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		if (state == null || state.getBlock() != BlockRegistry.DUNGEON_DOOR_COMBINATION)
			return;
		EnumFacing facing = state.getValue(BlockDungeonDoorCombination.FACING);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		switch (facing) {
		case UP:
		case DOWN:
		case NORTH:
			GlStateManager.rotate(180F, 0F, 1F, 0F);
			break;
		case SOUTH:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case WEST:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		case EAST:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		bindTexture(getTextureFromRotationIndex(tile.top_code));
		RUNE_BLOCK_LAYER.renderTopOverlay(tile, TEXTURE_RUNE_GLOW, tile.renderTicks, 0.062505F, partialTick);

		bindTexture(getTextureFromRotationIndex(tile.mid_code));
		RUNE_BLOCK_LAYER.renderMidOverlay(tile, TEXTURE_RUNE_GLOW, tile.renderTicks, 0.062505F, partialTick);

		bindTexture(getTextureFromRotationIndex(tile.bottom_code));
		RUNE_BLOCK_LAYER.renderBottomOverlay(tile, TEXTURE_RUNE_GLOW, tile.renderTicks, 0.062505F, partialTick);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}


    public ResourceLocation getTextureFromRotationIndex(int index) {
		switch (index) {
		case 0 :
			return TEXTURE_1;
		case 1 :
			return TEXTURE_2;
		case 2 :
			return TEXTURE_3;
		case 3 :
			return TEXTURE_4;
		case 4 :
			return TEXTURE_5;
		case 5 :
			return TEXTURE_6;
		case 6 :
			return TEXTURE_7;
		case 7 :
			return TEXTURE_8;
		}
		return TEXTURE_1;
	}
}