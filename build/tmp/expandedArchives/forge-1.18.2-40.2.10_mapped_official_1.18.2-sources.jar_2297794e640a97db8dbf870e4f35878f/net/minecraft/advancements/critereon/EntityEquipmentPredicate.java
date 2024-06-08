package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;

public class EntityEquipmentPredicate {
   public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   public static final EntityEquipmentPredicate CAPTAIN = new EntityEquipmentPredicate(ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   private final ItemPredicate head;
   private final ItemPredicate chest;
   private final ItemPredicate legs;
   private final ItemPredicate feet;
   private final ItemPredicate mainhand;
   private final ItemPredicate offhand;

   public EntityEquipmentPredicate(ItemPredicate p_32186_, ItemPredicate p_32187_, ItemPredicate p_32188_, ItemPredicate p_32189_, ItemPredicate p_32190_, ItemPredicate p_32191_) {
      this.head = p_32186_;
      this.chest = p_32187_;
      this.legs = p_32188_;
      this.feet = p_32189_;
      this.mainhand = p_32190_;
      this.offhand = p_32191_;
   }

   public boolean matches(@Nullable Entity p_32194_) {
      if (this == ANY) {
         return true;
      } else if (!(p_32194_ instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity livingentity = (LivingEntity)p_32194_;
         if (!this.head.matches(livingentity.getItemBySlot(EquipmentSlot.HEAD))) {
            return false;
         } else if (!this.chest.matches(livingentity.getItemBySlot(EquipmentSlot.CHEST))) {
            return false;
         } else if (!this.legs.matches(livingentity.getItemBySlot(EquipmentSlot.LEGS))) {
            return false;
         } else if (!this.feet.matches(livingentity.getItemBySlot(EquipmentSlot.FEET))) {
            return false;
         } else if (!this.mainhand.matches(livingentity.getItemBySlot(EquipmentSlot.MAINHAND))) {
            return false;
         } else {
            return this.offhand.matches(livingentity.getItemBySlot(EquipmentSlot.OFFHAND));
         }
      }
   }

   public static EntityEquipmentPredicate fromJson(@Nullable JsonElement p_32196_) {
      if (p_32196_ != null && !p_32196_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_32196_, "equipment");
         ItemPredicate itempredicate = ItemPredicate.fromJson(jsonobject.get("head"));
         ItemPredicate itempredicate1 = ItemPredicate.fromJson(jsonobject.get("chest"));
         ItemPredicate itempredicate2 = ItemPredicate.fromJson(jsonobject.get("legs"));
         ItemPredicate itempredicate3 = ItemPredicate.fromJson(jsonobject.get("feet"));
         ItemPredicate itempredicate4 = ItemPredicate.fromJson(jsonobject.get("mainhand"));
         ItemPredicate itempredicate5 = ItemPredicate.fromJson(jsonobject.get("offhand"));
         return new EntityEquipmentPredicate(itempredicate, itempredicate1, itempredicate2, itempredicate3, itempredicate4, itempredicate5);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("head", this.head.serializeToJson());
         jsonobject.add("chest", this.chest.serializeToJson());
         jsonobject.add("legs", this.legs.serializeToJson());
         jsonobject.add("feet", this.feet.serializeToJson());
         jsonobject.add("mainhand", this.mainhand.serializeToJson());
         jsonobject.add("offhand", this.offhand.serializeToJson());
         return jsonobject;
      }
   }

   public static class Builder {
      private ItemPredicate head = ItemPredicate.ANY;
      private ItemPredicate chest = ItemPredicate.ANY;
      private ItemPredicate legs = ItemPredicate.ANY;
      private ItemPredicate feet = ItemPredicate.ANY;
      private ItemPredicate mainhand = ItemPredicate.ANY;
      private ItemPredicate offhand = ItemPredicate.ANY;

      public static EntityEquipmentPredicate.Builder equipment() {
         return new EntityEquipmentPredicate.Builder();
      }

      public EntityEquipmentPredicate.Builder head(ItemPredicate p_32206_) {
         this.head = p_32206_;
         return this;
      }

      public EntityEquipmentPredicate.Builder chest(ItemPredicate p_32209_) {
         this.chest = p_32209_;
         return this;
      }

      public EntityEquipmentPredicate.Builder legs(ItemPredicate p_32211_) {
         this.legs = p_32211_;
         return this;
      }

      public EntityEquipmentPredicate.Builder feet(ItemPredicate p_32213_) {
         this.feet = p_32213_;
         return this;
      }

      public EntityEquipmentPredicate.Builder mainhand(ItemPredicate p_149929_) {
         this.mainhand = p_149929_;
         return this;
      }

      public EntityEquipmentPredicate.Builder offhand(ItemPredicate p_149931_) {
         this.offhand = p_149931_;
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
      }
   }
}