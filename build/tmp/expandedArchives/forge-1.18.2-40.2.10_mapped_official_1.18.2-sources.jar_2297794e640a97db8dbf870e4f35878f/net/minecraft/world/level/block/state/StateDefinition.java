package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition<O, S extends StateHolder<O, S>> {
   static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final O owner;
   private final ImmutableSortedMap<String, Property<?>> propertiesByName;
   private final ImmutableList<S> states;

   protected StateDefinition(Function<O, S> p_61052_, O p_61053_, StateDefinition.Factory<O, S> p_61054_, Map<String, Property<?>> p_61055_) {
      this.owner = p_61053_;
      this.propertiesByName = ImmutableSortedMap.copyOf(p_61055_);
      Supplier<S> supplier = () -> {
         return p_61052_.apply(p_61053_);
      };
      MapCodec<S> mapcodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));

      for(Entry<String, Property<?>> entry : this.propertiesByName.entrySet()) {
         mapcodec = appendPropertyCodec(mapcodec, supplier, entry.getKey(), entry.getValue());
      }

      MapCodec<S> mapcodec1 = mapcodec;
      Map<Map<Property<?>, Comparable<?>>, S> map = Maps.newLinkedHashMap();
      List<S> list = Lists.newArrayList();
      Stream<List<Pair<Property<?>, Comparable<?>>>> stream = Stream.of(Collections.emptyList());

      for(Property<?> property : this.propertiesByName.values()) {
         stream = stream.flatMap((p_61072_) -> {
            return property.getPossibleValues().stream().map((p_155961_) -> {
               List<Pair<Property<?>, Comparable<?>>> list1 = Lists.newArrayList(p_61072_);
               list1.add(Pair.of(property, p_155961_));
               return list1;
            });
         });
      }

      stream.forEach((p_61063_) -> {
         ImmutableMap<Property<?>, Comparable<?>> immutablemap = p_61063_.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
         S s1 = p_61054_.create(p_61053_, immutablemap, mapcodec1);
         map.put(immutablemap, s1);
         list.add(s1);
      });

      for(S s : list) {
         s.populateNeighbours(map);
      }

      this.states = ImmutableList.copyOf(list);
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> appendPropertyCodec(MapCodec<S> p_61077_, Supplier<S> p_61078_, String p_61079_, Property<T> p_61080_) {
      return Codec.mapPair(p_61077_, p_61080_.valueCodec().fieldOf(p_61079_).orElseGet((p_187541_) -> {
      }, () -> {
         return p_61080_.value(p_61078_.get());
      })).xmap((p_187536_) -> {
         return p_187536_.getFirst().setValue(p_61080_, p_187536_.getSecond().value());
      }, (p_187533_) -> {
         return Pair.of(p_187533_, p_61080_.value(p_187533_));
      });
   }

   public ImmutableList<S> getPossibleStates() {
      return this.states;
   }

   public S any() {
      return this.states.get(0);
   }

   public O getOwner() {
      return this.owner;
   }

   public Collection<Property<?>> getProperties() {
      return this.propertiesByName.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
   }

   @Nullable
   public Property<?> getProperty(String p_61082_) {
      return this.propertiesByName.get(p_61082_);
   }

   public static class Builder<O, S extends StateHolder<O, S>> {
      private final O owner;
      private final Map<String, Property<?>> properties = Maps.newHashMap();

      public Builder(O p_61098_) {
         this.owner = p_61098_;
      }

      public StateDefinition.Builder<O, S> add(Property<?>... p_61105_) {
         for(Property<?> property : p_61105_) {
            this.validateProperty(property);
            this.properties.put(property.getName(), property);
         }

         return this;
      }

      private <T extends Comparable<T>> void validateProperty(Property<T> p_61100_) {
         String s = p_61100_.getName();
         if (!StateDefinition.NAME_PATTERN.matcher(s).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + s);
         } else {
            Collection<T> collection = p_61100_.getPossibleValues();
            if (collection.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + s + " with <= 1 possible values");
            } else {
               for(T t : collection) {
                  String s1 = p_61100_.getName(t);
                  if (!StateDefinition.NAME_PATTERN.matcher(s1).matches()) {
                     throw new IllegalArgumentException(this.owner + " has property: " + s + " with invalidly named value: " + s1);
                  }
               }

               if (this.properties.containsKey(s)) {
                  throw new IllegalArgumentException(this.owner + " has duplicate property: " + s);
               }
            }
         }
      }

      public StateDefinition<O, S> create(Function<O, S> p_61102_, StateDefinition.Factory<O, S> p_61103_) {
         return new StateDefinition<>(p_61102_, this.owner, p_61103_, this.properties);
      }
   }

   public interface Factory<O, S> {
      S create(O p_61107_, ImmutableMap<Property<?>, Comparable<?>> p_61108_, MapCodec<S> p_61109_);
   }
}