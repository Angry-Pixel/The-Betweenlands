package thebetweenlands.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlantableFoodItem extends Item {

	private final Block block;

	public PlantableFoodItem(Block block, Properties properties) {
		super(properties);
		this.block = block;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		BlockState block = level.getBlockState(pos);
		boolean isReplacing = block.canBeReplaced(new BlockPlaceContext(context));
		BlockPos facingOffset = pos.relative(context.getClickedFace());
		if (isReplacing || (level.isEmptyBlock(facingOffset) || level.getBlockState(facingOffset).canBeReplaced())) {
			BlockPos newPos = isReplacing ? pos : facingOffset;
			block = level.getBlockState(newPos);
			BlockState placeBlock = this.block.defaultBlockState();
			if (block != placeBlock && placeBlock.canSurvive(level, newPos)) {
				if (!level.isClientSide()) {
					level.setBlockAndUpdate(newPos, placeBlock);
					level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, placeBlock.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (placeBlock.getSoundType().getVolume() + 1.0F) / 2.0F, placeBlock.getSoundType().getPitch() * 0.8F);
					stack.shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.FAIL;
	}
}
