package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class FossilFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<FossilFeatureConfiguration> CODEC = RecordCodecBuilder.create((p_159816_) -> {
      return p_159816_.group(ResourceLocation.CODEC.listOf().fieldOf("fossil_structures").forGetter((p_159830_) -> {
         return p_159830_.fossilStructures;
      }), ResourceLocation.CODEC.listOf().fieldOf("overlay_structures").forGetter((p_159828_) -> {
         return p_159828_.overlayStructures;
      }), StructureProcessorType.LIST_CODEC.fieldOf("fossil_processors").forGetter((p_204759_) -> {
         return p_204759_.fossilProcessors;
      }), StructureProcessorType.LIST_CODEC.fieldOf("overlay_processors").forGetter((p_204757_) -> {
         return p_204757_.overlayProcessors;
      }), Codec.intRange(0, 7).fieldOf("max_empty_corners_allowed").forGetter((p_159818_) -> {
         return p_159818_.maxEmptyCornersAllowed;
      })).apply(p_159816_, FossilFeatureConfiguration::new);
   });
   public final List<ResourceLocation> fossilStructures;
   public final List<ResourceLocation> overlayStructures;
   public final Holder<StructureProcessorList> fossilProcessors;
   public final Holder<StructureProcessorList> overlayProcessors;
   public final int maxEmptyCornersAllowed;

   public FossilFeatureConfiguration(List<ResourceLocation> p_204751_, List<ResourceLocation> p_204752_, Holder<StructureProcessorList> p_204753_, Holder<StructureProcessorList> p_204754_, int p_204755_) {
      if (p_204751_.isEmpty()) {
         throw new IllegalArgumentException("Fossil structure lists need at least one entry");
      } else if (p_204751_.size() != p_204752_.size()) {
         throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
      } else {
         this.fossilStructures = p_204751_;
         this.overlayStructures = p_204752_;
         this.fossilProcessors = p_204753_;
         this.overlayProcessors = p_204754_;
         this.maxEmptyCornersAllowed = p_204755_;
      }
   }
}