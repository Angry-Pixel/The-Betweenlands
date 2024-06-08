package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class UniformHeight extends HeightProvider {
   public static final Codec<UniformHeight> CODEC = RecordCodecBuilder.create((p_162033_) -> {
      return p_162033_.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((p_162043_) -> {
         return p_162043_.minInclusive;
      }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((p_162038_) -> {
         return p_162038_.maxInclusive;
      })).apply(p_162033_, UniformHeight::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final LongSet warnedFor = new LongOpenHashSet();

   private UniformHeight(VerticalAnchor p_162029_, VerticalAnchor p_162030_) {
      this.minInclusive = p_162029_;
      this.maxInclusive = p_162030_;
   }

   public static UniformHeight of(VerticalAnchor p_162035_, VerticalAnchor p_162036_) {
      return new UniformHeight(p_162035_, p_162036_);
   }

   public int sample(Random p_162040_, WorldGenerationContext p_162041_) {
      int i = this.minInclusive.resolveY(p_162041_);
      int j = this.maxInclusive.resolveY(p_162041_);
      if (i > j) {
         if (this.warnedFor.add((long)i << 32 | (long)j)) {
            LOGGER.warn("Empty height range: {}", (Object)this);
         }

         return i;
      } else {
         return Mth.randomBetweenInclusive(p_162040_, i, j);
      }
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.UNIFORM;
   }

   public String toString() {
      return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}