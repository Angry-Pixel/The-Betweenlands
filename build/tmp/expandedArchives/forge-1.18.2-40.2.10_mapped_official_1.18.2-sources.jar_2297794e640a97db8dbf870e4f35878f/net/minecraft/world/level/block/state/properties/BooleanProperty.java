package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class BooleanProperty extends Property<Boolean> {
   private final ImmutableSet<Boolean> values = ImmutableSet.of(true, false);

   protected BooleanProperty(String p_61459_) {
      super(p_61459_, Boolean.class);
   }

   public Collection<Boolean> getPossibleValues() {
      return this.values;
   }

   public static BooleanProperty create(String p_61466_) {
      return new BooleanProperty(p_61466_);
   }

   public Optional<Boolean> getValue(String p_61469_) {
      return !"true".equals(p_61469_) && !"false".equals(p_61469_) ? Optional.empty() : Optional.of(Boolean.valueOf(p_61469_));
   }

   public String getName(Boolean p_61462_) {
      return p_61462_.toString();
   }

   public boolean equals(Object p_61471_) {
      if (this == p_61471_) {
         return true;
      } else if (p_61471_ instanceof BooleanProperty && super.equals(p_61471_)) {
         BooleanProperty booleanproperty = (BooleanProperty)p_61471_;
         return this.values.equals(booleanproperty.values);
      } else {
         return false;
      }
   }

   public int generateHashCode() {
      return 31 * super.generateHashCode() + this.values.hashCode();
   }
}