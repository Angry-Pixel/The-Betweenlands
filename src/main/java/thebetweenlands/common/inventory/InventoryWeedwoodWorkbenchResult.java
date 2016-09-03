package thebetweenlands.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryCraftResult;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

public class InventoryWeedwoodWorkbenchResult extends InventoryCraftResult {
	private final TileEntityWeedwoodWorkbench tile;

	public InventoryWeedwoodWorkbenchResult(TileEntityWeedwoodWorkbench tile) {
		this.tile = tile;
	}

	@Override
	public void markDirty() {
		this.tile.markDirty();
		IBlockState state = this.tile.getWorld().getBlockState(this.tile.getPos());
		this.tile.getWorld().notifyBlockUpdate(this.tile.getPos(), state, state, 3);
	}
}
