package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BeeNestDestroyedTrigger extends SimpleCriterionTrigger<BeeNestDestroyedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

   public ResourceLocation getId() {
      return ID;
   }

   public BeeNestDestroyedTrigger.TriggerInstance createInstance(JsonObject p_17490_, EntityPredicate.Composite p_17491_, DeserializationContext p_17492_) {
      Block block = deserializeBlock(p_17490_);
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_17490_.get("item"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_17490_.get("num_bees_inside"));
      return new BeeNestDestroyedTrigger.TriggerInstance(p_17491_, block, itempredicate, minmaxbounds$ints);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_17488_) {
      if (p_17488_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_17488_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer p_146652_, BlockState p_146653_, ItemStack p_146654_, int p_146655_) {
      this.trigger(p_146652_, (p_146660_) -> {
         return p_146660_.matches(p_146653_, p_146654_, p_146655_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final ItemPredicate item;
      private final MinMaxBounds.Ints numBees;

      public TriggerInstance(EntityPredicate.Composite p_17504_, @Nullable Block p_17505_, ItemPredicate p_17506_, MinMaxBounds.Ints p_17507_) {
         super(BeeNestDestroyedTrigger.ID, p_17504_);
         this.block = p_17505_;
         this.item = p_17506_;
         this.numBees = p_17507_;
      }

      public static BeeNestDestroyedTrigger.TriggerInstance destroyedBeeNest(Block p_17513_, ItemPredicate.Builder p_17514_, MinMaxBounds.Ints p_17515_) {
         return new BeeNestDestroyedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_17513_, p_17514_.build(), p_17515_);
      }

      public boolean matches(BlockState p_146662_, ItemStack p_146663_, int p_146664_) {
         if (this.block != null && !p_146662_.is(this.block)) {
            return false;
         } else {
            return !this.item.matches(p_146663_) ? false : this.numBees.matches(p_146664_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_17517_) {
         JsonObject jsonobject = super.serializeToJson(p_17517_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("num_bees_inside", this.numBees.serializeToJson());
         return jsonobject;
      }
   }
}