package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlacedBlockTrigger extends SimpleCriterionTrigger<PlacedBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("placed_block");

   public ResourceLocation getId() {
      return ID;
   }

   public PlacedBlockTrigger.TriggerInstance createInstance(JsonObject p_59485_, EntityPredicate.Composite p_59486_, DeserializationContext p_59487_) {
      Block block = deserializeBlock(p_59485_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_59485_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_59475_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_59475_ + ":");
         });
      }

      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_59485_.get("location"));
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_59485_.get("item"));
      return new PlacedBlockTrigger.TriggerInstance(p_59486_, block, statepropertiespredicate, locationpredicate, itempredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_59483_) {
      if (p_59483_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_59483_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayer p_59470_, BlockPos p_59471_, ItemStack p_59472_) {
      BlockState blockstate = p_59470_.getLevel().getBlockState(p_59471_);
      this.trigger(p_59470_, (p_59481_) -> {
         return p_59481_.matches(blockstate, p_59471_, p_59470_.getLevel(), p_59472_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Block block;
      private final StatePropertiesPredicate state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_59500_, @Nullable Block p_59501_, StatePropertiesPredicate p_59502_, LocationPredicate p_59503_, ItemPredicate p_59504_) {
         super(PlacedBlockTrigger.ID, p_59500_);
         this.block = p_59501_;
         this.state = p_59502_;
         this.location = p_59503_;
         this.item = p_59504_;
      }

      public static PlacedBlockTrigger.TriggerInstance placedBlock(Block p_59506_) {
         return new PlacedBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_59506_, StatePropertiesPredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(BlockState p_59508_, BlockPos p_59509_, ServerLevel p_59510_, ItemStack p_59511_) {
         if (this.block != null && !p_59508_.is(this.block)) {
            return false;
         } else if (!this.state.matches(p_59508_)) {
            return false;
         } else if (!this.location.matches(p_59510_, (double)p_59509_.getX(), (double)p_59509_.getY(), (double)p_59509_.getZ())) {
            return false;
         } else {
            return this.item.matches(p_59511_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_59513_) {
         JsonObject jsonobject = super.serializeToJson(p_59513_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}