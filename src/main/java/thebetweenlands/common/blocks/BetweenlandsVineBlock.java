package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Random;

// uses multyface code and adds vine like effects like growth
public class BetweenlandsVineBlock extends BetweenlandsMultifaceBlock {

	public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

	public BetweenlandsVineBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultState());
	}

	public BlockState defaultState() {
		return this.defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(PERSISTENT, false);
	}

	@Override
	public BlockState updateShape(BlockState blockstate, Direction p_60542_, BlockState p_60543_, LevelAccessor level, BlockPos blockpos, BlockPos p_60546_) {

		level.scheduleTick(blockpos, this, 0);

		// Find unsuported faces
		for (Direction direction : Direction.values()) {
			// Face destroy conditions
			BlockState aboveState = level.getBlockState(blockpos.above());

			boolean faceNotSupportedByBlock = blockstate.getValue(PROPERTY_BY_DIRECTION.get(direction)) && (!level.getBlockState(blockpos.relative(direction)).isFaceSturdy(level, blockpos.relative(direction), direction.getOpposite()));
			boolean faceSupportedByUpFace = blockstate.getValue(PROPERTY_BY_DIRECTION.get(Direction.UP)) && direction != Direction.DOWN && direction != Direction.UP;
			boolean faceSupportedByAboveBlock = aboveState.is(this) && aboveState.getValue(PROPERTY_BY_DIRECTION.get(direction));

			if (faceNotSupportedByBlock && !faceSupportedByUpFace && !faceSupportedByAboveBlock) {
				blockstate = blockstate.setValue(PROPERTY_BY_DIRECTION.get(direction), false);
			}
		}

		// Apply all changes at once
		level.setBlock(blockpos, blockstate, UPDATE_ALL);

		return blockstate;
	}

	@Override
	public void randomTick(BlockState p_60551_, ServerLevel p_60552_, BlockPos p_60553_, RandomSource p_60554_) {
		// Stop ticking if cant grow anymore
		if (p_60551_.getValue(PERSISTENT)) {
			return;
		}

		int grow = p_60554_.nextInt(10);

		// On grow
		if (grow == 0 && !p_60551_.getValue(PROPERTY_BY_DIRECTION.get(Direction.DOWN))) {
			// Init
			int faceVal[] = {0, 0, 0, 0};
			int faceCount = 0;

			// Check faces
			for (int dir = 0; dir <= 3; dir++) {
				if (p_60551_.getValue(PROPERTY_BY_DIRECTION.get(Direction.from2DDataValue(dir)))) {
					faceVal[faceCount] = dir;
					faceCount++;
				}
			}

			// If block dosent have side face make one
			if (faceCount == 0) {
				p_60552_.setBlockAndUpdate(p_60553_, p_60551_.setValue(PROPERTY_BY_DIRECTION.get(Direction.from2DDataValue(p_60554_.nextInt(3) + 1)), true));
			}
			// If block does have side face grow down a random one
			else {
				if (p_60552_.getBlockState(p_60553_.below()) == Blocks.AIR.defaultBlockState()) {
					// If block to be replaced already is of this type
					BlockState beloworiginstate = p_60552_.getBlockState(p_60553_.below());
					if (beloworiginstate.is(this)) {
						p_60552_.setBlockAndUpdate(p_60553_.below(), beloworiginstate.setValue(PROPERTY_BY_DIRECTION.get(Direction.from2DDataValue(faceVal[p_60554_.nextInt(faceCount)])), true));
					} else {
						p_60552_.setBlockAndUpdate(p_60553_.below(), p_60551_.getBlock().defaultBlockState().setValue(PROPERTY_BY_DIRECTION.get(Direction.from2DDataValue(faceVal[p_60554_.nextInt(faceCount)])), true));
					}
				}
			}
		}
	}

	@Override
	public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {

		// Destroy if out of faces
		if (this.countFaces(p_60462_) == 0) {
			p_60463_.destroyBlock(p_60464_, false);
			return;
		}

		// Lock block random ticking
		if (p_60463_.getBlockState(p_60464_.below()) == Blocks.AIR.defaultBlockState()) {
			p_60463_.setBlockAndUpdate(p_60464_, p_60462_.setValue(PERSISTENT, false));
		} else {
			p_60463_.setBlockAndUpdate(p_60464_, p_60462_.setValue(PERSISTENT, true));
		}
	}

	// Add propertys to block
	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153309_) {
		p_153309_.add(PERSISTENT);
		super.createBlockStateDefinition(p_153309_);
	}
}
