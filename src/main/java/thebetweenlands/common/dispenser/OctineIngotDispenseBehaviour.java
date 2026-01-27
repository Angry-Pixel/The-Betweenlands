package thebetweenlands.common.dispenser;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import thebetweenlands.common.item.misc.OctineIngotItem;
import thebetweenlands.common.item.misc.OctineIngotItem.TinderResult;

public class OctineIngotDispenseBehaviour extends OptionalDispenseItemBehavior {
	@Override
	protected ItemStack execute(BlockSource source, ItemStack stack) {
		Level level = source.level();
		Direction facing = source.state().getValue(DispenserBlock.FACING);
		BlockPos pos = source.pos().relative(facing);

		// Check for tinder in front of the dispenser
		TinderResult tinder = OctineIngotItem.getTinder(level, pos, null);
		BlockPos tinderPos = tinder.tinderPos();

		// Check there's tinder and we're not igniting the wrong block
		if(!tinder.hasTinder() || !Objects.equals(tinderPos, pos)) {
			this.setSuccess(false);
			return stack;
		}

		// Check the block can actually be ignited
		if(!tinder.isBlockTinder() && !level.getBlockState(tinderPos).canBeReplaced()) {
			this.setSuccess(false);
			return stack;
		}

		// Ignite the block
		// Note: doesn't check if the fire block can actually be placed,
		//       but the normal octine ingot doesn't either
		this.setSuccess(true);
		level.setBlockAndUpdate(tinder.tinderPos(), Blocks.FIRE.defaultBlockState());

		return stack;
	}
	
	@Override
	protected void playSound(BlockSource blockSource) {
		super.playSound(blockSource);
		if(this.isSuccess()) {
			blockSource.level().playSound(null, blockSource.pos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
	}
}
