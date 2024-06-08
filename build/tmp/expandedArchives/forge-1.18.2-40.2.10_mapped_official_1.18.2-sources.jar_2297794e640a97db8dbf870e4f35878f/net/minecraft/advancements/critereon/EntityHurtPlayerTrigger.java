package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger<EntityHurtPlayerTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

   public ResourceLocation getId() {
      return ID;
   }

   public EntityHurtPlayerTrigger.TriggerInstance createInstance(JsonObject p_35188_, EntityPredicate.Composite p_35189_, DeserializationContext p_35190_) {
      DamagePredicate damagepredicate = DamagePredicate.fromJson(p_35188_.get("damage"));
      return new EntityHurtPlayerTrigger.TriggerInstance(p_35189_, damagepredicate);
   }

   public void trigger(ServerPlayer p_35175_, DamageSource p_35176_, float p_35177_, float p_35178_, boolean p_35179_) {
      this.trigger(p_35175_, (p_35186_) -> {
         return p_35186_.matches(p_35175_, p_35176_, p_35177_, p_35178_, p_35179_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;

      public TriggerInstance(EntityPredicate.Composite p_35198_, DamagePredicate p_35199_) {
         super(EntityHurtPlayerTrigger.ID, p_35198_);
         this.damage = p_35199_;
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer() {
         return new EntityHurtPlayerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY);
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate p_150188_) {
         return new EntityHurtPlayerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_150188_);
      }

      public static EntityHurtPlayerTrigger.TriggerInstance entityHurtPlayer(DamagePredicate.Builder p_35207_) {
         return new EntityHurtPlayerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_35207_.build());
      }

      public boolean matches(ServerPlayer p_35201_, DamageSource p_35202_, float p_35203_, float p_35204_, boolean p_35205_) {
         return this.damage.matches(p_35201_, p_35202_, p_35203_, p_35204_, p_35205_);
      }

      public JsonObject serializeToJson(SerializationContext p_35209_) {
         JsonObject jsonobject = super.serializeToJson(p_35209_);
         jsonobject.add("damage", this.damage.serializeToJson());
         return jsonobject;
      }
   }
}