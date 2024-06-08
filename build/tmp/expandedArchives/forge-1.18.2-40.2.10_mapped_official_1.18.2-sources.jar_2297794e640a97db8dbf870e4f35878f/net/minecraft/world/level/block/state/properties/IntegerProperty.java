package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class IntegerProperty extends Property<Integer> {
   private final ImmutableSet<Integer> values;

   protected IntegerProperty(String p_61623_, int p_61624_, int p_61625_) {
      super(p_61623_, Integer.class);
      if (p_61624_ < 0) {
         throw new IllegalArgumentException("Min value of " + p_61623_ + " must be 0 or greater");
      } else if (p_61625_ <= p_61624_) {
         throw new IllegalArgumentException("Max value of " + p_61623_ + " must be greater than min (" + p_61624_ + ")");
      } else {
         Set<Integer> set = Sets.newHashSet();

         for(int i = p_61624_; i <= p_61625_; ++i) {
            set.add(i);
         }

         this.values = ImmutableSet.copyOf(set);
      }
   }

   public Collection<Integer> getPossibleValues() {
      return this.values;
   }

   public boolean equals(Object p_61639_) {
      if (this == p_61639_) {
         return true;
      } else if (p_61639_ instanceof IntegerProperty && super.equals(p_61639_)) {
         IntegerProperty integerproperty = (IntegerProperty)p_61639_;
         return this.values.equals(integerproperty.values);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }

   public static IntegerProperty create(String p_61632_, int p_61633_, int p_61634_) {
      return new IntegerProperty(p_61632_, p_61633_, p_61634_);
   }

   public Optional<Integer> getValue(String p_61637_) {
      try {
         Integer integer = Integer.valueOf(p_61637_);
         return this.values.contains(integer) ? Optional.of(integer) : Optional.empty();
      } catch (NumberFormatException numberformatexception) {
         return Optional.empty();
      }
   }

   public String getName(Integer p_61630_) {
      return p_61630_.toString();
   }
}