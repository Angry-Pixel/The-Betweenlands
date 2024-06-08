package net.minecraft.world.entity.player;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;

public enum ChatVisiblity {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final ChatVisiblity[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ChatVisiblity::getId)).toArray((p_35970_) -> {
      return new ChatVisiblity[p_35970_];
   });
   private final int id;
   private final String key;

   private ChatVisiblity(int p_35963_, String p_35964_) {
      this.id = p_35963_;
      this.key = p_35964_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static ChatVisiblity byId(int p_35967_) {
      return BY_ID[Mth.positiveModulo(p_35967_, BY_ID.length)];
   }
}