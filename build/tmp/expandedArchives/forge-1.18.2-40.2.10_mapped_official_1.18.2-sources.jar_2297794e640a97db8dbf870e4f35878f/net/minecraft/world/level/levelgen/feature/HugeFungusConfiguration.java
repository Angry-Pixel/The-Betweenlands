package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class HugeFungusConfiguration implements FeatureConfiguration {
   public static final Codec<HugeFungusConfiguration> CODEC = RecordCodecBuilder.create((p_65912_) -> {
      return p_65912_.group(BlockState.CODEC.fieldOf("valid_base_block").forGetter((p_159875_) -> {
         return p_159875_.validBaseState;
      }), BlockState.CODEC.fieldOf("stem_state").forGetter((p_159873_) -> {
         return p_159873_.stemState;
      }), BlockState.CODEC.fieldOf("hat_state").forGetter((p_159871_) -> {
         return p_159871_.hatState;
      }), BlockState.CODEC.fieldOf("decor_state").forGetter((p_159869_) -> {
         return p_159869_.decorState;
      }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((p_159867_) -> {
         return p_159867_.planted;
      })).apply(p_65912_, HugeFungusConfiguration::new);
   });
   public final BlockState validBaseState;
   public final BlockState stemState;
   public final BlockState hatState;
   public final BlockState decorState;
   public final boolean planted;

   public HugeFungusConfiguration(BlockState p_65904_, BlockState p_65905_, BlockState p_65906_, BlockState p_65907_, boolean p_65908_) {
      this.validBaseState = p_65904_;
      this.stemState = p_65905_;
      this.hatState = p_65906_;
      this.decorState = p_65907_;
      this.planted = p_65908_;
   }
}