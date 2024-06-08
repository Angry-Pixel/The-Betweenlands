package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ExplosionCondition implements LootItemCondition {
   static final ExplosionCondition INSTANCE = new ExplosionCondition();

   private ExplosionCondition() {
   }

   public LootItemConditionType getType() {
      return LootItemConditions.SURVIVES_EXPLOSION;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext p_81659_) {
      Float f = p_81659_.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if (f != null) {
         Random random = p_81659_.getRandom();
         float f1 = 1.0F / f;
         return random.nextFloat() <= f1;
      } else {
         return true;
      }
   }

   public static LootItemCondition.Builder survivesExplosion() {
      return () -> {
         return INSTANCE;
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ExplosionCondition> {
      public void serialize(JsonObject p_81671_, ExplosionCondition p_81672_, JsonSerializationContext p_81673_) {
      }

      public ExplosionCondition deserialize(JsonObject p_81679_, JsonDeserializationContext p_81680_) {
         return ExplosionCondition.INSTANCE;
      }
   }
}