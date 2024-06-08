package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerLootTable extends LootItemConditionalFunction {
   final ResourceLocation name;
   final long seed;
   final BlockEntityType<?> type;

   SetContainerLootTable(LootItemCondition[] p_193045_, ResourceLocation p_193046_, long p_193047_, BlockEntityType<?> p_193048_) {
      super(p_193045_);
      this.name = p_193046_;
      this.seed = p_193047_;
      this.type = p_193048_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_LOOT_TABLE;
   }

   public ItemStack run(ItemStack p_80967_, LootContext p_80968_) {
      if (p_80967_.isEmpty()) {
         return p_80967_;
      } else {
         CompoundTag compoundtag = BlockItem.getBlockEntityData(p_80967_);
         if (compoundtag == null) {
            compoundtag = new CompoundTag();
         }

         compoundtag.putString("LootTable", this.name.toString());
         if (this.seed != 0L) {
            compoundtag.putLong("LootTableSeed", this.seed);
         }

         BlockItem.setBlockEntityData(p_80967_, this.type, compoundtag);
         return p_80967_;
      }
   }

   public void validate(ValidationContext p_80970_) {
      if (p_80970_.hasVisitedTable(this.name)) {
         p_80970_.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(p_80970_);
         LootTable loottable = p_80970_.resolveLootTable(this.name);
         if (loottable == null) {
            p_80970_.reportProblem("Unknown loot table called " + this.name);
         } else {
            loottable.validate(p_80970_.enterTable("->{" + this.name + "}", this.name));
         }

      }
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> p_193050_, ResourceLocation p_193051_) {
      return simpleBuilder((p_193064_) -> {
         return new SetContainerLootTable(p_193064_, p_193051_, 0L, p_193050_);
      });
   }

   public static LootItemConditionalFunction.Builder<?> withLootTable(BlockEntityType<?> p_193053_, ResourceLocation p_193054_, long p_193055_) {
      return simpleBuilder((p_193060_) -> {
         return new SetContainerLootTable(p_193060_, p_193054_, p_193055_, p_193053_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerLootTable> {
      public void serialize(JsonObject p_80986_, SetContainerLootTable p_80987_, JsonSerializationContext p_80988_) {
         super.serialize(p_80986_, p_80987_, p_80988_);
         p_80986_.addProperty("name", p_80987_.name.toString());
         p_80986_.addProperty("type", Registry.BLOCK_ENTITY_TYPE.getKey(p_80987_.type).toString());
         if (p_80987_.seed != 0L) {
            p_80986_.addProperty("seed", p_80987_.seed);
         }

      }

      public SetContainerLootTable deserialize(JsonObject p_80978_, JsonDeserializationContext p_80979_, LootItemCondition[] p_80980_) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_80978_, "name"));
         long i = GsonHelper.getAsLong(p_80978_, "seed", 0L);
         ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(p_80978_, "type"));
         BlockEntityType<?> blockentitytype = Registry.BLOCK_ENTITY_TYPE.getOptional(resourcelocation1).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block entity type id '" + resourcelocation1 + "'");
         });
         return new SetContainerLootTable(p_80980_, resourcelocation, i, blockentitytype);
      }
   }
}