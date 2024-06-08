package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class MsgCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138061_) {
      LiteralCommandNode<CommandSourceStack> literalcommandnode = p_138061_.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_138063_) -> {
         return sendMessage(p_138063_.getSource(), EntityArgument.getPlayers(p_138063_, "targets"), MessageArgument.getMessage(p_138063_, "message"));
      }))));
      p_138061_.register(Commands.literal("tell").redirect(literalcommandnode));
      p_138061_.register(Commands.literal("w").redirect(literalcommandnode));
   }

   private static int sendMessage(CommandSourceStack p_138065_, Collection<ServerPlayer> p_138066_, Component p_138067_) {
      UUID uuid = p_138065_.getEntity() == null ? Util.NIL_UUID : p_138065_.getEntity().getUUID();
      Entity entity = p_138065_.getEntity();
      Consumer<Component> consumer;
      if (entity instanceof ServerPlayer) {
         ServerPlayer serverplayer = (ServerPlayer)entity;
         consumer = (p_138059_) -> {
            serverplayer.sendMessage((new TranslatableComponent("commands.message.display.outgoing", p_138059_, p_138067_)).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), serverplayer.getUUID());
         };
      } else {
         consumer = (p_138071_) -> {
            p_138065_.sendSuccess((new TranslatableComponent("commands.message.display.outgoing", p_138071_, p_138067_)).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), false);
         };
      }

      for(ServerPlayer serverplayer1 : p_138066_) {
         consumer.accept(serverplayer1.getDisplayName());
         serverplayer1.sendMessage((new TranslatableComponent("commands.message.display.incoming", p_138065_.getDisplayName(), p_138067_)).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), uuid);
      }

      return p_138066_.size();
   }
}