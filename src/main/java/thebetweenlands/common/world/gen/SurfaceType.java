package thebetweenlands.common.world.gen;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.List;

public enum SurfaceType implements Predicate<BlockState> {

	GRASS(ImmutableList.of(
		BlockPredicate.forBlock(Blocks.GRASS_BLOCK)::test,
		BlockPredicate.forBlock(Blocks.MYCELIUM)::test,
		BlockPredicate.forBlock(BlockRegistry.SWAMP_GRASS.get())::test,
		BlockPredicate.forBlock(BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get())::test,
		BlockPredicate.forBlock(BlockRegistry.DEAD_GRASS.get())::test
	)),
	DIRT(ImmutableList.of( //TODO: other dirt blocks from vanilla?
		BlockPredicate.forBlock(Blocks.DIRT)::test,
		BlockPredicate.forBlock(BlockRegistry.SWAMP_DIRT.get())::test,
		BlockPredicate.forBlock(BlockRegistry.PURIFIED_SWAMP_DIRT.get())::test,
		BlockPredicate.forBlock(BlockRegistry.DUG_SWAMP_DIRT.get())::test,
		BlockPredicate.forBlock(BlockRegistry.MUD.get())::test,
		BlockPredicate.forBlock(BlockRegistry.COMPACTED_MUD.get())::test,
		BlockPredicate.forBlock(BlockRegistry.SLUDGY_DIRT.get())::test,
		BlockPredicate.forBlock(BlockRegistry.PEAT.get())::test,
		BlockPredicate.forBlock(BlockRegistry.COARSE_SWAMP_DIRT.get())::test,
		BlockPredicate.forBlock(BlockRegistry.SPREADING_SLUDGY_DIRT.get())::test
	)),
	SAND(ImmutableList.of( //TODO: red sand?
		BlockPredicate.forBlock(Blocks.SAND)::test,
		BlockPredicate.forBlock(BlockRegistry.SILT.get())::test
	)),
	WATER(ImmutableList.of(
		BlockPredicate.forBlock(Blocks.WATER)::test,
		BlockPredicate.forBlock(BlockRegistry.SWAMP_WATER.get())::test
	)),
	PEAT(ImmutableList.of(
		BlockPredicate.forBlock(BlockRegistry.PEAT.get())::test
	)),
	MIXED_GROUND(ImmutableList.of(BlockPredicate.forBlock(BlockRegistry.CRAGROCK.get())::test), GRASS, DIRT, SAND, PEAT),
	UNDERGROUND(ImmutableList.of(
		BlockPredicate.forBlock(BlockRegistry.BETWEENSTONE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.PITSTONE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.LIMESTONE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.OCTINE_ORE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.SCABYST_ORE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.SLIMY_BONE_ORE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.SULFUR_ORE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.SYRMORITE_ORE.get())::test,
		BlockPredicate.forBlock(BlockRegistry.VALONITE_ORE.get())::test
	)),
	GRASS_AND_DIRT(GRASS, DIRT),
	MIXED_GROUND_AND_UNDERGROUND(MIXED_GROUND, UNDERGROUND),
	MIXED_GROUND_OR_REPLACEABLE(ImmutableList.of(BlockBehaviour.BlockStateBase::canBeReplaced), MIXED_GROUND),
	CRAGROCK_MOSSY(ImmutableList.of(
		BlockPredicate.forBlock(BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get())::test,
		BlockPredicate.forBlock(BlockRegistry.MOSSY_CRAGROCK_TOP.get())::test
	)),
	PLANT_DECORATION_SOIL(ImmutableList.of(
		BlockPredicate.forBlock(BlockRegistry.GIANT_ROOT.get())::test),
		GRASS_AND_DIRT, CRAGROCK_MOSSY);

	@Nullable
	private final List<Predicate<BlockState>> matchers;
	private final SurfaceType[] types;

	SurfaceType(@Nullable List<Predicate<BlockState>> matchers, SurfaceType... types) {
		this.matchers = matchers;
		this.types = types;
	}

	SurfaceType(SurfaceType... types) {
		this(null, types);
	}

	@Override
	public boolean apply(BlockState input) {
		if (input == null)
			return false;
		if (this.types != null) {
			for (SurfaceType type : this.types) {
				if (type.apply(input))
					return true;
			}
		}
		if (this.matchers != null) {
			for (Predicate<BlockState> matcher : matchers) {
				if (matcher.apply(input))
					return true;
			}
		}
		return false;
	}

	public boolean matches(Level level, BlockPos pos) {
		return level.isLoaded(pos) && this.apply(level.getBlockState(pos));
	}

	public boolean matches(BlockState state) {
		return this.apply(state);
	}
}
