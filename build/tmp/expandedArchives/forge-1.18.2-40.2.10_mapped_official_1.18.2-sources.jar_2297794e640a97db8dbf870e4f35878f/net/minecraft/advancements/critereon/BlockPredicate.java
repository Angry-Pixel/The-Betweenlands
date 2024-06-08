package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate {
   public static final BlockPredicate ANY = new BlockPredicate((TagKey<Block>)null, (Set<Block>)null, StatePropertiesPredicate.ANY, NbtPredicate.ANY);
   @Nullable
   private final TagKey<Block> tag;
   @Nullable
   private final Set<Block> blocks;
   private final StatePropertiesPredicate properties;
   private final NbtPredicate nbt;

   public BlockPredicate(@Nullable TagKey<Block> p_204023_, @Nullable Set<Block> p_204024_, StatePropertiesPredicate p_204025_, NbtPredicate p_204026_) {
      this.tag = p_204023_;
      this.blocks = p_204024_;
      this.properties = p_204025_;
      this.nbt = p_204026_;
   }

   public boolean matches(ServerLevel p_17915_, BlockPos p_17916_) {
      if (this == ANY) {
         return true;
      } else if (!p_17915_.isLoaded(p_17916_)) {
         return false;
      } else {
         BlockState blockstate = p_17915_.getBlockState(p_17916_);
         if (this.tag != null && !blockstate.is(this.tag)) {
            return false;
         } else if (this.blocks != null && !this.blocks.contains(blockstate.getBlock())) {
            return false;
         } else if (!this.properties.matches(blockstate)) {
            return false;
         } else {
            if (this.nbt != NbtPredicate.ANY) {
               BlockEntity blockentity = p_17915_.getBlockEntity(p_17916_);
               if (blockentity == null || !this.nbt.matches(blockentity.saveWithFullMetadata())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static BlockPredicate fromJson(@Nullable JsonElement p_17918_) {
      if (p_17918_ != null && !p_17918_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_17918_, "block");
         NbtPredicate nbtpredicate = NbtPredicate.fromJson(jsonobject.get("nbt"));
         Set<Block> set = null;
         JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "blocks", (JsonArray)null);
         if (jsonarray != null) {
            ImmutableSet.Builder<Block> builder = ImmutableSet.builder();

            for(JsonElement jsonelement : jsonarray) {
               ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.convertToString(jsonelement, "block"));
               builder.add(Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown block id '" + resourcelocation + "'");
               }));
            }

            set = builder.build();
         }

         TagKey<Block> tagkey = null;
         if (jsonobject.has("tag")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(jsonobject, "tag"));
            tagkey = TagKey.create(Registry.BLOCK_REGISTRY, resourcelocation1);
         }

         StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(jsonobject.get("state"));
         return new BlockPredicate(tagkey, set, statepropertiespredicate, nbtpredicate);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.blocks != null) {
            JsonArray jsonarray = new JsonArray();

            for(Block block : this.blocks) {
               jsonarray.add(Registry.BLOCK.getKey(block).toString());
            }

            jsonobject.add("blocks", jsonarray);
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", this.tag.location().toString());
         }

         jsonobject.add("nbt", this.nbt.serializeToJson());
         jsonobject.add("state", this.properties.serializeToJson());
         return jsonobject;
      }
   }

   public static class Builder {
      @Nullable
      private Set<Block> blocks;
      @Nullable
      private TagKey<Block> tag;
      private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;
      private NbtPredicate nbt = NbtPredicate.ANY;

      private Builder() {
      }

      public static BlockPredicate.Builder block() {
         return new BlockPredicate.Builder();
      }

      public BlockPredicate.Builder of(Block... p_146727_) {
         this.blocks = ImmutableSet.copyOf(p_146727_);
         return this;
      }

      public BlockPredicate.Builder of(Iterable<Block> p_146723_) {
         this.blocks = ImmutableSet.copyOf(p_146723_);
         return this;
      }

      public BlockPredicate.Builder of(TagKey<Block> p_204028_) {
         this.tag = p_204028_;
         return this;
      }

      public BlockPredicate.Builder hasNbt(CompoundTag p_146725_) {
         this.nbt = new NbtPredicate(p_146725_);
         return this;
      }

      public BlockPredicate.Builder setProperties(StatePropertiesPredicate p_17930_) {
         this.properties = p_17930_;
         return this;
      }

      public BlockPredicate build() {
         return new BlockPredicate(this.tag, this.blocks, this.properties, this.nbt);
      }
   }
}