package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelPossessedBlock;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.tile.TileEntityPossessedBlock;

@SideOnly(Side.CLIENT)
public class TileEntityPossessedBlockRenderer extends TileEntitySpecialRenderer<TileEntityPossessedBlock> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/possessedBlock.png");
	private final ModelPossessedBlock model = new ModelPossessedBlock();

	@SuppressWarnings("incomplete-switch")
	@Override
	public final void renderTileEntityAt(TileEntityPossessedBlock tile, double x, double y, double z, float partialTicks, int destroyStage) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		EnumFacing facing = state.getValue(BlockPossessedBlock.FACING);

		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);

		switch (facing) {
		case NORTH:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case SOUTH:
			GlStateManager.rotate(180F, 0.0F, 1F, 0F);
			break;
		case WEST:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		case EAST:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		}

		model.render(tile);
		GlStateManager.popMatrix();
	}
}