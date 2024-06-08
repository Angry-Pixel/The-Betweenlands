package net.minecraft.world.scores;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardSaveData extends SavedData {
   public static final String FILE_ID = "scoreboard";
   private final Scoreboard scoreboard;

   public ScoreboardSaveData(Scoreboard p_166101_) {
      this.scoreboard = p_166101_;
   }

   public ScoreboardSaveData load(CompoundTag p_166103_) {
      this.loadObjectives(p_166103_.getList("Objectives", 10));
      this.scoreboard.loadPlayerScores(p_166103_.getList("PlayerScores", 10));
      if (p_166103_.contains("DisplaySlots", 10)) {
         this.loadDisplaySlots(p_166103_.getCompound("DisplaySlots"));
      }

      if (p_166103_.contains("Teams", 9)) {
         this.loadTeams(p_166103_.getList("Teams", 10));
      }

      return this;
   }

   private void loadTeams(ListTag p_83525_) {
      for(int i = 0; i < p_83525_.size(); ++i) {
         CompoundTag compoundtag = p_83525_.getCompound(i);
         String s = compoundtag.getString("Name");
         PlayerTeam playerteam = this.scoreboard.addPlayerTeam(s);
         Component component = Component.Serializer.fromJson(compoundtag.getString("DisplayName"));
         if (component != null) {
            playerteam.setDisplayName(component);
         }

         if (compoundtag.contains("TeamColor", 8)) {
            playerteam.setColor(ChatFormatting.getByName(compoundtag.getString("TeamColor")));
         }

         if (compoundtag.contains("AllowFriendlyFire", 99)) {
            playerteam.setAllowFriendlyFire(compoundtag.getBoolean("AllowFriendlyFire"));
         }

         if (compoundtag.contains("SeeFriendlyInvisibles", 99)) {
            playerteam.setSeeFriendlyInvisibles(compoundtag.getBoolean("SeeFriendlyInvisibles"));
         }

         if (compoundtag.contains("MemberNamePrefix", 8)) {
            Component component1 = Component.Serializer.fromJson(compoundtag.getString("MemberNamePrefix"));
            if (component1 != null) {
               playerteam.setPlayerPrefix(component1);
            }
         }

         if (compoundtag.contains("MemberNameSuffix", 8)) {
            Component component2 = Component.Serializer.fromJson(compoundtag.getString("MemberNameSuffix"));
            if (component2 != null) {
               playerteam.setPlayerSuffix(component2);
            }
         }

         if (compoundtag.contains("NameTagVisibility", 8)) {
            Team.Visibility team$visibility = Team.Visibility.byName(compoundtag.getString("NameTagVisibility"));
            if (team$visibility != null) {
               playerteam.setNameTagVisibility(team$visibility);
            }
         }

         if (compoundtag.contains("DeathMessageVisibility", 8)) {
            Team.Visibility team$visibility1 = Team.Visibility.byName(compoundtag.getString("DeathMessageVisibility"));
            if (team$visibility1 != null) {
               playerteam.setDeathMessageVisibility(team$visibility1);
            }
         }

         if (compoundtag.contains("CollisionRule", 8)) {
            Team.CollisionRule team$collisionrule = Team.CollisionRule.byName(compoundtag.getString("CollisionRule"));
            if (team$collisionrule != null) {
               playerteam.setCollisionRule(team$collisionrule);
            }
         }

         this.loadTeamPlayers(playerteam, compoundtag.getList("Players", 8));
      }

   }

   private void loadTeamPlayers(PlayerTeam p_83515_, ListTag p_83516_) {
      for(int i = 0; i < p_83516_.size(); ++i) {
         this.scoreboard.addPlayerToTeam(p_83516_.getString(i), p_83515_);
      }

   }

   private void loadDisplaySlots(CompoundTag p_83531_) {
      for(int i = 0; i < 19; ++i) {
         if (p_83531_.contains("slot_" + i, 8)) {
            String s = p_83531_.getString("slot_" + i);
            Objective objective = this.scoreboard.getObjective(s);
            this.scoreboard.setDisplayObjective(i, objective);
         }
      }

   }

   private void loadObjectives(ListTag p_83529_) {
      for(int i = 0; i < p_83529_.size(); ++i) {
         CompoundTag compoundtag = p_83529_.getCompound(i);
         ObjectiveCriteria.byName(compoundtag.getString("CriteriaName")).ifPresent((p_83523_) -> {
            String s = compoundtag.getString("Name");
            Component component = Component.Serializer.fromJson(compoundtag.getString("DisplayName"));
            ObjectiveCriteria.RenderType objectivecriteria$rendertype = ObjectiveCriteria.RenderType.byId(compoundtag.getString("RenderType"));
            this.scoreboard.addObjective(s, p_83523_, component, objectivecriteria$rendertype);
         });
      }

   }

   public CompoundTag save(CompoundTag p_83527_) {
      p_83527_.put("Objectives", this.saveObjectives());
      p_83527_.put("PlayerScores", this.scoreboard.savePlayerScores());
      p_83527_.put("Teams", this.saveTeams());
      this.saveDisplaySlots(p_83527_);
      return p_83527_;
   }

   private ListTag saveTeams() {
      ListTag listtag = new ListTag();

      for(PlayerTeam playerteam : this.scoreboard.getPlayerTeams()) {
         CompoundTag compoundtag = new CompoundTag();
         compoundtag.putString("Name", playerteam.getName());
         compoundtag.putString("DisplayName", Component.Serializer.toJson(playerteam.getDisplayName()));
         if (playerteam.getColor().getId() >= 0) {
            compoundtag.putString("TeamColor", playerteam.getColor().getName());
         }

         compoundtag.putBoolean("AllowFriendlyFire", playerteam.isAllowFriendlyFire());
         compoundtag.putBoolean("SeeFriendlyInvisibles", playerteam.canSeeFriendlyInvisibles());
         compoundtag.putString("MemberNamePrefix", Component.Serializer.toJson(playerteam.getPlayerPrefix()));
         compoundtag.putString("MemberNameSuffix", Component.Serializer.toJson(playerteam.getPlayerSuffix()));
         compoundtag.putString("NameTagVisibility", playerteam.getNameTagVisibility().name);
         compoundtag.putString("DeathMessageVisibility", playerteam.getDeathMessageVisibility().name);
         compoundtag.putString("CollisionRule", playerteam.getCollisionRule().name);
         ListTag listtag1 = new ListTag();

         for(String s : playerteam.getPlayers()) {
            listtag1.add(StringTag.valueOf(s));
         }

         compoundtag.put("Players", listtag1);
         listtag.add(compoundtag);
      }

      return listtag;
   }

   private void saveDisplaySlots(CompoundTag p_83533_) {
      CompoundTag compoundtag = new CompoundTag();
      boolean flag = false;

      for(int i = 0; i < 19; ++i) {
         Objective objective = this.scoreboard.getDisplayObjective(i);
         if (objective != null) {
            compoundtag.putString("slot_" + i, objective.getName());
            flag = true;
         }
      }

      if (flag) {
         p_83533_.put("DisplaySlots", compoundtag);
      }

   }

   private ListTag saveObjectives() {
      ListTag listtag = new ListTag();

      for(Objective objective : this.scoreboard.getObjectives()) {
         if (objective.getCriteria() != null) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("Name", objective.getName());
            compoundtag.putString("CriteriaName", objective.getCriteria().getName());
            compoundtag.putString("DisplayName", Component.Serializer.toJson(objective.getDisplayName()));
            compoundtag.putString("RenderType", objective.getRenderType().getId());
            listtag.add(compoundtag);
         }
      }

      return listtag;
   }
}