package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.PlayerList;

public class BanListCommands {
   public static void register(CommandDispatcher<CommandSourceStack> p_136544_) {
      p_136544_.register(Commands.literal("banlist").requires((p_136548_) -> {
         return p_136548_.hasPermission(3);
      }).executes((p_136555_) -> {
         PlayerList playerlist = p_136555_.getSource().getServer().getPlayerList();
         return showList(p_136555_.getSource(), Lists.newArrayList(Iterables.concat(playerlist.getBans().getEntries(), playerlist.getIpBans().getEntries())));
      }).then(Commands.literal("ips").executes((p_136553_) -> {
         return showList(p_136553_.getSource(), p_136553_.getSource().getServer().getPlayerList().getIpBans().getEntries());
      })).then(Commands.literal("players").executes((p_136546_) -> {
         return showList(p_136546_.getSource(), p_136546_.getSource().getServer().getPlayerList().getBans().getEntries());
      })));
   }

   private static int showList(CommandSourceStack p_136550_, Collection<? extends BanListEntry<?>> p_136551_) {
      if (p_136551_.isEmpty()) {
         p_136550_.sendSuccess(new TranslatableComponent("commands.banlist.none"), false);
      } else {
         p_136550_.sendSuccess(new TranslatableComponent("commands.banlist.list", p_136551_.size()), false);

         for(BanListEntry<?> banlistentry : p_136551_) {
            p_136550_.sendSuccess(new TranslatableComponent("commands.banlist.entry", banlistentry.getDisplayName(), banlistentry.getSource(), banlistentry.getReason()), false);
         }
      }

      return p_136551_.size();
   }
}