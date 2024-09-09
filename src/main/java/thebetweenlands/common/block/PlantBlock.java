package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.registries.ItemRegistry;

public class PlantBlock extends BushBlock implements FarmablePlant {

	public static final VoxelShape GRASS_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
	public static final VoxelShape FLOWER_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
	private final VoxelShape shape;
	private final boolean moveWithOffset;

	public PlantBlock(VoxelShape shape, boolean moveWithOffset, Properties properties) {
		super(properties);
		this.shape = shape;
		this.moveWithOffset = moveWithOffset;
	}

	@Override
	protected MapCodec<? extends BushBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (this.moveWithOffset) {
			Vec3 vec3 = state.getOffset(level, pos);
			return this.shape.move(vec3.x, vec3.y, vec3.z);
		} else {
			return this.shape;
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.is(ItemRegistry.COMPOST)) {
			pos = pos.below();
			for (int i = 0; i < 3; i++) {
				state = level.getBlockState(pos);
				if (state.getBlock() instanceof DugSoilBlock) {
					return state.useItemOn(stack, level, player, hand, hitResult);
				} else if (!state.is(this)) {
					break;
				}
				pos = pos.below();
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public float getSpreadChance(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return 0.25F;
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return level.isEmptyBlock(targetPos) && state.canSurvive(level, targetPos);
	}

	@Override
	public int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 4;
	}

	@Override
	public void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random) {
		level.destroyBlock(pos, false);
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		level.setBlockAndUpdate(pos, this.defaultBlockState());
	}
}
