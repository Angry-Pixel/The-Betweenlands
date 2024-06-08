package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class IntRange {
   @Nullable
   final NumberProvider min;
   @Nullable
   final NumberProvider max;
   private final IntRange.IntLimiter limiter;
   private final IntRange.IntChecker predicate;

   public Set<LootContextParam<?>> getReferencedContextParams() {
      Builder<LootContextParam<?>> builder = ImmutableSet.builder();
      if (this.min != null) {
         builder.addAll(this.min.getReferencedContextParams());
      }

      if (this.max != null) {
         builder.addAll(this.max.getReferencedContextParams());
      }

      return builder.build();
   }

   IntRange(@Nullable NumberProvider p_165006_, @Nullable NumberProvider p_165007_) {
      this.min = p_165006_;
      this.max = p_165007_;
      if (p_165006_ == null) {
         if (p_165007_ == null) {
            this.limiter = (p_165050_, p_165051_) -> {
               return p_165051_;
            };
            this.predicate = (p_165043_, p_165044_) -> {
               return true;
            };
         } else {
            this.limiter = (p_165054_, p_165055_) -> {
               return Math.min(p_165007_.getInt(p_165054_), p_165055_);
            };
            this.predicate = (p_165047_, p_165048_) -> {
               return p_165048_ <= p_165007_.getInt(p_165047_);
            };
         }
      } else if (p_165007_ == null) {
         this.limiter = (p_165033_, p_165034_) -> {
            return Math.max(p_165006_.getInt(p_165033_), p_165034_);
         };
         this.predicate = (p_165019_, p_165020_) -> {
            return p_165020_ >= p_165006_.getInt(p_165019_);
         };
      } else {
         this.limiter = (p_165038_, p_165039_) -> {
            return Mth.clamp(p_165039_, p_165006_.getInt(p_165038_), p_165007_.getInt(p_165038_));
         };
         this.predicate = (p_165024_, p_165025_) -> {
            return p_165025_ >= p_165006_.getInt(p_165024_) && p_165025_ <= p_165007_.getInt(p_165024_);
         };
      }

   }

   public static IntRange exact(int p_165010_) {
      ConstantValue constantvalue = ConstantValue.exactly((float)p_165010_);
      return new IntRange(constantvalue, constantvalue);
   }

   public static IntRange range(int p_165012_, int p_165013_) {
      return new IntRange(ConstantValue.exactly((float)p_165012_), ConstantValue.exactly((float)p_165013_));
   }

   public static IntRange lowerBound(int p_165027_) {
      return new IntRange(ConstantValue.exactly((float)p_165027_), (NumberProvider)null);
   }

   public static IntRange upperBound(int p_165041_) {
      return new IntRange((NumberProvider)null, ConstantValue.exactly((float)p_165041_));
   }

   public int clamp(LootContext p_165015_, int p_165016_) {
      return this.limiter.apply(p_165015_, p_165016_);
   }

   public boolean test(LootContext p_165029_, int p_165030_) {
      return this.predicate.test(p_165029_, p_165030_);
   }

   @FunctionalInterface
   interface IntChecker {
      boolean test(LootContext p_165057_, int p_165058_);
   }

   @FunctionalInterface
   interface IntLimiter {
      int apply(LootContext p_165060_, int p_165061_);
   }

   public static class Serializer implements JsonDeserializer<IntRange>, JsonSerializer<IntRange> {
      public IntRange deserialize(JsonElement p_165064_, Type p_165065_, JsonDeserializationContext p_165066_) {
         if (p_165064_.isJsonPrimitive()) {
            return IntRange.exact(p_165064_.getAsInt());
         } else {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_165064_, "value");
            NumberProvider numberprovider = jsonobject.has("min") ? GsonHelper.getAsObject(jsonobject, "min", p_165066_, NumberProvider.class) : null;
            NumberProvider numberprovider1 = jsonobject.has("max") ? GsonHelper.getAsObject(jsonobject, "max", p_165066_, NumberProvider.class) : null;
            return new IntRange(numberprovider, numberprovider1);
         }
      }

      public JsonElement serialize(IntRange p_165068_, Type p_165069_, JsonSerializationContext p_165070_) {
         JsonObject jsonobject = new JsonObject();
         if (Objects.equals(p_165068_.max, p_165068_.min)) {
            return p_165070_.serialize(p_165068_.min);
         } else {
            if (p_165068_.max != null) {
               jsonobject.add("max", p_165070_.serialize(p_165068_.max));
            }

            if (p_165068_.min != null) {
               jsonobject.add("min", p_165070_.serialize(p_165068_.min));
            }

            return jsonobject;
         }
      }
   }
}