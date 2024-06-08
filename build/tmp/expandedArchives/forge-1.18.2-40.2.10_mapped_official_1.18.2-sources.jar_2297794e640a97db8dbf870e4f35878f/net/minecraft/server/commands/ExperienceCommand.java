package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceCommand {
   private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("commands.experience.set.points.invalid"));

   public static void register(CommandDispatcher<CommandSourceStack> p_137307_) {
      LiteralCommandNode<CommandSourceStack> literalcommandnode = p_137307_.register(Commands.literal("experience").requires((p_137324_) -> {
         return p_137324_.hasPermission(2);
      }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer()).executes((p_137341_) -> {
         return addExperience(p_137341_.getSource(), EntityArgument.getPlayers(p_137341_, "targets"), IntegerArgumentType.getInteger(p_137341_, "amount"), ExperienceCommand.Type.POINTS);
      }).then(Commands.literal("points").executes((p_137339_) -> {
         return addExperience(p_137339_.getSource(), EntityArgument.getPlayers(p_137339_, "targets"), IntegerArgumentType.getInteger(p_137339_, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("levels").executes((p_137337_) -> {
         return addExperience(p_137337_.getSource(), EntityArgument.getPlayers(p_137337_, "targets"), IntegerArgumentType.getInteger(p_137337_, "amount"), ExperienceCommand.Type.LEVELS);
      }))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((p_137335_) -> {
         return setExperience(p_137335_.getSource(), EntityArgument.getPlayers(p_137335_, "targets"), IntegerArgumentType.getInteger(p_137335_, "amount"), ExperienceCommand.Type.POINTS);
      }).then(Commands.literal("points").executes((p_137333_) -> {
         return setExperience(p_137333_.getSource(), EntityArgument.getPlayers(p_137333_, "targets"), IntegerArgumentType.getInteger(p_137333_, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("levels").executes((p_137331_) -> {
         return setExperience(p_137331_.getSource(), EntityArgument.getPlayers(p_137331_, "targets"), IntegerArgumentType.getInteger(p_137331_, "amount"), ExperienceCommand.Type.LEVELS);
      }))))).then(Commands.literal("query").then(Commands.argument("targets", EntityArgument.player()).then(Commands.literal("points").executes((p_137322_) -> {
         return queryExperience(p_137322_.getSource(), EntityArgument.getPlayer(p_137322_, "targets"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("levels").executes((p_137309_) -> {
         return queryExperience(p_137309_.getSource(), EntityArgument.getPlayer(p_137309_, "targets"), ExperienceCommand.Type.LEVELS);
      })))));
      p_137307_.register(Commands.literal("xp").requires((p_137311_) -> {
         return p_137311_.hasPermission(2);
      }).redirect(literalcommandnode));
   }

   private static int queryExperience(CommandSourceStack p_137313_, ServerPlayer p_137314_, ExperienceCommand.Type p_137315_) {
      int i = p_137315_.query.applyAsInt(p_137314_);
      p_137313_.sendSuccess(new TranslatableComponent("commands.experience.query." + p_137315_.name, p_137314_.getDisplayName(), i), false);
      return i;
   }

   private static int addExperience(CommandSourceStack p_137317_, Collection<? extends ServerPlayer> p_137318_, int p_137319_, ExperienceCommand.Type p_137320_) {
      for(ServerPlayer serverplayer : p_137318_) {
         p_137320_.add.accept(serverplayer, p_137319_);
      }

      if (p_137318_.size() == 1) {
         p_137317_.sendSuccess(new TranslatableComponent("commands.experience.add." + p_137320_.name + ".success.single", p_137319_, p_137318_.iterator().next().getDisplayName()), true);
      } else {
         p_137317_.sendSuccess(new TranslatableComponent("commands.experience.add." + p_137320_.name + ".success.multiple", p_137319_, p_137318_.size()), true);
      }

      return p_137318_.size();
   }

   private static int setExperience(CommandSourceStack p_137326_, Collection<? extends ServerPlayer> p_137327_, int p_137328_, ExperienceCommand.Type p_137329_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayer serverplayer : p_137327_) {
         if (p_137329_.set.test(serverplayer, p_137328_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_SET_POINTS_INVALID.create();
      } else {
         if (p_137327_.size() == 1) {
            p_137326_.sendSuccess(new TranslatableComponent("commands.experience.set." + p_137329_.name + ".success.single", p_137328_, p_137327_.iterator().next().getDisplayName()), true);
         } else {
            p_137326_.sendSuccess(new TranslatableComponent("commands.experience.set." + p_137329_.name + ".success.multiple", p_137328_, p_137327_.size()), true);
         }

         return p_137327_.size();
      }
   }

   static enum Type {
      POINTS("points", Player::giveExperiencePoints, (p_137367_, p_137368_) -> {
         if (p_137368_ >= p_137367_.getXpNeededForNextLevel()) {
            return false;
         } else {
            p_137367_.setExperiencePoints(p_137368_);
            return true;
         }
      }, (p_137365_) -> {
         return Mth.floor(p_137365_.experienceProgress * (float)p_137365_.getXpNeededForNextLevel());
      }),
      LEVELS("levels", ServerPlayer::giveExperienceLevels, (p_137360_, p_137361_) -> {
         p_137360_.setExperienceLevels(p_137361_);
         return true;
      }, (p_137358_) -> {
         return p_137358_.experienceLevel;
      });

      public final BiConsumer<ServerPlayer, Integer> add;
      public final BiPredicate<ServerPlayer, Integer> set;
      public final String name;
      final ToIntFunction<ServerPlayer> query;

      private Type(String p_137353_, BiConsumer<ServerPlayer, Integer> p_137354_, BiPredicate<ServerPlayer, Integer> p_137355_, ToIntFunction<ServerPlayer> p_137356_) {
         this.add = p_137354_;
         this.name = p_137353_;
         this.set = p_137355_;
         this.query = p_137356_;
      }
   }
}