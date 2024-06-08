package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardValue implements NumberProvider {
   final ScoreboardNameProvider target;
   final String score;
   final float scale;

   ScoreboardValue(ScoreboardNameProvider p_165745_, String p_165746_, float p_165747_) {
      this.target = p_165745_;
      this.score = p_165746_;
      this.scale = p_165747_;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.SCORE;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.target.getReferencedContextParams();
   }

   public static ScoreboardValue fromScoreboard(LootContext.EntityTarget p_165750_, String p_165751_) {
      return fromScoreboard(p_165750_, p_165751_, 1.0F);
   }

   public static ScoreboardValue fromScoreboard(LootContext.EntityTarget p_165753_, String p_165754_, float p_165755_) {
      return new ScoreboardValue(ContextScoreboardNameProvider.forTarget(p_165753_), p_165754_, p_165755_);
   }

   public float getFloat(LootContext p_165758_) {
      String s = this.target.getScoreboardName(p_165758_);
      if (s == null) {
         return 0.0F;
      } else {
         Scoreboard scoreboard = p_165758_.getLevel().getScoreboard();
         Objective objective = scoreboard.getObjective(this.score);
         if (objective == null) {
            return 0.0F;
         } else {
            return !scoreboard.hasPlayerScore(s, objective) ? 0.0F : (float)scoreboard.getOrCreatePlayerScore(s, objective).getScore() * this.scale;
         }
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ScoreboardValue> {
      public ScoreboardValue deserialize(JsonObject p_165772_, JsonDeserializationContext p_165773_) {
         String s = GsonHelper.getAsString(p_165772_, "score");
         float f = GsonHelper.getAsFloat(p_165772_, "scale", 1.0F);
         ScoreboardNameProvider scoreboardnameprovider = GsonHelper.getAsObject(p_165772_, "target", p_165773_, ScoreboardNameProvider.class);
         return new ScoreboardValue(scoreboardnameprovider, s, f);
      }

      public void serialize(JsonObject p_165764_, ScoreboardValue p_165765_, JsonSerializationContext p_165766_) {
         p_165764_.addProperty("score", p_165765_.score);
         p_165764_.add("target", p_165766_.serialize(p_165765_.target));
         p_165764_.addProperty("scale", p_165765_.scale);
      }
   }
}