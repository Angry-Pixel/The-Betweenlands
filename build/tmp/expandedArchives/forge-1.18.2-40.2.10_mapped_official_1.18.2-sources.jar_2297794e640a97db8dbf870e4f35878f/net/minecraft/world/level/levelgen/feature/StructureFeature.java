package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.NetherFossilFeature;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.slf4j.Logger;

public abstract class StructureFeature<C extends FeatureConfiguration> extends net.minecraftforge.registries.ForgeRegistryEntry<StructureFeature<?>> {
   private static final Map<StructureFeature<?>, GenerationStep.Decoration> STEP = Maps.newHashMap();
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final StructureFeature<JigsawConfiguration> PILLAGER_OUTPOST = register("pillager_outpost", new PillagerOutpostFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<MineshaftConfiguration> MINESHAFT = register("mineshaft", new MineshaftFeature(MineshaftConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> WOODLAND_MANSION = register("mansion", new WoodlandMansionFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> JUNGLE_TEMPLE = register("jungle_pyramid", new JunglePyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> DESERT_PYRAMID = register("desert_pyramid", new DesertPyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> IGLOO = register("igloo", new IglooFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<RuinedPortalConfiguration> RUINED_PORTAL = register("ruined_portal", new RuinedPortalFeature(RuinedPortalConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<ShipwreckConfiguration> SHIPWRECK = register("shipwreck", new ShipwreckFeature(ShipwreckConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> SWAMP_HUT = register("swamp_hut", new SwamplandHutFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD = register("stronghold", new StrongholdFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.STRONGHOLDS);
   public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT = register("monument", new OceanMonumentFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN = register("ocean_ruin", new OceanRuinFeature(OceanRuinConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> FORTRESS = register("fortress", new NetherFortressFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
   public static final StructureFeature<NoneFeatureConfiguration> END_CITY = register("endcity", new EndCityFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<ProbabilityFeatureConfiguration> BURIED_TREASURE = register("buried_treasure", new BuriedTreasureFeature(ProbabilityFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
   public static final StructureFeature<JigsawConfiguration> VILLAGE = register("village", new VillageFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<RangeConfiguration> NETHER_FOSSIL = register("nether_fossil", new NetherFossilFeature(RangeConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
   public static final StructureFeature<JigsawConfiguration> BASTION_REMNANT = register("bastion_remnant", new BastionFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final int MAX_STRUCTURE_RANGE = 8;
   private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec;
   private final PieceGeneratorSupplier<C> pieceGenerator;
   private final PostPlacementProcessor postPlacementProcessor;

   private static <F extends StructureFeature<?>> F register(String p_67090_, F p_67091_, GenerationStep.Decoration p_67092_) {
      STEP.put(p_67091_, p_67092_);
      return Registry.register(Registry.STRUCTURE_FEATURE, p_67090_, p_67091_);
   }

   public StructureFeature(Codec<C> p_197165_, PieceGeneratorSupplier<C> p_197166_) {
      this(p_197165_, p_197166_, PostPlacementProcessor.NONE);
   }

   public StructureFeature(Codec<C> p_197168_, PieceGeneratorSupplier<C> p_197169_, PostPlacementProcessor p_197170_) {
      this.configuredStructureCodec = RecordCodecBuilder.create((p_209759_) -> {
         return p_209759_.group(p_197168_.fieldOf("config").forGetter((p_209786_) -> {
            return (C)p_209786_.config;
         }), RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("biomes").forGetter(ConfiguredStructureFeature::biomes), Codec.BOOL.optionalFieldOf("adapt_noise", Boolean.valueOf(false)).forGetter((p_209784_) -> {
            return p_209784_.adaptNoise;
         }), Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values())).fieldOf("spawn_overrides").forGetter((p_209761_) -> {
            return p_209761_.spawnOverrides;
         })).apply(p_209759_, (p_209779_, p_209780_, p_209781_, p_209782_) -> {
            return new ConfiguredStructureFeature<>(this, p_209779_, p_209780_, p_209781_, p_209782_);
         });
      });
      this.pieceGenerator = p_197169_;
      this.postPlacementProcessor = p_197170_;
   }

   public GenerationStep.Decoration step() {
      return STEP.get(this);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart loadStaticStart(StructurePieceSerializationContext p_191129_, CompoundTag p_191130_, long p_191131_) {
      String s = p_191130_.getString("id");
      if ("INVALID".equals(s)) {
         return StructureStart.INVALID_START;
      } else {
         Registry<ConfiguredStructureFeature<?, ?>> registry = p_191129_.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
         ConfiguredStructureFeature<?, ?> configuredstructurefeature = registry.get(new ResourceLocation(s));
         if (configuredstructurefeature == null) {
            LOGGER.error("Unknown feature id: {}", (Object)s);
            return null;
         } else {
            ChunkPos chunkpos = new ChunkPos(p_191130_.getInt("ChunkX"), p_191130_.getInt("ChunkZ"));
            int i = p_191130_.getInt("references");
            ListTag listtag = p_191130_.getList("Children", 10);

            try {
               PiecesContainer piecescontainer = PiecesContainer.load(listtag, p_191129_);
               if (configuredstructurefeature.feature == OCEAN_MONUMENT) {
                  piecescontainer = OceanMonumentFeature.regeneratePiecesAfterLoad(chunkpos, p_191131_, piecescontainer);
               }

               return new StructureStart(configuredstructurefeature, chunkpos, i, piecescontainer);
            } catch (Exception exception) {
               LOGGER.error("Failed Start with id {}", s, exception);
               return null;
            }
         }
      }
   }

   public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec() {
      return this.configuredStructureCodec;
   }

   public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C p_209763_, TagKey<Biome> p_209764_) {
      return this.configured(p_209763_, p_209764_, false);
   }

   public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C p_209770_, TagKey<Biome> p_209771_, boolean p_209772_) {
      return new ConfiguredStructureFeature<>(this, p_209770_, BuiltinRegistries.BIOME.getOrCreateTag(p_209771_), p_209772_, Map.of());
   }

   public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C p_209766_, TagKey<Biome> p_209767_, Map<MobCategory, StructureSpawnOverride> p_209768_) {
      return new ConfiguredStructureFeature<>(this, p_209766_, BuiltinRegistries.BIOME.getOrCreateTag(p_209767_), false, p_209768_);
   }

   public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C p_209774_, TagKey<Biome> p_209775_, boolean p_209776_, Map<MobCategory, StructureSpawnOverride> p_209777_) {
      return new ConfiguredStructureFeature<>(this, p_209774_, BuiltinRegistries.BIOME.getOrCreateTag(p_209775_), p_209776_, p_209777_);
   }

   public static BlockPos getLocatePos(RandomSpreadStructurePlacement p_204767_, ChunkPos p_204768_) {
      return (new BlockPos(p_204768_.getMinBlockX(), 0, p_204768_.getMinBlockZ())).offset(p_204767_.locateOffset());
   }

   public boolean canGenerate(RegistryAccess p_197172_, ChunkGenerator p_197173_, BiomeSource p_197174_, StructureManager p_197175_, long p_197176_, ChunkPos p_197177_, C p_197178_, LevelHeightAccessor p_197179_, Predicate<Holder<Biome>> p_197180_) {
      return this.pieceGenerator.createGenerator(new PieceGeneratorSupplier.Context<>(p_197173_, p_197174_, p_197176_, p_197177_, p_197178_, p_197179_, p_197180_, p_197175_, p_197172_)).isPresent();
   }

   public PieceGeneratorSupplier<C> pieceGeneratorSupplier() {
      return this.pieceGenerator;
   }

   public PostPlacementProcessor getPostPlacementProcessor() {
      return this.postPlacementProcessor;
   }
}
