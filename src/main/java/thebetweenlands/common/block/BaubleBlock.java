package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.world.event.WinterEvent;

public class BaubleBlock extends Block {

	protected static final VoxelShape AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	public static final BooleanProperty DIAGONAL = BooleanProperty.create("diagonal");

	public BaubleBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(DIAGONAL, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		int rotation = Mth.floor(((context.getPlayer().getYRot() + 180.0F) * 8.0F / 360.0F) + 0.5D) & 7;
		return this.defaultBlockState().setValue(DIAGONAL, rotation % 2 == 1);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.isFaceFull(level.getBlockState(pos.above()).getCollisionShape(level, pos.above()), Direction.DOWN);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool);

		if (!level.isClientSide()) {
			ExperienceOrb.award((ServerLevel) level, Vec3.atCenterOf(pos), 1);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!WinterEvent.isFroooosty(level)) {
			level.removeBlock(pos, false);
		}
	}
}
