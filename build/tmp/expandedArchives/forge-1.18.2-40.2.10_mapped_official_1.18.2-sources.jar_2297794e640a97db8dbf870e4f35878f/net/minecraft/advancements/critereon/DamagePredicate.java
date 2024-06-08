package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

public class DamagePredicate {
   public static final DamagePredicate ANY = DamagePredicate.Builder.damageInstance().build();
   private final MinMaxBounds.Doubles dealtDamage;
   private final MinMaxBounds.Doubles takenDamage;
   private final EntityPredicate sourceEntity;
   @Nullable
   private final Boolean blocked;
   private final DamageSourcePredicate type;

   public DamagePredicate() {
      this.dealtDamage = MinMaxBounds.Doubles.ANY;
      this.takenDamage = MinMaxBounds.Doubles.ANY;
      this.sourceEntity = EntityPredicate.ANY;
      this.blocked = null;
      this.type = DamageSourcePredicate.ANY;
   }

   public DamagePredicate(MinMaxBounds.Doubles p_24911_, MinMaxBounds.Doubles p_24912_, EntityPredicate p_24913_, @Nullable Boolean p_24914_, DamageSourcePredicate p_24915_) {
      this.dealtDamage = p_24911_;
      this.takenDamage = p_24912_;
      this.sourceEntity = p_24913_;
      this.blocked = p_24914_;
      this.type = p_24915_;
   }

   public boolean matches(ServerPlayer p_24918_, DamageSource p_24919_, float p_24920_, float p_24921_, boolean p_24922_) {
      if (this == ANY) {
         return true;
      } else if (!this.dealtDamage.matches((double)p_24920_)) {
         return false;
      } else if (!this.takenDamage.matches((double)p_24921_)) {
         return false;
      } else if (!this.sourceEntity.matches(p_24918_, p_24919_.getEntity())) {
         return false;
      } else if (this.blocked != null && this.blocked != p_24922_) {
         return false;
      } else {
         return this.type.matches(p_24918_, p_24919_);
      }
   }

   public static DamagePredicate fromJson(@Nullable JsonElement p_24924_) {
      if (p_24924_ != null && !p_24924_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_24924_, "damage");
         MinMaxBounds.Doubles minmaxbounds$doubles = MinMaxBounds.Doubles.fromJson(jsonobject.get("dealt"));
         MinMaxBounds.Doubles minmaxbounds$doubles1 = MinMaxBounds.Doubles.fromJson(jsonobject.get("taken"));
         Boolean obool = jsonobject.has("blocked") ? GsonHelper.getAsBoolean(jsonobject, "blocked") : null;
         EntityPredicate entitypredicate = EntityPredicate.fromJson(jsonobject.get("source_entity"));
         DamageSourcePredicate damagesourcepredicate = DamageSourcePredicate.fromJson(jsonobject.get("type"));
         return new DamagePredicate(minmaxbounds$doubles, minmaxbounds$doubles1, entitypredicate, obool, damagesourcepredicate);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("dealt", this.dealtDamage.serializeToJson());
         jsonobject.add("taken", this.takenDamage.serializeToJson());
         jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
         jsonobject.add("type", this.type.serializeToJson());
         if (this.blocked != null) {
            jsonobject.addProperty("blocked", this.blocked);
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private MinMaxBounds.Doubles dealtDamage = MinMaxBounds.Doubles.ANY;
      private MinMaxBounds.Doubles takenDamage = MinMaxBounds.Doubles.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;
      @Nullable
      private Boolean blocked;
      private DamageSourcePredicate type = DamageSourcePredicate.ANY;

      public static DamagePredicate.Builder damageInstance() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder dealtDamage(MinMaxBounds.Doubles p_148146_) {
         this.dealtDamage = p_148146_;
         return this;
      }

      public DamagePredicate.Builder takenDamage(MinMaxBounds.Doubles p_148148_) {
         this.takenDamage = p_148148_;
         return this;
      }

      public DamagePredicate.Builder sourceEntity(EntityPredicate p_148144_) {
         this.sourceEntity = p_148144_;
         return this;
      }

      public DamagePredicate.Builder blocked(Boolean p_24935_) {
         this.blocked = p_24935_;
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate p_148142_) {
         this.type = p_148142_;
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate.Builder p_24933_) {
         this.type = p_24933_.build();
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}