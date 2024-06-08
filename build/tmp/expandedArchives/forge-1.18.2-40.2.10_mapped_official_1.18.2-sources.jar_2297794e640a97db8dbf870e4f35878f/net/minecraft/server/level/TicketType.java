package net.minecraft.server.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;

public class TicketType<T> {
   private final String name;
   private final Comparator<T> comparator;
   private final long timeout;
   public static final TicketType<Unit> START = create("start", (p_9471_, p_9472_) -> {
      return 0;
   });
   public static final TicketType<Unit> DRAGON = create("dragon", (p_9460_, p_9461_) -> {
      return 0;
   });
   public static final TicketType<ChunkPos> PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType<ChunkPos> FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType<ChunkPos> LIGHT = create("light", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType<BlockPos> PORTAL = create("portal", Vec3i::compareTo, 300);
   public static final TicketType<Integer> POST_TELEPORT = create("post_teleport", Integer::compareTo, 5);
   public static final TicketType<ChunkPos> UNKNOWN = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

   public static <T> TicketType<T> create(String p_9463_, Comparator<T> p_9464_) {
      return new TicketType<>(p_9463_, p_9464_, 0L);
   }

   public static <T> TicketType<T> create(String p_9466_, Comparator<T> p_9467_, int p_9468_) {
      return new TicketType<>(p_9466_, p_9467_, (long)p_9468_);
   }

   protected TicketType(String p_9455_, Comparator<T> p_9456_, long p_9457_) {
      this.name = p_9455_;
      this.comparator = p_9456_;
      this.timeout = p_9457_;
   }

   public String toString() {
      return this.name;
   }

   public Comparator<T> getComparator() {
      return this.comparator;
   }

   public long timeout() {
      return this.timeout;
   }
}