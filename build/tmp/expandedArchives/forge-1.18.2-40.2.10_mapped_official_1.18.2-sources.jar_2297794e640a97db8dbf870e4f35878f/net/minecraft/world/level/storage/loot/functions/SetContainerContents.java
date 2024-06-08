package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   final List<LootPoolEntryContainer> entries;
   final BlockEntityType<?> type;

   SetContainerContents(LootItemCondition[] p_193033_, BlockEntityType<?> p_193034_, List<LootPoolEntryContainer> p_193035_) {
      super(p_193033_);
      this.type = p_193034_;
      this.entries = ImmutableList.copyOf(p_193035_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_CONTENTS;
   }

   public ItemStack run(ItemStack p_80911_, LootContext p_80912_) {
      if (p_80911_.isEmpty()) {
         return p_80911_;
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.create();
         this.entries.forEach((p_80916_) -> {
            p_80916_.expand(p_80912_, (p_165321_) -> {
               p_165321_.createItemStack(LootTable.createStackSplitter(nonnulllist::add), p_80912_);
            });
         });
         CompoundTag compoundtag = new CompoundTag();
         ContainerHelper.saveAllItems(compoundtag, nonnulllist);
         CompoundTag compoundtag1 = BlockItem.getBlockEntityData(p_80911_);
         if (compoundtag1 == null) {
            compoundtag1 = compoundtag;
         } else {
            compoundtag1.merge(compoundtag);
         }

         BlockItem.setBlockEntityData(p_80911_, this.type, compoundtag1);
         return p_80911_;
      }
   }

   public void validate(ValidationContext p_80918_) {
      super.validate(p_80918_);

      for(int i = 0; i < this.entries.size(); ++i) {
         this.entries.get(i).validate(p_80918_.forChild(".entry[" + i + "]"));
      }

   }

   public static SetContainerContents.Builder setContents(BlockEntityType<?> p_193037_) {
      return new SetContainerContents.Builder(p_193037_);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetContainerContents.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();
      private final BlockEntityType<?> type;

      public Builder(BlockEntityType<?> p_193040_) {
         this.type = p_193040_;
      }

      protected SetContainerContents.Builder getThis() {
         return this;
      }

      public SetContainerContents.Builder withEntry(LootPoolEntryContainer.Builder<?> p_80931_) {
         this.entries.add(p_80931_.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.type, this.entries);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerContents> {
      public void serialize(JsonObject p_80944_, SetContainerContents p_80945_, JsonSerializationContext p_80946_) {
         super.serialize(p_80944_, p_80945_, p_80946_);
         p_80944_.addProperty("type", Registry.BLOCK_ENTITY_TYPE.getKey(p_80945_.type).toString());
         p_80944_.add("entries", p_80946_.serialize(p_80945_.entries));
      }

      public SetContainerContents deserialize(JsonObject p_80936_, JsonDeserializationContext p_80937_, LootItemCondition[] p_80938_) {
         LootPoolEntryContainer[] alootpoolentrycontainer = GsonHelper.getAsObject(p_80936_, "entries", p_80937_, LootPoolEntryContainer[].class);
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_80936_, "type"));
         BlockEntityType<?> blockentitytype = Registry.BLOCK_ENTITY_TYPE.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block entity type id '" + resourcelocation + "'");
         });
         return new SetContainerContents(p_80938_, blockentitytype, Arrays.asList(alootpoolentrycontainer));
      }
   }
}