package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite.EnumLifeCrystalType;
import thebetweenlands.common.registries.BlockRegistry;

public class OreGens {
	public static final WorldGenerator SULFUR = 
			new WorldGenMinableBL(BlockRegistry.SULFUR_ORE.getDefaultState(), 14, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator SYRMORITE = 
			new WorldGenMinableBL(BlockRegistry.SYRMORITE_ORE.getDefaultState(), 9, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator BONE_ORE = 
			new WorldGenMinableBL(BlockRegistry.SLIMY_BONE_ORE.getDefaultState(), 11, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator OCTINE = 
			new WorldGenMinableBL(BlockRegistry.OCTINE_ORE.getDefaultState(), 8, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator SWAMP_DIRT = 
			new WorldGenMinableBL(BlockRegistry.SWAMP_DIRT.getDefaultState(), 25, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator LIMESTONE = 
			new WorldGenMinableBL(BlockRegistry.LIMESTONE.getDefaultState(), 100, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator VALONITE = 
			new WorldGenMinableBL(BlockRegistry.VALONITE_ORE.getDefaultState(), 5, BlockMatcher.forBlock(BlockRegistry.PITSTONE));

	public static final WorldGenerator SCABYST = 
			new WorldGenMinableBL(BlockRegistry.SCABYST_ORE.getDefaultState(), 6, BlockMatcher.forBlock(BlockRegistry.PITSTONE));

	public static final WorldGenerator LIFE_GEM = 
			new WorldGenerator() {
		@Override
		public boolean generate(World world, Random rand, BlockPos pos) {
			if(world.getBlockState(pos).getBlock() == BlockRegistry.SWAMP_WATER && world.getBlockState(pos.down()).getBlock() == BlockRegistry.PITSTONE) {
				boolean genOre = rand.nextInt(36) == 0;
				int height = 0;
				while(world.getBlockState(pos.add(0, ++height, 0)).getBlock() == BlockRegistry.SWAMP_WATER && height < 8);
				height--;
				if(height >= 2) {
					height = rand.nextInt(Math.min(height - 1, 6)) + 2;
					int oreBlock = rand.nextInt(height);
					for(int i = 0; i <= height; i++) {
						IBlockState blockState = BlockRegistry.LIFE_CRYSTAL_STALACTITE.getDefaultState();
						if(genOre && (i == oreBlock || rand.nextInt(18) == 0))
							blockState = blockState.withProperty(BlockLifeCrystalStalactite.VARIANT, EnumLifeCrystalType.ORE);
						world.setBlockState(pos.add(0, i, 0), blockState);
					}
					return true;
				}
			}
			return false;
		}
	};
}
