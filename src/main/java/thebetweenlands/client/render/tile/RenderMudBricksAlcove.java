package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelMudBricksAlcove;
import thebetweenlands.common.block.container.BlockMudBricksAlcove;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityMudBricksAlcove;

@SideOnly(Side.CLIENT)
public class RenderMudBricksAlcove extends TileEntitySpecialRenderer<TileEntityMudBricksAlcove> {

	private final ModelMudBricksAlcove ALCOVE = new ModelMudBricksAlcove();
	private final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/tiles/mud_bricks_alcove.png");

	public void renderTile(TileEntityMudBricksAlcove tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		if(state == null || state.getBlock() != BlockRegistry.MUD_BRICKS_ALCOVE)
			return;
		EnumFacing facing = state.getValue(BlockMudBricksAlcove.FACING);
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
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
		ALCOVE.render(tile, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	public void render(TileEntityMudBricksAlcove tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(tile == null || !tile.hasWorld()) {
			renderTileAsItem(x, y, z);
			return;
		}
		renderTile(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	private void renderTileAsItem(double x, double y, double z) {
		GlStateManager.pushMatrix();
		bindTexture(TEXTURE);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		ALCOVE.renderItem(0.0625F);
		GlStateManager.popMatrix();
	}
}