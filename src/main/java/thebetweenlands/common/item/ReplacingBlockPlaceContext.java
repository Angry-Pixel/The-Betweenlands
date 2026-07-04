package thebetweenlands.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;

public class ReplacingBlockPlaceContext extends BlockPlaceContext {

	public ReplacingBlockPlaceContext(BlockPlaceContext context, BlockPos pos, boolean replace) {
		super(BlockPlaceContext.at(context, pos, context.getClickedFace()));
		this.replaceClicked = replace;
	}
}
