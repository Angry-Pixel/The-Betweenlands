package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RootSystemConfiguration implements FeatureConfiguration {
   public static final Codec<RootSystemConfiguration> CODEC = RecordCodecBuilder.create((p_198371_) -> {
      return p_198371_.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((p_204840_) -> {
         return p_204840_.treeFeature;
      }), Codec.intRange(1, 64).fieldOf("required_vertical_space_for_tree").forGetter((p_161151_) -> {
         return p_161151_.requiredVerticalSpaceForTree;
      }), Codec.intRange(1, 64).fieldOf("root_radius").forGetter((p_161149_) -> {
         return p_161149_.rootRadius;
      }), TagKey.hashedCodec(Registry.BLOCK_REGISTRY).fieldOf("root_replaceable").forGetter((p_204838_) -> {
         return p_204838_.rootReplaceable;
      }), BlockStateProvider.CODEC.fieldOf("root_state_provider").forGetter((p_161145_) -> {
         return p_161145_.rootStateProvider;
      }), Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter((p_161143_) -> {
         return p_161143_.rootPlacementAttempts;
      }), Codec.intRange(1, 4096).fieldOf("root_column_max_height").forGetter((p_161141_) -> {
         return p_161141_.rootColumnMaxHeight;
      }), Codec.intRange(1, 64).fieldOf("hanging_root_radius").forGetter((p_161139_) -> {
         return p_161139_.hangingRootRadius;
      }), Codec.intRange(0, 16).fieldOf("hanging_roots_vertical_span").forGetter((p_161137_) -> {
         return p_161137_.hangingRootsVerticalSpan;
      }), BlockStateProvider.CODEC.fieldOf("hanging_root_state_provider").forGetter((p_161135_) -> {
         return p_161135_.hangingRootStateProvider;
      }), Codec.intRange(1, 256).fieldOf("hanging_root_placement_attempts").forGetter((p_161133_) -> {
         return p_161133_.hangingRootPlacementAttempts;
      }), Codec.intRange(1, 64).fieldOf("allowed_vertical_water_for_tree").forGetter((p_161131_) -> {
         return p_161131_.allowedVerticalWaterForTree;
      }), BlockPredicate.CODEC.fieldOf("allowed_tree_position").forGetter((p_198373_) -> {
         return p_198373_.allowedTreePosition;
      })).apply(p_198371_, RootSystemConfiguration::new);
   });
   public final Holder<PlacedFeature> treeFeature;
   public final int requiredVerticalSpaceForTree;
   public final int rootRadius;
   public final TagKey<Block> rootReplaceable;
   public final BlockStateProvider rootStateProvider;
   public final int rootPlacementAttempts;
   public final int rootColumnMaxHeight;
   public final int hangingRootRadius;
   public final int hangingRootsVerticalSpan;
   public final BlockStateProvider hangingRootStateProvider;
   public final int hangingRootPlacementAttempts;
   public final int allowedVerticalWaterForTree;
   public final BlockPredicate allowedTreePosition;

   public RootSystemConfiguration(Holder<PlacedFeature> p_204824_, int p_204825_, int p_204826_, TagKey<Block> p_204827_, BlockStateProvider p_204828_, int p_204829_, int p_204830_, int p_204831_, int p_204832_, BlockStateProvider p_204833_, int p_204834_, int p_204835_, BlockPredicate p_204836_) {
      this.treeFeature = p_204824_;
      this.requiredVerticalSpaceForTree = p_204825_;
      this.rootRadius = p_204826_;
      this.rootReplaceable = p_204827_;
      this.rootStateProvider = p_204828_;
      this.rootPlacementAttempts = p_204829_;
      this.rootColumnMaxHeight = p_204830_;
      this.hangingRootRadius = p_204831_;
      this.hangingRootsVerticalSpan = p_204832_;
      this.hangingRootStateProvider = p_204833_;
      this.hangingRootPlacementAttempts = p_204834_;
      this.allowedVerticalWaterForTree = p_204835_;
      this.allowedTreePosition = p_204836_;
   }
}