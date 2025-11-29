package thebetweenlands.common.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import thebetweenlands.api.world.IBetweenlandsBiomeSource;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.warp.BLLegacyBlendedNoise;
import thebetweenlands.common.world.gen.warp.BLNoiseInterpolator;
import thebetweenlands.common.world.gen.warp.NoiseModifier;
import thebetweenlands.common.world.gen.warp.NoiseSlider;
import thebetweenlands.common.world.gen.warp.TerrainWarper;
import thebetweenlands.util.IBetweenlandsRandomStateExtension;

public class BetweenlandsChunkGenerator extends NoiseBasedChunkGenerator {
	public static final MapCodec<BetweenlandsChunkGenerator> BL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		BiomeSource.CODEC.fieldOf("biome_source").forGetter((object) -> object.biomeSource),
		NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((object) -> object.settings)
	).apply(instance, instance.stable(BetweenlandsChunkGenerator::new)));

	protected final Holder<NoiseGeneratorSettings> settings;
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	protected final Climate.Sampler sampler;
	protected final TerrainWarper warper;
	private final int cellWidth;
	private final int cellHeight;

	// TODO extra settings
	
	public BetweenlandsChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
		super(biomeSource, settings);

		// net.minecraft.server.level.ChunkMap gets NoiseGeneratorSettings from this class, and passes it to RandomState.create(...)
		// RandomState's constructor does settings.getRandomSource().newInstance(levelSeed).forkPositional();
		// with legacy_random_source set in the noise generator settings, this should be a LegacyPositionalRandomFactory
		
		// Info on the blended noise from 1.12.2:
		// lower noise receives a random with the world seed
		// upper noise receives a random with the world seed advanced 16 * 262 = 4192 values
		// blend noise receives a random with the world seed advanced 16 * 262 * 2 = 8384 values
		
		this.settings = settings;
		if (settings.isBound()) {
//			NoiseGeneratorSettings settingsValue = settings.value();
			NoiseSettings noise = settings.value().noiseSettings();
			this.defaultBlock = settings.value().defaultBlock();
			this.defaultFluid = settings.value().defaultFluid();
			this.cellWidth = noise.getCellWidth();
			this.cellHeight = noise.getCellHeight();
			NoiseSlider topSlide = new NoiseSlider(-10.0D, 3, 0);
			NoiseSlider bottomSlide = new NoiseSlider(15.0D, 3, 0);
			BLLegacyBlendedNoise blend = BLLegacyBlendedNoise.createUnseeded(8.0F, 8.0F, 80.0F, 160.0F, 1.0D);
			this.warper = new TerrainWarper(this.cellWidth, this.cellHeight, noise.height() / this.cellHeight, biomeSource, noise, topSlide, bottomSlide, NoiseModifier.PASS, blend);
		} else {
			this.defaultBlock = BlockRegistry.BETWEENSTONE.get().defaultBlockState();
			this.defaultFluid = BlockRegistry.SWAMP_WATER.get().defaultBlockState();
			this.cellWidth = 0;
			this.cellHeight = 0;
			this.warper = null;
		}
		this.sampler = new Climate.Sampler(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), List.of()); //dummy value
	}

	@Override
	protected MapCodec<? extends ChunkGenerator> codec() {
		return BL_CODEC;
	}

	@Override
	public CompletableFuture<ChunkAccess> createBiomes(RandomState random, Blender blender, StructureManager manager, ChunkAccess access) {
		return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
			access.fillBiomesFromNoise(this.getBiomeSource(), Climate.empty());
			return access;
		}), Util.backgroundExecutor());
	}

	@Override
	public ChunkAccess doFill(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess access, int min, int max) {
		int cellCountX = 16 / this.cellWidth;
		int cellCountZ = 16 / this.cellWidth;
		Heightmap oceanfloor = access.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
		Heightmap surface = access.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
		ChunkPos chunkpos = access.getPos();
		int minX = chunkpos.getMinBlockX();
		int minZ = chunkpos.getMinBlockZ();
		TerrainWarper terrainWarper = this.warper.usingSeed(IBetweenlandsRandomStateExtension.getLevelSeed(random));
		BLNoiseInterpolator interpolator = new BLNoiseInterpolator(cellCountX, max, cellCountZ, chunkpos, min, (double[] _columns, int _x, int _z, int _min, int _max) -> this.fillNoiseColumn(terrainWarper, _columns, _x, _z, _min, _max));
		List<BLNoiseInterpolator> list = Lists.newArrayList(interpolator);
		list.forEach(BLNoiseInterpolator::initialiseFirstX);

		for (int cellX = 0; cellX < cellCountX; cellX++) {
			int advX = cellX;
			list.forEach((noiseint) -> noiseint.advanceX(advX));

			for (int cellZ = 0; cellZ < cellCountZ; cellZ++) {
				int sections = access.getSectionsCount() - 1;
				LevelChunkSection section = access.getSection(sections);

				for (int cellY = max - 1; cellY >= 0; cellY--) {
					int advY = cellY;
					int advZ = cellZ;
					list.forEach((noiseint) -> noiseint.selectYZ(advY, advZ));

					for(int height = this.cellHeight - 1; height >= 0; height--) {
						int minheight = (min + cellY) * this.cellHeight + height;
						int mincellY = minheight & 15;
						int minindexY = access.getSectionIndex(minheight);

						if (sections != minindexY) {
							sections = minindexY;
							section = access.getSection(minindexY);
						}

						double heightdiv = (double)height / (double)this.cellHeight;
						list.forEach((noiseint) -> noiseint.updateY(heightdiv));

						for (int widthX = 0; widthX < this.cellWidth; widthX++) {
							int minwidthX = minX + cellX * this.cellWidth + widthX;
							int mincellX = minwidthX & 15;
							double widthdivX = (double)widthX / (double)this.cellWidth;
							list.forEach((noiseint) -> noiseint.updateX(widthdivX));

							for (int widthZ = 0; widthZ < this.cellWidth; widthZ++) {
								int minwidthZ = minZ + cellZ * this.cellWidth + widthZ;
								int mincellZ = minwidthZ & 15;
								double widthdivZ = (double)widthZ / (double)this.cellWidth;
								double noiseval = interpolator.updateZ(widthdivZ);
								BlockState state = this.generateBaseState(noiseval, minheight);

								if (state != Blocks.AIR.defaultBlockState()) {
									section.setBlockState(mincellX, mincellY, mincellZ, state, false);
									oceanfloor.update(mincellX, minheight, mincellZ, state);
									surface.update(mincellX, minheight, mincellZ, state);
								}
							}
						}
					}
				}
			}

			list.forEach(BLNoiseInterpolator::swapSlices);
		}

		access = applyEarlyGenerators(blender, structureManager, random, access, oceanfloor, surface, min, max);
		
		return access;
	}
	
	protected ChunkAccess applyEarlyGenerators(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess access, Heightmap oceanfloorHeightmap, Heightmap surfaceHeightmap, int min, int max) {
		if(this.biomeSource instanceof IBetweenlandsBiomeSource biomeSource) {
			Set<Holder<Biome>> biomeSet = getBiomeSet(access, min * this.cellHeight, max * this.cellHeight);
			
			// TODO feature sorting
			
			boolean hasFeatures = false;
			List<ConfiguredEarlyGenerator<?, ?>> rootGenerators = new ArrayList<>(biomeSet.size());
			for (Holder<Biome> holder : biomeSet) {
				HolderSet<ConfiguredEarlyGenerator<?, ?>> generators = biomeSource.getBiomeGenerators(holder);
				if(generators.size() != 0) {
					hasFeatures = true;
					generators.stream().map(Holder::value).forEach(rootGenerators::add);
				}
			}
			
			if(hasFeatures) {
				
			}
		}
		
		return access;
	}

	protected Set<Holder<Biome>> getBiomeSet(ChunkAccess access, int minBlockY, int maxBlockY) {
		final int minSectionIndex = 
			Math.max(
				access.getSectionIndex(minBlockY),
				0
			);
		
		final int maxSectionIndex = 
				Math.min(
						access.getSectionIndex(maxBlockY) + 1,
						access.getSectionsCount()
					);

		final LevelChunkSection[] sections = access.getSections();
		
		final Set<Holder<Biome>> set = new ObjectArraySet<>();
		for (int i = minSectionIndex; i < maxSectionIndex; ++i) {
			LevelChunkSection levelchunksection = sections[i];
			levelchunksection.getBiomes().getAll(set::add);
		}
		set.retainAll(this.biomeSource.possibleBiomes());
		
		return set;
	}
	
	@Override
	public OptionalInt iterateNoiseColumn(LevelHeightAccessor level, RandomState random, int x, int z, MutableObject<NoiseColumn> column, Predicate<BlockState> stoppingState) {
		NoiseSettings noise = this.settings.value().noiseSettings().clampToHeightAccessor(level);
		int min = Math.floorDiv(noise.minY(), this.cellHeight);
		int max = Math.floorDiv(noise.height(), this.cellHeight);

		if (max <= 0) {
			return OptionalInt.empty();
		} else {
			TerrainWarper terrainWarper = this.warper.usingSeed(IBetweenlandsRandomStateExtension.getLevelSeed(random));
			
			BlockState[] states = null;
			if (column != null) {
				states = new BlockState[max * noise.getCellHeight()];
				column.setValue(new NoiseColumn(noise.minY(), states));
			}
			int xDiv = Math.floorDiv(x, this.cellWidth);
			int zDiv = Math.floorDiv(z, this.cellWidth);
			int xMod = Math.floorMod(x, this.cellWidth);
			int zMod = Math.floorMod(z, this.cellWidth);
			int xMin = xMod / this.cellWidth;
			int zMin = zMod / this.cellWidth;
			double[][] columns = new double[][]{
				this.makeAndFillNoiseColumn(terrainWarper, xDiv, zDiv, min, max, noise.height()),
				this.makeAndFillNoiseColumn(terrainWarper, xDiv, zDiv + 1, min, max, noise.height()),
				this.makeAndFillNoiseColumn(terrainWarper, xDiv + 1, zDiv, min, max, noise.height()),
				this.makeAndFillNoiseColumn(terrainWarper, xDiv + 1, zDiv + 1, min, max, noise.height())
			};
			//Aquifers?

			for (int cell = max - 1; cell >= 0; cell--) {
				double d10 = columns[0][cell];
				double d20 = columns[1][cell];
				double d30 = columns[2][cell];
				double d40 = columns[3][cell];
				double d11 = columns[0][cell + 1];
				double d21 = columns[1][cell + 1];
				double d31 = columns[2][cell + 1];
				double d41 = columns[3][cell + 1];

				for (int height = this.cellHeight - 1; height >= 0; height--) {
					double dcell = height / (double)this.cellHeight;
					double lcell = Mth.lerp3(dcell, xMin, zMin, d10, d11, d30, d31, d20, d21, d40, d41);
					int layer = cell * this.cellHeight + height;
					int maxlayer = layer + min * this.cellHeight;
					BlockState state = this.generateBaseState(lcell, layer);
					if (states != null) {
						states[layer] = state;
					}

					if (stoppingState != null && stoppingState.test(state)) {
						return OptionalInt.of(maxlayer + 1);
					}
				}
			}

			return OptionalInt.empty();
		}
	}

	// TODO get rid of worldHeight
	
	private double[] makeAndFillNoiseColumn(TerrainWarper terrainWarper, int x, int z, int min, int max, int worldHeight) {
		double[] columns = new double[max + 1];
		this.fillNoiseColumn(terrainWarper, columns, x, z, min, max, worldHeight);
		return columns;
	}

	private void fillNoiseColumn(TerrainWarper terrainWarper, double[] columns, int x, int z, int min, int max, int worldHeight) {
		terrainWarper.fillNoiseColumn(columns, x, z, this.sampler, this.getSeaLevel(), worldHeight, min, max);
	}

	private void fillNoiseColumn(TerrainWarper terrainWarper, double[] columns, int x, int z, int min, int max) {
		this.fillNoiseColumn(terrainWarper, columns, x, z, min, max, max * this.cellHeight);
	}

	private BlockState generateBaseState(double a, double b) {
		BlockState state;

		if (a > 0.0D) {
			state = this.defaultBlock;
		} else if (b <= this.getSeaLevel()) {
			state = this.defaultFluid;
		} else {
			state = Blocks.AIR.defaultBlockState();
		}

		return state;
	}
}
