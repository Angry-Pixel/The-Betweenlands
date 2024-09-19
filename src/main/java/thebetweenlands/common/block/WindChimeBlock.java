package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.block.entity.WindChimeBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class WindChimeBlock extends BaseEntityBlock {

	public static final VoxelShape SHAPE = Shapes.or(
		Block.box(7.5F, 14.0D, 7.5F, 8.5F, 16.0F, 8.5F),
		Block.box(6.5F, 13.0D, 6.5F, 9.5F, 14.0F, 9.5F),
		Block.box(6.0F, 11.0D, 6.0F, 10.0F, 13.0F, 10.0F),
		Block.box(5.5F, 1.0D, 5.5F, 10.5F, 11.0F, 10.5F)
	);

	public WindChimeBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.above()).isFaceSturdy(level, pos.below(), Direction.DOWN);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (player.isShiftKeyDown() && !level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof WindChimeBlockEntity chime) {

				ResourceLocation newAttunement = chime.cycleAttunedEvent(level);

				IEnvironmentEvent attunedEvent;
				if (newAttunement != null) {
					attunedEvent = BetweenlandsWorldStorage.getOrThrow(level).getEnvironmentEventRegistry().getEvent(newAttunement);
				} else {
					attunedEvent = null;
				}

				if (newAttunement != null) {
					player.displayClientMessage(Component.translatable("chat.wind_chime.changed_attunement", Component.translatable(attunedEvent.getDescriptionId())), true);
				} else {
					player.displayClientMessage(Component.translatable("chat.wind_chime.removed_attunement"), true);
				}
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WindChimeBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.WIND_CHIME.get(), WindChimeBlockEntity::tick);
	}
}
