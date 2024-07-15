package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.block.entity.BeamRelayBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class BeamRelayBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public BeamRelayBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP).setValue(POWERED, false));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide())
			return InteractionResult.SUCCESS;
		if (level.getBlockEntity(pos) instanceof BeamRelayBlockEntity relay) {
			relay.deactivateBlock(level, pos, state);
		}
		state = state.cycle(FACING);
		level.setBlockAndUpdate(pos, state);
		level.playSound(null, pos, SoundRegistry.BEAM_SWITCH.get(), SoundSource.BLOCKS, 0.5F, 1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.9F);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof BeamRelayBlockEntity relay) {
			relay.deactivateBlock(level, pos, state);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BeamRelayBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.BEAM_RELAY.get(), BeamRelayBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(POWERED));
	}
}
