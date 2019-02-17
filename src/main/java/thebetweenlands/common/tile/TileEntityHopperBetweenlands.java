package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityHopperBetweenlands extends TileEntityHopper {
	//TODO 1.13 BL Hopper can't extend TileEntityHopper because the TileEntityType is hardcoded...
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock(); //Urgh why is this even a thing
	}
	
    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "tile.thebetweenlands.syrmorite_hopper.name";
    }
}
