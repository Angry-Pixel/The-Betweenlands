package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   @Nullable
   private final Boolean isProjectile;
   @Nullable
   private final Boolean isExplosion;
   @Nullable
   private final Boolean bypassesArmor;
   @Nullable
   private final Boolean bypassesInvulnerability;
   @Nullable
   private final Boolean bypassesMagic;
   @Nullable
   private final Boolean isFire;
   @Nullable
   private final Boolean isMagic;
   @Nullable
   private final Boolean isLightning;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean p_25433_, @Nullable Boolean p_25434_, @Nullable Boolean p_25435_, @Nullable Boolean p_25436_, @Nullable Boolean p_25437_, @Nullable Boolean p_25438_, @Nullable Boolean p_25439_, @Nullable Boolean p_25440_, EntityPredicate p_25441_, EntityPredicate p_25442_) {
      this.isProjectile = p_25433_;
      this.isExplosion = p_25434_;
      this.bypassesArmor = p_25435_;
      this.bypassesInvulnerability = p_25436_;
      this.bypassesMagic = p_25437_;
      this.isFire = p_25438_;
      this.isMagic = p_25439_;
      this.isLightning = p_25440_;
      this.directEntity = p_25441_;
      this.sourceEntity = p_25442_;
   }

   public boolean matches(ServerPlayer p_25449_, DamageSource p_25450_) {
      return this.matches(p_25449_.getLevel(), p_25449_.position(), p_25450_);
   }

   public boolean matches(ServerLevel p_25445_, Vec3 p_25446_, DamageSource p_25447_) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != p_25447_.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != p_25447_.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != p_25447_.isBypassArmor()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != p_25447_.isBypassInvul()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != p_25447_.isBypassMagic()) {
         return false;
      } else if (this.isFire != null && this.isFire != p_25447_.isFire()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != p_25447_.isMagic()) {
         return false;
      } else if (this.isLightning != null && this.isLightning != (p_25447_ == DamageSource.LIGHTNING_BOLT)) {
         return false;
      } else if (!this.directEntity.matches(p_25445_, p_25446_, p_25447_.getDirectEntity())) {
         return false;
      } else {
         return this.sourceEntity.matches(p_25445_, p_25446_, p_25447_.getEntity());
      }
   }

   public static DamageSourcePredicate fromJson(@Nullable JsonElement p_25452_) {
      if (p_25452_ != null && !p_25452_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_25452_, "damage type");
         Boolean obool = getOptionalBoolean(jsonobject, "is_projectile");
         Boolean obool1 = getOptionalBoolean(jsonobject, "is_explosion");
         Boolean obool2 = getOptionalBoolean(jsonobject, "bypasses_armor");
         Boolean obool3 = getOptionalBoolean(jsonobject, "bypasses_invulnerability");
         Boolean obool4 = getOptionalBoolean(jsonobject, "bypasses_magic");
         Boolean obool5 = getOptionalBoolean(jsonobject, "is_fire");
         Boolean obool6 = getOptionalBoolean(jsonobject, "is_magic");
         Boolean obool7 = getOptionalBoolean(jsonobject, "is_lightning");
         EntityPredicate entitypredicate = EntityPredicate.fromJson(jsonobject.get("direct_entity"));
         EntityPredicate entitypredicate1 = EntityPredicate.fromJson(jsonobject.get("source_entity"));
         return new DamageSourcePredicate(obool, obool1, obool2, obool3, obool4, obool5, obool6, obool7, entitypredicate, entitypredicate1);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject p_25454_, String p_25455_) {
      return p_25454_.has(p_25455_) ? GsonHelper.getAsBoolean(p_25454_, p_25455_) : null;
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.addOptionally(jsonobject, "is_projectile", this.isProjectile);
         this.addOptionally(jsonobject, "is_explosion", this.isExplosion);
         this.addOptionally(jsonobject, "bypasses_armor", this.bypassesArmor);
         this.addOptionally(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addOptionally(jsonobject, "bypasses_magic", this.bypassesMagic);
         this.addOptionally(jsonobject, "is_fire", this.isFire);
         this.addOptionally(jsonobject, "is_magic", this.isMagic);
         this.addOptionally(jsonobject, "is_lightning", this.isLightning);
         jsonobject.add("direct_entity", this.directEntity.serializeToJson());
         jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
         return jsonobject;
      }
   }

   private void addOptionally(JsonObject p_25457_, String p_25458_, @Nullable Boolean p_25459_) {
      if (p_25459_ != null) {
         p_25457_.addProperty(p_25458_, p_25459_);
      }

   }

   public static class Builder {
      @Nullable
      private Boolean isProjectile;
      @Nullable
      private Boolean isExplosion;
      @Nullable
      private Boolean bypassesArmor;
      @Nullable
      private Boolean bypassesInvulnerability;
      @Nullable
      private Boolean bypassesMagic;
      @Nullable
      private Boolean isFire;
      @Nullable
      private Boolean isMagic;
      @Nullable
      private Boolean isLightning;
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean p_25475_) {
         this.isProjectile = p_25475_;
         return this;
      }

      public DamageSourcePredicate.Builder isExplosion(Boolean p_148236_) {
         this.isExplosion = p_148236_;
         return this;
      }

      public DamageSourcePredicate.Builder bypassesArmor(Boolean p_148238_) {
         this.bypassesArmor = p_148238_;
         return this;
      }

      public DamageSourcePredicate.Builder bypassesInvulnerability(Boolean p_148240_) {
         this.bypassesInvulnerability = p_148240_;
         return this;
      }

      public DamageSourcePredicate.Builder bypassesMagic(Boolean p_148242_) {
         this.bypassesMagic = p_148242_;
         return this;
      }

      public DamageSourcePredicate.Builder isFire(Boolean p_148244_) {
         this.isFire = p_148244_;
         return this;
      }

      public DamageSourcePredicate.Builder isMagic(Boolean p_148246_) {
         this.isMagic = p_148246_;
         return this;
      }

      public DamageSourcePredicate.Builder isLightning(Boolean p_25478_) {
         this.isLightning = p_25478_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate p_148230_) {
         this.directEntity = p_148230_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder p_25473_) {
         this.directEntity = p_25473_.build();
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate p_148234_) {
         this.sourceEntity = p_148234_;
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate.Builder p_148232_) {
         this.sourceEntity = p_148232_.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
      }
   }
}