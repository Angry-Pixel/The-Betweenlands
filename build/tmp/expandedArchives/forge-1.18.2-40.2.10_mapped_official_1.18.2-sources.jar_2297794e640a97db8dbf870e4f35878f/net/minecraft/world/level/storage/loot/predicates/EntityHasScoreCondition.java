package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class EntityHasScoreCondition implements LootItemCondition {
   final Map<String, IntRange> scores;
   final LootContext.EntityTarget entityTarget;

   EntityHasScoreCondition(Map<String, IntRange> p_81618_, LootContext.EntityTarget p_81619_) {
      this.scores = ImmutableMap.copyOf(p_81618_);
      this.entityTarget = p_81619_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ENTITY_SCORES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Stream.concat(Stream.of(this.entityTarget.getParam()), this.scores.values().stream().flatMap((p_165487_) -> {
         return p_165487_.getReferencedContextParams().stream();
      })).collect(ImmutableSet.toImmutableSet());
   }

   public boolean test(LootContext p_81631_) {
      Entity entity = p_81631_.getParamOrNull(this.entityTarget.getParam());
      if (entity == null) {
         return false;
      } else {
         Scoreboard scoreboard = entity.level.getScoreboard();

         for(Entry<String, IntRange> entry : this.scores.entrySet()) {
            if (!this.hasScore(p_81631_, entity, scoreboard, entry.getKey(), entry.getValue())) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean hasScore(LootContext p_165491_, Entity p_165492_, Scoreboard p_165493_, String p_165494_, IntRange p_165495_) {
      Objective objective = p_165493_.getObjective(p_165494_);
      if (objective == null) {
         return false;
      } else {
         String s = p_165492_.getScoreboardName();
         return !p_165493_.hasPlayerScore(s, objective) ? false : p_165495_.test(p_165491_, p_165493_.getOrCreatePlayerScore(s, objective).getScore());
      }
   }

   public static EntityHasScoreCondition.Builder hasScores(LootContext.EntityTarget p_165489_) {
      return new EntityHasScoreCondition.Builder(p_165489_);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Map<String, IntRange> scores = Maps.newHashMap();
      private final LootContext.EntityTarget entityTarget;

      public Builder(LootContext.EntityTarget p_165499_) {
         this.entityTarget = p_165499_;
      }

      public EntityHasScoreCondition.Builder withScore(String p_165501_, IntRange p_165502_) {
         this.scores.put(p_165501_, p_165502_);
         return this;
      }

      public LootItemCondition build() {
         return new EntityHasScoreCondition(this.scores, this.entityTarget);
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<EntityHasScoreCondition> {
      public void serialize(JsonObject p_81644_, EntityHasScoreCondition p_81645_, JsonSerializationContext p_81646_) {
         JsonObject jsonobject = new JsonObject();

         for(Entry<String, IntRange> entry : p_81645_.scores.entrySet()) {
            jsonobject.add(entry.getKey(), p_81646_.serialize(entry.getValue()));
         }

         p_81644_.add("scores", jsonobject);
         p_81644_.add("entity", p_81646_.serialize(p_81645_.entityTarget));
      }

      public EntityHasScoreCondition deserialize(JsonObject p_81652_, JsonDeserializationContext p_81653_) {
         Set<Entry<String, JsonElement>> set = GsonHelper.getAsJsonObject(p_81652_, "scores").entrySet();
         Map<String, IntRange> map = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> entry : set) {
            map.put(entry.getKey(), GsonHelper.convertToObject(entry.getValue(), "score", p_81653_, IntRange.class));
         }

         return new EntityHasScoreCondition(map, GsonHelper.getAsObject(p_81652_, "entity", p_81653_, LootContext.EntityTarget.class));
      }
   }
}