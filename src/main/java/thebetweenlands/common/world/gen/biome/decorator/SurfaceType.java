package thebetweenlands.common.world.gen.biome.decorator;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public enum SurfaceType implements Predicate<IBlockState> {
	GRASS,
	DIRT,
	SAND,
	WATER,
	PEAT,
	MIXED,
	UNDERGROUND;

	@Override
	public boolean apply(IBlockState input) {
		if(input == null)
			return false;
		Block block = input.getBlock();
		switch (this) {
		case GRASS:
			return block == Blocks.GRASS || block == Blocks.MYCELIUM || block == BlockRegistry.SWAMP_GRASS || 
			block == BlockRegistry.DEAD_GRASS;
		case DIRT:
			return block == BlockRegistry.SWAMP_DIRT || block == Blocks.DIRT || 
			block == BlockRegistry.MUD || block == BlockRegistry.SLUDGY_DIRT ||
			block == BlockRegistry.PEAT;
		case SAND:
			return block == Blocks.SAND || block == BlockRegistry.SILT;
		case WATER:
			return block == BlockRegistry.SWAMP_WATER || block == Blocks.WATER || block == Blocks.FLOWING_WATER;
		case PEAT:
			return block == BlockRegistry.PEAT;
		case MIXED:
			return block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.SAND || 
			block == BlockRegistry.SWAMP_GRASS || block == BlockRegistry.SWAMP_DIRT || 
			block == BlockRegistry.DEAD_GRASS || block == BlockRegistry.MUD || 
			block == BlockRegistry.PEAT || block == BlockRegistry.SLUDGY_DIRT || 
			block == BlockRegistry.SILT;
		case UNDERGROUND:
			return block == BlockRegistry.BETWEENSTONE || block == BlockRegistry.PITSTONE|| block == BlockRegistry.LIMESTONE ||
			block == BlockRegistry.OCTINE_ORE || block == BlockRegistry.SCABYST_ORE || block == BlockRegistry.SLIMY_BONE_ORE ||
			block == BlockRegistry.SULFUR_ORE || block == BlockRegistry.SYRMORITE_ORE || block == BlockRegistry.VALONITE_ORE;
		default:
			return false;
		}
	}

	public boolean matches(World world, BlockPos pos) {
		return this.apply(world.getBlockState(pos));
	}

	public boolean matches(IBlockState state) {
		return this.apply(state);
	}
}
