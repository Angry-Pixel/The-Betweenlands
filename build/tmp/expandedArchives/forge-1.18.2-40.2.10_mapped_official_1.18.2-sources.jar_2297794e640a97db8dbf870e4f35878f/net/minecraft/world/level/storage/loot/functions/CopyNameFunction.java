package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   final CopyNameFunction.NameSource source;

   CopyNameFunction(LootItemCondition[] p_80177_, CopyNameFunction.NameSource p_80178_) {
      super(p_80177_);
      this.source = p_80178_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_NAME;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack p_80185_, LootContext p_80186_) {
      Object object = p_80186_.getParamOrNull(this.source.param);
      if (object instanceof Nameable) {
         Nameable nameable = (Nameable)object;
         if (nameable.hasCustomName()) {
            p_80185_.setHoverName(nameable.getDisplayName());
         }
      }

      return p_80185_;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(CopyNameFunction.NameSource p_80188_) {
      return simpleBuilder((p_80191_) -> {
         return new CopyNameFunction(p_80191_, p_80188_);
      });
   }

   public static enum NameSource {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public final String name;
      public final LootContextParam<?> param;

      private NameSource(String p_80206_, LootContextParam<?> p_80207_) {
         this.name = p_80206_;
         this.param = p_80207_;
      }

      public static CopyNameFunction.NameSource getByName(String p_80209_) {
         for(CopyNameFunction.NameSource copynamefunction$namesource : values()) {
            if (copynamefunction$namesource.name.equals(p_80209_)) {
               return copynamefunction$namesource;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + p_80209_);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CopyNameFunction> {
      public void serialize(JsonObject p_80219_, CopyNameFunction p_80220_, JsonSerializationContext p_80221_) {
         super.serialize(p_80219_, p_80220_, p_80221_);
         p_80219_.addProperty("source", p_80220_.source.name);
      }

      public CopyNameFunction deserialize(JsonObject p_80215_, JsonDeserializationContext p_80216_, LootItemCondition[] p_80217_) {
         CopyNameFunction.NameSource copynamefunction$namesource = CopyNameFunction.NameSource.getByName(GsonHelper.getAsString(p_80215_, "source"));
         return new CopyNameFunction(p_80217_, copynamefunction$namesource);
      }
   }
}