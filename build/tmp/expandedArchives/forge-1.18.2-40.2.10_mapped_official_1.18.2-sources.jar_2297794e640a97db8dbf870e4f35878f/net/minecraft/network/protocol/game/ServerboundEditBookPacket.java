package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundEditBookPacket implements Packet<ServerGamePacketListener> {
   public static final int MAX_BYTES_PER_CHAR = 4;
   private static final int TITLE_MAX_CHARS = 128;
   private static final int PAGE_MAX_CHARS = 8192;
   private static final int MAX_PAGES_COUNT = 200;
   private final int slot;
   private final List<String> pages;
   private final Optional<String> title;

   public ServerboundEditBookPacket(int p_182749_, List<String> p_182750_, Optional<String> p_182751_) {
      this.slot = p_182749_;
      this.pages = ImmutableList.copyOf(p_182750_);
      this.title = p_182751_;
   }

   public ServerboundEditBookPacket(FriendlyByteBuf p_179592_) {
      this.slot = p_179592_.readVarInt();
      this.pages = p_179592_.readCollection(FriendlyByteBuf.limitValue(Lists::newArrayListWithCapacity, 200), (p_182763_) -> {
         return p_182763_.readUtf(8192);
      });
      this.title = p_179592_.readOptional((p_182757_) -> {
         return p_182757_.readUtf(128);
      });
   }

   public void write(FriendlyByteBuf p_134011_) {
      p_134011_.writeVarInt(this.slot);
      p_134011_.writeCollection(this.pages, (p_182759_, p_182760_) -> {
         p_182759_.writeUtf(p_182760_, 8192);
      });
      p_134011_.writeOptional(this.title, (p_182753_, p_182754_) -> {
         p_182753_.writeUtf(p_182754_, 128);
      });
   }

   public void handle(ServerGamePacketListener p_134008_) {
      p_134008_.handleEditBook(this);
   }

   public List<String> getPages() {
      return this.pages;
   }

   public Optional<String> getTitle() {
      return this.title;
   }

   public int getSlot() {
      return this.slot;
   }
}