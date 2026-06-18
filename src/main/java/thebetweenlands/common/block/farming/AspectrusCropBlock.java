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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.block.AspectFogBlock;
import thebetweenlands.common.block.entity.AspectrusCropBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class AspectrusCropBlock extends DecayableCropBlock implements EntityBlock {
    protected static int ASPECT_FOG_RADIUS = 6; //not sure if this is defined elsewhere
    protected static int ASPECT_PER_FRUIT = 250;

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

    // @Override
    // protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    //     if (!state.is(newState.getBlock())) {
    //         super.onRemove(state, level, pos, newState, movedByPiston);
    //     }
    // }
    
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
		return Block.box(6, 0, 6, 10, 16, 10);
	}

    @Override 
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean removed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        level.setBlock(pos, BlockRegistry.RUBBER_TREE_FENCE.get().defaultBlockState(), level.isClientSide ? 11 : 3);
        return removed;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos)
            ? BlockRegistry.RUBBER_TREE_FENCE.get().defaultBlockState()
            : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
    
}
