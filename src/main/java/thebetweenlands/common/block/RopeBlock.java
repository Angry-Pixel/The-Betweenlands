package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class RopeBlock extends Block {

	public static final EnumProperty<RopeVariant> VARIANT = EnumProperty.create("variant", RopeVariant.class);
	protected static final VoxelShape SHAPE = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);

	public RopeBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState above = level.getBlockState(pos.above());
		return above.isFaceSturdy(level, pos.above(), Direction.DOWN) || above.is(this);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? this.getRopeState(this.defaultBlockState(), level, pos) : Blocks.AIR.defaultBlockState();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.getRopeState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
	}

	private BlockState getRopeState(BlockState state, LevelReader level, BlockPos pos) {
		state = state.setValue(VARIANT, RopeVariant.MIDDLE);

		if (!level.getBlockState(pos.above()).is(this)) {
			if (level.getBlockState(pos.below()).is(this) || level.getBlockState(pos.below()).getBlock() instanceof LanternBlock) {
				state = state.setValue(VARIANT, RopeVariant.TOP);
			} else {
				state = state.setValue(VARIANT, RopeVariant.SINGLE);
			}
		} else if (!level.getBlockState(pos.below()).is(this)) {
			if (level.getBlockState(pos.below()).getBlock() instanceof LanternBlock) {
				state = state.setValue(VARIANT, RopeVariant.MIDDLE);
			} else {
				state = state.setValue(VARIANT, RopeVariant.BOTTOM);
			}
		}

		return state;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (player.isCrouching()) {
			BlockPos offsetPos = pos.below();
			while (level.getBlockState(offsetPos).is(this)) {
				offsetPos = offsetPos.below();
			}
			offsetPos = offsetPos.above();
			if (offsetPos.getY() != pos.getY()) {
				if (!level.isClientSide()) {
					level.removeBlock(offsetPos, false);

					if (!player.isCreative() && !player.getInventory().add(new ItemStack(this.asItem()))) {
						player.drop(new ItemStack(this.asItem()), false);
					}
				}

				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(VARIANT);
	}

	public enum RopeVariant implements StringRepresentable {
		SINGLE,
		TOP,
		MIDDLE,
		BOTTOM;

		private final String name;

		RopeVariant() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
