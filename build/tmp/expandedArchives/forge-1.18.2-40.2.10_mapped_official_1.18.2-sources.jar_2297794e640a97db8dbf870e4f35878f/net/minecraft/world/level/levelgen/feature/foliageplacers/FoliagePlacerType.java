package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class FoliagePlacerType<P extends FoliagePlacer> extends net.minecraftforge.registries.ForgeRegistryEntry<FoliagePlacerType<?>> {
   public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer.CODEC);
   public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer.CODEC);
   public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer.CODEC);
   public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
   public static final FoliagePlacerType<BushFoliagePlacer> BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", BushFoliagePlacer.CODEC);
   public static final FoliagePlacerType<FancyFoliagePlacer> FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", FancyFoliagePlacer.CODEC);
   public static final FoliagePlacerType<MegaJungleFoliagePlacer> MEGA_JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", MegaJungleFoliagePlacer.CODEC);
   public static final FoliagePlacerType<MegaPineFoliagePlacer> MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
   public static final FoliagePlacerType<DarkOakFoliagePlacer> DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
   public static final FoliagePlacerType<RandomSpreadFoliagePlacer> RANDOM_SPREAD_FOLIAGE_PLACER = register("random_spread_foliage_placer", RandomSpreadFoliagePlacer.CODEC);
   private final Codec<P> codec;

   private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String p_68606_, Codec<P> p_68607_) {
      return Registry.register(Registry.FOLIAGE_PLACER_TYPES, p_68606_, new FoliagePlacerType<>(p_68607_));
   }

   public FoliagePlacerType(Codec<P> p_68603_) {
      this.codec = p_68603_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
