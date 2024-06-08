package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(new TranslatableComponent("commands.trigger.failed.unprimed"));
   private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(new TranslatableComponent("commands.trigger.failed.invalid"));

   public static void register(CommandDispatcher<CommandSourceStack> p_139142_) {
      p_139142_.register(Commands.literal("trigger").then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_139146_, p_139147_) -> {
         return suggestObjectives(p_139146_.getSource(), p_139147_);
      }).executes((p_139165_) -> {
         return simpleTrigger(p_139165_.getSource(), getScore(p_139165_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_139165_, "objective")));
      }).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_139159_) -> {
         return addValue(p_139159_.getSource(), getScore(p_139159_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_139159_, "objective")), IntegerArgumentType.getInteger(p_139159_, "value"));
      }))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_139144_) -> {
         return setValue(p_139144_.getSource(), getScore(p_139144_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_139144_, "objective")), IntegerArgumentType.getInteger(p_139144_, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> suggestObjectives(CommandSourceStack p_139149_, SuggestionsBuilder p_139150_) {
      Entity entity = p_139149_.getEntity();
      List<String> list = Lists.newArrayList();
      if (entity != null) {
         Scoreboard scoreboard = p_139149_.getServer().getScoreboard();
         String s = entity.getScoreboardName();

         for(Objective objective : scoreboard.getObjectives()) {
            if (objective.getCriteria() == ObjectiveCriteria.TRIGGER && scoreboard.hasPlayerScore(s, objective)) {
               Score score = scoreboard.getOrCreatePlayerScore(s, objective);
               if (!score.isLocked()) {
                  list.add(objective.getName());
               }
            }
         }
      }

      return SharedSuggestionProvider.suggest(list, p_139150_);
   }

   private static int addValue(CommandSourceStack p_139155_, Score p_139156_, int p_139157_) {
      p_139156_.add(p_139157_);
      p_139155_.sendSuccess(new TranslatableComponent("commands.trigger.add.success", p_139156_.getObjective().getFormattedDisplayName(), p_139157_), true);
      return p_139156_.getScore();
   }

   private static int setValue(CommandSourceStack p_139161_, Score p_139162_, int p_139163_) {
      p_139162_.setScore(p_139163_);
      p_139161_.sendSuccess(new TranslatableComponent("commands.trigger.set.success", p_139162_.getObjective().getFormattedDisplayName(), p_139163_), true);
      return p_139163_;
   }

   private static int simpleTrigger(CommandSourceStack p_139152_, Score p_139153_) {
      p_139153_.add(1);
      p_139152_.sendSuccess(new TranslatableComponent("commands.trigger.simple.success", p_139153_.getObjective().getFormattedDisplayName()), true);
      return p_139153_.getScore();
   }

   private static Score getScore(ServerPlayer p_139139_, Objective p_139140_) throws CommandSyntaxException {
      if (p_139140_.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_INVALID_OBJECTIVE.create();
      } else {
         Scoreboard scoreboard = p_139139_.getScoreboard();
         String s = p_139139_.getScoreboardName();
         if (!scoreboard.hasPlayerScore(s, p_139140_)) {
            throw ERROR_NOT_PRIMED.create();
         } else {
            Score score = scoreboard.getOrCreatePlayerScore(s, p_139140_);
            if (score.isLocked()) {
               throw ERROR_NOT_PRIMED.create();
            } else {
               score.setLocked(true);
               return score;
            }
         }
      }
   }
}