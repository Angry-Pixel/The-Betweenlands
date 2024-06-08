package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;

public class TitleCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_139103_) {
      p_139103_.register(Commands.literal("title").requires((p_139107_) -> {
         return p_139107_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes((p_139134_) -> {
         return clearTitle(p_139134_.getSource(), EntityArgument.getPlayers(p_139134_, "targets"));
      })).then(Commands.literal("reset").executes((p_139132_) -> {
         return resetTitle(p_139132_.getSource(), EntityArgument.getPlayers(p_139132_, "targets"));
      })).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.textComponent()).executes((p_139130_) -> {
         return showTitle(p_139130_.getSource(), EntityArgument.getPlayers(p_139130_, "targets"), ComponentArgument.getComponent(p_139130_, "title"), "title", ClientboundSetTitleTextPacket::new);
      }))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.textComponent()).executes((p_139128_) -> {
         return showTitle(p_139128_.getSource(), EntityArgument.getPlayers(p_139128_, "targets"), ComponentArgument.getComponent(p_139128_, "title"), "subtitle", ClientboundSetSubtitleTextPacket::new);
      }))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.textComponent()).executes((p_139123_) -> {
         return showTitle(p_139123_.getSource(), EntityArgument.getPlayers(p_139123_, "targets"), ComponentArgument.getComponent(p_139123_, "title"), "actionbar", ClientboundSetActionBarTextPacket::new);
      }))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((p_139105_) -> {
         return setTimes(p_139105_.getSource(), EntityArgument.getPlayers(p_139105_, "targets"), IntegerArgumentType.getInteger(p_139105_, "fadeIn"), IntegerArgumentType.getInteger(p_139105_, "stay"), IntegerArgumentType.getInteger(p_139105_, "fadeOut"));
      })))))));
   }

   private static int clearTitle(CommandSourceStack p_139109_, Collection<ServerPlayer> p_139110_) {
      ClientboundClearTitlesPacket clientboundcleartitlespacket = new ClientboundClearTitlesPacket(false);

      for(ServerPlayer serverplayer : p_139110_) {
         serverplayer.connection.send(clientboundcleartitlespacket);
      }

      if (p_139110_.size() == 1) {
         p_139109_.sendSuccess(new TranslatableComponent("commands.title.cleared.single", p_139110_.iterator().next().getDisplayName()), true);
      } else {
         p_139109_.sendSuccess(new TranslatableComponent("commands.title.cleared.multiple", p_139110_.size()), true);
      }

      return p_139110_.size();
   }

   private static int resetTitle(CommandSourceStack p_139125_, Collection<ServerPlayer> p_139126_) {
      ClientboundClearTitlesPacket clientboundcleartitlespacket = new ClientboundClearTitlesPacket(true);

      for(ServerPlayer serverplayer : p_139126_) {
         serverplayer.connection.send(clientboundcleartitlespacket);
      }

      if (p_139126_.size() == 1) {
         p_139125_.sendSuccess(new TranslatableComponent("commands.title.reset.single", p_139126_.iterator().next().getDisplayName()), true);
      } else {
         p_139125_.sendSuccess(new TranslatableComponent("commands.title.reset.multiple", p_139126_.size()), true);
      }

      return p_139126_.size();
   }

   private static int showTitle(CommandSourceStack p_142781_, Collection<ServerPlayer> p_142782_, Component p_142783_, String p_142784_, Function<Component, Packet<?>> p_142785_) throws CommandSyntaxException {
      for(ServerPlayer serverplayer : p_142782_) {
         serverplayer.connection.send(p_142785_.apply(ComponentUtils.updateForEntity(p_142781_, p_142783_, serverplayer, 0)));
      }

      if (p_142782_.size() == 1) {
         p_142781_.sendSuccess(new TranslatableComponent("commands.title.show." + p_142784_ + ".single", p_142782_.iterator().next().getDisplayName()), true);
      } else {
         p_142781_.sendSuccess(new TranslatableComponent("commands.title.show." + p_142784_ + ".multiple", p_142782_.size()), true);
      }

      return p_142782_.size();
   }

   private static int setTimes(CommandSourceStack p_139112_, Collection<ServerPlayer> p_139113_, int p_139114_, int p_139115_, int p_139116_) {
      ClientboundSetTitlesAnimationPacket clientboundsettitlesanimationpacket = new ClientboundSetTitlesAnimationPacket(p_139114_, p_139115_, p_139116_);

      for(ServerPlayer serverplayer : p_139113_) {
         serverplayer.connection.send(clientboundsettitlesanimationpacket);
      }

      if (p_139113_.size() == 1) {
         p_139112_.sendSuccess(new TranslatableComponent("commands.title.times.single", p_139113_.iterator().next().getDisplayName()), true);
      } else {
         p_139112_.sendSuccess(new TranslatableComponent("commands.title.times.multiple", p_139113_.size()), true);
      }

      return p_139113_.size();
   }
}