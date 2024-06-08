package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;

public abstract class Property<T extends Comparable<T>> {
   private final Class<T> clazz;
   private final String name;
   @Nullable
   private Integer hashCode;
   private final Codec<T> codec = Codec.STRING.comapFlatMap((p_61698_) -> {
      return this.getValue(p_61698_).map(DataResult::success).orElseGet(() -> {
         return DataResult.error("Unable to read property: " + this + " with value: " + p_61698_);
      });
   }, this::getName);
   private final Codec<Property.Value<T>> valueCodec = this.codec.xmap(this::value, Property.Value::value);

   protected Property(String p_61692_, Class<T> p_61693_) {
      this.clazz = p_61693_;
      this.name = p_61692_;
   }

   public Property.Value<T> value(T p_61700_) {
      return new Property.Value<>(this, p_61700_);
   }

   public Property.Value<T> value(StateHolder<?, ?> p_61695_) {
      return new Property.Value<>(this, p_61695_.getValue(this));
   }

   public Stream<Property.Value<T>> getAllValues() {
      return this.getPossibleValues().stream().map(this::value);
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public Codec<Property.Value<T>> valueCodec() {
      return this.valueCodec;
   }

   public String getName() {
      return this.name;
   }

   public Class<T> getValueClass() {
      return this.clazz;
   }

   public abstract Collection<T> getPossibleValues();

   public abstract String getName(T p_61696_);

   public abstract Optional<T> getValue(String p_61701_);

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(Object p_61707_) {
      if (this == p_61707_) {
         return true;
      } else if (!(p_61707_ instanceof Property)) {
         return false;
      } else {
         Property<?> property = (Property)p_61707_;
         return this.clazz.equals(property.clazz) && this.name.equals(property.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.generateHashCode();
      }

      return this.hashCode;
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }

   public <U, S extends StateHolder<?, S>> DataResult<S> parseValue(DynamicOps<U> p_156032_, S p_156033_, U p_156034_) {
      DataResult<T> dataresult = this.codec.parse(p_156032_, p_156034_);
      return dataresult.map((p_156030_) -> {
         return p_156033_.setValue(this, p_156030_);
      }).setPartial(p_156033_);
   }

   public static record Value<T extends Comparable<T>>(Property<T> property, T value) {
      public Value {
         if (!property.getPossibleValues().contains(value)) {
            throw new IllegalArgumentException("Value " + value + " does not belong to property " + property);
         }
      }

      public String toString() {
         return this.property.getName() + "=" + this.property.getName(this.value);
      }
   }
}