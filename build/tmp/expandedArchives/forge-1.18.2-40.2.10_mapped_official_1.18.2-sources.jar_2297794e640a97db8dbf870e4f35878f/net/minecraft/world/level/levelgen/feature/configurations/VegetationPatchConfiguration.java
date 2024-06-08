package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationPatchConfiguration implements FeatureConfiguration {
   public static final Codec<VegetationPatchConfiguration> CODEC = RecordCodecBuilder.create((p_161304_) -> {
      return p_161304_.group(TagKey.hashedCodec(Registry.BLOCK_REGISTRY).fieldOf("replaceable").forGetter((p_204869_) -> {
         return p_204869_.replaceable;
      }), BlockStateProvider.CODEC.fieldOf("ground_state").forGetter((p_161322_) -> {
         return p_161322_.groundState;
      }), PlacedFeature.CODEC.fieldOf("vegetation_feature").forGetter((p_204867_) -> {
         return p_204867_.vegetationFeature;
      }), CaveSurface.CODEC.fieldOf("surface").forGetter((p_161318_) -> {
         return p_161318_.surface;
      }), IntProvider.codec(1, 128).fieldOf("depth").forGetter((p_161316_) -> {
         return p_161316_.depth;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter((p_161314_) -> {
         return p_161314_.extraBottomBlockChance;
      }), Codec.intRange(1, 256).fieldOf("vertical_range").forGetter((p_161312_) -> {
         return p_161312_.verticalRange;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter((p_161310_) -> {
         return p_161310_.vegetationChance;
      }), IntProvider.CODEC.fieldOf("xz_radius").forGetter((p_161308_) -> {
         return p_161308_.xzRadius;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter((p_161306_) -> {
         return p_161306_.extraEdgeColumnChance;
      })).apply(p_161304_, VegetationPatchConfiguration::new);
   });
   public final TagKey<Block> replaceable;
   public final BlockStateProvider groundState;
   public final Holder<PlacedFeature> vegetationFeature;
   public final CaveSurface surface;
   public final IntProvider depth;
   public final float extraBottomBlockChance;
   public final int verticalRange;
   public final float vegetationChance;
   public final IntProvider xzRadius;
   public final float extraEdgeColumnChance;

   public VegetationPatchConfiguration(TagKey<Block> p_204856_, BlockStateProvider p_204857_, Holder<PlacedFeature> p_204858_, CaveSurface p_204859_, IntProvider p_204860_, float p_204861_, int p_204862_, float p_204863_, IntProvider p_204864_, float p_204865_) {
      this.replaceable = p_204856_;
      this.groundState = p_204857_;
      this.vegetationFeature = p_204858_;
      this.surface = p_204859_;
      this.depth = p_204860_;
      this.extraBottomBlockChance = p_204861_;
      this.verticalRange = p_204862_;
      this.vegetationChance = p_204863_;
      this.xzRadius = p_204864_;
      this.extraEdgeColumnChance = p_204865_;
   }
}