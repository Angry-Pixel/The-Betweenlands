package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject p_21725_, EntityPredicate.Composite p_21726_, DeserializationContext p_21727_) {
      EntityPredicate.Composite[] aentitypredicate$composite = EntityPredicate.Composite.fromJsonArray(p_21725_, "victims", p_21727_);
      return new ChanneledLightningTrigger.TriggerInstance(p_21726_, aentitypredicate$composite);
   }

   public void trigger(ServerPlayer p_21722_, Collection<? extends Entity> p_21723_) {
      List<LootContext> list = p_21723_.stream().map((p_21720_) -> {
         return EntityPredicate.createContext(p_21722_, p_21720_);
      }).collect(Collectors.toList());
      this.trigger(p_21722_, (p_21730_) -> {
         return p_21730_.matches(list);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite[] victims;

      public TriggerInstance(EntityPredicate.Composite p_21738_, EntityPredicate.Composite[] p_21739_) {
         super(ChanneledLightningTrigger.ID, p_21738_);
         this.victims = p_21739_;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... p_21747_) {
         return new ChanneledLightningTrigger.TriggerInstance(EntityPredicate.Composite.ANY, Stream.of(p_21747_).map(EntityPredicate.Composite::wrap).toArray((p_21741_) -> {
            return new EntityPredicate.Composite[p_21741_];
         }));
      }

      public boolean matches(Collection<? extends LootContext> p_21745_) {
         for(EntityPredicate.Composite entitypredicate$composite : this.victims) {
            boolean flag = false;

            for(LootContext lootcontext : p_21745_) {
               if (entitypredicate$composite.matches(lootcontext)) {
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

      public JsonObject serializeToJson(SerializationContext p_21743_) {
         JsonObject jsonobject = super.serializeToJson(p_21743_);
         jsonobject.add("victims", EntityPredicate.Composite.toJson(this.victims, p_21743_));
         return jsonobject;
      }
   }
}