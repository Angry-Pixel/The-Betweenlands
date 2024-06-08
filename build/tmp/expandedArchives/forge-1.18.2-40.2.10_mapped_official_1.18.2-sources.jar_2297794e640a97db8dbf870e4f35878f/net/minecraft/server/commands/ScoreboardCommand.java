package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.add.duplicate"));
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.display.alreadyEmpty"));
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.display.alreadySet"));
   private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.players.enable.failed"));
   private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.players.enable.invalid"));
   private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType((p_138534_, p_138535_) -> {
      return new TranslatableComponent("commands.scoreboard.players.get.null", p_138534_, p_138535_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_138469_) {
      p_138469_.register(Commands.literal("scoreboard").requires((p_138552_) -> {
         return p_138552_.hasPermission(2);
      }).then(Commands.literal("objectives").then(Commands.literal("list").executes((p_138585_) -> {
         return listObjectives(p_138585_.getSource());
      })).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(Commands.argument("criteria", ObjectiveCriteriaArgument.criteria()).executes((p_138583_) -> {
         return addObjective(p_138583_.getSource(), StringArgumentType.getString(p_138583_, "objective"), ObjectiveCriteriaArgument.getCriteria(p_138583_, "criteria"), new TextComponent(StringArgumentType.getString(p_138583_, "objective")));
      }).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_138581_) -> {
         return addObjective(p_138581_.getSource(), StringArgumentType.getString(p_138581_, "objective"), ObjectiveCriteriaArgument.getCriteria(p_138581_, "criteria"), ComponentArgument.getComponent(p_138581_, "displayName"));
      }))))).then(Commands.literal("modify").then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_138579_) -> {
         return setDisplayName(p_138579_.getSource(), ObjectiveArgument.getObjective(p_138579_, "objective"), ComponentArgument.getComponent(p_138579_, "displayName"));
      }))).then(createRenderTypeModify()))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138577_) -> {
         return removeObjective(p_138577_.getSource(), ObjectiveArgument.getObjective(p_138577_, "objective"));
      }))).then(Commands.literal("setdisplay").then(Commands.argument("slot", ScoreboardSlotArgument.displaySlot()).executes((p_138575_) -> {
         return clearDisplaySlot(p_138575_.getSource(), ScoreboardSlotArgument.getDisplaySlot(p_138575_, "slot"));
      }).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138573_) -> {
         return setDisplaySlot(p_138573_.getSource(), ScoreboardSlotArgument.getDisplaySlot(p_138573_, "slot"), ObjectiveArgument.getObjective(p_138573_, "objective"));
      }))))).then(Commands.literal("players").then(Commands.literal("list").executes((p_138571_) -> {
         return listTrackedPlayers(p_138571_.getSource());
      }).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_138569_) -> {
         return listTrackedPlayerScores(p_138569_.getSource(), ScoreHolderArgument.getName(p_138569_, "target"));
      }))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes((p_138567_) -> {
         return setScore(p_138567_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138567_, "targets"), ObjectiveArgument.getWritableObjective(p_138567_, "objective"), IntegerArgumentType.getInteger(p_138567_, "score"));
      }))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138565_) -> {
         return getScore(p_138565_.getSource(), ScoreHolderArgument.getName(p_138565_, "target"), ObjectiveArgument.getObjective(p_138565_, "objective"));
      })))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_138563_) -> {
         return addScore(p_138563_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138563_, "targets"), ObjectiveArgument.getWritableObjective(p_138563_, "objective"), IntegerArgumentType.getInteger(p_138563_, "score"));
      }))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_138561_) -> {
         return removeScore(p_138561_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138561_, "targets"), ObjectiveArgument.getWritableObjective(p_138561_, "objective"), IntegerArgumentType.getInteger(p_138561_, "score"));
      }))))).then(Commands.literal("reset").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_138559_) -> {
         return resetScores(p_138559_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138559_, "targets"));
      }).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138550_) -> {
         return resetScore(p_138550_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138550_, "targets"), ObjectiveArgument.getObjective(p_138550_, "objective"));
      })))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_138473_, p_138474_) -> {
         return suggestTriggers(p_138473_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138473_, "targets"), p_138474_);
      }).executes((p_138537_) -> {
         return enableTrigger(p_138537_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138537_, "targets"), ObjectiveArgument.getObjective(p_138537_, "objective"));
      })))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes((p_138471_) -> {
         return performOperation(p_138471_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138471_, "targets"), ObjectiveArgument.getWritableObjective(p_138471_, "targetObjective"), OperationArgument.getOperation(p_138471_, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138471_, "source"), ObjectiveArgument.getObjective(p_138471_, "sourceObjective"));
      })))))))));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("rendertype");

      for(ObjectiveCriteria.RenderType objectivecriteria$rendertype : ObjectiveCriteria.RenderType.values()) {
         literalargumentbuilder.then(Commands.literal(objectivecriteria$rendertype.getId()).executes((p_138532_) -> {
            return setRenderType(p_138532_.getSource(), ObjectiveArgument.getObjective(p_138532_, "objective"), objectivecriteria$rendertype);
         }));
      }

      return literalargumentbuilder;
   }

   private static CompletableFuture<Suggestions> suggestTriggers(CommandSourceStack p_138511_, Collection<String> p_138512_, SuggestionsBuilder p_138513_) {
      List<String> list = Lists.newArrayList();
      Scoreboard scoreboard = p_138511_.getServer().getScoreboard();

      for(Objective objective : scoreboard.getObjectives()) {
         if (objective.getCriteria() == ObjectiveCriteria.TRIGGER) {
            boolean flag = false;

            for(String s : p_138512_) {
               if (!scoreboard.hasPlayerScore(s, objective) || scoreboard.getOrCreatePlayerScore(s, objective).isLocked()) {
                  flag = true;
                  break;
               }
            }

            if (flag) {
               list.add(objective.getName());
            }
         }
      }

      return SharedSuggestionProvider.suggest(list, p_138513_);
   }

   private static int getScore(CommandSourceStack p_138499_, String p_138500_, Objective p_138501_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138499_.getServer().getScoreboard();
      if (!scoreboard.hasPlayerScore(p_138500_, p_138501_)) {
         throw ERROR_NO_VALUE.create(p_138501_.getName(), p_138500_);
      } else {
         Score score = scoreboard.getOrCreatePlayerScore(p_138500_, p_138501_);
         p_138499_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.get.success", p_138500_, score.getScore(), p_138501_.getFormattedDisplayName()), false);
         return score.getScore();
      }
   }

   private static int performOperation(CommandSourceStack p_138524_, Collection<String> p_138525_, Objective p_138526_, OperationArgument.Operation p_138527_, Collection<String> p_138528_, Objective p_138529_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138524_.getServer().getScoreboard();
      int i = 0;

      for(String s : p_138525_) {
         Score score = scoreboard.getOrCreatePlayerScore(s, p_138526_);

         for(String s1 : p_138528_) {
            Score score1 = scoreboard.getOrCreatePlayerScore(s1, p_138529_);
            p_138527_.apply(score, score1);
         }

         i += score.getScore();
      }

      if (p_138525_.size() == 1) {
         p_138524_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.single", p_138526_.getFormattedDisplayName(), p_138525_.iterator().next(), i), true);
      } else {
         p_138524_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.multiple", p_138526_.getFormattedDisplayName(), p_138525_.size()), true);
      }

      return i;
   }

   private static int enableTrigger(CommandSourceStack p_138515_, Collection<String> p_138516_, Objective p_138517_) throws CommandSyntaxException {
      if (p_138517_.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_NOT_TRIGGER.create();
      } else {
         Scoreboard scoreboard = p_138515_.getServer().getScoreboard();
         int i = 0;

         for(String s : p_138516_) {
            Score score = scoreboard.getOrCreatePlayerScore(s, p_138517_);
            if (score.isLocked()) {
               score.setLocked(false);
               ++i;
            }
         }

         if (i == 0) {
            throw ERROR_TRIGGER_ALREADY_ENABLED.create();
         } else {
            if (p_138516_.size() == 1) {
               p_138515_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.single", p_138517_.getFormattedDisplayName(), p_138516_.iterator().next()), true);
            } else {
               p_138515_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.multiple", p_138517_.getFormattedDisplayName(), p_138516_.size()), true);
            }

            return i;
         }
      }
   }

   private static int resetScores(CommandSourceStack p_138508_, Collection<String> p_138509_) {
      Scoreboard scoreboard = p_138508_.getServer().getScoreboard();

      for(String s : p_138509_) {
         scoreboard.resetPlayerScore(s, (Objective)null);
      }

      if (p_138509_.size() == 1) {
         p_138508_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.single", p_138509_.iterator().next()), true);
      } else {
         p_138508_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.multiple", p_138509_.size()), true);
      }

      return p_138509_.size();
   }

   private static int resetScore(CommandSourceStack p_138541_, Collection<String> p_138542_, Objective p_138543_) {
      Scoreboard scoreboard = p_138541_.getServer().getScoreboard();

      for(String s : p_138542_) {
         scoreboard.resetPlayerScore(s, p_138543_);
      }

      if (p_138542_.size() == 1) {
         p_138541_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.single", p_138543_.getFormattedDisplayName(), p_138542_.iterator().next()), true);
      } else {
         p_138541_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.multiple", p_138543_.getFormattedDisplayName(), p_138542_.size()), true);
      }

      return p_138542_.size();
   }

   private static int setScore(CommandSourceStack p_138519_, Collection<String> p_138520_, Objective p_138521_, int p_138522_) {
      Scoreboard scoreboard = p_138519_.getServer().getScoreboard();

      for(String s : p_138520_) {
         Score score = scoreboard.getOrCreatePlayerScore(s, p_138521_);
         score.setScore(p_138522_);
      }

      if (p_138520_.size() == 1) {
         p_138519_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.single", p_138521_.getFormattedDisplayName(), p_138520_.iterator().next(), p_138522_), true);
      } else {
         p_138519_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.multiple", p_138521_.getFormattedDisplayName(), p_138520_.size(), p_138522_), true);
      }

      return p_138522_ * p_138520_.size();
   }

   private static int addScore(CommandSourceStack p_138545_, Collection<String> p_138546_, Objective p_138547_, int p_138548_) {
      Scoreboard scoreboard = p_138545_.getServer().getScoreboard();
      int i = 0;

      for(String s : p_138546_) {
         Score score = scoreboard.getOrCreatePlayerScore(s, p_138547_);
         score.setScore(score.getScore() + p_138548_);
         i += score.getScore();
      }

      if (p_138546_.size() == 1) {
         p_138545_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.single", p_138548_, p_138547_.getFormattedDisplayName(), p_138546_.iterator().next(), i), true);
      } else {
         p_138545_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.multiple", p_138548_, p_138547_.getFormattedDisplayName(), p_138546_.size()), true);
      }

      return i;
   }

   private static int removeScore(CommandSourceStack p_138554_, Collection<String> p_138555_, Objective p_138556_, int p_138557_) {
      Scoreboard scoreboard = p_138554_.getServer().getScoreboard();
      int i = 0;

      for(String s : p_138555_) {
         Score score = scoreboard.getOrCreatePlayerScore(s, p_138556_);
         score.setScore(score.getScore() - p_138557_);
         i += score.getScore();
      }

      if (p_138555_.size() == 1) {
         p_138554_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.single", p_138557_, p_138556_.getFormattedDisplayName(), p_138555_.iterator().next(), i), true);
      } else {
         p_138554_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.multiple", p_138557_, p_138556_.getFormattedDisplayName(), p_138555_.size()), true);
      }

      return i;
   }

   private static int listTrackedPlayers(CommandSourceStack p_138476_) {
      Collection<String> collection = p_138476_.getServer().getScoreboard().getTrackedPlayers();
      if (collection.isEmpty()) {
         p_138476_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.empty"), false);
      } else {
         p_138476_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.success", collection.size(), ComponentUtils.formatList(collection)), false);
      }

      return collection.size();
   }

   private static int listTrackedPlayerScores(CommandSourceStack p_138496_, String p_138497_) {
      Map<Objective, Score> map = p_138496_.getServer().getScoreboard().getPlayerScores(p_138497_);
      if (map.isEmpty()) {
         p_138496_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.empty", p_138497_), false);
      } else {
         p_138496_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.success", p_138497_, map.size()), false);

         for(Entry<Objective, Score> entry : map.entrySet()) {
            p_138496_.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.entry", entry.getKey().getFormattedDisplayName(), entry.getValue().getScore()), false);
         }
      }

      return map.size();
   }

   private static int clearDisplaySlot(CommandSourceStack p_138478_, int p_138479_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138478_.getServer().getScoreboard();
      if (scoreboard.getDisplayObjective(p_138479_) == null) {
         throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
      } else {
         scoreboard.setDisplayObjective(p_138479_, (Objective)null);
         p_138478_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[p_138479_]), true);
         return 0;
      }
   }

   private static int setDisplaySlot(CommandSourceStack p_138481_, int p_138482_, Objective p_138483_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138481_.getServer().getScoreboard();
      if (scoreboard.getDisplayObjective(p_138482_) == p_138483_) {
         throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
      } else {
         scoreboard.setDisplayObjective(p_138482_, p_138483_);
         p_138481_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[p_138482_], p_138483_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack p_138492_, Objective p_138493_, Component p_138494_) {
      if (!p_138493_.getDisplayName().equals(p_138494_)) {
         p_138493_.setDisplayName(p_138494_);
         p_138492_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.displayname", p_138493_.getName(), p_138493_.getFormattedDisplayName()), true);
      }

      return 0;
   }

   private static int setRenderType(CommandSourceStack p_138488_, Objective p_138489_, ObjectiveCriteria.RenderType p_138490_) {
      if (p_138489_.getRenderType() != p_138490_) {
         p_138489_.setRenderType(p_138490_);
         p_138488_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.rendertype", p_138489_.getFormattedDisplayName()), true);
      }

      return 0;
   }

   private static int removeObjective(CommandSourceStack p_138485_, Objective p_138486_) {
      Scoreboard scoreboard = p_138485_.getServer().getScoreboard();
      scoreboard.removeObjective(p_138486_);
      p_138485_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.remove.success", p_138486_.getFormattedDisplayName()), true);
      return scoreboard.getObjectives().size();
   }

   private static int addObjective(CommandSourceStack p_138503_, String p_138504_, ObjectiveCriteria p_138505_, Component p_138506_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138503_.getServer().getScoreboard();
      if (scoreboard.getObjective(p_138504_) != null) {
         throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
      } else {
         scoreboard.addObjective(p_138504_, p_138505_, p_138506_, p_138505_.getDefaultRenderType());
         Objective objective = scoreboard.getObjective(p_138504_);
         p_138503_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.add.success", objective.getFormattedDisplayName()), true);
         return scoreboard.getObjectives().size();
      }
   }

   private static int listObjectives(CommandSourceStack p_138539_) {
      Collection<Objective> collection = p_138539_.getServer().getScoreboard().getObjectives();
      if (collection.isEmpty()) {
         p_138539_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.empty"), false);
      } else {
         p_138539_.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.success", collection.size(), ComponentUtils.formatList(collection, Objective::getFormattedDisplayName)), false);
      }

      return collection.size();
   }
}