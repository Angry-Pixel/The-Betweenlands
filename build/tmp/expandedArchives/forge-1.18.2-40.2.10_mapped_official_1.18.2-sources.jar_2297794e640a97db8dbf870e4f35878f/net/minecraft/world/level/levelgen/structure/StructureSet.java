package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSet.StructureSelectionEntry> structures, StructurePlacement placement) {
   public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create((p_210014_) -> {
      return p_210014_.group(StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(p_210014_, StructureSet::new);
   });
   public static final Codec<Holder<StructureSet>> CODEC = RegistryFileCodec.create(Registry.STRUCTURE_SET_REGISTRY, DIRECT_CODEC);

   public StructureSet(Holder<ConfiguredStructureFeature<?, ?>> p_210007_, StructurePlacement p_210008_) {
      this(List.of(new StructureSet.StructureSelectionEntry(p_210007_, 1)), p_210008_);
   }

   public static StructureSet.StructureSelectionEntry entry(Holder<ConfiguredStructureFeature<?, ?>> p_210018_, int p_210019_) {
      return new StructureSet.StructureSelectionEntry(p_210018_, p_210019_);
   }

   public static StructureSet.StructureSelectionEntry entry(Holder<ConfiguredStructureFeature<?, ?>> p_210016_) {
      return new StructureSet.StructureSelectionEntry(p_210016_, 1);
   }

   public static record StructureSelectionEntry(Holder<ConfiguredStructureFeature<?, ?>> structure, int weight) {
      public static final Codec<StructureSet.StructureSelectionEntry> CODEC = RecordCodecBuilder.create((p_210034_) -> {
         return p_210034_.group(ConfiguredStructureFeature.CODEC.fieldOf("structure").forGetter(StructureSet.StructureSelectionEntry::structure), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.StructureSelectionEntry::weight)).apply(p_210034_, StructureSet.StructureSelectionEntry::new);
      });

      public boolean generatesInMatchingBiome(Predicate<Holder<Biome>> p_210036_) {
         HolderSet<Biome> holderset = this.structure().value().biomes();
         return holderset.stream().anyMatch(p_210036_);
      }
   }
}