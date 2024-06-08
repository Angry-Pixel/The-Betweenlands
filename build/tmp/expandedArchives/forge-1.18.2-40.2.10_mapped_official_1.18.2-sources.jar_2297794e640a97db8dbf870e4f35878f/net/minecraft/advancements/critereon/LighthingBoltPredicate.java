package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class LighthingBoltPredicate {
   public static final LighthingBoltPredicate ANY = new LighthingBoltPredicate(MinMaxBounds.Ints.ANY, EntityPredicate.ANY);
   private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
   private static final String ENTITY_STRUCK_KEY = "entity_struck";
   private final MinMaxBounds.Ints blocksSetOnFire;
   private final EntityPredicate entityStruck;

   private LighthingBoltPredicate(MinMaxBounds.Ints p_153239_, EntityPredicate p_153240_) {
      this.blocksSetOnFire = p_153239_;
      this.entityStruck = p_153240_;
   }

   public static LighthingBoltPredicate blockSetOnFire(MinMaxBounds.Ints p_153251_) {
      return new LighthingBoltPredicate(p_153251_, EntityPredicate.ANY);
   }

   public static LighthingBoltPredicate fromJson(@Nullable JsonElement p_153253_) {
      if (p_153253_ != null && !p_153253_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_153253_, "lightning");
         return new LighthingBoltPredicate(MinMaxBounds.Ints.fromJson(jsonobject.get("blocks_set_on_fire")), EntityPredicate.fromJson(jsonobject.get("entity_struck")));
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("blocks_set_on_fire", this.blocksSetOnFire.serializeToJson());
         jsonobject.add("entity_struck", this.entityStruck.serializeToJson());
         return jsonobject;
      }
   }

   public boolean matches(Entity p_153247_, ServerLevel p_153248_, @Nullable Vec3 p_153249_) {
      if (this == ANY) {
         return true;
      } else if (!(p_153247_ instanceof LightningBolt)) {
         return false;
      } else {
         LightningBolt lightningbolt = (LightningBolt)p_153247_;
         return this.blocksSetOnFire.matches(lightningbolt.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || lightningbolt.getHitEntities().anyMatch((p_153245_) -> {
            return this.entityStruck.matches(p_153248_, p_153249_, p_153245_);
         }));
      }
   }
}