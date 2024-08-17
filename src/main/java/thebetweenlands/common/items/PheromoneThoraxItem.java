package thebetweenlands.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.WeedwoodBushBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class PheromoneThoraxItem extends Item {
	public PheromoneThoraxItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);
		if (!level.isClientSide() && state.getBlock() instanceof WeedwoodBushBlock) {
			level.levelEvent(null, 2001, pos, Block.getId(BlockRegistry.WEEDWOOD_BUSH.get().defaultBlockState()));
			level.setBlockAndUpdate(pos, BlockRegistry.DEAD_WEEDWOOD_BUSH.get().defaultBlockState());
//			Swarm swarm = new Swarm(level); TODO
//			swarm.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//			level.addFreshEntity(swarm);
			context.getItemInHand().consume(1, context.getPlayer());
			return InteractionResult.SUCCESS;
		}
		return super.useOn(context);
	}
}
