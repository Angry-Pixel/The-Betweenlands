package net.minecraft.server;

import net.minecraft.commands.CommandSourceStack;

public class ConsoleInput {
   public final String msg;
   public final CommandSourceStack source;

   public ConsoleInput(String p_135931_, CommandSourceStack p_135932_) {
      this.msg = p_135931_;
      this.source = p_135932_;
   }
}