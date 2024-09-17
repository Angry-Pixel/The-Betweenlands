package thebetweenlands.common.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.warp.*;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

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

	private static final BlockState[] EMPTY_COLUMN = new BlockState[0];

	public BetweenlandsChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
		super(biomeSource, settings);

		this.settings = settings;
		if (settings.isBound()) {
			NoiseSettings noise = settings.value().noiseSettings();
			this.defaultBlock = settings.value().defaultBlock();
			this.defaultFluid = settings.value().defaultFluid();
			this.cellWidth = noise.getCellWidth();
			this.cellHeight = noise.getCellHeight();
			NoiseSlider topSlide = new NoiseSlider(-10.0D, 3, 0);
			NoiseSlider bottomSlide = new NoiseSlider(15.0D, 3, 0);
			BlendedNoise blend = new BetweenlandsBlendedNoise(new WorldgenRandom(new LegacyRandomSource(0L)));
			this.warper = new TerrainWarper(this.cellWidth, this.cellHeight, noise.height() / this.cellHeight, biomeSource, noise, topSlide, bottomSlide, blend, NoiseModifier.PASS);
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
	public int getBaseHeight(int x, int z, Heightmap.Types height, LevelHeightAccessor level, RandomState rand) {
		NoiseSettings settings = this.settings.value().noiseSettings();
		int minY = Math.max(settings.minY(), level.getMinBuildHeight());
		int maxY = Math.min(settings.minY() + settings.height(), level.getMaxBuildHeight());
		int mincell = Math.floorDiv(minY, this.cellHeight);
		int maxcell = Math.floorDiv(maxY - minY, this.cellHeight);

		if (maxcell <= 0) {
			return level.getMinBuildHeight();
		} else {
			return this.iterateNoiseColumn(x, z, null, height.isOpaque(), mincell, maxcell).orElse(level.getMinBuildHeight());
		}
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
		NoiseSettings settings = this.settings.value().noiseSettings();
		int minY = Math.max(settings.minY(), level.getMinBuildHeight());
		int maxY = Math.min(settings.minY() + settings.height(), level.getMaxBuildHeight());
		int mincell = Math.floorDiv(minY, this.cellHeight);
		int maxcell = Math.floorDiv(maxY - minY, this.cellHeight);

		if (maxcell <= 0) {
			return new NoiseColumn(minY, EMPTY_COLUMN);
		} else {
			BlockState[] ablockstate = new BlockState[maxcell * settings.getCellHeight()];
			this.iterateNoiseColumn(x, z, ablockstate, null, mincell, maxcell);
			return new NoiseColumn(minY, ablockstate);
		}
	}

	@Override
	public CompletableFuture<ChunkAccess> createBiomes(RandomState random, Blender blender, StructureManager manager, ChunkAccess access) {
		return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
			access.fillBiomesFromNoise(this.getBiomeSource(), Climate.empty());
			return access;
		}), Util.backgroundExecutor());
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random, StructureManager manager, ChunkAccess access) {
		NoiseSettings settings = this.settings.value().noiseSettings();
		int minY = Math.max(settings.minY(), access.getMinBuildHeight());
		int maxY = Math.min(settings.minY() + settings.height(), access.getMaxBuildHeight());
		int mincell = Math.floorDiv(minY, this.cellHeight);
		int maxcell = Math.floorDiv(maxY - minY, this.cellHeight);

		return maxcell <= 0 ? CompletableFuture.completedFuture(access) : CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise_bl", () -> {
			int maxIndex = access.getSectionIndex(maxcell * this.cellHeight - 1 + minY);
			int minIndex = access.getSectionIndex(minY);
			Set<LevelChunkSection> sections = Sets.newHashSet();

			for (int index = maxIndex; index >= minIndex; index--) {
				LevelChunkSection section = access.getSection(index);
				section.acquire();
				sections.add(section);
			}

			ChunkAccess chunk;
			try {
				chunk = this.doFill(access, mincell, maxcell);
			} finally {
				for (LevelChunkSection section : sections) {
					section.release();
				}
			}
			return chunk;
		}), Util.backgroundExecutor());
	}

	private ChunkAccess doFill(ChunkAccess access, int min, int max) {
		int cellCountX = 16 / this.cellWidth;
		int cellCountZ = 16 / this.cellWidth;
		Heightmap oceanfloor = access.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
		Heightmap surface = access.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
		ChunkPos chunkpos = access.getPos();
		int minX = chunkpos.getMinBlockX();
		int minZ = chunkpos.getMinBlockZ();
		BLNoiseInterpolator interpolator = new BLNoiseInterpolator(cellCountX, max, cellCountZ, chunkpos, min, this::fillNoiseColumn);
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

		return access;
	}

	protected OptionalInt iterateNoiseColumn(int x, int z, BlockState[] states, Predicate<BlockState> predicate, int min, int max) {
		int xDiv = Math.floorDiv(x, this.cellWidth);
		int zDiv = Math.floorDiv(z, this.cellWidth);
		int xMod = Math.floorMod(x, this.cellWidth);
		int zMod = Math.floorMod(z, this.cellWidth);
		int xMin = xMod / this.cellWidth;
		int zMin = zMod / this.cellWidth;
		double[][] columns = new double[][]{
			this.makeAndFillNoiseColumn(xDiv, zDiv, min, max),
			this.makeAndFillNoiseColumn(xDiv, zDiv + 1, min, max),
			this.makeAndFillNoiseColumn(xDiv + 1, zDiv, min, max),
			this.makeAndFillNoiseColumn(xDiv + 1, zDiv + 1, min, max)
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

				if (predicate != null && predicate.test(state)) {
					return OptionalInt.of(maxlayer + 1);
				}
			}
		}

		return OptionalInt.empty();
	}

	private double[] makeAndFillNoiseColumn(int x, int z, int min, int max) {
		double[] columns = new double[max + 1];
		this.fillNoiseColumn(columns, x, z, min, max);
		return columns;
	}

	private void fillNoiseColumn(double[] columns, int x, int z, int min, int max) {
		this.warper.fillNoiseColumn(columns, x, z, sampler, this.getSeaLevel(), min, max);
	}

	private BlockState generateBaseState(double a, double b) {
		BlockState state;

		if (a > 0.0D) {
			state = this.defaultBlock;
		} else if (b < this.getSeaLevel()) {
			state = this.defaultFluid;
		} else {
			state = Blocks.AIR.defaultBlockState();
		}

		return state;
	}
}
