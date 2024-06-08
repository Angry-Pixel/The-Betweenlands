package net.minecraft.commands;

import java.util.UUID;
import net.minecraft.network.chat.Component;

public interface CommandSource {
   CommandSource NULL = new CommandSource() {
      public void sendMessage(Component p_80172_, UUID p_80173_) {
      }

      public boolean acceptsSuccess() {
         return false;
      }

      public boolean acceptsFailure() {
         return false;
      }

      public boolean shouldInformAdmins() {
         return false;
      }
   };

   void sendMessage(Component p_80166_, UUID p_80167_);

   boolean acceptsSuccess();

   boolean acceptsFailure();

   boolean shouldInformAdmins();

   default boolean alwaysAccepts() {
      return false;
   }
}