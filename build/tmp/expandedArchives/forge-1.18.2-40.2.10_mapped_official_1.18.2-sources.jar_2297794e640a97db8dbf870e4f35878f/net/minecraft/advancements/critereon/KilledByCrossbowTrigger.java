package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger<KilledByCrossbowTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public KilledByCrossbowTrigger.TriggerInstance createInstance(JsonObject p_46875_, EntityPredicate.Composite p_46876_, DeserializationContext p_46877_) {
      EntityPredicate.Composite[] aentitypredicate$composite = EntityPredicate.Composite.fromJsonArray(p_46875_, "victims", p_46877_);
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_46875_.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.TriggerInstance(p_46876_, aentitypredicate$composite, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer p_46872_, Collection<Entity> p_46873_) {
      List<LootContext> list = Lists.newArrayList();
      Set<EntityType<?>> set = Sets.newHashSet();

      for(Entity entity : p_46873_) {
         set.add(entity.getType());
         list.add(EntityPredicate.createContext(p_46872_, entity));
      }

      this.trigger(p_46872_, (p_46881_) -> {
         return p_46881_.matches(list, set.size());
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite[] victims;
      private final MinMaxBounds.Ints uniqueEntityTypes;

      public TriggerInstance(EntityPredicate.Composite p_46890_, EntityPredicate.Composite[] p_46891_, MinMaxBounds.Ints p_46892_) {
         super(KilledByCrossbowTrigger.ID, p_46890_);
         this.victims = p_46891_;
         this.uniqueEntityTypes = p_46892_;
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(EntityPredicate.Builder... p_46901_) {
         EntityPredicate.Composite[] aentitypredicate$composite = new EntityPredicate.Composite[p_46901_.length];

         for(int i = 0; i < p_46901_.length; ++i) {
            EntityPredicate.Builder entitypredicate$builder = p_46901_[i];
            aentitypredicate$composite[i] = EntityPredicate.Composite.wrap(entitypredicate$builder.build());
         }

         return new KilledByCrossbowTrigger.TriggerInstance(EntityPredicate.Composite.ANY, aentitypredicate$composite, MinMaxBounds.Ints.ANY);
      }

      public static KilledByCrossbowTrigger.TriggerInstance crossbowKilled(MinMaxBounds.Ints p_46894_) {
         EntityPredicate.Composite[] aentitypredicate$composite = new EntityPredicate.Composite[0];
         return new KilledByCrossbowTrigger.TriggerInstance(EntityPredicate.Composite.ANY, aentitypredicate$composite, p_46894_);
      }

      public boolean matches(Collection<LootContext> p_46898_, int p_46899_) {
         if (this.victims.length > 0) {
            List<LootContext> list = Lists.newArrayList(p_46898_);

            for(EntityPredicate.Composite entitypredicate$composite : this.victims) {
               boolean flag = false;
               Iterator<LootContext> iterator = list.iterator();

               while(iterator.hasNext()) {
                  LootContext lootcontext = iterator.next();
                  if (entitypredicate$composite.matches(lootcontext)) {
                     iterator.remove();
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }

         return this.uniqueEntityTypes.matches(p_46899_);
      }

      public JsonObject serializeToJson(SerializationContext p_46896_) {
         JsonObject jsonobject = super.serializeToJson(p_46896_);
         jsonobject.add("victims", EntityPredicate.Composite.toJson(this.victims, p_46896_));
         jsonobject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return jsonobject;
      }
   }
}