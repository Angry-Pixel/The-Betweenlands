package thebetweenlands.common.block.structure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;

public class BlockMudBrickSpikeTrap extends BlockSpikeTrap {

	public BlockMudBrickSpikeTrap() {
		super();
		setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.UP));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMudBricksSpikeTrap();
	}
}