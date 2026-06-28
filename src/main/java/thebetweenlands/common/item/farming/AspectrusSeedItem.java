package thebetweenlands.common.item.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.item.ReplacingBlockPlaceContext;
import thebetweenlands.common.registries.BlockRegistry;

public class AspectrusSeedItem extends PlantableSeedItem {

    public AspectrusSeedItem(Properties properties) {
        super(BlockRegistry.ASPECTRUS_CROP.get(), nonDecayedSoil(), properties);
    }

	@Nullable
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
		BlockPos checkPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
		if (context.getLevel().getBlockState(checkPos).getBlock() instanceof FenceBlock) {
			return new ReplacingBlockPlaceContext(context, checkPos, true);
		}
		return null;
	}

	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		return this.soilMatcher.test(context.getLevel().getBlockState(context.getClickedPos().below())) && state.canSurvive(context.getLevel(), context.getClickedPos());
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		BlockState fence = context.getLevel().getBlockState(context.getClickedPos());
		boolean ret = super.placeBlock(context, state);
		if (ret) {
			BlockEntity entity = context.getLevel().getBlockEntity(context.getClickedPos());
			if (entity instanceof AspectrusCropBlockEntity crop) {
				crop.setFence(fence);
			}
		}
		return ret;
	}
}