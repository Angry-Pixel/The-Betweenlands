package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;

public final class BelowZeroRetrogen {
   private static final BitSet EMPTY = new BitSet(0);
   private static final Codec<BitSet> BITSET_CODEC = Codec.LONG_STREAM.xmap((p_188484_) -> {
      return BitSet.valueOf(p_188484_.toArray());
   }, (p_188482_) -> {
      return LongStream.of(p_188482_.toLongArray());
   });
   private static final Codec<ChunkStatus> NON_EMPTY_CHUNK_STATUS = Registry.CHUNK_STATUS.byNameCodec().comapFlatMap((p_188473_) -> {
      return p_188473_ == ChunkStatus.EMPTY ? DataResult.error("target_status cannot be empty") : DataResult.success(p_188473_);
   }, Function.identity());
   public static final Codec<BelowZeroRetrogen> CODEC = RecordCodecBuilder.create((p_188471_) -> {
      return p_188471_.group(NON_EMPTY_CHUNK_STATUS.fieldOf("target_status").forGetter(BelowZeroRetrogen::targetStatus), BITSET_CODEC.optionalFieldOf("missing_bedrock").forGetter((p_188480_) -> {
         return p_188480_.missingBedrock.isEmpty() ? Optional.empty() : Optional.of(p_188480_.missingBedrock);
      })).apply(p_188471_, BelowZeroRetrogen::new);
   });
   private static final Set<ResourceKey<Biome>> RETAINED_RETROGEN_BIOMES = Set.of(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES);
   public static final LevelHeightAccessor UPGRADE_HEIGHT_ACCESSOR = new LevelHeightAccessor() {
      public int getHeight() {
         return 64;
      }

      public int getMinBuildHeight() {
         return -64;
      }
   };
   private final ChunkStatus targetStatus;
   private final BitSet missingBedrock;

   private BelowZeroRetrogen(ChunkStatus p_188464_, Optional<BitSet> p_188465_) {
      this.targetStatus = p_188464_;
      this.missingBedrock = p_188465_.orElse(EMPTY);
   }

   @Nullable
   public static BelowZeroRetrogen read(CompoundTag p_188486_) {
      ChunkStatus chunkstatus = ChunkStatus.byName(p_188486_.getString("target_status"));
      return chunkstatus == ChunkStatus.EMPTY ? null : new BelowZeroRetrogen(chunkstatus, Optional.of(BitSet.valueOf(p_188486_.getLongArray("missing_bedrock"))));
   }

   public static void replaceOldBedrock(ProtoChunk p_188475_) {
      int i = 4;
      BlockPos.betweenClosed(0, 0, 0, 15, 4, 15).forEach((p_188492_) -> {
         if (p_188475_.getBlockState(p_188492_).is(Blocks.BEDROCK)) {
            p_188475_.setBlockState(p_188492_, Blocks.DEEPSLATE.defaultBlockState(), false);
         }

      });
   }

   public void applyBedrockMask(ProtoChunk p_198222_) {
      LevelHeightAccessor levelheightaccessor = p_198222_.getHeightAccessorForGeneration();
      int i = levelheightaccessor.getMinBuildHeight();
      int j = levelheightaccessor.getMaxBuildHeight() - 1;

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            if (this.hasBedrockHole(k, l)) {
               BlockPos.betweenClosed(k, i, l, k, j, l).forEach((p_198219_) -> {
                  p_198222_.setBlockState(p_198219_, Blocks.AIR.defaultBlockState(), false);
               });
            }
         }
      }

   }

   public ChunkStatus targetStatus() {
      return this.targetStatus;
   }

   public boolean hasBedrockHoles() {
      return !this.missingBedrock.isEmpty();
   }

   public boolean hasBedrockHole(int p_198215_, int p_198216_) {
      return this.missingBedrock.get((p_198216_ & 15) * 16 + (p_198215_ & 15));
   }

   public static BiomeResolver getBiomeResolver(BiomeResolver p_204532_, ChunkAccess p_204533_) {
      if (!p_204533_.isUpgrading()) {
         return p_204532_;
      } else {
         Predicate<ResourceKey<Biome>> predicate = RETAINED_RETROGEN_BIOMES::contains;
         return (p_204538_, p_204539_, p_204540_, p_204541_) -> {
            Holder<Biome> holder = p_204532_.getNoiseBiome(p_204538_, p_204539_, p_204540_, p_204541_);
            return holder.is(predicate) ? holder : p_204533_.getNoiseBiome(p_204538_, 0, p_204540_);
         };
      }
   }
}