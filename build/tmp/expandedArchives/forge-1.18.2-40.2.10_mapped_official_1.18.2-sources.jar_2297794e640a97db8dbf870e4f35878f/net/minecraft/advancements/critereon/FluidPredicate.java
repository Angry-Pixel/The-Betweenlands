package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidPredicate {
   public static final FluidPredicate ANY = new FluidPredicate((TagKey<Fluid>)null, (Fluid)null, StatePropertiesPredicate.ANY);
   @Nullable
   private final TagKey<Fluid> tag;
   @Nullable
   private final Fluid fluid;
   private final StatePropertiesPredicate properties;

   public FluidPredicate(@Nullable TagKey<Fluid> p_204102_, @Nullable Fluid p_204103_, StatePropertiesPredicate p_204104_) {
      this.tag = p_204102_;
      this.fluid = p_204103_;
      this.properties = p_204104_;
   }

   public boolean matches(ServerLevel p_41105_, BlockPos p_41106_) {
      if (this == ANY) {
         return true;
      } else if (!p_41105_.isLoaded(p_41106_)) {
         return false;
      } else {
         FluidState fluidstate = p_41105_.getFluidState(p_41106_);
         if (this.tag != null && !fluidstate.is(this.tag)) {
            return false;
         } else if (this.fluid != null && !fluidstate.is(this.fluid)) {
            return false;
         } else {
            return this.properties.matches(fluidstate);
         }
      }
   }

   public static FluidPredicate fromJson(@Nullable JsonElement p_41108_) {
      if (p_41108_ != null && !p_41108_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_41108_, "fluid");
         Fluid fluid = null;
         if (jsonobject.has("fluid")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonobject, "fluid"));
            fluid = Registry.FLUID.get(resourcelocation);
         }

         TagKey<Fluid> tagkey = null;
         if (jsonobject.has("tag")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(jsonobject, "tag"));
            tagkey = TagKey.create(Registry.FLUID_REGISTRY, resourcelocation1);
         }

         StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(jsonobject.get("state"));
         return new FluidPredicate(tagkey, fluid, statepropertiespredicate);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.fluid != null) {
            jsonobject.addProperty("fluid", Registry.FLUID.getKey(this.fluid).toString());
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", this.tag.location().toString());
         }

         jsonobject.add("state", this.properties.serializeToJson());
         return jsonobject;
      }
   }

   public static class Builder {
      @Nullable
      private Fluid fluid;
      @Nullable
      private TagKey<Fluid> fluids;
      private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;

      private Builder() {
      }

      public static FluidPredicate.Builder fluid() {
         return new FluidPredicate.Builder();
      }

      public FluidPredicate.Builder of(Fluid p_151172_) {
         this.fluid = p_151172_;
         return this;
      }

      public FluidPredicate.Builder of(TagKey<Fluid> p_204106_) {
         this.fluids = p_204106_;
         return this;
      }

      public FluidPredicate.Builder setProperties(StatePropertiesPredicate p_151170_) {
         this.properties = p_151170_;
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.fluids, this.fluid, this.properties);
      }
   }
}