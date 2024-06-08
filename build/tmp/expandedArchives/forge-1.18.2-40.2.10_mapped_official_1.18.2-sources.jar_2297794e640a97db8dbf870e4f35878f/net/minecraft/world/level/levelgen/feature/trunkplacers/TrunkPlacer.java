package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public abstract class TrunkPlacer {
   public static final Codec<TrunkPlacer> CODEC = Registry.TRUNK_PLACER_TYPES.byNameCodec().dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
   private static final int MAX_BASE_HEIGHT = 32;
   private static final int MAX_RAND = 24;
   public static final int MAX_HEIGHT = 80;
   protected final int baseHeight;
   protected final int heightRandA;
   protected final int heightRandB;

   protected static <P extends TrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> trunkPlacerParts(Instance<P> p_70306_) {
      return p_70306_.group(Codec.intRange(0, 32).fieldOf("base_height").forGetter((p_70314_) -> {
         return p_70314_.baseHeight;
      }), Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter((p_70312_) -> {
         return p_70312_.heightRandA;
      }), Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter((p_70308_) -> {
         return p_70308_.heightRandB;
      }));
   }

   public TrunkPlacer(int p_70268_, int p_70269_, int p_70270_) {
      this.baseHeight = p_70268_;
      this.heightRandA = p_70269_;
      this.heightRandB = p_70270_;
   }

   protected abstract TrunkPlacerType<?> type();

   public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader p_161868_, BiConsumer<BlockPos, BlockState> p_161869_, Random p_161870_, int p_161871_, BlockPos p_161872_, TreeConfiguration p_161873_);

   public int getTreeHeight(Random p_70310_) {
      return this.baseHeight + p_70310_.nextInt(this.heightRandA + 1) + p_70310_.nextInt(this.heightRandB + 1);
   }

   private static boolean isDirt(LevelSimulatedReader p_70296_, BlockPos p_70297_) {
      return p_70296_.isStateAtPosition(p_70297_, (p_70304_) -> {
         return Feature.isDirt(p_70304_) && !p_70304_.is(Blocks.GRASS_BLOCK) && !p_70304_.is(Blocks.MYCELIUM);
      });
   }

   protected static void setDirtAt(LevelSimulatedReader p_161881_, BiConsumer<BlockPos, BlockState> p_161882_, Random p_161883_, BlockPos p_161884_, TreeConfiguration p_161885_) {
      if (p_161885_.forceDirt || !isDirt(p_161881_, p_161884_)) {
         p_161882_.accept(p_161884_, p_161885_.dirtProvider.getState(p_161883_, p_161884_));
      }

   }

   protected static boolean placeLog(LevelSimulatedReader p_161894_, BiConsumer<BlockPos, BlockState> p_161895_, Random p_161896_, BlockPos p_161897_, TreeConfiguration p_161898_) {
      return placeLog(p_161894_, p_161895_, p_161896_, p_161897_, p_161898_, Function.identity());
   }

   protected static boolean placeLog(LevelSimulatedReader p_161887_, BiConsumer<BlockPos, BlockState> p_161888_, Random p_161889_, BlockPos p_161890_, TreeConfiguration p_161891_, Function<BlockState, BlockState> p_161892_) {
      if (TreeFeature.validTreePos(p_161887_, p_161890_)) {
         p_161888_.accept(p_161890_, p_161892_.apply(p_161891_.trunkProvider.getState(p_161889_, p_161890_)));
         return true;
      } else {
         return false;
      }
   }

   protected static void placeLogIfFree(LevelSimulatedReader p_161875_, BiConsumer<BlockPos, BlockState> p_161876_, Random p_161877_, BlockPos.MutableBlockPos p_161878_, TreeConfiguration p_161879_) {
      if (TreeFeature.isFree(p_161875_, p_161878_)) {
         placeLog(p_161875_, p_161876_, p_161877_, p_161878_, p_161879_);
      }

   }
}