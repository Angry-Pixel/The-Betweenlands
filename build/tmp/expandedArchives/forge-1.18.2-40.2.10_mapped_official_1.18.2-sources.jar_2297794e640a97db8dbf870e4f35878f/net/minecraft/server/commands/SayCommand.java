package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class SayCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138410_) {
      p_138410_.register(Commands.literal("say").requires((p_138414_) -> {
         return p_138414_.hasPermission(2);
      }).then(Commands.argument("message", MessageArgument.message()).executes((p_138412_) -> {
         Component component = MessageArgument.getMessage(p_138412_, "message");
         Component component1 = new TranslatableComponent("chat.type.announcement", p_138412_.getSource().getDisplayName(), component);
         Entity entity = p_138412_.getSource().getEntity();
         if (entity != null) {
            p_138412_.getSource().getServer().getPlayerList().broadcastMessage(component1, ChatType.CHAT, entity.getUUID());
         } else {
            p_138412_.getSource().getServer().getPlayerList().broadcastMessage(component1, ChatType.SYSTEM, Util.NIL_UUID);
         }

         return 1;
      })));
   }
}