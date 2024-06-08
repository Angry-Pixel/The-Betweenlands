package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<FishingRodHookedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public ResourceLocation getId() {
      return ID;
   }

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject p_40427_, EntityPredicate.Composite p_40428_, DeserializationContext p_40429_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_40427_.get("rod"));
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_40427_, "entity", p_40429_);
      ItemPredicate itempredicate1 = ItemPredicate.fromJson(p_40427_.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(p_40428_, itempredicate, entitypredicate$composite, itempredicate1);
   }

   public void trigger(ServerPlayer p_40417_, ItemStack p_40418_, FishingHook p_40419_, Collection<ItemStack> p_40420_) {
      LootContext lootcontext = EntityPredicate.createContext(p_40417_, (Entity)(p_40419_.getHookedIn() != null ? p_40419_.getHookedIn() : p_40419_));
      this.trigger(p_40417_, (p_40425_) -> {
         return p_40425_.matches(p_40418_, lootcontext, p_40420_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate rod;
      private final EntityPredicate.Composite entity;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_40439_, ItemPredicate p_40440_, EntityPredicate.Composite p_40441_, ItemPredicate p_40442_) {
         super(FishingRodHookedTrigger.ID, p_40439_);
         this.rod = p_40440_;
         this.entity = p_40441_;
         this.item = p_40442_;
      }

      public static FishingRodHookedTrigger.TriggerInstance fishedItem(ItemPredicate p_40448_, EntityPredicate p_40449_, ItemPredicate p_40450_) {
         return new FishingRodHookedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_40448_, EntityPredicate.Composite.wrap(p_40449_), p_40450_);
      }

      public boolean matches(ItemStack p_40444_, LootContext p_40445_, Collection<ItemStack> p_40446_) {
         if (!this.rod.matches(p_40444_)) {
            return false;
         } else if (!this.entity.matches(p_40445_)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean flag = false;
               Entity entity = p_40445_.getParamOrNull(LootContextParams.THIS_ENTITY);
               if (entity instanceof ItemEntity) {
                  ItemEntity itementity = (ItemEntity)entity;
                  if (this.item.matches(itementity.getItem())) {
                     flag = true;
                  }
               }

               for(ItemStack itemstack : p_40446_) {
                  if (this.item.matches(itemstack)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonObject serializeToJson(SerializationContext p_40452_) {
         JsonObject jsonobject = super.serializeToJson(p_40452_);
         jsonobject.add("rod", this.rod.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_40452_));
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}