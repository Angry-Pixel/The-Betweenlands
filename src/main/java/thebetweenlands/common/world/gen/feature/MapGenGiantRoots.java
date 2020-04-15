package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.IBlockStateAccessOnly;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;

public class MapGenGiantRoots extends MapGenBase {
	protected CoarseIslandsFeature coarseIslandsFeature = new CoarseIslandsFeature();

	protected List<WorldGenGiantRoot> giantRootGens = new ArrayList<>();

	protected Set<Biome> biomes;
	
	public MapGenGiantRoots(long worldSeed, Set<Biome> biomes) {
		this.range = 5;
		this.biomes = biomes;
		this.coarseIslandsFeature.initializeGenerators(worldSeed, BiomeRegistry.COARSE_ISLANDS);
	}

	@Override
	public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
		this.giantRootGens.clear();

		super.generate(worldIn, x, z, primer);

		for(WorldGenGiantRoot root : this.giantRootGens) {
			root.setGenBounds(new AxisAlignedBB(x * 16, 0, z * 16, x * 16 + 15, 256, z * 16 + 15));
			Random rootRand = new Random();
			rootRand.setSeed(root.start.getX() ^ root.start.getY() ^ root.start.getZ() ^ worldIn.getSeed());
			root.generate(IBlockStateAccessOnly.from(x, z, primer), x, z, true, rootRand, root.start);
		}
	}

	@Override
	protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn) {
		int subDivs = 2;
		int step = 16 / subDivs;

		//this.coarseIslandsFeature.generateNoise(chunkX, chunkZ, BiomeRegistry.COARSE_ISLANDS);

		List<BlockPos> startCandidates = new ArrayList<>();

		int inChunkX = 0;
		int inChunkZ = 0;
		for(int xs = 0; xs < subDivs; xs++) {
			for(int zs = 0; zs < subDivs; zs++) {
				if(this.biomes.contains(worldIn.getBiomeProvider().getBiome(new BlockPos(chunkX * 16 + inChunkX, 64, chunkZ * 16 + inChunkZ))) && this.rand.nextInt(28) == 0 /*&& this.coarseIslandsFeature.isIslandAt(inChunkX, inChunkZ, 1) && !this.coarseIslandsFeature.isIslandCragrockAt(inChunkX, inChunkZ)*/) {
					BlockPos candidate = new BlockPos(chunkX * 16 + inChunkX, WorldProviderBetweenlands.LAYER_HEIGHT - 6, chunkZ * 16 + inChunkZ);
					startCandidates.add(candidate);
				}

				inChunkZ += step;
			}
			inChunkX += step;
		}

		Collections.shuffle(startCandidates, this.rand);

		for(BlockPos startCandidate : startCandidates) {
			BlockPos endCandidate = this.findEndpoint(startCandidate.getX() >> 4, startCandidate.getZ() >> 4, startCandidate, 2);

			if(endCandidate != null) {
				WorldGenGiantRoot gen = new WorldGenGiantRoot(startCandidate, endCandidate, this.rand.nextInt(11) == 0 ? 35 : 14);
				if(gen.start.distanceSq(gen.end) >= 60*60 && this.rand.nextInt(15) == 0) {
					gen.setMaxWidth(4).setMinWidth(4);
				}
				this.giantRootGens.add(gen);
			}
		}
	}

	@Nullable
	protected BlockPos findEndpoint(int chunkX, int chunkZ, BlockPos start, int chunkRange) {
		List<BlockPos> endCandidates = new ArrayList<>();

		int subDivs = 4;
		int step = 16 / subDivs;

		for (int newChunkX = chunkX - chunkRange; newChunkX <= chunkX + chunkRange; ++newChunkX) {
			for (int newChunkZ = chunkZ - chunkRange; newChunkZ <= chunkZ + chunkRange; ++newChunkZ) {

				//this.coarseIslandsFeature.generateNoise(newChunkX, newChunkZ, BiomeRegistry.COARSE_ISLANDS);

				int inChunkX = 0;
				int inChunkZ = 0;
				for(int xs = 0; xs < subDivs; xs++) {
					for(int zs = 0; zs < subDivs; zs++) {
						if(this.rand.nextInt(100) == 0 /*&& this.coarseIslandsFeature.isIslandAt(inChunkX, inChunkZ, 1) && !this.coarseIslandsFeature.isIslandCragrockAt(inChunkX, inChunkZ)*/) {
							endCandidates.add(new BlockPos(newChunkX * 16 + inChunkX, WorldProviderBetweenlands.LAYER_HEIGHT - 6, newChunkZ * 16 + inChunkZ));
						}

						inChunkZ += step;
					}
					inChunkX += step;
				}

			}
		}

		if(!endCandidates.isEmpty()) {
			Collections.shuffle(endCandidates, this.rand);

			for(BlockPos endCandidate : endCandidates) {
				if(endCandidate.distanceSq(start) >= 40*40) {
					return endCandidate; //TODO Check if end candidate is already used?
				}
			}
		}

		return null;
	}
}
