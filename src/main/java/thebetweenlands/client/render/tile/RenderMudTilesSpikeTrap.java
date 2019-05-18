package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelSpikeBlock;
import thebetweenlands.common.block.structure.BlockMudTilesSpikeTrap;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityMudTilesSpikeTrap;

@SideOnly(Side.CLIENT)
public class RenderMudTilesSpikeTrap extends TileEntitySpecialRenderer<TileEntityMudTilesSpikeTrap> {
	private static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/mud_tiles_spike_block_active.png");
	private static final ResourceLocation INACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/mud_tiles_spike_block_inactive.png");
	private static final ModelSpikeBlock MODEL = new ModelSpikeBlock();

	@Override
	public void render(TileEntityMudTilesSpikeTrap tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		IBlockState state = tile.getWorld().getBlockState(tile.getPos());

		if(state == null || state.getBlock() != BlockRegistry.MUD_TILES_SPIKE_TRAP)
			return;

		EnumFacing facing = state.getValue(BlockMudTilesSpikeTrap.FACING);

		if (tile.type == 1)
			bindTexture(ACTIVE_TEXTURE);
		else
			bindTexture(INACTIVE_TEXTURE);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);

		switch (facing) {
		case UP:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case DOWN:
			GlStateManager.rotate(180F, 1F, 0F, 0F);
			break;
		case NORTH:
			GlStateManager.rotate(90F, 1F, 0F, 0F);
			break;
		case SOUTH:
			GlStateManager.rotate(-90F, 1.0F, 0F, 0F);
			break;
		case WEST:
			GlStateManager.rotate(90F, 0.0F, 0F, 1F);
			break;
		case EAST:
			GlStateManager.rotate(-90F, 0.0F, 0F, 1F);
			break;
		}

		GlStateManager.translate(0F, -1F, 0F);
		GlStateManager.disableCull();
		MODEL.renderSpikes(tile, partialTicks);
		GlStateManager.enableCull();;
		GlStateManager.popMatrix();
	}
}