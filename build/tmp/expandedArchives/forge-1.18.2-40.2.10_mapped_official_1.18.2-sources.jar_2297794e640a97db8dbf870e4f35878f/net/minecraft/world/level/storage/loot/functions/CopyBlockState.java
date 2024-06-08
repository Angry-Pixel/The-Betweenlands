package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState extends LootItemConditionalFunction {
   final Block block;
   final Set<Property<?>> properties;

   CopyBlockState(LootItemCondition[] p_80050_, Block p_80051_, Set<Property<?>> p_80052_) {
      super(p_80050_);
      this.block = p_80051_;
      this.properties = p_80052_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_STATE;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   protected ItemStack run(ItemStack p_80060_, LootContext p_80061_) {
      BlockState blockstate = p_80061_.getParamOrNull(LootContextParams.BLOCK_STATE);
      if (blockstate != null) {
         CompoundTag compoundtag = p_80060_.getOrCreateTag();
         CompoundTag compoundtag1;
         if (compoundtag.contains("BlockStateTag", 10)) {
            compoundtag1 = compoundtag.getCompound("BlockStateTag");
         } else {
            compoundtag1 = new CompoundTag();
            compoundtag.put("BlockStateTag", compoundtag1);
         }

         this.properties.stream().filter(blockstate::hasProperty).forEach((p_80072_) -> {
            compoundtag1.putString(p_80072_.getName(), serialize(blockstate, p_80072_));
         });
      }

      return p_80060_;
   }

   public static CopyBlockState.Builder copyState(Block p_80063_) {
      return new CopyBlockState.Builder(p_80063_);
   }

   private static <T extends Comparable<T>> String serialize(BlockState p_80065_, Property<T> p_80066_) {
      T t = p_80065_.getValue(p_80066_);
      return p_80066_.getName(t);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyBlockState.Builder> {
      private final Block block;
      private final Set<Property<?>> properties = Sets.newHashSet();

      Builder(Block p_80079_) {
         this.block = p_80079_;
      }

      public CopyBlockState.Builder copy(Property<?> p_80085_) {
         if (!this.block.getStateDefinition().getProperties().contains(p_80085_)) {
            throw new IllegalStateException("Property " + p_80085_ + " is not present on block " + this.block);
         } else {
            this.properties.add(p_80085_);
            return this;
         }
      }

      protected CopyBlockState.Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyBlockState(this.getConditions(), this.block, this.properties);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CopyBlockState> {
      public void serialize(JsonObject p_80097_, CopyBlockState p_80098_, JsonSerializationContext p_80099_) {
         super.serialize(p_80097_, p_80098_, p_80099_);
         p_80097_.addProperty("block", Registry.BLOCK.getKey(p_80098_.block).toString());
         JsonArray jsonarray = new JsonArray();
         p_80098_.properties.forEach((p_80091_) -> {
            jsonarray.add(p_80091_.getName());
         });
         p_80097_.add("properties", jsonarray);
      }

      public CopyBlockState deserialize(JsonObject p_80093_, JsonDeserializationContext p_80094_, LootItemCondition[] p_80095_) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_80093_, "block"));
         Block block = Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + resourcelocation);
         });
         StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
         Set<Property<?>> set = Sets.newHashSet();
         JsonArray jsonarray = GsonHelper.getAsJsonArray(p_80093_, "properties", (JsonArray)null);
         if (jsonarray != null) {
            jsonarray.forEach((p_80111_) -> {
               set.add(statedefinition.getProperty(GsonHelper.convertToString(p_80111_, "property")));
            });
         }

         return new CopyBlockState(p_80095_, block, set);
      }
   }
}