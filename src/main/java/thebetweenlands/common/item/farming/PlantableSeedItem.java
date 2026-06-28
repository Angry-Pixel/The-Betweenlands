package thebetweenlands.common.item.farming;

import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.farming.DugSoilBlock;

import java.util.function.Predicate;

public class PlantableSeedItem extends ItemNameBlockItem {

	protected final Predicate<BlockState> soilMatcher;

	public PlantableSeedItem(Block block, Predicate<BlockState> soilMatcher, Properties properties) {
		super(block, properties);
		this.soilMatcher = soilMatcher;
	}

	public static Predicate<BlockState> nonDecayedSoil() {
		return state -> state.getBlock() instanceof DugSoilBlock && !state.getValue(DugSoilBlock.DECAYED);
	}

	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		return this.soilMatcher.test(context.getLevel().getBlockState(context.getClickedPos().below())) && super.canPlace(context, state);
	}
}
