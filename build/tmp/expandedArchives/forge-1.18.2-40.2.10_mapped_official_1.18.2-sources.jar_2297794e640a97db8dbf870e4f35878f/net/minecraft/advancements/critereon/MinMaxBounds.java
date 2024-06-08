package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;

public abstract class MinMaxBounds<T extends Number> {
   public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.empty"));
   public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.swapped"));
   @Nullable
   protected final T min;
   @Nullable
   protected final T max;

   protected MinMaxBounds(@Nullable T p_55303_, @Nullable T p_55304_) {
      this.min = p_55303_;
      this.max = p_55304_;
   }

   @Nullable
   public T getMin() {
      return this.min;
   }

   @Nullable
   public T getMax() {
      return this.max;
   }

   public boolean isAny() {
      return this.min == null && this.max == null;
   }

   public JsonElement serializeToJson() {
      if (this.isAny()) {
         return JsonNull.INSTANCE;
      } else if (this.min != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.min != null) {
            jsonobject.addProperty("min", this.min);
         }

         if (this.max != null) {
            jsonobject.addProperty("max", this.max);
         }

         return jsonobject;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement p_55307_, R p_55308_, BiFunction<JsonElement, String, T> p_55309_, MinMaxBounds.BoundsFactory<T, R> p_55310_) {
      if (p_55307_ != null && !p_55307_.isJsonNull()) {
         if (GsonHelper.isNumberValue(p_55307_)) {
            T t2 = p_55309_.apply(p_55307_, "value");
            return p_55310_.create(t2, t2);
         } else {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_55307_, "value");
            T t = jsonobject.has("min") ? p_55309_.apply(jsonobject.get("min"), "min") : null;
            T t1 = jsonobject.has("max") ? p_55309_.apply(jsonobject.get("max"), "max") : null;
            return p_55310_.create(t, t1);
         }
      } else {
         return p_55308_;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader p_55314_, MinMaxBounds.BoundsFromReaderFactory<T, R> p_55315_, Function<String, T> p_55316_, Supplier<DynamicCommandExceptionType> p_55317_, Function<T, T> p_55318_) throws CommandSyntaxException {
      if (!p_55314_.canRead()) {
         throw ERROR_EMPTY.createWithContext(p_55314_);
      } else {
         int i = p_55314_.getCursor();

         try {
            T t = optionallyFormat(readNumber(p_55314_, p_55316_, p_55317_), p_55318_);
            T t1;
            if (p_55314_.canRead(2) && p_55314_.peek() == '.' && p_55314_.peek(1) == '.') {
               p_55314_.skip();
               p_55314_.skip();
               t1 = optionallyFormat(readNumber(p_55314_, p_55316_, p_55317_), p_55318_);
               if (t == null && t1 == null) {
                  throw ERROR_EMPTY.createWithContext(p_55314_);
               }
            } else {
               t1 = t;
            }

            if (t == null && t1 == null) {
               throw ERROR_EMPTY.createWithContext(p_55314_);
            } else {
               return p_55315_.create(p_55314_, t, t1);
            }
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_55314_.setCursor(i);
            throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(), commandsyntaxexception.getInput(), i);
         }
      }
   }

   @Nullable
   private static <T extends Number> T readNumber(StringReader p_55320_, Function<String, T> p_55321_, Supplier<DynamicCommandExceptionType> p_55322_) throws CommandSyntaxException {
      int i = p_55320_.getCursor();

      while(p_55320_.canRead() && isAllowedInputChat(p_55320_)) {
         p_55320_.skip();
      }

      String s = p_55320_.getString().substring(i, p_55320_.getCursor());
      if (s.isEmpty()) {
         return (T)null;
      } else {
         try {
            return p_55321_.apply(s);
         } catch (NumberFormatException numberformatexception) {
            throw p_55322_.get().createWithContext(p_55320_, s);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader p_55312_) {
      char c0 = p_55312_.peek();
      if ((c0 < '0' || c0 > '9') && c0 != '-') {
         if (c0 != '.') {
            return false;
         } else {
            return !p_55312_.canRead(2) || p_55312_.peek(1) != '.';
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static <T> T optionallyFormat(@Nullable T p_55324_, Function<T, T> p_55325_) {
      return (T)(p_55324_ == null ? null : p_55325_.apply(p_55324_));
   }

   @FunctionalInterface
   protected interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(@Nullable T p_55330_, @Nullable T p_55331_);
   }

   @FunctionalInterface
   protected interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader p_55333_, @Nullable T p_55334_, @Nullable T p_55335_) throws CommandSyntaxException;
   }

   public static class Doubles extends MinMaxBounds<Double> {
      public static final MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles((Double)null, (Double)null);
      @Nullable
      private final Double minSq;
      @Nullable
      private final Double maxSq;

      private static MinMaxBounds.Doubles create(StringReader p_154796_, @Nullable Double p_154797_, @Nullable Double p_154798_) throws CommandSyntaxException {
         if (p_154797_ != null && p_154798_ != null && p_154797_ > p_154798_) {
            throw ERROR_SWAPPED.createWithContext(p_154796_);
         } else {
            return new MinMaxBounds.Doubles(p_154797_, p_154798_);
         }
      }

      @Nullable
      private static Double squareOpt(@Nullable Double p_154803_) {
         return p_154803_ == null ? null : p_154803_ * p_154803_;
      }

      private Doubles(@Nullable Double p_154784_, @Nullable Double p_154785_) {
         super(p_154784_, p_154785_);
         this.minSq = squareOpt(p_154784_);
         this.maxSq = squareOpt(p_154785_);
      }

      public static MinMaxBounds.Doubles exactly(double p_154787_) {
         return new MinMaxBounds.Doubles(p_154787_, p_154787_);
      }

      public static MinMaxBounds.Doubles between(double p_154789_, double p_154790_) {
         return new MinMaxBounds.Doubles(p_154789_, p_154790_);
      }

      public static MinMaxBounds.Doubles atLeast(double p_154805_) {
         return new MinMaxBounds.Doubles(p_154805_, (Double)null);
      }

      public static MinMaxBounds.Doubles atMost(double p_154809_) {
         return new MinMaxBounds.Doubles((Double)null, p_154809_);
      }

      public boolean matches(double p_154811_) {
         if (this.min != null && this.min > p_154811_) {
            return false;
         } else {
            return this.max == null || !(this.max < p_154811_);
         }
      }

      public boolean matchesSqr(double p_154813_) {
         if (this.minSq != null && this.minSq > p_154813_) {
            return false;
         } else {
            return this.maxSq == null || !(this.maxSq < p_154813_);
         }
      }

      public static MinMaxBounds.Doubles fromJson(@Nullable JsonElement p_154792_) {
         return fromJson(p_154792_, ANY, GsonHelper::convertToDouble, MinMaxBounds.Doubles::new);
      }

      public static MinMaxBounds.Doubles fromReader(StringReader p_154794_) throws CommandSyntaxException {
         return fromReader(p_154794_, (p_154807_) -> {
            return p_154807_;
         });
      }

      public static MinMaxBounds.Doubles fromReader(StringReader p_154800_, Function<Double, Double> p_154801_) throws CommandSyntaxException {
         return fromReader(p_154800_, MinMaxBounds.Doubles::create, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble, p_154801_);
      }
   }

   public static class Ints extends MinMaxBounds<Integer> {
      public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints((Integer)null, (Integer)null);
      @Nullable
      private final Long minSq;
      @Nullable
      private final Long maxSq;

      private static MinMaxBounds.Ints create(StringReader p_55378_, @Nullable Integer p_55379_, @Nullable Integer p_55380_) throws CommandSyntaxException {
         if (p_55379_ != null && p_55380_ != null && p_55379_ > p_55380_) {
            throw ERROR_SWAPPED.createWithContext(p_55378_);
         } else {
            return new MinMaxBounds.Ints(p_55379_, p_55380_);
         }
      }

      @Nullable
      private static Long squareOpt(@Nullable Integer p_55385_) {
         return p_55385_ == null ? null : p_55385_.longValue() * p_55385_.longValue();
      }

      private Ints(@Nullable Integer p_55369_, @Nullable Integer p_55370_) {
         super(p_55369_, p_55370_);
         this.minSq = squareOpt(p_55369_);
         this.maxSq = squareOpt(p_55370_);
      }

      public static MinMaxBounds.Ints exactly(int p_55372_) {
         return new MinMaxBounds.Ints(p_55372_, p_55372_);
      }

      public static MinMaxBounds.Ints between(int p_154815_, int p_154816_) {
         return new MinMaxBounds.Ints(p_154815_, p_154816_);
      }

      public static MinMaxBounds.Ints atLeast(int p_55387_) {
         return new MinMaxBounds.Ints(p_55387_, (Integer)null);
      }

      public static MinMaxBounds.Ints atMost(int p_154820_) {
         return new MinMaxBounds.Ints((Integer)null, p_154820_);
      }

      public boolean matches(int p_55391_) {
         if (this.min != null && this.min > p_55391_) {
            return false;
         } else {
            return this.max == null || this.max >= p_55391_;
         }
      }

      public boolean matchesSqr(long p_154818_) {
         if (this.minSq != null && this.minSq > p_154818_) {
            return false;
         } else {
            return this.maxSq == null || this.maxSq >= p_154818_;
         }
      }

      public static MinMaxBounds.Ints fromJson(@Nullable JsonElement p_55374_) {
         return fromJson(p_55374_, ANY, GsonHelper::convertToInt, MinMaxBounds.Ints::new);
      }

      public static MinMaxBounds.Ints fromReader(StringReader p_55376_) throws CommandSyntaxException {
         return fromReader(p_55376_, (p_55389_) -> {
            return p_55389_;
         });
      }

      public static MinMaxBounds.Ints fromReader(StringReader p_55382_, Function<Integer, Integer> p_55383_) throws CommandSyntaxException {
         return fromReader(p_55382_, MinMaxBounds.Ints::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, p_55383_);
      }
   }
}