package thebetweenlands.common.world.gen.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageUUID;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.block.terrain.BlockCragrock.EnumCragrockType;
import thebetweenlands.common.block.terrain.BlockHanger;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.IBlockStateAccessOnly;
import thebetweenlands.common.world.gen.biome.feature.CoarseIslandsFeature;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class MapGenFloatingIslands extends MapGenBase {
	protected CoarseIslandsFeature coarseIslandsFeature = new CoarseIslandsFeature();

	protected List<BlockPos> islands = new ArrayList<>();

	protected List<WorldGenGiantRoot> giantRootGens = new ArrayList<>();

	private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];

	private NoiseGeneratorPerlin cragNoiseGen;
	private double[] cragNoise = new double[256];

	public MapGenFloatingIslands(long worldSeed) {
		this.range = 5;
		Random rng = new Random(worldSeed);
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 3);
		this.cragNoiseGen = new NoiseGeneratorPerlin(rng, 5);
	}

	@Override
	public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
		this.islands.clear();
		this.giantRootGens.clear();

		super.generate(worldIn, x, z, primer);

		for(BlockPos island : this.islands) {
			//early skip islands outside range
			if(island.getDistance(x * 16, island.getY(), z * 16) <= 52) {
				Random rand = new Random();
				rand.setSeed(island.getX() ^ island.getY() ^ island.getZ() ^ worldIn.getSeed());
				this.generateIsland(island, rand, x, z, primer);
			}
		}

		for(WorldGenGiantRoot root : this.giantRootGens) {
			root.setGenBounds(new AxisAlignedBB(x * 16, 0, z * 16, x * 16 + 15, 256, z * 16 + 15));
			Random rootRand = new Random();
			rootRand.setSeed(root.start.getX() ^ root.start.getY() ^ root.start.getZ() ^ worldIn.getSeed());
			root.generate(IBlockStateAccessOnly.from(x, z, primer), x, z, true, rootRand, root.start);
		}
	}

	@Override
	protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn) {
		if(this.rand.nextInt(80) == 0) {
			int inChunkX = this.rand.nextInt(16);
			int inChunkZ = this.rand.nextInt(16);

			if(worldIn.getBiomeProvider().getBiome(new BlockPos(chunkX * 16 + inChunkX, 64, chunkZ * 16 + inChunkZ)) == BiomeRegistry.RAISED_ISLES) {
				List<BlockPos> surfaceRootCandidates = new ArrayList<>();
				List<BlockPos> islandRootCandidates = new ArrayList<>();

				BlockPos islandCandidate = new BlockPos(chunkX * 16 + inChunkX, WorldProviderBetweenlands.LAYER_HEIGHT + 25 + this.rand.nextInt(40), chunkZ * 16 + inChunkZ);

				int numRoots = this.rand.nextInt(4) + 3;
				for(int i = 0; i < numRoots; i++) {
					int range = (i + 1) * 5 + 10;
					surfaceRootCandidates.add(islandCandidate.add(this.rand.nextInt(range) - range / 2, -6, this.rand.nextInt(range) - range / 2));
				}

				islandRootCandidates.add(islandCandidate.add(this.rand.nextInt(32) - 16, 1, this.rand.nextInt(32) - 16));

				int numSmallRoots = this.rand.nextInt(3);
				for(int i = 0; i < numSmallRoots; i++) {
					BlockPos start = islandCandidate.add(this.rand.nextInt(32) - 16, 1, this.rand.nextInt(32) - 16);
					BlockPos end = islandCandidate.add(this.rand.nextInt(32) - 16, 1, this.rand.nextInt(32) - 16);
					this.giantRootGens.add(new WorldGenGiantRoot(start, end, 1, 1.5D, 3, 2, 7, false, false, true, true, null));
				}

				Collections.shuffle(surfaceRootCandidates, this.rand);
				Collections.shuffle(islandRootCandidates, this.rand);

				this.islands.add(islandCandidate);

				for(BlockPos start : surfaceRootCandidates) {
					BlockPos end = this.findSurfaceEndpoint(start.getX() >> 4, start.getZ() >> 4, start, 2);

					if(end != null) {
						this.giantRootGens.add(new WorldGenGiantRoot(start, end, 2, 4, 10, 7, -12, true, false, true, true, null));
					}
				}

				for(BlockPos start : islandRootCandidates) {
					BlockPos end = this.findIslandEndpoint(start.getX() >> 4, start.getZ() >> 4, start, 3);

					if(end != null) {
						this.giantRootGens.add(new WorldGenGiantRoot(start, end, 1, 2, 6, 4, 25, false, false, true, true, null));
					}
				}
			}
		}
	}

	protected void generateIsland(BlockPos center, Random rand, int chunkX, int chunkZ, ChunkPrimer primer) {
		long locationSeed = rand.nextLong(); //Need to always generate the seed otherwise world gen desyncs in other chunks
		
		if(center.getX() >> 4 == chunkX && center.getZ() >> 4 == chunkZ) {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(this.world);
			LocationStorage locationStorage = new LocationStorage(worldStorage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(center), "floating_island", EnumLocationType.FLOATING_ISLAND);
			locationStorage.addBounds(new AxisAlignedBB(center).grow(30, 15, 30).offset(0, 8, 0));
			locationStorage.setVisible(false);
			locationStorage.setSeed(locationSeed);
			locationStorage.setDirty(true);
			worldStorage.getLocalStorageHandler().addLocalStorage(locationStorage);
		}
		
		double[] islandMask = new double[4096];

		int i = rand.nextInt(4) + 6;

		float size = rand.nextFloat();

		double minSize = 20 + size * 8;
		double maxSize = 40 + size * 18;

		for(int round = 0; round < i; ++round) {
			double wx = rand.nextDouble() * (maxSize - minSize) + minSize;
			double wz = rand.nextDouble() * (maxSize - minSize) + minSize;
			double mx = 32 + (rand.nextDouble() - 0.5D) * wx * 0.5D;
			double mz = 32 + (rand.nextDouble() - 0.5D) * wz * 0.5D;

			for(int ix = 1; ix < 63; ++ix) {
				for(int iz = 1; iz < 63; ++iz) {
					double dx = ((double)ix - mx) / wx;
					double dz = ((double)iz - mz) / wz;
					double d = dx * dx + dz * dz;

					islandMask[ix * 64 + iz] = Math.max(islandMask[ix * 64 + iz], 1 - d);
				}
			}
		}

		int relX = center.getX() - chunkX * 16;
		int relZ = center.getZ() - chunkZ * 16;

		this.islandNoise = this.islandNoiseGen.getRegion(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.4D, 0.4D, 1.0D);
		this.cragNoise = this.cragNoiseGen.getRegion(this.cragNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.55D, 0.55D, 1.0D);

		double minVal = 0.8D;

		boolean hasPond = rand.nextInt(3) == 0;

		for(int bx = 0; bx < 16; ++bx) {
			for(int bz = 0; bz < 16; ++bz) {
				int ix = bx - relX + 32;
				int iz = bz - relZ + 32;

				if(ix >= 0 && ix < 64 && iz >= 0 && iz < 64 && islandMask[ix * 64 + iz] > minVal) {
					double islandNoiseVal = this.islandNoise[bz * 16 + bx] / 0.9f + 2.1f;

					double cragNoise = this.cragNoise[bz * 16 + bx] / 2.1f + 2.0f;

					boolean isCrag = cragNoise <= 0;

					double islandMaskVal = islandMask[ix * 64 + iz] - minVal;

					int depth = MathHelper.ceil(islandMaskVal * islandMaskVal * 200 + islandNoiseVal * islandNoiseVal * islandMaskVal);
					int height = MathHelper.floor(islandMaskVal * 12.0f - islandNoiseVal * islandMaskVal * 0.1f + (cragNoise < 0.2f ? 1 : 0));

					int lowering = 0;
					if(hasPond && islandMaskVal * 12.0f > 1.5f) {
						lowering = MathHelper.ceil((islandMaskVal * 12.0f - 1.5f) * 5.0f);
					}

					int water = -1;

					int surface = center.getY();
					for(; surface > 0; surface--) {
						IBlockState state = primer.getBlockState(bx, surface, bz);

						boolean liquid = state.getMaterial().isLiquid();

						if(liquid && water < 0) {
							water = surface;
						}

						if(state.getBlock() != Blocks.AIR && !liquid) {
							break;
						}
					}

					for(int y = 0; y > -depth; y--) {
						if(!primer.getBlockState(bx, surface, bz).getMaterial().isLiquid()) {
							if(surface + y <= WorldProviderBetweenlands.LAYER_HEIGHT) {
								primer.setBlockState(bx, surface + y, bz, BlockRegistry.SWAMP_WATER.getDefaultState());
							} else {
								primer.setBlockState(bx, surface + y, bz, Blocks.AIR.getDefaultState());
							}
						}
					}

					boolean isPondColumn = height - lowering < 0 && lowering > 0;

					for(int y = height - lowering; y > -depth - lowering; y--) {
						int by = center.getY() + y;

						if(isCrag) {
							if(y == height - lowering) {
								primer.setBlockState(bx, by, bz, BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_1));
							} else if(y == height - lowering - 1) {
								primer.setBlockState(bx, by, bz, BlockRegistry.CRAGROCK.getDefaultState().withProperty(BlockCragrock.VARIANT, EnumCragrockType.MOSSY_2));
							} else if(y == height - lowering - 2) {
								primer.setBlockState(bx, by, bz, BlockRegistry.CRAGROCK.getDefaultState());
							} else {
								primer.setBlockState(bx, by, bz, BlockRegistry.BETWEENSTONE.getDefaultState());
							}
						} else {
							if(isPondColumn) {
								primer.setBlockState(bx, by, bz, BlockRegistry.MUD.getDefaultState());
							} else {
								if(y == height - lowering) {
									primer.setBlockState(bx, by, bz, BlockRegistry.SWAMP_GRASS.getDefaultState());
								} else if(y >= height - lowering - 2) {
									primer.setBlockState(bx, by, bz, BlockRegistry.SWAMP_DIRT.getDefaultState());
								} else {
									primer.setBlockState(bx, by, bz, BlockRegistry.BETWEENSTONE.getDefaultState());
								}
							}
						}
					}

					if(isPondColumn) {
						for(int y = 0; y > height - lowering; y--) {
							int by = center.getY() + y;

							primer.setBlockState(bx, by, bz, BlockRegistry.SWAMP_WATER.getDefaultState());
						}
					}

					if(rand.nextInt(20) == 0 && height + depth <= 3 && !isCrag) {
						int rootLength = 1 + rand.nextInt(12);

						for(int y = -depth - lowering; y > -depth - rootLength - lowering; y--) {
							int by = center.getY() + y;

							primer.setBlockState(bx, by, bz, BlockRegistry.ROOT.getDefaultState());
						}
					} else if(rand.nextInt(13) == 0) {
						int hangerLength = 1 + rand.nextInt(8);

						for(int y = -depth - lowering; y > -depth - hangerLength - lowering; y--) {
							int by = center.getY() + y;

							primer.setBlockState(bx, by, bz, BlockRegistry.HANGER.getDefaultState().withProperty(BlockHanger.CAN_GROW, false));
						}
					} else if(rand.nextInt(400) == 0 && water > 0 && height + depth >= 4) {
						primer.setBlockState(bx, center.getY() - depth - lowering + 3, bz, BlockRegistry.SWAMP_WATER.getDefaultState());
						for(int by = center.getY() - depth - lowering + 2; by > water; by--) {
							//vertical flowing fluid has meta 1
							primer.setBlockState(bx, by, bz, BlockRegistry.SWAMP_WATER.getDefaultState().withProperty(BlockSwampWater.LEVEL, 1));
						}
					}
				}
			}
		}
	}

	@Nullable
	protected BlockPos findIslandEndpoint(int chunkX, int chunkZ, BlockPos start, int chunkRange) {
		List<BlockPos> endCandidates = new ArrayList<>();

		int subDivs = 2;
		int step = 16 / subDivs;

		for (int newChunkX = chunkX - chunkRange; newChunkX <= chunkX + chunkRange; ++newChunkX) {
			for (int newChunkZ = chunkZ - chunkRange; newChunkZ <= chunkZ + chunkRange; ++newChunkZ) {

				int inChunkX = 0;
				int inChunkZ = 0;
				for(int xs = 0; xs < subDivs; xs++) {
					for(int zs = 0; zs < subDivs; zs++) {
						if(this.rand.nextInt(60) == 0) {
							int wx = newChunkX * 16 + inChunkX;
							int wz = newChunkZ * 16 + inChunkZ;

							for(BlockPos island : this.islands) {
								if(!island.equals(start)) {
									int dx = island.getX() - wx;
									int dz = island.getZ() - wz;

									if((dx*dx + dz*dz) >> 4 <= 2) {
										endCandidates.add(new BlockPos(newChunkX * 16 + inChunkX, island.getY(), newChunkZ * 16 + inChunkZ));
									}
								}
							}
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
				if(endCandidate.distanceSq(start) >= 20*20) {
					return endCandidate; //TODO Check if end candidate is already used?
				}
			}
		}

		return null;
	}

	@Nullable
	protected BlockPos findSurfaceEndpoint(int chunkX, int chunkZ, BlockPos start, int chunkRange) {
		List<BlockPos> endCandidates = new ArrayList<>();

		int subDivs = 4;
		int step = 16 / subDivs;

		for (int newChunkX = chunkX - chunkRange; newChunkX <= chunkX + chunkRange; ++newChunkX) {
			for (int newChunkZ = chunkZ - chunkRange; newChunkZ <= chunkZ + chunkRange; ++newChunkZ) {

				int inChunkX = 0;
				int inChunkZ = 0;
				for(int xs = 0; xs < subDivs; xs++) {
					for(int zs = 0; zs < subDivs; zs++) {
						if(this.rand.nextInt(60) == 0) {
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
