package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger<ChangeDimensionTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("changed_dimension");

   public ResourceLocation getId() {
      return ID;
   }

   public ChangeDimensionTrigger.TriggerInstance createInstance(JsonObject p_19762_, EntityPredicate.Composite p_19763_, DeserializationContext p_19764_) {
      ResourceKey<Level> resourcekey = p_19762_.has("from") ? ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(GsonHelper.getAsString(p_19762_, "from"))) : null;
      ResourceKey<Level> resourcekey1 = p_19762_.has("to") ? ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(GsonHelper.getAsString(p_19762_, "to"))) : null;
      return new ChangeDimensionTrigger.TriggerInstance(p_19763_, resourcekey, resourcekey1);
   }

   public void trigger(ServerPlayer p_19758_, ResourceKey<Level> p_19759_, ResourceKey<Level> p_19760_) {
      this.trigger(p_19758_, (p_19768_) -> {
         return p_19768_.matches(p_19759_, p_19760_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final ResourceKey<Level> from;
      @Nullable
      private final ResourceKey<Level> to;

      public TriggerInstance(EntityPredicate.Composite p_19777_, @Nullable ResourceKey<Level> p_19778_, @Nullable ResourceKey<Level> p_19779_) {
         super(ChangeDimensionTrigger.ID, p_19777_);
         this.from = p_19778_;
         this.to = p_19779_;
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimension() {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, (ResourceKey<Level>)null, (ResourceKey<Level>)null);
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimension(ResourceKey<Level> p_147561_, ResourceKey<Level> p_147562_) {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_147561_, p_147562_);
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionTo(ResourceKey<Level> p_19783_) {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, (ResourceKey<Level>)null, p_19783_);
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionFrom(ResourceKey<Level> p_147564_) {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_147564_, (ResourceKey<Level>)null);
      }

      public boolean matches(ResourceKey<Level> p_19785_, ResourceKey<Level> p_19786_) {
         if (this.from != null && this.from != p_19785_) {
            return false;
         } else {
            return this.to == null || this.to == p_19786_;
         }
      }

      public JsonObject serializeToJson(SerializationContext p_19781_) {
         JsonObject jsonobject = super.serializeToJson(p_19781_);
         if (this.from != null) {
            jsonobject.addProperty("from", this.from.location().toString());
         }

         if (this.to != null) {
            jsonobject.addProperty("to", this.to.location().toString());
         }

         return jsonobject;
      }
   }
}