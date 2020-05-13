package thebetweenlands.common.world.gen.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite;
import thebetweenlands.common.block.terrain.BlockLifeCrystalStalactite.EnumLifeCrystalType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class OreGens {
	public static final WorldGenerator SULFUR = 
			new WorldGenBLMinable(BlockRegistry.SULFUR_ORE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountSulfur, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator SYRMORITE = 
			new WorldGenBLMinable(BlockRegistry.SYRMORITE_ORE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountSyrmorite, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator BONE_ORE = 
			new WorldGenBLMinable(BlockRegistry.SLIMY_BONE_ORE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountBone, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator OCTINE =
			new WorldGenBLMinable(BlockRegistry.OCTINE_ORE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountOctine, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator SWAMP_DIRT = 
			new WorldGenBLMinable(BlockRegistry.SWAMP_DIRT.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountSwampDirt, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator LIMESTONE = 
			new WorldGenBLMinable(BlockRegistry.LIMESTONE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountLimestone, BlockMatcher.forBlock(BlockRegistry.BETWEENSTONE));

	public static final WorldGenerator VALONITE = 
			new WorldGenBLMinable(BlockRegistry.VALONITE_ORE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountValonite, BlockMatcher.forBlock(BlockRegistry.PITSTONE));

	public static final WorldGenerator SCABYST = 
			new WorldGenBLMinable(BlockRegistry.SCABYST_ORE.getDefaultState(), BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountScabyst, BlockMatcher.forBlock(BlockRegistry.PITSTONE));

	public static final WorldGenerator LIFE_GEM = 
			new WorldGenerator() {
		@Override
		public boolean generate(World world, Random rand, BlockPos pos) {
			if(world.getBlockState(pos).getBlock() == BlockRegistry.SWAMP_WATER && world.getBlockState(pos.down()).getBlock() == BlockRegistry.PITSTONE) {
				boolean genOre = rand.nextInt(BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountLifeGem) == 0;
				int height = 0;
				while(world.getBlockState(pos.add(0, ++height, 0)).getBlock() == BlockRegistry.SWAMP_WATER && height < 8);
				height--;
				if(height >= 2) {
					height = rand.nextInt(Math.min(height - 1, 6)) + 2;
					int oreBlock = rand.nextInt(height);
					for(int i = 0; i <= height; i++) {
						IBlockState blockState = BlockRegistry.LIFE_CRYSTAL_STALACTITE.getDefaultState();
						if(genOre && (i == oreBlock || rand.nextInt(BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockCountLifeGem / 2) == 0))
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
