package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class TeamCommand {
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.add.duplicate"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.empty.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.name.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.color.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.friendlyfire.alreadyEnabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.friendlyfire.alreadyDisabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.nametagVisibility.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.deathMessageVisibility.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.collisionRule.unchanged"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138878_) {
      p_138878_.register(Commands.literal("team").requires((p_183713_) -> {
         return p_183713_.hasPermission(2);
      }).then(Commands.literal("list").executes((p_183711_) -> {
         return listTeams(p_183711_.getSource());
      }).then(Commands.argument("team", TeamArgument.team()).executes((p_138876_) -> {
         return listMembers(p_138876_.getSource(), TeamArgument.getTeam(p_138876_, "team"));
      }))).then(Commands.literal("add").then(Commands.argument("team", StringArgumentType.word()).executes((p_138995_) -> {
         return createTeam(p_138995_.getSource(), StringArgumentType.getString(p_138995_, "team"));
      }).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_138993_) -> {
         return createTeam(p_138993_.getSource(), StringArgumentType.getString(p_138993_, "team"), ComponentArgument.getComponent(p_138993_, "displayName"));
      })))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((p_138991_) -> {
         return deleteTeam(p_138991_.getSource(), TeamArgument.getTeam(p_138991_, "team"));
      }))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((p_138989_) -> {
         return emptyTeam(p_138989_.getSource(), TeamArgument.getTeam(p_138989_, "team"));
      }))).then(Commands.literal("join").then(Commands.argument("team", TeamArgument.team()).executes((p_138987_) -> {
         return joinTeam(p_138987_.getSource(), TeamArgument.getTeam(p_138987_, "team"), Collections.singleton(p_138987_.getSource().getEntityOrException().getScoreboardName()));
      }).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_138985_) -> {
         return joinTeam(p_138985_.getSource(), TeamArgument.getTeam(p_138985_, "team"), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138985_, "members"));
      })))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_138983_) -> {
         return leaveTeam(p_138983_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138983_, "members"));
      }))).then(Commands.literal("modify").then(Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_138981_) -> {
         return setDisplayName(p_138981_.getSource(), TeamArgument.getTeam(p_138981_, "team"), ComponentArgument.getComponent(p_138981_, "displayName"));
      }))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((p_138979_) -> {
         return setColor(p_138979_.getSource(), TeamArgument.getTeam(p_138979_, "team"), ColorArgument.getColor(p_138979_, "value"));
      }))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_138977_) -> {
         return setFriendlyFire(p_138977_.getSource(), TeamArgument.getTeam(p_138977_, "team"), BoolArgumentType.getBool(p_138977_, "allowed"));
      }))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_138975_) -> {
         return setFriendlySight(p_138975_.getSource(), TeamArgument.getTeam(p_138975_, "team"), BoolArgumentType.getBool(p_138975_, "allowed"));
      }))).then(Commands.literal("nametagVisibility").then(Commands.literal("never").executes((p_138973_) -> {
         return setNametagVisibility(p_138973_.getSource(), TeamArgument.getTeam(p_138973_, "team"), Team.Visibility.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_138971_) -> {
         return setNametagVisibility(p_138971_.getSource(), TeamArgument.getTeam(p_138971_, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_138969_) -> {
         return setNametagVisibility(p_138969_.getSource(), TeamArgument.getTeam(p_138969_, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_138967_) -> {
         return setNametagVisibility(p_138967_.getSource(), TeamArgument.getTeam(p_138967_, "team"), Team.Visibility.ALWAYS);
      }))).then(Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((p_138965_) -> {
         return setDeathMessageVisibility(p_138965_.getSource(), TeamArgument.getTeam(p_138965_, "team"), Team.Visibility.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_138963_) -> {
         return setDeathMessageVisibility(p_138963_.getSource(), TeamArgument.getTeam(p_138963_, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_138961_) -> {
         return setDeathMessageVisibility(p_138961_.getSource(), TeamArgument.getTeam(p_138961_, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_138959_) -> {
         return setDeathMessageVisibility(p_138959_.getSource(), TeamArgument.getTeam(p_138959_, "team"), Team.Visibility.ALWAYS);
      }))).then(Commands.literal("collisionRule").then(Commands.literal("never").executes((p_138957_) -> {
         return setCollision(p_138957_.getSource(), TeamArgument.getTeam(p_138957_, "team"), Team.CollisionRule.NEVER);
      })).then(Commands.literal("pushOwnTeam").executes((p_138955_) -> {
         return setCollision(p_138955_.getSource(), TeamArgument.getTeam(p_138955_, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      })).then(Commands.literal("pushOtherTeams").executes((p_138953_) -> {
         return setCollision(p_138953_.getSource(), TeamArgument.getTeam(p_138953_, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      })).then(Commands.literal("always").executes((p_138951_) -> {
         return setCollision(p_138951_.getSource(), TeamArgument.getTeam(p_138951_, "team"), Team.CollisionRule.ALWAYS);
      }))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.textComponent()).executes((p_138942_) -> {
         return setPrefix(p_138942_.getSource(), TeamArgument.getTeam(p_138942_, "team"), ComponentArgument.getComponent(p_138942_, "prefix"));
      }))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.textComponent()).executes((p_138923_) -> {
         return setSuffix(p_138923_.getSource(), TeamArgument.getTeam(p_138923_, "team"), ComponentArgument.getComponent(p_138923_, "suffix"));
      }))))));
   }

   private static int leaveTeam(CommandSourceStack p_138918_, Collection<String> p_138919_) {
      Scoreboard scoreboard = p_138918_.getServer().getScoreboard();

      for(String s : p_138919_) {
         scoreboard.removePlayerFromTeam(s);
      }

      if (p_138919_.size() == 1) {
         p_138918_.sendSuccess(new TranslatableComponent("commands.team.leave.success.single", p_138919_.iterator().next()), true);
      } else {
         p_138918_.sendSuccess(new TranslatableComponent("commands.team.leave.success.multiple", p_138919_.size()), true);
      }

      return p_138919_.size();
   }

   private static int joinTeam(CommandSourceStack p_138895_, PlayerTeam p_138896_, Collection<String> p_138897_) {
      Scoreboard scoreboard = p_138895_.getServer().getScoreboard();

      for(String s : p_138897_) {
         scoreboard.addPlayerToTeam(s, p_138896_);
      }

      if (p_138897_.size() == 1) {
         p_138895_.sendSuccess(new TranslatableComponent("commands.team.join.success.single", p_138897_.iterator().next(), p_138896_.getFormattedDisplayName()), true);
      } else {
         p_138895_.sendSuccess(new TranslatableComponent("commands.team.join.success.multiple", p_138897_.size(), p_138896_.getFormattedDisplayName()), true);
      }

      return p_138897_.size();
   }

   private static int setNametagVisibility(CommandSourceStack p_138891_, PlayerTeam p_138892_, Team.Visibility p_138893_) throws CommandSyntaxException {
      if (p_138892_.getNameTagVisibility() == p_138893_) {
         throw ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
      } else {
         p_138892_.setNameTagVisibility(p_138893_);
         p_138891_.sendSuccess(new TranslatableComponent("commands.team.option.nametagVisibility.success", p_138892_.getFormattedDisplayName(), p_138893_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSourceStack p_138930_, PlayerTeam p_138931_, Team.Visibility p_138932_) throws CommandSyntaxException {
      if (p_138931_.getDeathMessageVisibility() == p_138932_) {
         throw ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
      } else {
         p_138931_.setDeathMessageVisibility(p_138932_);
         p_138930_.sendSuccess(new TranslatableComponent("commands.team.option.deathMessageVisibility.success", p_138931_.getFormattedDisplayName(), p_138932_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setCollision(CommandSourceStack p_138887_, PlayerTeam p_138888_, Team.CollisionRule p_138889_) throws CommandSyntaxException {
      if (p_138888_.getCollisionRule() == p_138889_) {
         throw ERROR_TEAM_COLLISION_UNCHANGED.create();
      } else {
         p_138888_.setCollisionRule(p_138889_);
         p_138887_.sendSuccess(new TranslatableComponent("commands.team.option.collisionRule.success", p_138888_.getFormattedDisplayName(), p_138889_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setFriendlySight(CommandSourceStack p_138907_, PlayerTeam p_138908_, boolean p_138909_) throws CommandSyntaxException {
      if (p_138908_.canSeeFriendlyInvisibles() == p_138909_) {
         if (p_138909_) {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
         }
      } else {
         p_138908_.setSeeFriendlyInvisibles(p_138909_);
         p_138907_.sendSuccess(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles." + (p_138909_ ? "enabled" : "disabled"), p_138908_.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setFriendlyFire(CommandSourceStack p_138938_, PlayerTeam p_138939_, boolean p_138940_) throws CommandSyntaxException {
      if (p_138939_.isAllowFriendlyFire() == p_138940_) {
         if (p_138940_) {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
         }
      } else {
         p_138939_.setAllowFriendlyFire(p_138940_);
         p_138938_.sendSuccess(new TranslatableComponent("commands.team.option.friendlyfire." + (p_138940_ ? "enabled" : "disabled"), p_138939_.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack p_138903_, PlayerTeam p_138904_, Component p_138905_) throws CommandSyntaxException {
      if (p_138904_.getDisplayName().equals(p_138905_)) {
         throw ERROR_TEAM_ALREADY_NAME.create();
      } else {
         p_138904_.setDisplayName(p_138905_);
         p_138903_.sendSuccess(new TranslatableComponent("commands.team.option.name.success", p_138904_.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setColor(CommandSourceStack p_138899_, PlayerTeam p_138900_, ChatFormatting p_138901_) throws CommandSyntaxException {
      if (p_138900_.getColor() == p_138901_) {
         throw ERROR_TEAM_ALREADY_COLOR.create();
      } else {
         p_138900_.setColor(p_138901_);
         p_138899_.sendSuccess(new TranslatableComponent("commands.team.option.color.success", p_138900_.getFormattedDisplayName(), p_138901_.getName()), true);
         return 0;
      }
   }

   private static int emptyTeam(CommandSourceStack p_138884_, PlayerTeam p_138885_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138884_.getServer().getScoreboard();
      Collection<String> collection = Lists.newArrayList(p_138885_.getPlayers());
      if (collection.isEmpty()) {
         throw ERROR_TEAM_ALREADY_EMPTY.create();
      } else {
         for(String s : collection) {
            scoreboard.removePlayerFromTeam(s, p_138885_);
         }

         p_138884_.sendSuccess(new TranslatableComponent("commands.team.empty.success", collection.size(), p_138885_.getFormattedDisplayName()), true);
         return collection.size();
      }
   }

   private static int deleteTeam(CommandSourceStack p_138927_, PlayerTeam p_138928_) {
      Scoreboard scoreboard = p_138927_.getServer().getScoreboard();
      scoreboard.removePlayerTeam(p_138928_);
      p_138927_.sendSuccess(new TranslatableComponent("commands.team.remove.success", p_138928_.getFormattedDisplayName()), true);
      return scoreboard.getPlayerTeams().size();
   }

   private static int createTeam(CommandSourceStack p_138911_, String p_138912_) throws CommandSyntaxException {
      return createTeam(p_138911_, p_138912_, new TextComponent(p_138912_));
   }

   private static int createTeam(CommandSourceStack p_138914_, String p_138915_, Component p_138916_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_138914_.getServer().getScoreboard();
      if (scoreboard.getPlayerTeam(p_138915_) != null) {
         throw ERROR_TEAM_ALREADY_EXISTS.create();
      } else {
         PlayerTeam playerteam = scoreboard.addPlayerTeam(p_138915_);
         playerteam.setDisplayName(p_138916_);
         p_138914_.sendSuccess(new TranslatableComponent("commands.team.add.success", playerteam.getFormattedDisplayName()), true);
         return scoreboard.getPlayerTeams().size();
      }
   }

   private static int listMembers(CommandSourceStack p_138944_, PlayerTeam p_138945_) {
      Collection<String> collection = p_138945_.getPlayers();
      if (collection.isEmpty()) {
         p_138944_.sendSuccess(new TranslatableComponent("commands.team.list.members.empty", p_138945_.getFormattedDisplayName()), false);
      } else {
         p_138944_.sendSuccess(new TranslatableComponent("commands.team.list.members.success", p_138945_.getFormattedDisplayName(), collection.size(), ComponentUtils.formatList(collection)), false);
      }

      return collection.size();
   }

   private static int listTeams(CommandSourceStack p_138882_) {
      Collection<PlayerTeam> collection = p_138882_.getServer().getScoreboard().getPlayerTeams();
      if (collection.isEmpty()) {
         p_138882_.sendSuccess(new TranslatableComponent("commands.team.list.teams.empty"), false);
      } else {
         p_138882_.sendSuccess(new TranslatableComponent("commands.team.list.teams.success", collection.size(), ComponentUtils.formatList(collection, PlayerTeam::getFormattedDisplayName)), false);
      }

      return collection.size();
   }

   private static int setPrefix(CommandSourceStack p_138934_, PlayerTeam p_138935_, Component p_138936_) {
      p_138935_.setPlayerPrefix(p_138936_);
      p_138934_.sendSuccess(new TranslatableComponent("commands.team.option.prefix.success", p_138936_), false);
      return 1;
   }

   private static int setSuffix(CommandSourceStack p_138947_, PlayerTeam p_138948_, Component p_138949_) {
      p_138948_.setPlayerSuffix(p_138949_);
      p_138947_.sendSuccess(new TranslatableComponent("commands.team.option.suffix.success", p_138949_), false);
      return 1;
   }
}