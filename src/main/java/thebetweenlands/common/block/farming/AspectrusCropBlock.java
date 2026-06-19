package thebetweenlands.common.block.farming;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.block.AspectFogBlock;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class AspectrusCropBlock extends DecayableCropBlock implements EntityBlock {
    protected static int ASPECT_FOG_RADIUS = 6;
    protected static int ASPECT_PER_FRUIT = 250;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{ 
        Block.box(4, 0, 4, 12, 6, 12),
        Block.box(3, 0, 3, 13, 8, 13),
        Block.box(2, 0, 2, 13+1, 12, 13+1),
        Block.box(1, 0, 1, 15, 16, 15),
    };
    private static final VoxelShape FENCE_SHAPE = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape[] SHAPE_BY_STAGE = new VoxelShape[]{
        Shapes.join(SHAPE_BY_AGE[0], FENCE_SHAPE, BooleanOp.OR),
        Shapes.join(SHAPE_BY_AGE[1], FENCE_SHAPE, BooleanOp.OR),
        Shapes.join(SHAPE_BY_AGE[2], FENCE_SHAPE, BooleanOp.OR),
        Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR),
        Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR),
        Shapes.join(SHAPE_BY_AGE[3], FENCE_SHAPE, BooleanOp.OR),
    };

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_STAGE[this.getAge(state)];
    }

    public AspectrusCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxHeight() {
        return 3;
    }

    @Override
	public int getMaxAge() {
		return 5;
	}

    @Override
    protected ItemLike getBaseSeedId() {
        return ItemRegistry.ASPECTRUS_SEEDS;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.updateAspectSource(level, pos, state);
        super.randomTick(state, level, pos, random);
    }

    protected void updateAspectSource(Level level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof AspectrusCropBlockEntity tile)) return;

        boolean hasSource = false;
        Aspect aspect = tile.getAspect();

        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-ASPECT_FOG_RADIUS, -this.getMaxHeight(), -ASPECT_FOG_RADIUS), pos.offset(ASPECT_FOG_RADIUS, 0, ASPECT_FOG_RADIUS))) {
            BlockState offsetState = level.getBlockState(checkPos);
            if (offsetState.getBlock() instanceof AspectFogBlock fog) {
                Holder<AspectType> fogType = fog.getAspectFogType(level, checkPos, offsetState);
                if (fogType != null) {
                    if (aspect != null) {
                        if (aspect.type().equals(fogType)) {
                            hasSource = true;
                            break;
                        }
                    } else {
                        tile.setAspect(new Aspect(fogType, ASPECT_PER_FRUIT));
                        hasSource = true;
                        break;
                    }
                }
            }
        }

        tile.setHasSource(hasSource);
    }

    @Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AspectrusCropBlockEntity(pos, state);
	}
    
    @Override
    protected boolean canGrow(LevelReader level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof AspectrusCropBlockEntity tile) {
            // TheBetweenlands.LOGGER.warn("canGrow called at " + pos + " with result " + ((tile.getAspect() != null && tile.hasSource()) && super.canGrow(level, pos, state)));
            return (tile.getAspect() != null && tile.hasSource()) && super.canGrow(level, pos, state);
        }
        // TheBetweenlands.LOGGER.warn("canGrow called at " + pos + " but no block entity present");
        return false;
    }

    @Override
    protected boolean canGrowUp(LevelReader level, BlockPos pos, BlockState state, int height) {
		return level.getBlockState(pos.above()).getBlock() instanceof RubberTreeFenceBlock && (this.getMaxHeight() == -1 || height < this.getMaxHeight());
	}

    @Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return FENCE_SHAPE;
	}

    private static final ThreadLocal<Boolean> BREAKING_PILLAR = ThreadLocal.withInitial(() -> false);

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                    boolean willHarvest, FluidState fluid) {
        if (BREAKING_PILLAR.get()) {
            return false; // already breaking the pillar of crops
        }
        BREAKING_PILLAR.set(true);
        try {
            BlockPos base = findCropStackBase(level, pos);
            boolean shouldUpdateSoil = this.getAge(state) >= this.getMaxAge() || base.getY() != pos.getY();
            breakEntireCropStack(level, base);
            if (shouldUpdateSoil && level instanceof ServerLevel) {
                this.harvestAndUpdateSoil(level, base, 10); // base is directly above soil
            }
            return false;
        } finally {
            BREAKING_PILLAR.set(false);
        }
    }

    @Override
    public void onDestroyedByPuddles(ServerLevel level, BlockPos pos, BlockState puddleState) {
        BlockPos base = findCropStackBase(level, pos);
        boolean shouldUpdateSoil = this.getAge(level.getBlockState(base)) >= this.getMaxAge() || base.getY() != pos.getY(); 
        breakEntireCropStack(level, base);
        if (shouldUpdateSoil && level instanceof ServerLevel) {
            this.harvestAndUpdateSoil(level, base, 10); // base is directly above soil
        }
    }

    private static BlockPos findCropStackBase(LevelReader level, BlockPos pos) {
        BlockPos.MutableBlockPos check = pos.mutable();
        while (level.getBlockState(check.below()).getBlock() instanceof AspectrusCropBlock) {
            check.move(Direction.DOWN);
        }
        return check.immutable();
    }

    private void breakEntireCropStack(Level level, BlockPos base) {
        BlockPos.MutableBlockPos check = base.mutable();
        for (int i = 0; i < this.getMaxHeight(); i++) {
            BlockState cropState = level.getBlockState(check);
            if (!(cropState.getBlock() instanceof AspectrusCropBlock)) {
                break;
            }
            if (level instanceof ServerLevel serverLevel) {
                dropResources(cropState, serverLevel, check.immutable(), level.getBlockEntity(check));
            }
            level.setBlock(check, BlockRegistry.RUBBER_TREE_FENCE.get().defaultBlockState(), 3);
            check.move(Direction.UP);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (!state.canSurvive(level, currentPos)) {
            if (level instanceof ServerLevel serverLevel) {
                dropResources(state, serverLevel, currentPos, serverLevel.getBlockEntity(currentPos));
            }
            return BlockRegistry.RUBBER_TREE_FENCE.get().defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
    
}
