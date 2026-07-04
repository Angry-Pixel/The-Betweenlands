package thebetweenlands.common.block.farming;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.block.AspectFogBlock;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class AspectrusCropBlock extends DecayableCropBlock implements EntityBlock {

	protected static int ASPECT_FOG_RADIUS = 6;
	protected static int ASPECT_PER_FRUIT = 250;
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
		Block.box(5, 0, 5, 11, 6, 11),
		Block.box(5, 0, 5, 11, 8, 11),
		Block.box(5, 0, 5, 11, 12, 11),
		Block.box(5, 0, 5, 11, 16, 11)
	};
	private static final VoxelShape FENCE_SHAPE = Block.box(6, 0, 6, 10, 16, 10);
	private static final VoxelShape[] SHAPE_BY_STAGE = new VoxelShape[]{
		Shapes.join(SHAPE_BY_AGE[0], FENCE_SHAPE, BooleanOp.OR),
		Shapes.join(SHAPE_BY_AGE[1], FENCE_SHAPE, BooleanOp.OR),
		Shapes.join(SHAPE_BY_AGE[2], FENCE_SHAPE, BooleanOp.OR),
		Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR),
		Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR),
		Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR),
		Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR)
	};

	private static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);

	public AspectrusCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getMaxHeight() {
		return 3;
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return 15;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ItemRegistry.ASPECTRUS_SEEDS;
	}

	@Nullable
	public Aspect getAspect(LevelReader level, BlockPos pos) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof AspectrusCropBlockEntity crop) {
			return crop.getAspect();
		}
		return null;
	}

	public void setAspect(LevelReader level, BlockPos pos, @Nullable Aspect aspect) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof AspectrusCropBlockEntity crop) {
			crop.setAspect(aspect);
		}
	}

	@Nullable
	public AspectrusCropBlockEntity getTile(LevelReader level, BlockPos pos) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof AspectrusCropBlockEntity crop) {
			return crop;
		}
		return null;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.updateAspectSource(level, pos);
		super.randomTick(state, level, pos, random);

		Aspect aspect = this.getAspect(level, pos);

		if (aspect != null && random.nextInt(3) == 0) {
			BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

			checkPos.set(pos.getX(), pos.getY() - 1, pos.getZ());

			for (int i = 0; i < this.getMaxHeight(); i++) {
				BlockState offsetState = level.getBlockState(checkPos);

				if (offsetState.getBlock() instanceof DugSoilBlock soil) {
					if (!soil.isPurified(level, checkPos, offsetState)) {
						DugSoilBlockEntity te = (DugSoilBlockEntity) level.getBlockEntity(checkPos);

						if (te != null && !te.isFullyDecayed()) {
							te.setDecay(level, checkPos, te.getDecay() + 5);
						}
					}

					break;
				}

				checkPos.set(checkPos.getX(), checkPos.getY() - 1, checkPos.getZ());
			}
		}
	}

	protected void updateAspectSource(Level level, BlockPos pos) {
		boolean hasSource = false;

		Aspect aspect = this.getAspect(level, pos);

		for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-ASPECT_FOG_RADIUS, -this.getMaxHeight(), -ASPECT_FOG_RADIUS), pos.offset(ASPECT_FOG_RADIUS, 0, ASPECT_FOG_RADIUS))) {
			if (level.isLoaded(checkPos)) {
				BlockState offsetState = level.getBlockState(checkPos);
				Block offsetBlock = offsetState.getBlock();

				if (offsetBlock instanceof AspectFogBlock fog) {
					Holder<AspectType> aspectType = fog.getAspectFogType(level, checkPos, offsetState);

					if (aspectType != null) {
						if (aspect != null) {
							if (aspect.type() == aspectType) {
								hasSource = true;
								break;
							}
						} else {
							this.setAspect(level, pos, new Aspect(aspectType, ASPECT_PER_FRUIT));
							hasSource = true;
							break;
						}
					}
				}
			}
		}

		if (level.getBlockEntity(pos) instanceof AspectrusCropBlockEntity crop) {
			crop.setHasSource(hasSource);
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_STAGE[getMagicIndex(state)];
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return FENCE_SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AspectrusCropBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.ASPECTRUS_CROP.get(), AspectrusCropBlockEntity::tick);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
		return clientType == serverType ? (BlockEntityTicker<A>)ticker : null;
	}

	@Override
	protected boolean canGrow(LevelReader level, BlockPos pos, BlockState state) {
		AspectrusCropBlockEntity tile = this.getTile(level, pos);
		return tile != null && tile.getAspect() != null && tile.hasSource();
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	protected boolean canGrowUp(LevelReader level, BlockPos pos, BlockState state, int height) {
		if (this.getMaxHeight() == -1 || height < this.getMaxHeight()) {
			BlockPos posUp = pos.above();
			BlockState upState = level.getBlockState(posUp);
			if (!upState.getFluidState().isEmpty()) return false;
			if (upState.getBlock() instanceof FenceBlock) {
				for (Direction dir : Direction.Plane.HORIZONTAL) {
					BlockPos neighborPos = posUp.relative(dir);
					BlockState neighborState = level.getBlockState(neighborPos);

					if (!neighborState.getCollisionShape(level, neighborPos).isEmpty()) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void growUp(Level level, BlockPos pos) {
		BlockState fence = level.getBlockState(pos.above());
		super.growUp(level, pos);

		Aspect aspect = this.getAspect(level, pos);
		if (aspect != null) {
			BlockPos posUp = pos.above();

			AspectrusCropBlockEntity tile = this.getTile(level, posUp);
			if (tile != null) {
				tile.setAspect(aspect);
				tile.setHasSource(true);
				tile.setFence(fence);
			}
		}
	}

	@Override
	protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		BlockEntity tileentity = level.getBlockEntity(pos);
		return tileentity != null && tileentity.triggerEvent(id, param);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			AspectrusCropBlockEntity crop = this.getTile(level, pos);
			if (crop != null && crop.getFence() != null) {
				super.onRemove(state, level, pos, newState, movedByPiston);
				level.setBlockAndUpdate(pos, crop.getFence());
			} else {
				super.onRemove(state, level, pos, newState, movedByPiston);
				level.setBlockAndUpdate(pos, BlockRegistry.RUBBER_TREE_FENCE.get().defaultBlockState());
			}
		}
	}

	public static int getMagicIndex(BlockState state) {
		int index = state.getValue(BlockRegistry.ASPECTRUS_CROP.get().getAgeProperty()) / 2;
		if (index <= 4) {
			index = index / 2;
		} else {
			index = index - 2;
		}
		if (index >= 5 && state.getValue(DecayableCropBlock.DECAYED)) {
			index = 6;
		}
		return index;
	}
}
