package thebetweenlands.common.world.gen.biome.decorator;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.List;

public enum SurfaceType implements Predicate<BlockState> {
    GRASS(ImmutableList.of(
            BlockStatePredicate.forBlock(Blocks.GRASS),
            BlockStatePredicate.forBlock(Blocks.MYCELIUM),
            BlockStatePredicate.forBlock(BlockRegistry.SWAMP_GRASS.get()),
            BlockStatePredicate.forBlock(BlockRegistry.DEAD_SWAMP_GRASS.get())
            //BlockMatcher.forBlock(BlockRegistry.DUG_PURIFIED_SWAMP_GRASS),
    )),
    DIRT(ImmutableList.of(
            BlockStatePredicate.forBlock(BlockRegistry.SWAMP_DIRT.get()),
            //BlockMatcher.forBlock(BlockRegistry.PURIFIED_SWAMP_DIRT),
            //BlockMatcher.forBlock(BlockRegistry.DUG_SWAMP_DIRT),
            BlockStatePredicate.forBlock(Blocks.DIRT),
            BlockStatePredicate.forBlock(BlockRegistry.MUD.get()),//,
            //BlockMatcher.forBlock(BlockRegistry.COMPACTED_MUD),
            //BlockMatcher.forBlock(BlockRegistry.SLUDGY_DIRT),
            BlockStatePredicate.forBlock(BlockRegistry.PEAT.get())
            //BlockMatcher.forBlock(BlockRegistry.COARSE_SWAMP_DIRT),
            //BlockMatcher.forBlock(BlockRegistry.SPREADING_SLUDGY_DIRT)
    )),
    SAND(ImmutableList.of(
            BlockStatePredicate.forBlock(Blocks.SAND),
            BlockStatePredicate.forBlock(BlockRegistry.SILT.get())
    )),
    WATER(ImmutableList.of(
            BlockStatePredicate.forBlock(BlockRegistry.SWAMP_WATER_BLOCK.get()),
            BlockStatePredicate.forBlock(Blocks.WATER)
    )),
    PEAT(ImmutableList.of(
            BlockStatePredicate.forBlock(BlockRegistry.PEAT.get())
    )),
    MIXED_GROUND(ImmutableList.of(BlockStatePredicate.forBlock(BlockRegistry.CRAGROCK.get())), GRASS, DIRT, SAND, PEAT),
    UNDERGROUND(ImmutableList.of(
            BlockStatePredicate.forBlock(BlockRegistry.BETWEENSTONE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.PITSTONE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.LIMESTONE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.OCTINE_ORE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.SCABYST_ORE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.SLIMY_BONE_ORE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.SULFUR_ORE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.SYRMORITE_ORE.get()),
            BlockStatePredicate.forBlock(BlockRegistry.VALONITE_ORE.get())
    )),
            GRASS_AND_DIRT(GRASS, DIRT),
            MIXED_GROUND_AND_UNDERGROUND(MIXED_GROUND, UNDERGROUND),
            //MIXED_GROUND_OR_REPLACEABLE(ImmutableList.of(state -> state.getMaterial().isReplaceable()), MIXED_GROUND),
            //CRAGROCK_MOSSY(ImmutableList.of(state -> state.getBlock() == BlockRegistry.CRAGROCK.get() && state.getValue(BlockCragrock.VARIANT) != BlockCragrock.EnumCragrockType.DEFAULT)),
            PLANT_DECORATION_SOIL(ImmutableList.of(
                    BlockStatePredicate.forBlock(BlockRegistry.GIANT_ROOT.get())),
                    GRASS_AND_DIRT/*, CRAGROCK_MOSSY*/);

    private final List<BlockStatePredicate> matchers;
    private final SurfaceType types[];
    private SurfaceType(@Nullable ImmutableList<BlockStatePredicate> matchers, SurfaceType... types) {
        this.matchers = matchers;
        this.types = types;
    }

    private SurfaceType(SurfaceType... types) {
        this(null, types);
    }

    @Override
    public boolean apply(BlockState input) {
        if(input == null)
            return false;
        if(this.types != null && this.types.length > 0){
            for(SurfaceType type : this.types)
                if(type.apply(input))
                    return true;
        }
        if(this.matchers != null) {
            for(BlockStatePredicate matcher : this.matchers) {
                if(matcher.test(input))
                    return true;
            }
        }
        return false;
    }

    public boolean matches(WorldGenLevel world, BlockPos pos) {
        return this.apply(world.getBlockState(pos));
    }

    public boolean matches(BlockState state) {
        return this.apply(state);
    }
}