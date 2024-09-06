package thebetweenlands.common.inventory.container;

import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.WeedwoodCraftingTableBlockEntity;

public class WeedwoodResultContainer extends ResultContainer {

	private final WeedwoodCraftingTableBlockEntity tile;

	public WeedwoodResultContainer(WeedwoodCraftingTableBlockEntity tile) {
		super();
		this.tile = tile;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		this.tile.setChanged();
		BlockState state = this.tile.getLevel().getBlockState(this.tile.getBlockPos());
		this.tile.getLevel().sendBlockUpdated(this.tile.getBlockPos(), state, state, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
	}

}
