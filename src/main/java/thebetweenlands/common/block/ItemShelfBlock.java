package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import thebetweenlands.common.block.entity.ItemShelfBlockEntity;

import javax.annotation.Nullable;
import java.util.Map;

public class ItemShelfBlock extends HorizontalBaseEntityBlock {

	public static final MapCodec<ItemShelfBlock> CODEC = simpleCodec(ItemShelfBlock::new);

	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
		Direction.WEST, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D),
		Direction.SOUTH, Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D),
		Direction.EAST, Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	));

	public ItemShelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (hand == InteractionHand.MAIN_HAND) {
			if (!level.isClientSide()) {
				BlockEntity te = level.getBlockEntity(pos);

				if (te instanceof ItemShelfBlockEntity shelf) {
					InvWrapper wrapper = new InvWrapper(shelf);

					int slot = this.getSlot(state.getValue(FACING), hitResult.getLocation());

					if (!stack.isEmpty()) {
						ItemStack result = wrapper.insertItem(slot, stack, true);
						if (result.isEmpty() || result.getCount() != stack.getCount()) {
							result = wrapper.insertItem(slot, stack.copy(), false);
							level.sendBlockUpdated(pos, state, state, 2);
							if (!player.isCreative()) {
								player.setItemInHand(hand, result);
							}
							level.playSound(null, pos, SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 1, 1);
						}
					}
				}
			}
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	protected int getSlot(Direction blockDir, Vec3 hitVec) {

		Vec3i up = new Vec3i(0, 1, 0);
		Vec3i dir = up.cross(blockDir.getNormal());

		double cx = dir.getX() * hitVec.x() + dir.getZ() * hitVec.z();
		double cy = hitVec.y();

		if (cx <= 0.0D) {
			cx = cx + 1;
		}

		int slot = 0;

		if (cx >= 0.0D && cx <= 0.5D) {
			slot++;
		}

		if (cy >= 0.0D && cy <= 0.5D) {
			slot += 2;
		}

		return slot;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemShelfBlockEntity(pos, state);
	}
}
