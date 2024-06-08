package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;

public class ListPlayersCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_137821_) {
      p_137821_.register(Commands.literal("list").executes((p_137830_) -> {
         return listPlayers(p_137830_.getSource());
      }).then(Commands.literal("uuids").executes((p_137823_) -> {
         return listPlayersWithUuids(p_137823_.getSource());
      })));
   }

   private static int listPlayers(CommandSourceStack p_137825_) {
      return format(p_137825_, Player::getDisplayName);
   }

   private static int listPlayersWithUuids(CommandSourceStack p_137832_) {
      return format(p_137832_, (p_137819_) -> {
         return new TranslatableComponent("commands.list.nameAndId", p_137819_.getName(), p_137819_.getGameProfile().getId());
      });
   }

   private static int format(CommandSourceStack p_137827_, Function<ServerPlayer, Component> p_137828_) {
      PlayerList playerlist = p_137827_.getServer().getPlayerList();
      List<ServerPlayer> list = playerlist.getPlayers();
      Component component = ComponentUtils.formatList(list, p_137828_);
      p_137827_.sendSuccess(new TranslatableComponent("commands.list.players", list.size(), playerlist.getMaxPlayers(), component), false);
      return list.size();
   }
}