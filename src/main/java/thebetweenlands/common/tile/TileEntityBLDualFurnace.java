package thebetweenlands.common.tile;

import thebetweenlands.common.block.container.BlockBLDualFurnace;

public class TileEntityBLDualFurnace extends TileEntityAbstractBLFurnace {

	public TileEntityBLDualFurnace() {
		super("dual_furnace_bl", 2);
	}

	@Override
	protected void updateState(boolean active) {
		BlockBLDualFurnace.setState(active, world, pos);
	}
}
