package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch {
   private final Map<Selector, List<Variant>> values = Maps.newHashMap();

   protected void putValue(Selector p_125320_, List<Variant> p_125321_) {
      List<Variant> list = this.values.put(p_125320_, p_125321_);
      if (list != null) {
         throw new IllegalStateException("Value " + p_125320_ + " is already defined");
      }
   }

   Map<Selector, List<Variant>> getEntries() {
      this.verifyComplete();
      return ImmutableMap.copyOf(this.values);
   }

   private void verifyComplete() {
      List<Property<?>> list = this.getDefinedProperties();
      Stream<Selector> stream = Stream.of(Selector.empty());

      for(Property<?> property : list) {
         stream = stream.flatMap((p_125316_) -> {
            return property.getAllValues().map(p_125316_::extend);
         });
      }

      List<Selector> list1 = stream.filter((p_125318_) -> {
         return !this.values.containsKey(p_125318_);
      }).collect(Collectors.toList());
      if (!list1.isEmpty()) {
         throw new IllegalStateException("Missing definition for properties: " + list1);
      }
   }

   abstract List<Property<?>> getDefinedProperties();

   public static <T1 extends Comparable<T1>> PropertyDispatch.C1<T1> property(Property<T1> p_125295_) {
      return new PropertyDispatch.C1<>(p_125295_);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> PropertyDispatch.C2<T1, T2> properties(Property<T1> p_125297_, Property<T2> p_125298_) {
      return new PropertyDispatch.C2<>(p_125297_, p_125298_);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> PropertyDispatch.C3<T1, T2, T3> properties(Property<T1> p_125300_, Property<T2> p_125301_, Property<T3> p_125302_) {
      return new PropertyDispatch.C3<>(p_125300_, p_125301_, p_125302_);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> PropertyDispatch.C4<T1, T2, T3, T4> properties(Property<T1> p_125304_, Property<T2> p_125305_, Property<T3> p_125306_, Property<T4> p_125307_) {
      return new PropertyDispatch.C4<>(p_125304_, p_125305_, p_125306_, p_125307_);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> PropertyDispatch.C5<T1, T2, T3, T4, T5> properties(Property<T1> p_125309_, Property<T2> p_125310_, Property<T3> p_125311_, Property<T4> p_125312_, Property<T5> p_125313_) {
      return new PropertyDispatch.C5<>(p_125309_, p_125310_, p_125311_, p_125312_, p_125313_);
   }

   public static class C1<T1 extends Comparable<T1>> extends PropertyDispatch {
      private final Property<T1> property1;

      C1(Property<T1> p_125325_) {
         this.property1 = p_125325_;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1);
      }

      public PropertyDispatch.C1<T1> select(T1 p_125333_, List<Variant> p_125334_) {
         Selector selector = Selector.of(this.property1.value(p_125333_));
         this.putValue(selector, p_125334_);
         return this;
      }

      public PropertyDispatch.C1<T1> select(T1 p_125330_, Variant p_125331_) {
         return this.select(p_125330_, Collections.singletonList(p_125331_));
      }

      public PropertyDispatch generate(Function<T1, Variant> p_125336_) {
         this.property1.getPossibleValues().forEach((p_125340_) -> {
            this.select(p_125340_, p_125336_.apply(p_125340_));
         });
         return this;
      }

      public PropertyDispatch generateList(Function<T1, List<Variant>> p_176314_) {
         this.property1.getPossibleValues().forEach((p_176312_) -> {
            this.select(p_176312_, p_176314_.apply(p_176312_));
         });
         return this;
      }
   }

   public static class C2<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends PropertyDispatch {
      private final Property<T1> property1;
      private final Property<T2> property2;

      C2(Property<T1> p_125344_, Property<T2> p_125345_) {
         this.property1 = p_125344_;
         this.property2 = p_125345_;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1, this.property2);
      }

      public PropertyDispatch.C2<T1, T2> select(T1 p_125355_, T2 p_125356_, List<Variant> p_125357_) {
         Selector selector = Selector.of(this.property1.value(p_125355_), this.property2.value(p_125356_));
         this.putValue(selector, p_125357_);
         return this;
      }

      public PropertyDispatch.C2<T1, T2> select(T1 p_125351_, T2 p_125352_, Variant p_125353_) {
         return this.select(p_125351_, p_125352_, Collections.singletonList(p_125353_));
      }

      public PropertyDispatch generate(BiFunction<T1, T2, Variant> p_125363_) {
         this.property1.getPossibleValues().forEach((p_125376_) -> {
            this.property2.getPossibleValues().forEach((p_176322_) -> {
               this.select((T1)p_125376_, p_176322_, p_125363_.apply((T1)p_125376_, p_176322_));
            });
         });
         return this;
      }

      public PropertyDispatch generateList(BiFunction<T1, T2, List<Variant>> p_125373_) {
         this.property1.getPossibleValues().forEach((p_125366_) -> {
            this.property2.getPossibleValues().forEach((p_176318_) -> {
               this.select((T1)p_125366_, p_176318_, p_125373_.apply((T1)p_125366_, p_176318_));
            });
         });
         return this;
      }
   }

   public static class C3<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> extends PropertyDispatch {
      private final Property<T1> property1;
      private final Property<T2> property2;
      private final Property<T3> property3;

      C3(Property<T1> p_125381_, Property<T2> p_125382_, Property<T3> p_125383_) {
         this.property1 = p_125381_;
         this.property2 = p_125382_;
         this.property3 = p_125383_;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1, this.property2, this.property3);
      }

      public PropertyDispatch.C3<T1, T2, T3> select(T1 p_125397_, T2 p_125398_, T3 p_125399_, List<Variant> p_125400_) {
         Selector selector = Selector.of(this.property1.value(p_125397_), this.property2.value(p_125398_), this.property3.value(p_125399_));
         this.putValue(selector, p_125400_);
         return this;
      }

      public PropertyDispatch.C3<T1, T2, T3> select(T1 p_125392_, T2 p_125393_, T3 p_125394_, Variant p_125395_) {
         return this.select(p_125392_, p_125393_, p_125394_, Collections.singletonList(p_125395_));
      }

      public PropertyDispatch generate(PropertyDispatch.TriFunction<T1, T2, T3, Variant> p_125390_) {
         this.property1.getPossibleValues().forEach((p_125404_) -> {
            this.property2.getPossibleValues().forEach((p_176343_) -> {
               this.property3.getPossibleValues().forEach((p_176339_) -> {
                  this.select((T1)p_125404_, (T2)p_176343_, p_176339_, p_125390_.apply((T1)p_125404_, (T2)p_176343_, p_176339_));
               });
            });
         });
         return this;
      }

      public PropertyDispatch generateList(PropertyDispatch.TriFunction<T1, T2, T3, List<Variant>> p_176345_) {
         this.property1.getPossibleValues().forEach((p_176334_) -> {
            this.property2.getPossibleValues().forEach((p_176331_) -> {
               this.property3.getPossibleValues().forEach((p_176327_) -> {
                  this.select((T1)p_176334_, (T2)p_176331_, p_176327_, p_176345_.apply((T1)p_176334_, (T2)p_176331_, p_176327_));
               });
            });
         });
         return this;
      }
   }

   public static class C4<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> extends PropertyDispatch {
      private final Property<T1> property1;
      private final Property<T2> property2;
      private final Property<T3> property3;
      private final Property<T4> property4;

      C4(Property<T1> p_125419_, Property<T2> p_125420_, Property<T3> p_125421_, Property<T4> p_125422_) {
         this.property1 = p_125419_;
         this.property2 = p_125420_;
         this.property3 = p_125421_;
         this.property4 = p_125422_;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1, this.property2, this.property3, this.property4);
      }

      public PropertyDispatch.C4<T1, T2, T3, T4> select(T1 p_125436_, T2 p_125437_, T3 p_125438_, T4 p_125439_, List<Variant> p_125440_) {
         Selector selector = Selector.of(this.property1.value(p_125436_), this.property2.value(p_125437_), this.property3.value(p_125438_), this.property4.value(p_125439_));
         this.putValue(selector, p_125440_);
         return this;
      }

      public PropertyDispatch.C4<T1, T2, T3, T4> select(T1 p_125430_, T2 p_125431_, T3 p_125432_, T4 p_125433_, Variant p_125434_) {
         return this.select(p_125430_, p_125431_, p_125432_, p_125433_, Collections.singletonList(p_125434_));
      }

      public PropertyDispatch generate(PropertyDispatch.QuadFunction<T1, T2, T3, T4, Variant> p_176362_) {
         this.property1.getPossibleValues().forEach((p_176385_) -> {
            this.property2.getPossibleValues().forEach((p_176380_) -> {
               this.property3.getPossibleValues().forEach((p_176376_) -> {
                  this.property4.getPossibleValues().forEach((p_176371_) -> {
                     this.select((T1)p_176385_, (T2)p_176380_, (T3)p_176376_, p_176371_, p_176362_.apply((T1)p_176385_, (T2)p_176380_, (T3)p_176376_, p_176371_));
                  });
               });
            });
         });
         return this;
      }

      public PropertyDispatch generateList(PropertyDispatch.QuadFunction<T1, T2, T3, T4, List<Variant>> p_176382_) {
         this.property1.getPossibleValues().forEach((p_176365_) -> {
            this.property2.getPossibleValues().forEach((p_176360_) -> {
               this.property3.getPossibleValues().forEach((p_176356_) -> {
                  this.property4.getPossibleValues().forEach((p_176351_) -> {
                     this.select((T1)p_176365_, (T2)p_176360_, (T3)p_176356_, p_176351_, p_176382_.apply((T1)p_176365_, (T2)p_176360_, (T3)p_176356_, p_176351_));
                  });
               });
            });
         });
         return this;
      }
   }

   public static class C5<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> extends PropertyDispatch {
      private final Property<T1> property1;
      private final Property<T2> property2;
      private final Property<T3> property3;
      private final Property<T4> property4;
      private final Property<T5> property5;

      C5(Property<T1> p_125448_, Property<T2> p_125449_, Property<T3> p_125450_, Property<T4> p_125451_, Property<T5> p_125452_) {
         this.property1 = p_125448_;
         this.property2 = p_125449_;
         this.property3 = p_125450_;
         this.property4 = p_125451_;
         this.property5 = p_125452_;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1, this.property2, this.property3, this.property4, this.property5);
      }

      public PropertyDispatch.C5<T1, T2, T3, T4, T5> select(T1 p_125468_, T2 p_125469_, T3 p_125470_, T4 p_125471_, T5 p_125472_, List<Variant> p_125473_) {
         Selector selector = Selector.of(this.property1.value(p_125468_), this.property2.value(p_125469_), this.property3.value(p_125470_), this.property4.value(p_125471_), this.property5.value(p_125472_));
         this.putValue(selector, p_125473_);
         return this;
      }

      public PropertyDispatch.C5<T1, T2, T3, T4, T5> select(T1 p_125461_, T2 p_125462_, T3 p_125463_, T4 p_125464_, T5 p_125465_, Variant p_125466_) {
         return this.select(p_125461_, p_125462_, p_125463_, p_125464_, p_125465_, Collections.singletonList(p_125466_));
      }

      public PropertyDispatch generate(PropertyDispatch.PentaFunction<T1, T2, T3, T4, T5, Variant> p_176409_) {
         this.property1.getPossibleValues().forEach((p_176439_) -> {
            this.property2.getPossibleValues().forEach((p_176434_) -> {
               this.property3.getPossibleValues().forEach((p_176430_) -> {
                  this.property4.getPossibleValues().forEach((p_176425_) -> {
                     this.property5.getPossibleValues().forEach((p_176419_) -> {
                        this.select((T1)p_176439_, (T2)p_176434_, (T3)p_176430_, (T4)p_176425_, p_176419_, p_176409_.apply((T1)p_176439_, (T2)p_176434_, (T3)p_176430_, (T4)p_176425_, p_176419_));
                     });
                  });
               });
            });
         });
         return this;
      }

      public PropertyDispatch generateList(PropertyDispatch.PentaFunction<T1, T2, T3, T4, T5, List<Variant>> p_176436_) {
         this.property1.getPossibleValues().forEach((p_176412_) -> {
            this.property2.getPossibleValues().forEach((p_176407_) -> {
               this.property3.getPossibleValues().forEach((p_176403_) -> {
                  this.property4.getPossibleValues().forEach((p_176398_) -> {
                     this.property5.getPossibleValues().forEach((p_176392_) -> {
                        this.select((T1)p_176412_, (T2)p_176407_, (T3)p_176403_, (T4)p_176398_, p_176392_, p_176436_.apply((T1)p_176412_, (T2)p_176407_, (T3)p_176403_, (T4)p_176398_, p_176392_));
                     });
                  });
               });
            });
         });
         return this;
      }
   }

   @FunctionalInterface
   public interface PentaFunction<P1, P2, P3, P4, P5, R> {
      R apply(P1 p_176441_, P2 p_176442_, P3 p_176443_, P4 p_176444_, P5 p_176445_);
   }

   @FunctionalInterface
   public interface QuadFunction<P1, P2, P3, P4, R> {
      R apply(P1 p_176447_, P2 p_176448_, P3 p_176449_, P4 p_176450_);
   }

   @FunctionalInterface
   public interface TriFunction<P1, P2, P3, R> {
      R apply(P1 p_125476_, P2 p_125477_, P3 p_125478_);
   }
}