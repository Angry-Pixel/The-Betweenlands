package net.minecraft.world.level.levelgen;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Column {
   public static Column.Range around(int p_158165_, int p_158166_) {
      return new Column.Range(p_158165_ - 1, p_158166_ + 1);
   }

   public static Column.Range inside(int p_158189_, int p_158190_) {
      return new Column.Range(p_158189_, p_158190_);
   }

   public static Column below(int p_158163_) {
      return new Column.Ray(p_158163_, false);
   }

   public static Column fromHighest(int p_158187_) {
      return new Column.Ray(p_158187_ + 1, false);
   }

   public static Column above(int p_158194_) {
      return new Column.Ray(p_158194_, true);
   }

   public static Column fromLowest(int p_158196_) {
      return new Column.Ray(p_158196_ - 1, true);
   }

   public static Column line() {
      return Column.Line.INSTANCE;
   }

   public static Column create(OptionalInt p_158184_, OptionalInt p_158185_) {
      if (p_158184_.isPresent() && p_158185_.isPresent()) {
         return inside(p_158184_.getAsInt(), p_158185_.getAsInt());
      } else if (p_158184_.isPresent()) {
         return above(p_158184_.getAsInt());
      } else {
         return p_158185_.isPresent() ? below(p_158185_.getAsInt()) : line();
      }
   }

   public abstract OptionalInt getCeiling();

   public abstract OptionalInt getFloor();

   public abstract OptionalInt getHeight();

   public Column withFloor(OptionalInt p_158182_) {
      return create(p_158182_, this.getCeiling());
   }

   public Column withCeiling(OptionalInt p_158192_) {
      return create(this.getFloor(), p_158192_);
   }

   public static Optional<Column> scan(LevelSimulatedReader p_158176_, BlockPos p_158177_, int p_158178_, Predicate<BlockState> p_158179_, Predicate<BlockState> p_158180_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_158177_.mutable();
      if (!p_158176_.isStateAtPosition(p_158177_, p_158179_)) {
         return Optional.empty();
      } else {
         int i = p_158177_.getY();
         OptionalInt optionalint = scanDirection(p_158176_, p_158178_, p_158179_, p_158180_, blockpos$mutableblockpos, i, Direction.UP);
         OptionalInt optionalint1 = scanDirection(p_158176_, p_158178_, p_158179_, p_158180_, blockpos$mutableblockpos, i, Direction.DOWN);
         return Optional.of(create(optionalint1, optionalint));
      }
   }

   private static OptionalInt scanDirection(LevelSimulatedReader p_158168_, int p_158169_, Predicate<BlockState> p_158170_, Predicate<BlockState> p_158171_, BlockPos.MutableBlockPos p_158172_, int p_158173_, Direction p_158174_) {
      p_158172_.setY(p_158173_);

      for(int i = 1; i < p_158169_ && p_158168_.isStateAtPosition(p_158172_, p_158170_); ++i) {
         p_158172_.move(p_158174_);
      }

      return p_158168_.isStateAtPosition(p_158172_, p_158171_) ? OptionalInt.of(p_158172_.getY()) : OptionalInt.empty();
   }

   public static final class Line extends Column {
      static final Column.Line INSTANCE = new Column.Line();

      private Line() {
      }

      public OptionalInt getCeiling() {
         return OptionalInt.empty();
      }

      public OptionalInt getFloor() {
         return OptionalInt.empty();
      }

      public OptionalInt getHeight() {
         return OptionalInt.empty();
      }

      public String toString() {
         return "C(-)";
      }
   }

   public static final class Range extends Column {
      private final int floor;
      private final int ceiling;

      protected Range(int p_158207_, int p_158208_) {
         this.floor = p_158207_;
         this.ceiling = p_158208_;
         if (this.height() < 0) {
            throw new IllegalArgumentException("Column of negative height: " + this);
         }
      }

      public OptionalInt getCeiling() {
         return OptionalInt.of(this.ceiling);
      }

      public OptionalInt getFloor() {
         return OptionalInt.of(this.floor);
      }

      public OptionalInt getHeight() {
         return OptionalInt.of(this.height());
      }

      public int ceiling() {
         return this.ceiling;
      }

      public int floor() {
         return this.floor;
      }

      public int height() {
         return this.ceiling - this.floor - 1;
      }

      public String toString() {
         return "C(" + this.ceiling + "-" + this.floor + ")";
      }
   }

   public static final class Ray extends Column {
      private final int edge;
      private final boolean pointingUp;

      public Ray(int p_158219_, boolean p_158220_) {
         this.edge = p_158219_;
         this.pointingUp = p_158220_;
      }

      public OptionalInt getCeiling() {
         return this.pointingUp ? OptionalInt.empty() : OptionalInt.of(this.edge);
      }

      public OptionalInt getFloor() {
         return this.pointingUp ? OptionalInt.of(this.edge) : OptionalInt.empty();
      }

      public OptionalInt getHeight() {
         return OptionalInt.empty();
      }

      public String toString() {
         return this.pointingUp ? "C(" + this.edge + "-)" : "C(-" + this.edge + ")";
      }
   }
}