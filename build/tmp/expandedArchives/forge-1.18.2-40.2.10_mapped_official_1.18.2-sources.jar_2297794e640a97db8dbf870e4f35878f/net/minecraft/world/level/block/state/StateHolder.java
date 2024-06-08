package net.minecraft.world.level.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class StateHolder<O, S> {
   public static final String NAME_TAG = "Name";
   public static final String PROPERTIES_TAG = "Properties";
   private static final Function<Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Entry<Property<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<Property<?>, Comparable<?>> p_61155_) {
         if (p_61155_ == null) {
            return "<NULL>";
         } else {
            Property<?> property = p_61155_.getKey();
            return property.getName() + "=" + this.getName(property, p_61155_.getValue());
         }
      }

      private <T extends Comparable<T>> String getName(Property<T> p_61152_, Comparable<?> p_61153_) {
         return p_61152_.getName((T)p_61153_);
      }
   };
   protected final O owner;
   private final ImmutableMap<Property<?>, Comparable<?>> values;
   private Table<Property<?>, Comparable<?>, S> neighbours;
   protected final MapCodec<S> propertiesCodec;

   protected StateHolder(O p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<S> p_61119_) {
      this.owner = p_61117_;
      this.values = p_61118_;
      this.propertiesCodec = p_61119_;
   }

   public <T extends Comparable<T>> S cycle(Property<T> p_61123_) {
      return this.setValue(p_61123_, findNextInCollection(p_61123_.getPossibleValues(), this.getValue(p_61123_)));
   }

   protected static <T> T findNextInCollection(Collection<T> p_61131_, T p_61132_) {
      Iterator<T> iterator = p_61131_.iterator();

      while(iterator.hasNext()) {
         if (iterator.next().equals(p_61132_)) {
            if (iterator.hasNext()) {
               return iterator.next();
            }

            return p_61131_.iterator().next();
         }
      }

      return iterator.next();
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(this.owner);
      if (!this.getValues().isEmpty()) {
         stringbuilder.append('[');
         stringbuilder.append(this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   public Collection<Property<?>> getProperties() {
      return Collections.unmodifiableCollection(this.values.keySet());
   }

   public <T extends Comparable<T>> boolean hasProperty(Property<T> p_61139_) {
      return this.values.containsKey(p_61139_);
   }

   public <T extends Comparable<T>> T getValue(Property<T> p_61144_) {
      Comparable<?> comparable = this.values.get(p_61144_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot get property " + p_61144_ + " as it does not exist in " + this.owner);
      } else {
         return p_61144_.getValueClass().cast(comparable);
      }
   }

   public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> p_61146_) {
      Comparable<?> comparable = this.values.get(p_61146_);
      return comparable == null ? Optional.empty() : Optional.of(p_61146_.getValueClass().cast(comparable));
   }

   public <T extends Comparable<T>, V extends T> S setValue(Property<T> p_61125_, V p_61126_) {
      Comparable<?> comparable = this.values.get(p_61125_);
      if (comparable == null) {
         throw new IllegalArgumentException("Cannot set property " + p_61125_ + " as it does not exist in " + this.owner);
      } else if (comparable == p_61126_) {
         return (S)this;
      } else {
         S s = this.neighbours.get(p_61125_, p_61126_);
         if (s == null) {
            throw new IllegalArgumentException("Cannot set property " + p_61125_ + " to " + p_61126_ + " on " + this.owner + ", it is not an allowed value");
         } else {
            return s;
         }
      }
   }

   public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> p_61134_) {
      if (this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         Table<Property<?>, Comparable<?>, S> table = HashBasedTable.create();

         for(Entry<Property<?>, Comparable<?>> entry : this.values.entrySet()) {
            Property<?> property = entry.getKey();

            for(Comparable<?> comparable : property.getPossibleValues()) {
               if (comparable != entry.getValue()) {
                  table.put(property, comparable, p_61134_.get(this.makeNeighbourValues(property, comparable)));
               }
            }
         }

         this.neighbours = (Table<Property<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
      }
   }

   private Map<Property<?>, Comparable<?>> makeNeighbourValues(Property<?> p_61141_, Comparable<?> p_61142_) {
      Map<Property<?>, Comparable<?>> map = Maps.newHashMap(this.values);
      map.put(p_61141_, p_61142_);
      return map;
   }

   public ImmutableMap<Property<?>, Comparable<?>> getValues() {
      return this.values;
   }

   protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> p_61128_, Function<O, S> p_61129_) {
      return p_61128_.dispatch("Name", (p_61121_) -> {
         return p_61121_.owner;
      }, (p_187547_) -> {
         S s = p_61129_.apply(p_187547_);
         return s.getValues().isEmpty() ? Codec.unit(s) : s.propertiesCodec.codec().optionalFieldOf("Properties").xmap((p_187544_) -> {
            return p_187544_.orElse(s);
         }, Optional::of).codec();
      });
   }
}