package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
   public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
   public static final long SILVER_RATIO_64 = 7640891576956012809L;
   private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

   @VisibleForTesting
   public static long mixStafford13(long p_189330_) {
      p_189330_ = (p_189330_ ^ p_189330_ >>> 30) * -4658895280553007687L;
      p_189330_ = (p_189330_ ^ p_189330_ >>> 27) * -7723592293110705685L;
      return p_189330_ ^ p_189330_ >>> 31;
   }

   public static RandomSupport.Seed128bit upgradeSeedTo128bit(long p_189332_) {
      long i = p_189332_ ^ 7640891576956012809L;
      long j = i + -7046029254386353131L;
      return new RandomSupport.Seed128bit(mixStafford13(i), mixStafford13(j));
   }

   public static long seedUniquifier() {
      return SEED_UNIQUIFIER.updateAndGet((p_189334_) -> {
         return p_189334_ * 1181783497276652981L;
      }) ^ System.nanoTime();
   }

   public static record Seed128bit(long seedLo, long seedHi) {
   }
}