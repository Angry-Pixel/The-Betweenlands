package thebetweenlands.common.world.gen.biome.decorator;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.registries.BlockRegistry;

public enum SurfaceType implements Predicate<IBlockState> {
	GRASS(ImmutableList.of(
			BlockMatcher.forBlock(Blocks.GRASS),
			BlockMatcher.forBlock(Blocks.MYCELIUM),
			BlockMatcher.forBlock(BlockRegistry.SWAMP_GRASS),
			BlockMatcher.forBlock(BlockRegistry.DUG_PURIFIED_SWAMP_GRASS),
			BlockMatcher.forBlock(BlockRegistry.DEAD_GRASS)
			)),
	DIRT(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.SWAMP_DIRT),
			BlockMatcher.forBlock(BlockRegistry.PURIFIED_SWAMP_DIRT),
			BlockMatcher.forBlock(BlockRegistry.DUG_SWAMP_DIRT),
			BlockMatcher.forBlock(Blocks.DIRT),
			BlockMatcher.forBlock(BlockRegistry.MUD),
			BlockMatcher.forBlock(BlockRegistry.COMPACTED_MUD),
			BlockMatcher.forBlock(BlockRegistry.SLUDGY_DIRT),
			BlockMatcher.forBlock(BlockRegistry.PEAT),
			BlockMatcher.forBlock(BlockRegistry.COARSE_SWAMP_DIRT),
			BlockMatcher.forBlock(BlockRegistry.SPREADING_SLUDGY_DIRT),
			BlockMatcher.forBlock(BlockRegistry.MOULDY_SOIL)
			)),
	SAND(ImmutableList.of(
			BlockMatcher.forBlock(Blocks.SAND),
			BlockMatcher.forBlock(BlockRegistry.SILT)
			)),
	WATER(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.SWAMP_WATER),
			BlockMatcher.forBlock(Blocks.WATER),
			BlockMatcher.forBlock(Blocks.FLOWING_WATER)
			)),
	PEAT(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.PEAT)
			)),
	MIXED_GROUND(ImmutableList.of(BlockMatcher.forBlock(BlockRegistry.CRAGROCK)), GRASS, DIRT, SAND, PEAT),
	UNDERGROUND(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE),
			BlockMatcher.forBlock(BlockRegistry.PITSTONE),
			BlockMatcher.forBlock(BlockRegistry.LIMESTONE),
			BlockMatcher.forBlock(BlockRegistry.OCTINE_ORE),
			BlockMatcher.forBlock(BlockRegistry.SCABYST_ORE),
			BlockMatcher.forBlock(BlockRegistry.SLIMY_BONE_ORE),
			BlockMatcher.forBlock(BlockRegistry.SULFUR_ORE),
			BlockMatcher.forBlock(BlockRegistry.SYRMORITE_ORE),
			BlockMatcher.forBlock(BlockRegistry.VALONITE_ORE)
			)),
	GRASS_AND_DIRT(GRASS, DIRT),
	MIXED_GROUND_AND_UNDERGROUND(MIXED_GROUND, UNDERGROUND),
	MIXED_GROUND_OR_REPLACEABLE(ImmutableList.of(state -> state.getMaterial().isReplaceable()), MIXED_GROUND),
	CRAGROCK_MOSSY(ImmutableList.of(state -> state.getBlock() == BlockRegistry.CRAGROCK && state.getValue(BlockCragrock.VARIANT) != BlockCragrock.EnumCragrockType.DEFAULT)),
	PLANT_DECORATION_SOIL(ImmutableList.of(
			BlockMatcher.forBlock(BlockRegistry.GIANT_ROOT)),
			GRASS_AND_DIRT, CRAGROCK_MOSSY);

	private final List<Predicate<IBlockState>> matchers;
	private final SurfaceType types[];

	private SurfaceType(@Nullable List<Predicate<IBlockState>> matchers, SurfaceType... types) {
		this.matchers = matchers;
		this.types = types;
	}

	private SurfaceType(SurfaceType... types) {
		this(null, types);
	}

	@Override
	public boolean apply(IBlockState input) {
		if(input == null)
			return false;
		if(this.types != null && this.types.length > 0){
			for(SurfaceType type : this.types)
				if(type.apply(input))
					return true;
		}
		if(this.matchers != null) {
			for(Predicate<IBlockState> matcher : this.matchers) {
				if(matcher.apply(input))
					return true;
			}
		}
		return false;
	}

	public boolean matches(World world, BlockPos pos) {
		return world.isBlockLoaded(pos) && this.apply(world.getBlockState(pos));
	}

	public boolean matches(IBlockState state) {
		return this.apply(state);
	}
}
