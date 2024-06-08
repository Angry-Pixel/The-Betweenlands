package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootItemEntityPropertyCondition implements LootItemCondition {
   final EntityPredicate predicate;
   final LootContext.EntityTarget entityTarget;

   LootItemEntityPropertyCondition(EntityPredicate p_81849_, LootContext.EntityTarget p_81850_) {
      this.predicate = p_81849_;
      this.entityTarget = p_81850_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ENTITY_PROPERTIES;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN, this.entityTarget.getParam());
   }

   public boolean test(LootContext p_81871_) {
      Entity entity = p_81871_.getParamOrNull(this.entityTarget.getParam());
      Vec3 vec3 = p_81871_.getParamOrNull(LootContextParams.ORIGIN);
      return this.predicate.matches(p_81871_.getLevel(), vec3, entity);
   }

   public static LootItemCondition.Builder entityPresent(LootContext.EntityTarget p_81863_) {
      return hasProperties(p_81863_, EntityPredicate.Builder.entity());
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget p_81865_, EntityPredicate.Builder p_81866_) {
      return () -> {
         return new LootItemEntityPropertyCondition(p_81866_.build(), p_81865_);
      };
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget p_81868_, EntityPredicate p_81869_) {
      return () -> {
         return new LootItemEntityPropertyCondition(p_81869_, p_81868_);
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemEntityPropertyCondition> {
      public void serialize(JsonObject p_81884_, LootItemEntityPropertyCondition p_81885_, JsonSerializationContext p_81886_) {
         p_81884_.add("predicate", p_81885_.predicate.serializeToJson());
         p_81884_.add("entity", p_81886_.serialize(p_81885_.entityTarget));
      }

      public LootItemEntityPropertyCondition deserialize(JsonObject p_81892_, JsonDeserializationContext p_81893_) {
         EntityPredicate entitypredicate = EntityPredicate.fromJson(p_81892_.get("predicate"));
         return new LootItemEntityPropertyCondition(entitypredicate, GsonHelper.getAsObject(p_81892_, "entity", p_81893_, LootContext.EntityTarget.class));
      }
   }
}