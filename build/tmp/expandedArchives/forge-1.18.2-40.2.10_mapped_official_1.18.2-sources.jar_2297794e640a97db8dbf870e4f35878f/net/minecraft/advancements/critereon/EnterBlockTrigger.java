package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockTrigger extends SimpleCriterionTrigger<EnterBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("enter_block");

   public ResourceLocation getId() {
      return ID;
   }

   public EnterBlockTrigger.TriggerInstance createInstance(JsonObject p_31281_, EntityPredicate.Composite p_31282_, DeserializationContext p_31283_) {
      Block block = deserializeBlock(p_31281_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_31281_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_31274_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_31274_);
         });
      }

      return new EnterBlockTrigger.TriggerInstance(p_31282_, block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_31279_) {
      if (p_31279_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_31279_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer p_31270_, BlockState p_31271_) {
      this.trigger(p_31270_, (p_31277_) -> {
         return p_31277_.matches(p_31271_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final StatePropertiesPredicate state;

      public TriggerInstance(EntityPredicate.Composite p_31294_, @Nullable Block p_31295_, StatePropertiesPredicate p_31296_) {
         super(EnterBlockTrigger.ID, p_31294_);
         this.block = p_31295_;
         this.state = p_31296_;
      }

      public static EnterBlockTrigger.TriggerInstance entersBlock(Block p_31298_) {
         return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_31298_, StatePropertiesPredicate.ANY);
      }

      public JsonObject serializeToJson(SerializationContext p_31302_) {
         JsonObject jsonobject = super.serializeToJson(p_31302_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState p_31300_) {
         if (this.block != null && !p_31300_.is(this.block)) {
            return false;
         } else {
            return this.state.matches(p_31300_);
         }
      }
   }
}