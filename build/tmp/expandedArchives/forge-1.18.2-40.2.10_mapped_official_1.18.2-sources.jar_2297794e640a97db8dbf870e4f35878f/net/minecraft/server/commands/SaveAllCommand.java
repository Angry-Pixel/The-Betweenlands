package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138272_) {
      p_138272_.register(Commands.literal("save-all").requires((p_138276_) -> {
         return p_138276_.hasPermission(4);
      }).executes((p_138281_) -> {
         return saveAll(p_138281_.getSource(), false);
      }).then(Commands.literal("flush").executes((p_138274_) -> {
         return saveAll(p_138274_.getSource(), true);
      })));
   }

   private static int saveAll(CommandSourceStack p_138278_, boolean p_138279_) throws CommandSyntaxException {
      p_138278_.sendSuccess(new TranslatableComponent("commands.save.saving"), false);
      MinecraftServer minecraftserver = p_138278_.getServer();
      boolean flag = minecraftserver.saveEverything(true, p_138279_, true);
      if (!flag) {
         throw ERROR_FAILED.create();
      } else {
         p_138278_.sendSuccess(new TranslatableComponent("commands.save.success"), true);
         return 1;
      }
   }
}