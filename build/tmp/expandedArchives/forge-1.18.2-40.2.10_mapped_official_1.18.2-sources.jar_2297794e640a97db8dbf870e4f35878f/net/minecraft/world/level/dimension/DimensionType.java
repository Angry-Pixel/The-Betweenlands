package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class DimensionType {
   public static final int BITS_FOR_Y = BlockPos.PACKED_Y_LENGTH;
   public static final int MIN_HEIGHT = 16;
   public static final int Y_SIZE = (1 << BITS_FOR_Y) - 32;
   public static final int MAX_Y = (Y_SIZE >> 1) - 1;
   public static final int MIN_Y = MAX_Y - Y_SIZE + 1;
   public static final int WAY_ABOVE_MAX_Y = MAX_Y << 4;
   public static final int WAY_BELOW_MIN_Y = MIN_Y << 4;
   public static final ResourceLocation OVERWORLD_EFFECTS = new ResourceLocation("overworld");
   public static final ResourceLocation NETHER_EFFECTS = new ResourceLocation("the_nether");
   public static final ResourceLocation END_EFFECTS = new ResourceLocation("the_end");
   public static final Codec<DimensionType> DIRECT_CODEC = RecordCodecBuilder.<DimensionType>create((p_63914_) -> {
      return p_63914_.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((p_156696_) -> {
         return p_156696_.map(OptionalLong::of).orElseGet(OptionalLong::empty);
      }, (p_156698_) -> {
         return p_156698_.isPresent() ? Optional.of(p_156698_.getAsLong()) : Optional.empty();
      }).forGetter((p_156731_) -> {
         return p_156731_.fixedTime;
      }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange((double)1.0E-5F, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::piglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), Codec.intRange(MIN_Y, MAX_Y).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, Y_SIZE).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, Y_SIZE).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.hashedCodec(Registry.BLOCK_REGISTRY).fieldOf("infiniburn").forGetter((p_204516_) -> {
         return p_204516_.infiniburn;
      }), ResourceLocation.CODEC.fieldOf("effects").orElse(OVERWORLD_EFFECTS).forGetter((p_156725_) -> {
         return p_156725_.effectsLocation;
      }), Codec.FLOAT.fieldOf("ambient_light").forGetter((p_156721_) -> {
         return p_156721_.ambientLight;
      })).apply(p_63914_, DimensionType::new);
   }).comapFlatMap(DimensionType::guardY, Function.identity());
   private static final int MOON_PHASES = 8;
   public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public static final ResourceKey<DimensionType> OVERWORLD_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld"));
   public static final ResourceKey<DimensionType> NETHER_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_nether"));
   public static final ResourceKey<DimensionType> END_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("the_end"));
   protected static final DimensionType DEFAULT_OVERWORLD = create(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, OVERWORLD_EFFECTS, 0.0F);
   protected static final DimensionType DEFAULT_NETHER = create(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 0, 256, 128, BlockTags.INFINIBURN_NETHER, NETHER_EFFECTS, 0.1F);
   protected static final DimensionType DEFAULT_END = create(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 0, 256, 256, BlockTags.INFINIBURN_END, END_EFFECTS, 0.0F);
   public static final ResourceKey<DimensionType> OVERWORLD_CAVES_LOCATION = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation("overworld_caves"));
   protected static final DimensionType DEFAULT_OVERWORLD_CAVES = create(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, OVERWORLD_EFFECTS, 0.0F);
   public static final Codec<Holder<DimensionType>> CODEC = RegistryFileCodec.create(Registry.DIMENSION_TYPE_REGISTRY, DIRECT_CODEC);
   private final OptionalLong fixedTime;
   private final boolean hasSkylight;
   private final boolean hasCeiling;
   private final boolean ultraWarm;
   private final boolean natural;
   private final double coordinateScale;
   private final boolean createDragonFight;
   private final boolean piglinSafe;
   private final boolean bedWorks;
   private final boolean respawnAnchorWorks;
   private final boolean hasRaids;
   private final int minY;
   private final int height;
   private final int logicalHeight;
   private final TagKey<Block> infiniburn;
   private final ResourceLocation effectsLocation;
   private final float ambientLight;
   public transient float[] brightnessRamp;

   private static DataResult<DimensionType> guardY(DimensionType p_156719_) {
      if (p_156719_.height() < 16) {
         return DataResult.error("height has to be at least 16");
      } else if (p_156719_.minY() + p_156719_.height() > MAX_Y + 1) {
         return DataResult.error("min_y + height cannot be higher than: " + (MAX_Y + 1));
      } else if (p_156719_.logicalHeight() > p_156719_.height()) {
         return DataResult.error("logical_height cannot be higher than height");
      } else if (p_156719_.height() % 16 != 0) {
         return DataResult.error("height has to be multiple of 16");
      } else {
         return p_156719_.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(p_156719_);
      }
   }

   private DimensionType(OptionalLong p_204454_, boolean p_204455_, boolean p_204456_, boolean p_204457_, boolean p_204458_, double p_204459_, boolean p_204460_, boolean p_204461_, boolean p_204462_, boolean p_204463_, int p_204464_, int p_204465_, int p_204466_, TagKey<Block> p_204467_, ResourceLocation p_204468_, float p_204469_) {
      this(p_204454_, p_204455_, p_204456_, p_204457_, p_204458_, p_204459_, false, p_204460_, p_204461_, p_204462_, p_204463_, p_204464_, p_204465_, p_204466_, p_204467_, p_204468_, p_204469_);
   }

   public static DimensionType create(OptionalLong p_204498_, boolean p_204499_, boolean p_204500_, boolean p_204501_, boolean p_204502_, double p_204503_, boolean p_204504_, boolean p_204505_, boolean p_204506_, boolean p_204507_, boolean p_204508_, int p_204509_, int p_204510_, int p_204511_, TagKey<Block> p_204512_, ResourceLocation p_204513_, float p_204514_) {
      DimensionType dimensiontype = new DimensionType(p_204498_, p_204499_, p_204500_, p_204501_, p_204502_, p_204503_, p_204504_, p_204505_, p_204506_, p_204507_, p_204508_, p_204509_, p_204510_, p_204511_, p_204512_, p_204513_, p_204514_);
      guardY(dimensiontype).error().ifPresent((p_156692_) -> {
         throw new IllegalStateException(p_156692_.message());
      });
      return dimensiontype;
   }

   /** @deprecated */
   @Deprecated
   public DimensionType(OptionalLong p_204471_, boolean p_204472_, boolean p_204473_, boolean p_204474_, boolean p_204475_, double p_204476_, boolean p_204477_, boolean p_204478_, boolean p_204479_, boolean p_204480_, boolean p_204481_, int p_204482_, int p_204483_, int p_204484_, TagKey<Block> p_204485_, ResourceLocation p_204486_, float p_204487_) {
      this.fixedTime = p_204471_;
      this.hasSkylight = p_204472_;
      this.hasCeiling = p_204473_;
      this.ultraWarm = p_204474_;
      this.natural = p_204475_;
      this.coordinateScale = p_204476_;
      this.createDragonFight = p_204477_;
      this.piglinSafe = p_204478_;
      this.bedWorks = p_204479_;
      this.respawnAnchorWorks = p_204480_;
      this.hasRaids = p_204481_;
      this.minY = p_204482_;
      this.height = p_204483_;
      this.logicalHeight = p_204484_;
      this.infiniburn = p_204485_;
      this.effectsLocation = p_204486_;
      this.ambientLight = p_204487_;
      this.brightnessRamp = fillBrightnessRamp(p_204487_);
   }

   private static float[] fillBrightnessRamp(float p_63901_) {
      float[] afloat = new float[16];

      for(int i = 0; i <= 15; ++i) {
         float f = (float)i / 15.0F;
         float f1 = f / (4.0F - 3.0F * f);
         afloat[i] = Mth.lerp(p_63901_, f1, 1.0F);
      }

      return afloat;
   }

   /** @deprecated */
   @Deprecated
   public static DataResult<ResourceKey<Level>> parseLegacy(Dynamic<?> p_63912_) {
      Optional<Number> optional = p_63912_.asNumber().result();
      if (optional.isPresent()) {
         int i = optional.get().intValue();
         if (i == -1) {
            return DataResult.success(Level.NETHER);
         }

         if (i == 0) {
            return DataResult.success(Level.OVERWORLD);
         }

         if (i == 1) {
            return DataResult.success(Level.END);
         }
      }

      return Level.RESOURCE_KEY_CODEC.parse(p_63912_);
   }

   public static RegistryAccess.Writable registerBuiltin(RegistryAccess.Writable p_204489_) {
      WritableRegistry<DimensionType> writableregistry = p_204489_.ownedWritableRegistryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      writableregistry.register(OVERWORLD_LOCATION, DEFAULT_OVERWORLD, Lifecycle.stable());
      writableregistry.register(OVERWORLD_CAVES_LOCATION, DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
      writableregistry.register(NETHER_LOCATION, DEFAULT_NETHER, Lifecycle.stable());
      writableregistry.register(END_LOCATION, DEFAULT_END, Lifecycle.stable());
      return p_204489_;
   }

   public static Registry<LevelStem> defaultDimensions(RegistryAccess p_204491_, long p_204492_) {
      return defaultDimensions(p_204491_, p_204492_, true);
   }

   public static Registry<LevelStem> defaultDimensions(RegistryAccess p_204494_, long p_204495_, boolean p_204496_) {
      WritableRegistry<LevelStem> writableregistry = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function<LevelStem, Holder.Reference<LevelStem>>)null);
      Registry<DimensionType> registry = p_204494_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<Biome> registry1 = p_204494_.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<StructureSet> registry2 = p_204494_.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      Registry<NoiseGeneratorSettings> registry3 = p_204494_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Registry<NormalNoise.NoiseParameters> registry4 = p_204494_.registryOrThrow(Registry.NOISE_REGISTRY);
      writableregistry.register(LevelStem.NETHER, new LevelStem(registry.getOrCreateHolder(NETHER_LOCATION), new NoiseBasedChunkGenerator(registry2, registry4, MultiNoiseBiomeSource.Preset.NETHER.biomeSource(registry1, p_204496_), p_204495_, registry3.getOrCreateHolder(NoiseGeneratorSettings.NETHER))), Lifecycle.stable());
      writableregistry.register(LevelStem.END, new LevelStem(registry.getOrCreateHolder(END_LOCATION), new NoiseBasedChunkGenerator(registry2, registry4, new TheEndBiomeSource(registry1, p_204495_), p_204495_, registry3.getOrCreateHolder(NoiseGeneratorSettings.END))), Lifecycle.stable());
      return writableregistry;
   }

   public static double getTeleportationScale(DimensionType p_63909_, DimensionType p_63910_) {
      double d0 = p_63909_.coordinateScale();
      double d1 = p_63910_.coordinateScale();
      return d0 / d1;
   }

   public static Path getStorageFolder(ResourceKey<Level> p_196976_, Path p_196977_) {
      if (p_196976_ == Level.OVERWORLD) {
         return p_196977_;
      } else if (p_196976_ == Level.END) {
         return p_196977_.resolve("DIM1");
      } else {
         return p_196976_ == Level.NETHER ? p_196977_.resolve("DIM-1") : p_196977_.resolve("dimensions").resolve(p_196976_.location().getNamespace()).resolve(p_196976_.location().getPath());
      }
   }

   public boolean hasSkyLight() {
      return this.hasSkylight;
   }

   public boolean hasCeiling() {
      return this.hasCeiling;
   }

   public boolean ultraWarm() {
      return this.ultraWarm;
   }

   public boolean natural() {
      return this.natural;
   }

   public double coordinateScale() {
      return this.coordinateScale;
   }

   public boolean piglinSafe() {
      return this.piglinSafe;
   }

   public boolean bedWorks() {
      return this.bedWorks;
   }

   public boolean respawnAnchorWorks() {
      return this.respawnAnchorWorks;
   }

   public boolean hasRaids() {
      return this.hasRaids;
   }

   public int minY() {
      return this.minY;
   }

   public int height() {
      return this.height;
   }

   public int logicalHeight() {
      return this.logicalHeight;
   }

   public boolean createDragonFight() {
      return this.createDragonFight;
   }

   public boolean hasFixedTime() {
      return this.fixedTime.isPresent();
   }

   public float timeOfDay(long p_63905_) {
      double d0 = Mth.frac((double)this.fixedTime.orElse(p_63905_) / 24000.0D - 0.25D);
      double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
      return (float)(d0 * 2.0D + d1) / 3.0F;
   }

   public int moonPhase(long p_63937_) {
      return (int)(p_63937_ / 24000L % 8L + 8L) % 8;
   }

   public float brightness(int p_63903_) {
      return this.brightnessRamp[p_63903_];
   }

   public TagKey<Block> infiniburn() {
      return this.infiniburn;
   }

   public ResourceLocation effectsLocation() {
      return this.effectsLocation;
   }
}