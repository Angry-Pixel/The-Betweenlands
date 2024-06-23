package thebetweenlands.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.material.WorldGenMaterialRule;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;
import thebetweenlands.common.world.gen.feature.legacy.MapGenCavesBetweenlands;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorOctaves;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorPerlin;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorSimplex;
import thebetweenlands.common.world.surfacegenerators.*;
import thebetweenlands.common.world.util.OpenBeardifier;
import thebetweenlands.common.world.util.postprossesors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/* TODO: big rewrite!
	- move things out of the doFill function and convert feature to vinila version
 */


public class ChunkGeneratorBetweenlands extends LegacyChunkGenerator {

	// Generator data pack inputs
	public final static Codec<BetweenlandsGeneratorSettings> SETTINGS_CODEC = RecordCodecBuilder.create((p_64475_) -> {
		return p_64475_.group(
				Codec.FLOAT.fieldOf("pitstone_base").forGetter(BetweenlandsGeneratorSettings::getPitstoneBase),
				Codec.BOOL.fieldOf("betweenlands_caves_enabled").forGetter(BetweenlandsGeneratorSettings::getBetweenlandsCaveStyle),
				Codec.BOOL.fieldOf("betweenlands_gen_enabled").forGetter(BetweenlandsGeneratorSettings::getBetweenlandsGen))
			.apply(p_64475_, BetweenlandsGeneratorSettings::new);
	});

	public static final Codec<ChunkGeneratorBetweenlands> CODEC = RecordCodecBuilder.create((p_188643_) -> {
		return commonCodec(p_188643_).and(p_188643_.group(RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter((p_188716_) -> {
			return p_188716_.noises;
		}), BiomeSource.CODEC.fieldOf("biome_source").forGetter((p_188711_) -> {
			return p_188711_.biomeSource;
		}), Codec.LONG.fieldOf("seed").stable().forGetter((p_188690_) -> {
			return p_188690_.seed;
		}), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_204585_) -> {
			return p_204585_.settings;
		}), SETTINGS_CODEC.fieldOf("betweenlands_settings").forGetter(ChunkGeneratorBetweenlands::getGeneratorSettings))).apply(p_188643_, p_188643_.stable(ChunkGeneratorBetweenlands::new));
	});

	//private final Random rand;
	private NoiseGeneratorOctaves minLimitPerlinNoise; // convert to createLegacyForBlendedNoise(RandomSource p_192886_, IntStream p_192887_)
	private NoiseGeneratorOctaves maxLimitPerlinNoise; // o
	private NoiseGeneratorOctaves mainPerlinNoise; // o
	private NoiseGeneratorPerlin surfaceNoise;
	public NoiseGeneratorOctaves scaleNoise; // o
	public NoiseGeneratorOctaves depthNoise; // o
	/**
	 * Technically this isn't a heightmap, it's a 3D density map
	 */
	private final double[] heightMap;
	private final float[] biomeWeights;
	private double[] surfaceNoiseBuffer = new double[256];
	private float[] terrainBiomeWeights = new float[25];
	private float[] interpolatedTerrainBiomeWeights = new float[256];
	public int[] biomesForGeneration;
	private double[] mainNoiseRegion;
	private double[] minLimitRegion;
	private double[] maxLimitRegion;
	private double[] depthRegion;
	//private final long seed;
	private final int layerHeight = 120;
	public Random rand;

	// TODO: move to biome wrapper
	public List<Float> biomeBaseHeights = List.of(
		(float) 120 - 1.25F,
		(float) 120 - 2,
		(float) 120 - 12,
		(float) 120 - 5,
		(float) 120 - 5,
		(float) 120 - 5,
		(float) 120 - 1,
		(float) 120 - 1,
		(float) 120 + 2,
		(float) 120 + 4);

	public List<Float> biomeHeightVariation = List.of(
		(float) 4.75F,
		(float) 1,
		(float) 5,
		(float) 4,
		(float) 4,
		(float) 3,
		(float) 1.1,
		(float) 1.1,
		(float) 1,
		(float) 0.5
	);

	private static final BlockState AIR = Blocks.AIR.defaultBlockState();
	private static final BlockState[] EMPTY_COLUMN = new BlockState[0];
	public final BlockState fillfluid;
	public final BlockState baseBlockState;
	public final Registry<NormalNoise.NoiseParameters> noises;
	public final long seed;
	public final Holder<NoiseGeneratorSettings> settings;
	public final BetweenlandsGeneratorSettings betweenlandsSettings;
	public final NoiseRouter sampler;
	private final SurfaceSystem surfaceSystem;
	private final Aquifer.FluidPicker globalFluidPicker;

	// SurfaceGenerators
	public final List<SurfaceGenerator> surfaceGenerator = new ArrayList<SurfaceGenerator>();
	public final List<SurfacePostProssesor> surfacePostProssesor = new ArrayList<SurfacePostProssesor>();

	// Biome souce
	public final BiomeSource biomesource;
	public BetweenlandsBiomeProvider betweenlandsBiomeProvider;

	// Noises
	public XoroshiroRandomSource source;
	public List<Integer> list = List.of(0, 1);
	public List<Integer> riverrange = List.of(-1, 1);
	public PerlinNoise perlinnoise;
	public SimplexNoise simplexnoise;
	public PerlinNoise vainrivernoise;

	// Noise modifyers
	public List<Float> linepoint = List.of(0.0f, 1.0f);
	public List<Float> linevalue = List.of(0.0f, 1.0f);

	public List<Float> spikespoint = List.of(0.0f, 0.1f, 0.8f, 0.9f, 1.0f);
	public List<Float> spikesvalue = List.of(1.0f, 0.0f, 1.0f, 0.0f, 1.0f);

	private MapGenCavesBetweenlands caveGenerator;

	// Values for features
	public BiomeWeights featureBiomeWeights;

	private NoiseGeneratorSimplex treeNoise;
	private NoiseGeneratorSimplex speleothemDensityNoise;

	public ChunkGeneratorBetweenlands(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_, BetweenlandsGeneratorSettings betweenlandssettings) {
		this(p_209106_, p_209107_, p_209108_, p_209108_, p_209109_, p_209110_, betweenlandssettings);
	}

	public ChunkGeneratorBetweenlands(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_188614_, BiomeSource biomeSource, BiomeSource p_188616_, long seed, Holder<NoiseGeneratorSettings> p_188618_, BetweenlandsGeneratorSettings betweenlandssettings) {
		super(p_209106_, p_188614_, biomeSource, seed, p_188618_);

		// I know this looks stupid, that's cos it is
		//if (seed == 0) {
		// Set to overworld seed if set to 0 in datapack
		//	seed = Minecraft.getInstance().level.getServer().overworld().getSeed();
		//}

		this.rand = new Random(seed);
		this.heightMap = new double[825];
		this.biomeWeights = new float[25];
		for (int i = -2; i <= 2; ++i) {
			for (int j = -2; j <= 2; ++j) {
				float f = 10.0F / Mth.sqrt((float) (i * i + j * j) + 0.2F);
				this.biomeWeights[i + 2 + (j + 2) * 5] = f;
			}
		}
		this.betweenlandsSettings = betweenlandssettings;
		this.biomesource = biomeSource;
		try {
			this.betweenlandsBiomeProvider = (BetweenlandsBiomeProvider) biomeSource;
		} catch (ClassCastException e) {
			TheBetweenlands.LOGGER.error(this.getClass().getName() + " requires biomeSource parameter to be an instance of BetweenlandsBiomeProvider!");
		}

		this.noises = p_188614_;
		this.seed = seed;
		this.settings = p_188618_;
		NoiseGeneratorSettings betweenlandsgeneratorsettings = this.settings.value();
		this.sampler = betweenlandsgeneratorsettings.createNoiseRouter(p_188614_, seed);
		this.baseBlockState = betweenlandsgeneratorsettings.defaultBlock();
		NoiseSettings noisesettings = betweenlandsgeneratorsettings.noiseSettings();
		Builder<WorldGenMaterialRule> builder = ImmutableList.builder();
		Aquifer.FluidStatus aquifer$fluidstatus = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
		int i = betweenlandsgeneratorsettings.seaLevel();
		Aquifer.FluidStatus aquifer$fluidstatus1 = new Aquifer.FluidStatus(i, betweenlandsgeneratorsettings.defaultFluid());
		Aquifer.FluidStatus aquifer$fluidstatus2 = new Aquifer.FluidStatus(noisesettings.minY() - 1, Blocks.AIR.defaultBlockState());
		this.globalFluidPicker = (p_198228_, p_198229_, p_198230_) -> {
			return p_198229_ < Math.min(-54, i) ? aquifer$fluidstatus : aquifer$fluidstatus1;
		};
		this.surfaceSystem = new SurfaceSystem(p_188614_, this.baseBlockState, i, seed, betweenlandsgeneratorsettings.getRandomSource());

		//Noises
		this.source = new XoroshiroRandomSource(seed);
		this.perlinnoise = PerlinNoise.create(source, list);
		this.simplexnoise = new SimplexNoise(source);
		this.vainrivernoise = PerlinNoise.create(source, riverrange);

		this.fillfluid = this.settings.value().defaultFluid();

		Random rand = new Random(this.seed);

		// Generators for eatch biome
		this.surfaceGenerator.add(new SwamplandsSurfaceGenerator(source));
		this.surfaceGenerator.add(new DeepWatersSurfaceGenerator(source));
		this.surfaceGenerator.add(new SlugePlainsSurfaceGenerator(source));
		this.surfaceGenerator.add(new MarshSurfaceGenerator(source, rand));
		this.surfaceGenerator.add(new ErodedMarshSurfaceGenerator(source, rand));
		this.surfaceGenerator.add(new PatchyIslandsSurfaceGenerator(source));
		this.surfaceGenerator.add(new CorseIslandsSurfaceGenerator(source));
		this.surfaceGenerator.add(new CorseIslandsSurfaceGenerator(source));

		// Postprossesors for biome blending
		this.surfacePostProssesor.add(new SwamplandsPostProssesor());
		this.surfacePostProssesor.add(new StandardPostProssesor());
		this.surfacePostProssesor.add(new StandardPostProssesor());
		this.surfacePostProssesor.add(new HardCutPostProssesor(5));
		this.surfacePostProssesor.add(new HardCutPostProssesor(5));
		this.surfacePostProssesor.add(new StandardPostProssesor());
		this.surfacePostProssesor.add(new CorseIslandsPostProssesor(8, 5));
		this.surfacePostProssesor.add(new CorseIslandsPostProssesor(8, 5));

		// Init noise
		//SimplexNoise test = new SimplexNoise();
		this.minLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16); // convert to createLegacyForBlendedNoise(RandomSource p_192886_, IntStream p_192887_)
		this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16); // o
		this.mainPerlinNoise = new NoiseGeneratorOctaves(rand, 8); // o
		this.surfaceNoise = new NoiseGeneratorPerlin(rand, 4);
		this.scaleNoise = new NoiseGeneratorOctaves(rand, 10); // o
		this.depthNoise = new NoiseGeneratorOctaves(rand, 16); // o
		this.treeNoise = new NoiseGeneratorSimplex(rand);
		this.speleothemDensityNoise = new NoiseGeneratorSimplex(rand);

		this.caveGenerator = new MapGenCavesBetweenlands(seed);

	}

	public void buildSurface(WorldGenRegion p_188636_, StructureFeatureManager p_188637_, ChunkAccess p_188638_) {

		// Betweenlands terrain generation (reads biome data for terrain features instead of noise)
		// TODO: this setting causes chunk treads to crash or hang, fix asap
		if (betweenlandsSettings.getBetweenlandsGen()) {
			// Collect cords for generating
			ChunkPos chunkpos = p_188638_.getPos();
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

			// Get frequent block states to avoid recalling slow getters
			BlockState betweenBedrock = BlockRegistry.BETWEENLANDS_BEDROCK.get().defaultBlockState();
			BlockState betweenStone = BlockRegistry.BETWEENSTONE.get().defaultBlockState();
			BlockState pitStone = BlockRegistry.PITSTONE.get().defaultBlockState();
			BlockState swampDirt = BlockRegistry.SWAMP_DIRT.get().defaultBlockState();
			BlockState mud = BlockRegistry.MUD.get().defaultBlockState();

			// Bedrock layer fill
			/*for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					p_188638_.setBlockState(pos.set(x, -64, z), betweenBedrock, false);
				}
			}*/

			// Pitstone layer fill
			/*for (int x = 0; x < 16; x++)
			{
				for (int y = -64; y < -42; y++)
				{
					for (int z = 0; z < 16; z++)
					{
						p_188638_.setBlockState(pos.set(x, y, z), pitStone, false);
					}
				}
			}*/
		}

		// Stock terrain generation (surface generation is handled via datapack)
		WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, p_188636_);
		NoiseGeneratorSettings noisegeneratorsettings = this.settings.value();
		NoiseChunk noisechunk = p_188638_.getOrCreateNoiseChunk(this.sampler, () -> {
			return new OpenBeardifier(p_188637_, p_188638_);
		}, noisegeneratorsettings, this.globalFluidPicker, Blender.of(p_188636_));
		this.surfaceSystem.buildSurface(p_188636_.getBiomeManager(), p_188636_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), noisegeneratorsettings.useLegacyRandomSource(), worldgenerationcontext, p_188638_, noisechunk, noisegeneratorsettings.surfaceRule());
	}

	/*
	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_188702_, Blender p_188703_, StructureFeatureManager p_188704_, ChunkAccess p_188705_) {

		// now unused
		//Throwable chunkerror = new Throwable("Betweenlands chunk generator: Cannot get server instance (likely called during server shutdown)");

		// ISSUE:		-Server calls chunk generation even after server level is no longer present
		// SOLUTION:	-Cancel chunk generation and tell server to not save the chunk
		// NOTES:		-This problem only ocures due to needing biome noise reading needed for some generation features
		//				and sever level (contains biome reading) no longer exists

		// EXTRA NOTES:	-Server level may no longer be nessisary due to core.world.util.UtilWorldFunctions.biomeNoiseSample no longer neading WorldGenRegion
		//				further testing needed

		// SOLUTION:	-removed need for server level entierly by sampleing biome noise directly from BetweenlandsBiomeSource
		//				however if BiomeSource is not of type BetweenlandsBiomeSource errors are expected but undocumented

		// Init vars
		NoiseGeneratorSettings noisegeneratorsettings = this.settings.value();
		BlockState fillblock = this.settings.value().defaultBlock();
		BlockState fillfluid = this.settings.value().defaultFluid();
		int height = p_188705_.getMaxBuildHeight();
		int floor = p_188705_.getMinBuildHeight();

		BetweenlandsBiomeProvider betweenlandsbiomesource = (BetweenlandsBiomeProvider) biomesource;

		//ScanReturn borderTest = UtilWorldFunctions.BorderScan(p_188705_,biomesource);

		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				int xin = (p_188705_.getPos().x * 16) + x;
				int zin = (p_188705_.getPos().z * 16) + z;

				//int scantranslatex = 4;
				//int scantranslatez = 4;

				VoronoiCellNoise.ReturnNoise BiomeSample = betweenlandsbiomesource.biomenoise.sample(xin-2, zin-2);

				// Get some biome scale in blocks for border detection
				//double biomeScale = betweenlandsbiomesource.biomenoise.cellscale * 4;

				// Get this biome
				//int biomecount = betweenlandsbiomesource.parameters.values().size();
				int biomeID = betweenlandsbiomesource.getBiomeFromCellData(BiomeSample.result);
				//Biome blockbiome = betweenlandsbiomesource.getBiomeFromID(betweenlandsbiomesource.getIdFromHolder(p_188705_.getNoiseBiome(x-2, 64, z-2)));//betweenlandsbiomesource.getBiomeFromID(biomeID);

				//int ScanRange = 10;
				//int ExtendedScanRange = 15;

				//int TrenchBaseHight = 58;
				//int DeepwatersBaseHight = 54;
				//int SwampBaseHight = 64;
				//int SlugeBaseHight = 64;
				//int PatchyIslandsBaseHight = 62;

				//int MarshBaseHight = 63;
				//int MarshUnderwaterBaseHight = 62;


				int ysurface = 64;
				//double mod = 0;

				//double val = UtilWorldFunctions.biomeNoiseSample((BetweenlandsBiomeProvider) biomesource, xin-2, 0, zin-2);
				double surfaceheight = 0;
				double underwaterheight = 0;
				double hightfactor = 0;

				// Biome specific generation
				SurfaceMapValues output = surfaceGenerator.get(biomeID).sample(xin, zin);

				// Use these values when disabling border detection
				underwaterheight = output.underwaterHeight;
				surfaceheight = output.surfaceHeight;
				hightfactor = output.surfaceFactor;


				// Biome border thingamabobs
				ScanReturn border = UtilWorldFunctions.BorderScan(true, betweenlandsbiomesource, xin,zin,24,8,biomeID,0);		// gets borderse

				// TEMP: quick test, mix values
				//double BlocksFromBorder = 10000;
				//int count = border.distances.size();
				//double factor = 1d / (double)count;
				//for (int index = 0; index < count; index++) {
				//
				//	if (BlocksFromBorder > border.distances.get(index) && index != BiomeSample.resultIndex && border.borderbiomes.get(BiomeSample.resultIndex) != border.borderbiomes.get(index)) {
				//		BlocksFromBorder = border.distances.get(index)*0.1d;
				//	}
				//}

				//if (BlocksFromBorder <= 4) {
				//	underwaterheight = 50;
				//	surfaceheight = 50;
				//	hightfactor = 0.5f;
				//}


				SurfaceMapValues outpost = surfacePostProssesor.get(biomeID).docheck(border, BiomeSample, surfaceGenerator, biomeID, xin, zin);		// uses borderScan output to edit surface values

				underwaterheight = outpost.underwaterHeight;
				surfaceheight = outpost.surfaceHeight;
				hightfactor = outpost.surfaceFactor;

				// Calculate ysurface to generate blocks up to
				ysurface = (int) ((underwaterheight * hightfactor) + (surfaceheight * (1 - hightfactor)));

				// Fill Blocks
				for(int y = floor; y <= ysurface; y++) {
					p_188705_.setBlockState(new BlockPos(x, y, z), fillblock, false);
				}
				// Sea level fill
				for (int y = ysurface + 1; y <= this.getSeaLevel(); y++) {
					p_188705_.setBlockState(new BlockPos(x, y, z), fillfluid, false);
				}
			}
		}

		return CompletableFuture.completedFuture(p_188705_);
	}*/

	public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_188702_, Blender p_188703_, StructureFeatureManager p_188704_, ChunkAccess chunkprimer) {

		// Build the proto chunk
		int chunkX = chunkprimer.getPos().x, chunkZ = chunkprimer.getPos().z;
		this.setBlocksInChunk(chunkX, chunkZ, chunkprimer);

		// Setup chunk noises

		// Setup for features
		// Interpolate biome weights
		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				float fractionZ = (z % 4) / 4.0F;
				float fractionX = (x % 4) / 4.0F;
				int biomeWeightZ = z / 4;
				int biomeWeightX = x / 4;

				float weightXCZC = this.terrainBiomeWeights[biomeWeightX + biomeWeightZ * 5];
				float weightXNZC = this.terrainBiomeWeights[biomeWeightX + 1 + biomeWeightZ * 5];
				float weightXCZN = this.terrainBiomeWeights[biomeWeightX + (biomeWeightZ + 1) * 5];
				float weightXNZN = this.terrainBiomeWeights[biomeWeightX + 1 + (biomeWeightZ + 1) * 5];

				float interpZAxisXC = weightXCZC + (weightXCZN - weightXCZC) * fractionZ;
				float interpZAxisXN = weightXNZC + (weightXNZN - weightXNZC) * fractionZ;
				float currentVal = interpZAxisXC + (interpZAxisXN - interpZAxisXC) * fractionX;

				this.interpolatedTerrainBiomeWeights[x + z * 16] = currentVal;
			}
		}

		this.featureBiomeWeights = new BiomeWeights(this.interpolatedTerrainBiomeWeights);

		this.biomesForGeneration = this.betweenlandsBiomeProvider.legacyGetBiomes(this.biomesForGeneration, chunkprimer.getPos().x * 16, chunkprimer.getPos().z * 16, 16, 16, false);

		this.surfaceNoiseBuffer = this.surfaceNoise.getRegion(this.surfaceNoiseBuffer, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

		// World generator modifiers
		List<BiomeGenerator> foundGenerators = new ArrayList<BiomeGenerator>();
		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				double baseBlockNoise = this.surfaceNoiseBuffer[z + x * 16];
				BiomeBetweenlands biome = this.betweenlandsBiomeProvider.BiomeFromID(this.biomesForGeneration[z + x * 16]);
				if (biome != null) {
					BiomeGenerator generator = biome.biomeGenerator;
					if (generator != null) {
						generator.initializeGenerators(this.seed);
						generator.generateNoise(chunkZ, chunkX);
						foundGenerators.add(generator);
						generator.runBiomeFeatures(chunkZ * 16 + z, chunkX * 16 + x, z, x, baseBlockNoise, chunkprimer, this, biomesForGeneration, this.featureBiomeWeights, EnumGeneratorPass.PRE_REPLACE_BIOME_BLOCKS);
						generator.replaceBiomeBlocks(chunkZ * 16 + z, chunkX * 16 + x, z, x, baseBlockNoise, this.rand, this.seed, chunkprimer, this, biomesForGeneration, this.featureBiomeWeights);
						generator.runBiomeFeatures(chunkZ * 16 + z, chunkX * 16 + x, z, x, baseBlockNoise, chunkprimer, this, biomesForGeneration, this.featureBiomeWeights, EnumGeneratorPass.POST_REPLACE_BIOME_BLOCKS);
					}
				}
			}
		}

		for (BiomeGenerator gen : foundGenerators) {
			gen.resetNoise();
		}

		// Cave carvers here!
		//this.caveGenerator.setBiomeTerrainWeights(featureBiomeWeights);
		//this.caveGenerator.generate(0, chunkX, chunkZ, chunkprimer);

		// TODO: move to features call
		// Add biome features (post cave)
		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				double baseBlockNoise = this.surfaceNoiseBuffer[z + x * 16];
				BiomeBetweenlands biome = this.betweenlandsBiomeProvider.BiomeFromID(this.biomesForGeneration[z + x * 16]);
				if (biome != null) {
					BiomeGenerator generator = biome.biomeGenerator;
					if (generator != null) {
						generator.runBiomeFeatures(chunkZ * 16 + z, chunkX * 16 + x, z, x, baseBlockNoise, chunkprimer, this, this.biomesForGeneration, featureBiomeWeights, EnumGeneratorPass.POST_GEN_CAVES);
					}
				}
			}
		}

		// Block placement done! -> next call is surface rule
		// TODO: Move surface block generation into this call
		return CompletableFuture.completedFuture(chunkprimer);
	}

	@Override
	public ChunkAccess doFill(Blender blend, StructureFeatureManager structure, ChunkAccess chunk, int min, int max) {
		return null;
	}

	/**
	 * Generates the base terrain
	 *
	 * @param chunkX
	 * @param chunkZ
	 * @param chunkaccess
	 */
	public void setBlocksInChunk(int chunkX, int chunkZ, ChunkAccess chunkaccess) {

		this.biomesForGeneration = this.betweenlandsBiomeProvider.legacyGetBiomesWithin(this.biomesForGeneration, (chunkX * 4) - 6, (chunkZ * 4) - 6, 15, 15);

		generateHeightmap(chunkX * 4, 0, chunkZ * 4);

		//X
		for (int heightMapX = 0; heightMapX < 4; ++heightMapX) {
			int indexXC = heightMapX * 5; //1
			int indexXN = (heightMapX + 1) * 5; //2

			/*
			 * 1 2
			 */

			//Z
			for (int heightMapZ = 0; heightMapZ < 4; ++heightMapZ) {
				int indexXCZC = (indexXC + heightMapZ) * 33; //1
				int indexXCZN = (indexXC + heightMapZ + 1) * 33; //2
				int indexXNZC = (indexXN + heightMapZ) * 33; //3
				int indexXNZN = (indexXN + heightMapZ + 1) * 33; //4

				/*
				 * 1 3
				 * 2 4
				 */

				//Y
				for (int heightMapY = 0; heightMapY < 32; ++heightMapY) {
					//Values
					double valXCZCYC = this.heightMap[indexXCZC + heightMapY]; //1
					double valXCZNYC = this.heightMap[indexXCZN + heightMapY]; //2
					double valXNZCYC = this.heightMap[indexXNZC + heightMapY]; //3
					double valXNZNYC = this.heightMap[indexXNZN + heightMapY]; //4
					double valXCZCYN = this.heightMap[indexXCZC + heightMapY + 1]; //5
					double valXCZNYN = this.heightMap[indexXCZN + heightMapY + 1]; //6
					double valXNZCYN = this.heightMap[indexXNZC + heightMapY + 1]; //7
					double valXNZNYN = this.heightMap[indexXNZN + heightMapY + 1]; //8

					//Step along Y axis (1/8 of the difference)
					double stepYAxisXCZC = (valXCZCYN - valXCZCYC) * 0.125D;
					double stepYAxisXCZN = (valXCZNYN - valXCZNYC) * 0.125D;
					double stepYAxisXNZC = (valXNZCYN - valXNZCYC) * 0.125D;
					double stepYAxisXNZN = (valXNZNYN - valXNZNYC) * 0.125D;

					double currentValXCZCYC = valXCZCYC;
					double currentValXCZNYC = valXCZNYC;
					double currentValXNZCYC = valXNZCYC;
					double currentValXNZNYC = valXNZNYC;

					/*
					 * At this point we have the values of a 2x2x2 cube and their difference along the Y axis (e.g. 5 - 1, 6 - 2, 7 - 3, 8 - 4)
					 *
					 * Y:
					 * 1 3
					 * 2 4
					 *
					 * Y+1:
					 * 5 7
					 * 6 8
					 */

					//Now it expands that cube into a 8x4x4 area and linearly interpolates the values

					//Step Y axis
					for (int blockY = 0; blockY < 8; ++blockY) {
						double currentValXCZC = currentValXCZCYC;
						double currentValXCZN = currentValXCZNYC;

						//Step along X axis
						double stepXAxisZC = (currentValXNZCYC - currentValXCZCYC) * 0.25D;
						double stepXAxisZN = (currentValXNZNYC - currentValXCZNYC) * 0.25D;

						//Step X axis
						for (int blockX = 0; blockX < 4; ++blockX) {
							//Step along Z axis
							double stepZAxis = (currentValXCZN - currentValXCZC) * 0.25D;

							double currentValZC = currentValXCZC - stepZAxis;

							//Step Z axis
							for (int blockZ = 0; blockZ < 4; ++blockZ) {
								if ((currentValZC += stepZAxis) > 0.0D) {
									chunkaccess.setBlockState(new BlockPos(heightMapX * 4 + blockX, heightMapY * 8 + blockY, heightMapZ * 4 + blockZ), BlockRegistry.BETWEENSTONE.get().defaultBlockState(), false);
								} else if (heightMapY * 8 + blockY <= this.layerHeight) {
									chunkaccess.setBlockState(new BlockPos(heightMapX * 4 + blockX, heightMapY * 8 + blockY, heightMapZ * 4 + blockZ), fillfluid, false);
								}
							}

							currentValXCZC += stepXAxisZC;
							currentValXCZN += stepXAxisZN;
						}

						currentValXCZCYC += stepYAxisXCZC;
						currentValXCZNYC += stepYAxisXCZN;
						currentValXNZCYC += stepYAxisXNZC;
						currentValXNZNYC += stepYAxisXNZN;
					}
				}
			}
		}
	}

	/**
	 * Generates a 33x5x5 (Y*X*Z) heightmap
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	private void generateHeightmap(int x, int y, int z) {
		this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
		float scaleXZ = 684.412F * 8;
		float scaleY = 684.412F * 8;
		this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5, (double) (scaleXZ / 80.0F), (double) (scaleY / 160.0F), (double) (scaleXZ / 80.0F));
		this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5, (double) scaleXZ, (double) scaleY, (double) scaleXZ);
		this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5, (double) scaleXZ, (double) scaleY, (double) scaleXZ);

		int noiseIndex = 0;
		int heightMapIndex = 0;

		for (int heightMapX = 0; heightMapX < 5; ++heightMapX) {
			for (int heightMapZ = 0; heightMapZ < 5; ++heightMapZ) {
				float biomeVariation = 0.0F;
				float biomeDepth = 0.0F;
				float totalBiomeWeight = 0.0F;

				int centerBiome = this.biomesForGeneration[heightMapX + 5 + (heightMapZ + 5) * 15];

				if (centerBiome > 9 || centerBiome < 0) {
					centerBiome = 0;
				}

				float nearestOtherBiomeSq = 50;

				// Averages biome height and variation in a 5x5 area and calculates the biome terrain weight from a 11x11 area
				for (int offsetX = -5; offsetX <= 5; ++offsetX) {
					for (int offsetZ = -5; offsetZ <= 5; ++offsetZ) {
						int nearbyBiome = this.biomesForGeneration[heightMapX + 5 + offsetX + (heightMapZ + 5 + offsetZ) * 15];

						if (nearbyBiome > 9 || nearbyBiome < 0) {
							nearbyBiome = 0;
						}
						float nearbyBiomeDepth = biomeBaseHeights.get(nearbyBiome);
						float nearbyBiomeVariation = biomeHeightVariation.get(nearbyBiome);

						//No amplified terrain
						/*if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F)
						{
							f5 = 1.0F + f5 * 2.0F;
							f6 = 1.0F + f6 * 4.0F;
						}*/

						if (offsetX >= -2 && offsetX <= 2 && offsetZ >= -2 && offsetZ <= 2) {
							float weight = this.biomeWeights[offsetX + 2 + (offsetZ + 2) * 5];

							if (biomeBaseHeights.get(nearbyBiome) > biomeBaseHeights.get(centerBiome)) {
								weight /= 2.0F;
							}

							biomeVariation += nearbyBiomeVariation * weight;
							biomeDepth += nearbyBiomeDepth * weight;
							totalBiomeWeight += weight;
						}

						float distWeighted = (offsetX * offsetX + offsetZ * offsetZ);
						if (nearbyBiome != centerBiome && distWeighted < nearestOtherBiomeSq) {
							nearestOtherBiomeSq = distWeighted;
						}
					}
				}

				//The 0 point is offset by some blocks so that the lerp doesn't cause problems later on
				this.terrainBiomeWeights[heightMapIndex] = Mth.clamp(Math.max((nearestOtherBiomeSq - 2) / 46.0F, 0.0F), 0.0F, 1.0F);

				biomeVariation = biomeVariation / totalBiomeWeight;
				biomeDepth = biomeDepth / totalBiomeWeight;

				//Small offset for biome depth?
				double depthPerturbation = this.depthRegion[heightMapIndex] / 8000.0D;

				//depthPerturbation = 0.0D;

				if (depthPerturbation < 0.0D) {
					depthPerturbation = -depthPerturbation * 0.3D;
				}

				depthPerturbation = depthPerturbation * 3.0D - 2.0D;

				if (depthPerturbation < 0.0D) {
					depthPerturbation = depthPerturbation / 2.0D;

					if (depthPerturbation < -1.0D) {
						depthPerturbation = -1.0D;
					}

					depthPerturbation = depthPerturbation / 1.4D;
					depthPerturbation = depthPerturbation / 2.0D;
				} else {
					if (depthPerturbation > 1.0D) {
						depthPerturbation = 1.0D;
					}

					depthPerturbation = depthPerturbation / 8.0D;
				}

				++heightMapIndex;

				//double depth = (biomeDepth * this.layerHeight) / 256.0D;

				for (int heightMapY = 0; heightMapY < 33; ++heightMapY) {
					double densityOffset = ((double) heightMapY * 8.0D - biomeDepth - (depthPerturbation * biomeVariation / 256.0D)) / 256.0D;

					double maxGenDensity16 = 32767.0D;
					/*double maxGenDensity16 = 0.0D;
					for(int i = 0; i < 16 - 1; i++) {
						maxGenDensity16 += 2 * Math.pow(2.0D, i);
					}
					maxGenDensity16 /= 2.0D;*/

					double maxGenDensity8 = 127.0;
					/*double maxGenDensity8 = 0.0D;
					for(int i = 0; i < 8 - 1; i++) {
						maxGenDensity8 += 2 * Math.pow(2.0D, i);
					}
					maxGenDensity8 /= 2.0D;*/

					double minDensity = (this.minLimitRegion[noiseIndex] / maxGenDensity16) * biomeVariation / 256.0D;
					double maxDensity = (this.maxLimitRegion[noiseIndex] / maxGenDensity16) * biomeVariation / 256.0D;
					double mainDensity = (this.mainNoiseRegion[noiseIndex] / maxGenDensity8);

					//TODO Not sure if clampedlerp is the right thing to use
					this.heightMap[noiseIndex] = Mth.clampedLerp(minDensity, maxDensity, mainDensity) - densityOffset;

					++noiseIndex;
				}
			}
		}
	}

	// Sets up data for test carvers
	// TODO: modify these carvers to not require modifying carving stage handler
	public void applyCarvers(WorldGenRegion p_187691_, long p_187692_, BiomeManager p_187693_, StructureFeatureManager p_187694_, ChunkAccess p_187695_, GenerationStep.Carving p_187696_) {
		ChunkPos chunkpos1 = p_187695_.getPos();

		BiomeGenerationSettings biomegenerationsettings = p_187695_.carverBiome(() -> {
			return this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()), 0, QuartPos.fromBlock(chunkpos1.getMinBlockZ()), this.climateSampler());
		}).value().getGenerationSettings();
		Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers(p_187696_);
		int l = 0;

		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				float fractionZ = (z % 4) / 4.0F;
				float fractionX = (x % 4) / 4.0F;
				int biomeWeightZ = z / 4;
				int biomeWeightX = x / 4;

				float weightXCZC = this.terrainBiomeWeights[biomeWeightX + biomeWeightZ * 5];
				float weightXNZC = this.terrainBiomeWeights[biomeWeightX + 1 + biomeWeightZ * 5];
				float weightXCZN = this.terrainBiomeWeights[biomeWeightX + (biomeWeightZ + 1) * 5];
				float weightXNZN = this.terrainBiomeWeights[biomeWeightX + 1 + (biomeWeightZ + 1) * 5];

				float interpZAxisXC = weightXCZC + (weightXCZN - weightXCZC) * fractionZ;
				float interpZAxisXN = weightXNZC + (weightXNZN - weightXNZC) * fractionZ;
				float currentVal = interpZAxisXC + (interpZAxisXN - interpZAxisXC) * fractionX;

				this.interpolatedTerrainBiomeWeights[x + z * 16] = currentVal;
			}
		}

		BiomeWeights biomes = new BiomeWeights(this.interpolatedTerrainBiomeWeights);

		for (Holder<ConfiguredWorldCarver<?>> holder : iterable) {
			ConfiguredWorldCarver<?> configuredworldcarver = holder.value();
			if (configuredworldcarver.worldCarver() instanceof CavesBetweenlands) {
				((CavesBetweenlands) configuredworldcarver.worldCarver()).setBiomeTerrainWeights(biomes);
			}
		}

		super.applyCarvers(p_187691_, p_187692_, p_187693_, p_187694_, p_187695_, p_187696_);
	}

	// Adds biome decorators to feature stage
	public void applyBiomeDecoration(WorldGenLevel p_187712_, ChunkAccess p_187713_, StructureFeatureManager p_187714_) {
		super.applyBiomeDecoration(p_187712_, p_187713_, p_187714_);

		this.biomesForGeneration = this.betweenlandsBiomeProvider.legacyGetBiomes(this.biomesForGeneration, p_187713_.getPos().x * 16, p_187713_.getPos().z * 16, 16, 16, false);

		List<Integer> biomeList = new ArrayList<>();

		// TODO: get all biomes in chunk and call the decorator for each biome at chunk origin block pos 0,0

		for (int z = 0; z < 16; z++) {
			for (int x = 0; x < 16; x++) {
				int biome = this.biomesForGeneration[(x + z * 16)];
				if (!biomeList.contains(biome)) {
					biomeList.add(biome);
				}
			}
		}

		for (int id : biomeList) {
			BiomeGenerator generator = this.betweenlandsBiomeProvider.BiomeFromID(id).biomeGenerator;
			if (generator != null) {
				generator.decorator.init(p_187712_, id, this, rand, p_187713_.getPos().x * 16, p_187713_.getPos().z * 16);
				generator.decorator.decorate();
			}
		}
	}

	public BetweenlandsGeneratorSettings getGeneratorSettings() {
		return this.betweenlandsSettings;
	}

	public double evalTreeNoise(double x, double z) {
		return this.treeNoise.getValue(x, z);
	}

	public double evalSpeleothemDensityNoise(double x, double z) {
		return this.speleothemDensityNoise.getValue(x, z);
	}

	public static class BetweenlandsGeneratorSettings {

		public final float pitstoneBase;
		public final boolean betweenlandsCaveStyle;
		public final boolean betweenlandsGen;

		public BetweenlandsGeneratorSettings(float pitstoneBase, boolean caveStyle, boolean betweenlandsGen) {
			this.pitstoneBase = pitstoneBase;
			this.betweenlandsCaveStyle = caveStyle;
			this.betweenlandsGen = betweenlandsGen;
		}

		public float getPitstoneBase() {
			return pitstoneBase;
		}

		public boolean getBetweenlandsOreEnabled() {
			return betweenlandsCaveStyle;
		}

		public boolean getBetweenlandsCaveStyle() {
			return betweenlandsCaveStyle;
		}

		public boolean getBetweenlandsGen() {
			return betweenlandsGen;
		}
	}
}
