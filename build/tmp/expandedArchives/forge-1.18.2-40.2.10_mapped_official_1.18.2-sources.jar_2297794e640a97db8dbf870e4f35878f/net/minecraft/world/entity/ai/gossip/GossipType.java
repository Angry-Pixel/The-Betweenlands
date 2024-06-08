package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum GossipType {
   MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
   MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
   MINOR_POSITIVE("minor_positive", 1, 200, 1, 5),
   MAJOR_POSITIVE("major_positive", 5, 100, 0, 100),
   TRADING("trading", 1, 25, 2, 20);

   public static final int REPUTATION_CHANGE_PER_EVENT = 25;
   public static final int REPUTATION_CHANGE_PER_EVERLASTING_MEMORY = 20;
   public static final int REPUTATION_CHANGE_PER_TRADE = 2;
   public final String id;
   public final int weight;
   public final int max;
   public final int decayPerDay;
   public final int decayPerTransfer;
   private static final Map<String, GossipType> BY_ID = Stream.of(values()).collect(ImmutableMap.toImmutableMap((p_26290_) -> {
      return p_26290_.id;
   }, Function.identity()));

   private GossipType(String p_26284_, int p_26285_, int p_26286_, int p_26287_, int p_26288_) {
      this.id = p_26284_;
      this.weight = p_26285_;
      this.max = p_26286_;
      this.decayPerDay = p_26287_;
      this.decayPerTransfer = p_26288_;
   }

   @Nullable
   public static GossipType byId(String p_26292_) {
      return BY_ID.get(p_26292_);
   }
}