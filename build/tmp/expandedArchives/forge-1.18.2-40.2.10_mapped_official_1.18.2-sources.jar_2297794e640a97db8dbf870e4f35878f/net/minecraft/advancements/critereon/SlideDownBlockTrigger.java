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

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<SlideDownBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("slide_down_block");

   public ResourceLocation getId() {
      return ID;
   }

   public SlideDownBlockTrigger.TriggerInstance createInstance(JsonObject p_66990_, EntityPredicate.Composite p_66991_, DeserializationContext p_66992_) {
      Block block = deserializeBlock(p_66990_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_66990_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_66983_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_66983_);
         });
      }

      return new SlideDownBlockTrigger.TriggerInstance(p_66991_, block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_66988_) {
      if (p_66988_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_66988_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer p_66979_, BlockState p_66980_) {
      this.trigger(p_66979_, (p_66986_) -> {
         return p_66986_.matches(p_66980_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final StatePropertiesPredicate state;

      public TriggerInstance(EntityPredicate.Composite p_67003_, @Nullable Block p_67004_, StatePropertiesPredicate p_67005_) {
         super(SlideDownBlockTrigger.ID, p_67003_);
         this.block = p_67004_;
         this.state = p_67005_;
      }

      public static SlideDownBlockTrigger.TriggerInstance slidesDownBlock(Block p_67007_) {
         return new SlideDownBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_67007_, StatePropertiesPredicate.ANY);
      }

      public JsonObject serializeToJson(SerializationContext p_67011_) {
         JsonObject jsonobject = super.serializeToJson(p_67011_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState p_67009_) {
         if (this.block != null && !p_67009_.is(this.block)) {
            return false;
         } else {
            return this.state.matches(p_67009_);
         }
      }
   }
}