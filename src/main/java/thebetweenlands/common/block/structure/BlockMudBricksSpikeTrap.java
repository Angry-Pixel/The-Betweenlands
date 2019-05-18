package thebetweenlands.common.block.structure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;

public class BlockMudBricksSpikeTrap extends BlockSpikeTrap {

	public BlockMudBricksSpikeTrap() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMudBricksSpikeTrap();
	}
}