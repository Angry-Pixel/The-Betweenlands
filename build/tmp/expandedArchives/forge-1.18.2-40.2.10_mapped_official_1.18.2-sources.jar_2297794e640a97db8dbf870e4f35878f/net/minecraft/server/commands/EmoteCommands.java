package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EmoteCommands {
   public static void register(CommandDispatcher<CommandSourceStack> p_136986_) {
      p_136986_.register(Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((p_136988_) -> {
         String s = StringArgumentType.getString(p_136988_, "action");
         Entity entity = p_136988_.getSource().getEntity();
         MinecraftServer minecraftserver = p_136988_.getSource().getServer();
         if (entity != null) {
            if (entity instanceof ServerPlayer) {
               ServerPlayer serverplayer = (ServerPlayer)entity;
               serverplayer.getTextFilter().processStreamMessage(s).thenAcceptAsync((p_180146_) -> {
                  String s1 = p_180146_.getFiltered();
                  Component component = s1.isEmpty() ? null : createMessage(p_136988_, s1);
                  Component component1 = createMessage(p_136988_, p_180146_.getRaw());
                  minecraftserver.getPlayerList().broadcastMessage(component1, (p_180140_) -> {
                     return serverplayer.shouldFilterMessageTo(p_180140_) ? component : component1;
                  }, ChatType.CHAT, entity.getUUID());
               }, minecraftserver);
               return 1;
            }

            minecraftserver.getPlayerList().broadcastMessage(createMessage(p_136988_, s), ChatType.CHAT, entity.getUUID());
         } else {
            minecraftserver.getPlayerList().broadcastMessage(createMessage(p_136988_, s), ChatType.SYSTEM, Util.NIL_UUID);
         }

         return 1;
      })));
   }

   private static Component createMessage(CommandContext<CommandSourceStack> p_136990_, String p_136991_) {
      return new TranslatableComponent("chat.type.emote", p_136990_.getSource().getDisplayName(), p_136991_);
   }
}