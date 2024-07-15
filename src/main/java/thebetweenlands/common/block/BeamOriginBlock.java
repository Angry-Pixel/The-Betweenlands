package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.BeamOriginBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class BeamOriginBlock extends BaseEntityBlock {

	private static final VoxelShape SHAPE = Block.box(-2.0D, -8.0D, -2.0D, 18.0D, 19.0D, 18.0D);
	private static final VoxelShape COLLISION_SHAPE = Block.box(-2.0D, -2.0D, -2.0D, 18.0D, 19.0D, 18.0D);

	public static final MapCodec<BeamOriginBlock> CODEC = simpleCodec(BeamOriginBlock::new);
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");


	public BeamOriginBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return COLLISION_SHAPE;
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof BeamOriginBlockEntity beam) {
			beam.deactivateBlock(level, pos, state);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BeamOriginBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.BEAM_ORIGIN.get(), BeamOriginBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}
}
