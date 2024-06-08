package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class GeodeBlockSettings {
   public final BlockStateProvider fillingProvider;
   public final BlockStateProvider innerLayerProvider;
   public final BlockStateProvider alternateInnerLayerProvider;
   public final BlockStateProvider middleLayerProvider;
   public final BlockStateProvider outerLayerProvider;
   public final List<BlockState> innerPlacements;
   public final TagKey<Block> cannotReplace;
   public final TagKey<Block> invalidBlocks;
   public static final Codec<GeodeBlockSettings> CODEC = RecordCodecBuilder.create((p_158307_) -> {
      return p_158307_.group(BlockStateProvider.CODEC.fieldOf("filling_provider").forGetter((p_158323_) -> {
         return p_158323_.fillingProvider;
      }), BlockStateProvider.CODEC.fieldOf("inner_layer_provider").forGetter((p_158321_) -> {
         return p_158321_.innerLayerProvider;
      }), BlockStateProvider.CODEC.fieldOf("alternate_inner_layer_provider").forGetter((p_158319_) -> {
         return p_158319_.alternateInnerLayerProvider;
      }), BlockStateProvider.CODEC.fieldOf("middle_layer_provider").forGetter((p_158317_) -> {
         return p_158317_.middleLayerProvider;
      }), BlockStateProvider.CODEC.fieldOf("outer_layer_provider").forGetter((p_158315_) -> {
         return p_158315_.outerLayerProvider;
      }), ExtraCodecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("inner_placements").forGetter((p_158313_) -> {
         return p_158313_.innerPlacements;
      }), TagKey.hashedCodec(Registry.BLOCK_REGISTRY).fieldOf("cannot_replace").forGetter((p_204566_) -> {
         return p_204566_.cannotReplace;
      }), TagKey.hashedCodec(Registry.BLOCK_REGISTRY).fieldOf("invalid_blocks").forGetter((p_204564_) -> {
         return p_204564_.invalidBlocks;
      })).apply(p_158307_, GeodeBlockSettings::new);
   });

   public GeodeBlockSettings(BlockStateProvider p_204555_, BlockStateProvider p_204556_, BlockStateProvider p_204557_, BlockStateProvider p_204558_, BlockStateProvider p_204559_, List<BlockState> p_204560_, TagKey<Block> p_204561_, TagKey<Block> p_204562_) {
      this.fillingProvider = p_204555_;
      this.innerLayerProvider = p_204556_;
      this.alternateInnerLayerProvider = p_204557_;
      this.middleLayerProvider = p_204558_;
      this.outerLayerProvider = p_204559_;
      this.innerPlacements = p_204560_;
      this.cannotReplace = p_204561_;
      this.invalidBlocks = p_204562_;
   }
}