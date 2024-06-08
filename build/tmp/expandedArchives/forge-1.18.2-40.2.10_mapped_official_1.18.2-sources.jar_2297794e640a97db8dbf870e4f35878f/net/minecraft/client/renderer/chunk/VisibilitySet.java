package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisibilitySet {
   private static final int FACINGS = Direction.values().length;
   private final BitSet data = new BitSet(FACINGS * FACINGS);

   public void add(Set<Direction> p_112991_) {
      for(Direction direction : p_112991_) {
         for(Direction direction1 : p_112991_) {
            this.set(direction, direction1, true);
         }
      }

   }

   public void set(Direction p_112987_, Direction p_112988_, boolean p_112989_) {
      this.data.set(p_112987_.ordinal() + p_112988_.ordinal() * FACINGS, p_112989_);
      this.data.set(p_112988_.ordinal() + p_112987_.ordinal() * FACINGS, p_112989_);
   }

   public void setAll(boolean p_112993_) {
      this.data.set(0, this.data.size(), p_112993_);
   }

   public boolean visibilityBetween(Direction p_112984_, Direction p_112985_) {
      return this.data.get(p_112984_.ordinal() + p_112985_.ordinal() * FACINGS);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(' ');

      for(Direction direction : Direction.values()) {
         stringbuilder.append(' ').append(direction.toString().toUpperCase().charAt(0));
      }

      stringbuilder.append('\n');

      for(Direction direction2 : Direction.values()) {
         stringbuilder.append(direction2.toString().toUpperCase().charAt(0));

         for(Direction direction1 : Direction.values()) {
            if (direction2 == direction1) {
               stringbuilder.append("  ");
            } else {
               boolean flag = this.visibilityBetween(direction2, direction1);
               stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
            }
         }

         stringbuilder.append('\n');
      }

      return stringbuilder.toString();
   }
}