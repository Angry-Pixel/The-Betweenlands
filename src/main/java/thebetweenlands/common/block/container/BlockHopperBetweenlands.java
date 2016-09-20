package thebetweenlands.common.block.container;

import net.minecraft.block.BlockHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;

public class BlockHopperBetweenlands extends BlockHopper {
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHopperBetweenlands();
    }
}
