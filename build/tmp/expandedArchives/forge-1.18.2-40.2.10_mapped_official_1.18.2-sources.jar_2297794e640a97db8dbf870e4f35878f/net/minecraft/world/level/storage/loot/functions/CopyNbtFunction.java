package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

public class CopyNbtFunction extends LootItemConditionalFunction {
   final NbtProvider source;
   final List<CopyNbtFunction.CopyOperation> operations;

   CopyNbtFunction(LootItemCondition[] p_165175_, NbtProvider p_165176_, List<CopyNbtFunction.CopyOperation> p_165177_) {
      super(p_165175_);
      this.source = p_165176_;
      this.operations = ImmutableList.copyOf(p_165177_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_NBT;
   }

   static NbtPathArgument.NbtPath compileNbtPath(String p_80268_) {
      try {
         return (new NbtPathArgument()).parse(new StringReader(p_80268_));
      } catch (CommandSyntaxException commandsyntaxexception) {
         throw new IllegalArgumentException("Failed to parse path " + p_80268_, commandsyntaxexception);
      }
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.source.getReferencedContextParams();
   }

   public ItemStack run(ItemStack p_80250_, LootContext p_80251_) {
      Tag tag = this.source.get(p_80251_);
      if (tag != null) {
         this.operations.forEach((p_80255_) -> {
            p_80255_.apply(p_80250_::getOrCreateTag, tag);
         });
      }

      return p_80250_;
   }

   public static CopyNbtFunction.Builder copyData(NbtProvider p_165181_) {
      return new CopyNbtFunction.Builder(p_165181_);
   }

   public static CopyNbtFunction.Builder copyData(LootContext.EntityTarget p_165179_) {
      return new CopyNbtFunction.Builder(ContextNbtProvider.forContextEntity(p_165179_));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyNbtFunction.Builder> {
      private final NbtProvider source;
      private final List<CopyNbtFunction.CopyOperation> ops = Lists.newArrayList();

      Builder(NbtProvider p_165183_) {
         this.source = p_165183_;
      }

      public CopyNbtFunction.Builder copy(String p_80283_, String p_80284_, CopyNbtFunction.MergeStrategy p_80285_) {
         this.ops.add(new CopyNbtFunction.CopyOperation(p_80283_, p_80284_, p_80285_));
         return this;
      }

      public CopyNbtFunction.Builder copy(String p_80280_, String p_80281_) {
         return this.copy(p_80280_, p_80281_, CopyNbtFunction.MergeStrategy.REPLACE);
      }

      protected CopyNbtFunction.Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyNbtFunction(this.getConditions(), this.source, this.ops);
      }
   }

   static class CopyOperation {
      private final String sourcePathText;
      private final NbtPathArgument.NbtPath sourcePath;
      private final String targetPathText;
      private final NbtPathArgument.NbtPath targetPath;
      private final CopyNbtFunction.MergeStrategy op;

      CopyOperation(String p_80294_, String p_80295_, CopyNbtFunction.MergeStrategy p_80296_) {
         this.sourcePathText = p_80294_;
         this.sourcePath = CopyNbtFunction.compileNbtPath(p_80294_);
         this.targetPathText = p_80295_;
         this.targetPath = CopyNbtFunction.compileNbtPath(p_80295_);
         this.op = p_80296_;
      }

      public void apply(Supplier<Tag> p_80306_, Tag p_80307_) {
         try {
            List<Tag> list = this.sourcePath.get(p_80307_);
            if (!list.isEmpty()) {
               this.op.merge(p_80306_.get(), this.targetPath, list);
            }
         } catch (CommandSyntaxException commandsyntaxexception) {
         }

      }

      public JsonObject toJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("source", this.sourcePathText);
         jsonobject.addProperty("target", this.targetPathText);
         jsonobject.addProperty("op", this.op.name);
         return jsonobject;
      }

      public static CopyNbtFunction.CopyOperation fromJson(JsonObject p_80304_) {
         String s = GsonHelper.getAsString(p_80304_, "source");
         String s1 = GsonHelper.getAsString(p_80304_, "target");
         CopyNbtFunction.MergeStrategy copynbtfunction$mergestrategy = CopyNbtFunction.MergeStrategy.getByName(GsonHelper.getAsString(p_80304_, "op"));
         return new CopyNbtFunction.CopyOperation(s, s1, copynbtfunction$mergestrategy);
      }
   }

   public static enum MergeStrategy {
      REPLACE("replace") {
         public void merge(Tag p_80362_, NbtPathArgument.NbtPath p_80363_, List<Tag> p_80364_) throws CommandSyntaxException {
            p_80363_.set(p_80362_, Iterables.getLast(p_80364_)::copy);
         }
      },
      APPEND("append") {
         public void merge(Tag p_80373_, NbtPathArgument.NbtPath p_80374_, List<Tag> p_80375_) throws CommandSyntaxException {
            List<Tag> list = p_80374_.getOrCreate(p_80373_, ListTag::new);
            list.forEach((p_80371_) -> {
               if (p_80371_ instanceof ListTag) {
                  p_80375_.forEach((p_165187_) -> {
                     ((ListTag)p_80371_).add(p_165187_.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void merge(Tag p_80387_, NbtPathArgument.NbtPath p_80388_, List<Tag> p_80389_) throws CommandSyntaxException {
            List<Tag> list = p_80388_.getOrCreate(p_80387_, CompoundTag::new);
            list.forEach((p_80385_) -> {
               if (p_80385_ instanceof CompoundTag) {
                  p_80389_.forEach((p_165190_) -> {
                     if (p_165190_ instanceof CompoundTag) {
                        ((CompoundTag)p_80385_).merge((CompoundTag)p_165190_);
                     }

                  });
               }

            });
         }
      };

      final String name;

      public abstract void merge(Tag p_80351_, NbtPathArgument.NbtPath p_80352_, List<Tag> p_80353_) throws CommandSyntaxException;

      MergeStrategy(String p_80341_) {
         this.name = p_80341_;
      }

      public static CopyNbtFunction.MergeStrategy getByName(String p_80350_) {
         for(CopyNbtFunction.MergeStrategy copynbtfunction$mergestrategy : values()) {
            if (copynbtfunction$mergestrategy.name.equals(p_80350_)) {
               return copynbtfunction$mergestrategy;
            }
         }

         throw new IllegalArgumentException("Invalid merge strategy" + p_80350_);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CopyNbtFunction> {
      public void serialize(JsonObject p_80399_, CopyNbtFunction p_80400_, JsonSerializationContext p_80401_) {
         super.serialize(p_80399_, p_80400_, p_80401_);
         p_80399_.add("source", p_80401_.serialize(p_80400_.source));
         JsonArray jsonarray = new JsonArray();
         p_80400_.operations.stream().map(CopyNbtFunction.CopyOperation::toJson).forEach(jsonarray::add);
         p_80399_.add("ops", jsonarray);
      }

      public CopyNbtFunction deserialize(JsonObject p_80395_, JsonDeserializationContext p_80396_, LootItemCondition[] p_80397_) {
         NbtProvider nbtprovider = GsonHelper.getAsObject(p_80395_, "source", p_80396_, NbtProvider.class);
         List<CopyNbtFunction.CopyOperation> list = Lists.newArrayList();

         for(JsonElement jsonelement : GsonHelper.getAsJsonArray(p_80395_, "ops")) {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "op");
            list.add(CopyNbtFunction.CopyOperation.fromJson(jsonobject));
         }

         return new CopyNbtFunction(p_80397_, nbtprovider, list);
      }
   }
}