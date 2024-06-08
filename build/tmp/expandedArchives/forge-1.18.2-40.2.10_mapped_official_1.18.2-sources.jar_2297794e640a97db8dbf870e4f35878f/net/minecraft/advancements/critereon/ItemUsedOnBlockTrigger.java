package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ItemUsedOnBlockTrigger extends SimpleCriterionTrigger<ItemUsedOnBlockTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("item_used_on_block");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemUsedOnBlockTrigger.TriggerInstance createInstance(JsonObject p_45493_, EntityPredicate.Composite p_45494_, DeserializationContext p_45495_) {
      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_45493_.get("location"));
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_45493_.get("item"));
      return new ItemUsedOnBlockTrigger.TriggerInstance(p_45494_, locationpredicate, itempredicate);
   }

   public void trigger(ServerPlayer p_45483_, BlockPos p_45484_, ItemStack p_45485_) {
      BlockState blockstate = p_45483_.getLevel().getBlockState(p_45484_);
      this.trigger(p_45483_, (p_45491_) -> {
         return p_45491_.matches(blockstate, p_45483_.getLevel(), p_45484_, p_45485_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_45504_, LocationPredicate p_45505_, ItemPredicate p_45506_) {
         super(ItemUsedOnBlockTrigger.ID, p_45504_);
         this.location = p_45505_;
         this.item = p_45506_;
      }

      public static ItemUsedOnBlockTrigger.TriggerInstance itemUsedOnBlock(LocationPredicate.Builder p_45508_, ItemPredicate.Builder p_45509_) {
         return new ItemUsedOnBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_45508_.build(), p_45509_.build());
      }

      public boolean matches(BlockState p_45511_, ServerLevel p_45512_, BlockPos p_45513_, ItemStack p_45514_) {
         return !this.location.matches(p_45512_, (double)p_45513_.getX() + 0.5D, (double)p_45513_.getY() + 0.5D, (double)p_45513_.getZ() + 0.5D) ? false : this.item.matches(p_45514_);
      }

      public JsonObject serializeToJson(SerializationContext p_45516_) {
         JsonObject jsonobject = super.serializeToJson(p_45516_);
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}