package thebetweenlands.common.tile;

import thebetweenlands.common.block.container.BlockBLDualFurnace;

public class TileEntityBLDualFurnace extends TileEntityAbstractBLFurnace {

	public TileEntityBLDualFurnace() {
		super("container.bl.dual_sulfur_furnace", 2);
	}

	@Override
	protected void updateState(boolean active) {
		BlockBLDualFurnace.setState(active, world, pos);
	}
}
