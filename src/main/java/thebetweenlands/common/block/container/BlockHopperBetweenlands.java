package thebetweenlands.common.block.container;

import net.minecraft.block.BlockHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;
import thebetweenlands.util.AdvancedStateMap;

public class BlockHopperBetweenlands extends BlockHopper implements BlockRegistry.IStateMappedBlock {
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHopperBetweenlands();
    }

    @Override
    public void setStateMapper(AdvancedStateMap.Builder builder) {
        builder.ignore(ENABLED);
    }
}
