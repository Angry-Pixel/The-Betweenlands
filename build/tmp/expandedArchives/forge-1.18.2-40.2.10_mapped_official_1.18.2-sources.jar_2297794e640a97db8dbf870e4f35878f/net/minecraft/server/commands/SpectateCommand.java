package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

public class SpectateCommand {
   private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType(new TranslatableComponent("commands.spectate.self"));
   private static final DynamicCommandExceptionType ERROR_NOT_SPECTATOR = new DynamicCommandExceptionType((p_138688_) -> {
      return new TranslatableComponent("commands.spectate.not_spectator", p_138688_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_138678_) {
      p_138678_.register(Commands.literal("spectate").requires((p_138682_) -> {
         return p_138682_.hasPermission(2);
      }).executes((p_138692_) -> {
         return spectate(p_138692_.getSource(), (Entity)null, p_138692_.getSource().getPlayerOrException());
      }).then(Commands.argument("target", EntityArgument.entity()).executes((p_138690_) -> {
         return spectate(p_138690_.getSource(), EntityArgument.getEntity(p_138690_, "target"), p_138690_.getSource().getPlayerOrException());
      }).then(Commands.argument("player", EntityArgument.player()).executes((p_138680_) -> {
         return spectate(p_138680_.getSource(), EntityArgument.getEntity(p_138680_, "target"), EntityArgument.getPlayer(p_138680_, "player"));
      }))));
   }

   private static int spectate(CommandSourceStack p_138684_, @Nullable Entity p_138685_, ServerPlayer p_138686_) throws CommandSyntaxException {
      if (p_138686_ == p_138685_) {
         throw ERROR_SELF.create();
      } else if (p_138686_.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
         throw ERROR_NOT_SPECTATOR.create(p_138686_.getDisplayName());
      } else {
         p_138686_.setCamera(p_138685_);
         if (p_138685_ != null) {
            p_138684_.sendSuccess(new TranslatableComponent("commands.spectate.success.started", p_138685_.getDisplayName()), false);
         } else {
            p_138684_.sendSuccess(new TranslatableComponent("commands.spectate.success.stopped"), false);
         }

         return 1;
      }
   }
}