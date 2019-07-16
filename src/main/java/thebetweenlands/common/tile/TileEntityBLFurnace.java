package thebetweenlands.common.tile;

import thebetweenlands.common.block.container.BlockBLFurnace;

public class TileEntityBLFurnace extends TileEntityAbstractBLFurnace {

    public TileEntityBLFurnace() {
        super("furnace_bl", 1);
    }

    @Override
    protected void updateState(boolean active) {
        BlockBLFurnace.setState(active, world, pos);
    }
}
