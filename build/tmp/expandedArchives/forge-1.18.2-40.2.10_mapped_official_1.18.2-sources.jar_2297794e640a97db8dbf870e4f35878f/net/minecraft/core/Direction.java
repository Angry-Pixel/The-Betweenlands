package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   public static final Codec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values, Direction::byName);
   public static final Codec<Direction> VERTICAL_CODEC = CODEC.flatXmap(Direction::verifyVertical, Direction::verifyVertical);
   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vec3i normal;
   private static final Direction[] VALUES = values();
   private static final Map<String, Direction> BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName, (p_122426_) -> {
      return p_122426_;
   }));
   private static final Direction[] BY_3D_DATA = Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_122423_) -> {
      return p_122423_.data3d;
   })).toArray((p_122418_) -> {
      return new Direction[p_122418_];
   });
   private static final Direction[] BY_2D_DATA = Arrays.stream(VALUES).filter((p_122420_) -> {
      return p_122420_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_122415_) -> {
      return p_122415_.data2d;
   })).toArray((p_122413_) -> {
      return new Direction[p_122413_];
   });
   private static final Long2ObjectMap<Direction> BY_NORMAL = Arrays.stream(VALUES).collect(Collectors.toMap((p_122410_) -> {
      return (new BlockPos(p_122410_.getNormal())).asLong();
   }, (p_122394_) -> {
      return p_122394_;
   }, (p_122396_, p_122397_) -> {
      throw new IllegalArgumentException("Duplicate keys");
   }, Long2ObjectOpenHashMap::new));

   private Direction(int p_122356_, int p_122357_, int p_122358_, String p_122359_, Direction.AxisDirection p_122360_, Direction.Axis p_122361_, Vec3i p_122362_) {
      this.data3d = p_122356_;
      this.data2d = p_122358_;
      this.oppositeIndex = p_122357_;
      this.name = p_122359_;
      this.axis = p_122361_;
      this.axisDirection = p_122360_;
      this.normal = p_122362_;
   }

   public static Direction[] orderedByNearest(Entity p_122383_) {
      float f = p_122383_.getViewXRot(1.0F) * ((float)Math.PI / 180F);
      float f1 = -p_122383_.getViewYRot(1.0F) * ((float)Math.PI / 180F);
      float f2 = Mth.sin(f);
      float f3 = Mth.cos(f);
      float f4 = Mth.sin(f1);
      float f5 = Mth.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      Direction direction = flag ? EAST : WEST;
      Direction direction1 = flag1 ? UP : DOWN;
      Direction direction2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return makeDirectionArray(direction1, direction, direction2);
         } else {
            return f10 > f7 ? makeDirectionArray(direction, direction2, direction1) : makeDirectionArray(direction, direction1, direction2);
         }
      } else if (f7 > f10) {
         return makeDirectionArray(direction1, direction2, direction);
      } else {
         return f9 > f7 ? makeDirectionArray(direction2, direction, direction1) : makeDirectionArray(direction2, direction1, direction);
      }
   }

   private static Direction[] makeDirectionArray(Direction p_122399_, Direction p_122400_, Direction p_122401_) {
      return new Direction[]{p_122399_, p_122400_, p_122401_, p_122401_.getOpposite(), p_122400_.getOpposite(), p_122399_.getOpposite()};
   }

   public static Direction rotate(Matrix4f p_122385_, Direction p_122386_) {
      Vec3i vec3i = p_122386_.getNormal();
      Vector4f vector4f = new Vector4f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), 0.0F);
      vector4f.transform(p_122385_);
      return getNearest(vector4f.x(), vector4f.y(), vector4f.z());
   }

   public Quaternion getRotation() {
      Quaternion quaternion = Vector3f.XP.rotationDegrees(90.0F);
      Quaternion quaternion1;
      switch(this) {
      case DOWN:
         quaternion1 = Vector3f.XP.rotationDegrees(180.0F);
         break;
      case UP:
         quaternion1 = Quaternion.ONE.copy();
         break;
      case NORTH:
         quaternion.mul(Vector3f.ZP.rotationDegrees(180.0F));
         quaternion1 = quaternion;
         break;
      case SOUTH:
         quaternion1 = quaternion;
         break;
      case WEST:
         quaternion.mul(Vector3f.ZP.rotationDegrees(90.0F));
         quaternion1 = quaternion;
         break;
      case EAST:
         quaternion.mul(Vector3f.ZP.rotationDegrees(-90.0F));
         quaternion1 = quaternion;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return quaternion1;
   }

   public int get3DDataValue() {
      return this.data3d;
   }

   public int get2DDataValue() {
      return this.data2d;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public static Direction getFacingAxis(Entity p_175358_, Direction.Axis p_175359_) {
      Direction direction;
      switch(p_175359_) {
      case X:
         direction = EAST.isFacingAngle(p_175358_.getViewYRot(1.0F)) ? EAST : WEST;
         break;
      case Z:
         direction = SOUTH.isFacingAngle(p_175358_.getViewYRot(1.0F)) ? SOUTH : NORTH;
         break;
      case Y:
         direction = p_175358_.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return direction;
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise(Direction.Axis p_175363_) {
      Direction direction;
      switch(p_175363_) {
      case X:
         direction = this != WEST && this != EAST ? this.getClockWiseX() : this;
         break;
      case Z:
         direction = this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
         break;
      case Y:
         direction = this != UP && this != DOWN ? this.getClockWise() : this;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return direction;
   }

   public Direction getCounterClockWise(Direction.Axis p_175365_) {
      Direction direction;
      switch(p_175365_) {
      case X:
         direction = this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
         break;
      case Z:
         direction = this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
         break;
      case Y:
         direction = this != UP && this != DOWN ? this.getCounterClockWise() : this;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return direction;
   }

   public Direction getClockWise() {
      Direction direction;
      switch(this) {
      case NORTH:
         direction = EAST;
         break;
      case SOUTH:
         direction = WEST;
         break;
      case WEST:
         direction = NORTH;
         break;
      case EAST:
         direction = SOUTH;
         break;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }

      return direction;
   }

   private Direction getClockWiseX() {
      Direction direction;
      switch(this) {
      case DOWN:
         direction = SOUTH;
         break;
      case UP:
         direction = NORTH;
         break;
      case NORTH:
         direction = DOWN;
         break;
      case SOUTH:
         direction = UP;
         break;
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      }

      return direction;
   }

   private Direction getCounterClockWiseX() {
      Direction direction;
      switch(this) {
      case DOWN:
         direction = NORTH;
         break;
      case UP:
         direction = SOUTH;
         break;
      case NORTH:
         direction = UP;
         break;
      case SOUTH:
         direction = DOWN;
         break;
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      }

      return direction;
   }

   private Direction getClockWiseZ() {
      Direction direction;
      switch(this) {
      case DOWN:
         direction = WEST;
         break;
      case UP:
         direction = EAST;
         break;
      case NORTH:
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         direction = UP;
         break;
      case EAST:
         direction = DOWN;
      }

      return direction;
   }

   private Direction getCounterClockWiseZ() {
      Direction direction;
      switch(this) {
      case DOWN:
         direction = EAST;
         break;
      case UP:
         direction = WEST;
         break;
      case NORTH:
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         direction = DOWN;
         break;
      case EAST:
         direction = UP;
      }

      return direction;
   }

   public Direction getCounterClockWise() {
      Direction direction;
      switch(this) {
      case NORTH:
         direction = WEST;
         break;
      case SOUTH:
         direction = EAST;
         break;
      case WEST:
         direction = SOUTH;
         break;
      case EAST:
         direction = NORTH;
         break;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }

      return direction;
   }

   public int getStepX() {
      return this.normal.getX();
   }

   public int getStepY() {
      return this.normal.getY();
   }

   public int getStepZ() {
      return this.normal.getZ();
   }

   public Vector3f step() {
      return new Vector3f((float)this.getStepX(), (float)this.getStepY(), (float)this.getStepZ());
   }

   public String getName() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String p_122403_) {
      return p_122403_ == null ? null : BY_NAME.get(p_122403_.toLowerCase(Locale.ROOT));
   }

   public static Direction from3DDataValue(int p_122377_) {
      return BY_3D_DATA[Mth.abs(p_122377_ % BY_3D_DATA.length)];
   }

   public static Direction from2DDataValue(int p_122408_) {
      return BY_2D_DATA[Mth.abs(p_122408_ % BY_2D_DATA.length)];
   }

   @Nullable
   public static Direction fromNormal(BlockPos p_175361_) {
      return BY_NORMAL.get(p_175361_.asLong());
   }

   @Nullable
   public static Direction fromNormal(int p_122379_, int p_122380_, int p_122381_) {
      return BY_NORMAL.get(BlockPos.asLong(p_122379_, p_122380_, p_122381_));
   }

   public static Direction fromYRot(double p_122365_) {
      return from2DDataValue(Mth.floor(p_122365_ / 90.0D + 0.5D) & 3);
   }

   public static Direction fromAxisAndDirection(Direction.Axis p_122388_, Direction.AxisDirection p_122389_) {
      Direction direction;
      switch(p_122388_) {
      case X:
         direction = p_122389_ == Direction.AxisDirection.POSITIVE ? EAST : WEST;
         break;
      case Z:
         direction = p_122389_ == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
         break;
      case Y:
         direction = p_122389_ == Direction.AxisDirection.POSITIVE ? UP : DOWN;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return direction;
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandom(Random p_122405_) {
      return Util.getRandom(VALUES, p_122405_);
   }

   public static Direction getNearest(double p_122367_, double p_122368_, double p_122369_) {
      return getNearest((float)p_122367_, (float)p_122368_, (float)p_122369_);
   }

   public static Direction getNearest(float p_122373_, float p_122374_, float p_122375_) {
      Direction direction = NORTH;
      float f = Float.MIN_VALUE;

      for(Direction direction1 : VALUES) {
         float f1 = p_122373_ * (float)direction1.normal.getX() + p_122374_ * (float)direction1.normal.getY() + p_122375_ * (float)direction1.normal.getZ();
         if (f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   private static DataResult<Direction> verifyVertical(Direction p_194529_) {
      return p_194529_.getAxis().isVertical() ? DataResult.success(p_194529_) : DataResult.error("Expected a vertical direction");
   }

   public static Direction get(Direction.AxisDirection p_122391_, Direction.Axis p_122392_) {
      for(Direction direction : VALUES) {
         if (direction.getAxisDirection() == p_122391_ && direction.getAxis() == p_122392_) {
            return direction;
         }
      }

      throw new IllegalArgumentException("No such direction: " + p_122391_ + " " + p_122392_);
   }

   public Vec3i getNormal() {
      return this.normal;
   }

   public boolean isFacingAngle(float p_122371_) {
      float f = p_122371_ * ((float)Math.PI / 180F);
      float f1 = -Mth.sin(f);
      float f2 = Mth.cos(f);
      return (float)this.normal.getX() * f1 + (float)this.normal.getZ() * f2 > 0.0F;
   }

   public static enum Axis implements StringRepresentable, Predicate<Direction> {
      X("x") {
         public int choose(int p_122496_, int p_122497_, int p_122498_) {
            return p_122496_;
         }

         public double choose(double p_122492_, double p_122493_, double p_122494_) {
            return p_122492_;
         }
      },
      Y("y") {
         public int choose(int p_122510_, int p_122511_, int p_122512_) {
            return p_122511_;
         }

         public double choose(double p_122506_, double p_122507_, double p_122508_) {
            return p_122507_;
         }
      },
      Z("z") {
         public int choose(int p_122524_, int p_122525_, int p_122526_) {
            return p_122526_;
         }

         public double choose(double p_122520_, double p_122521_, double p_122522_) {
            return p_122522_;
         }
      };

      public static final Direction.Axis[] VALUES = values();
      public static final Codec<Direction.Axis> CODEC = StringRepresentable.fromEnum(Direction.Axis::values, Direction.Axis::byName);
      private static final Map<String, Direction.Axis> BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Direction.Axis::getName, (p_122470_) -> {
         return p_122470_;
      }));
      private final String name;

      Axis(String p_122456_) {
         this.name = p_122456_;
      }

      @Nullable
      public static Direction.Axis byName(String p_122474_) {
         return BY_NAME.get(p_122474_.toLowerCase(Locale.ROOT));
      }

      public String getName() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public static Direction.Axis getRandom(Random p_122476_) {
         return Util.getRandom(VALUES, p_122476_);
      }

      public boolean test(@Nullable Direction p_122472_) {
         return p_122472_ != null && p_122472_.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         Direction.Plane direction$plane;
         switch(this) {
         case X:
         case Z:
            direction$plane = Direction.Plane.HORIZONTAL;
            break;
         case Y:
            direction$plane = Direction.Plane.VERTICAL;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return direction$plane;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract int choose(int p_122466_, int p_122467_, int p_122468_);

      public abstract double choose(double p_122463_, double p_122464_, double p_122465_);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int step;
      private final String name;

      private AxisDirection(int p_122538_, String p_122539_) {
         this.step = p_122538_;
         this.name = p_122539_;
      }

      public int getStep() {
         return this.step;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public Direction.AxisDirection opposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }
   }

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] faces;
      private final Direction.Axis[] axis;

      private Plane(Direction[] p_122555_, Direction.Axis[] p_122556_) {
         this.faces = p_122555_;
         this.axis = p_122556_;
      }

      public Direction getRandomDirection(Random p_122561_) {
         return Util.getRandom(this.faces, p_122561_);
      }

      public Direction.Axis getRandomAxis(Random p_122563_) {
         return Util.getRandom(this.axis, p_122563_);
      }

      public boolean test(@Nullable Direction p_122559_) {
         return p_122559_ != null && p_122559_.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      public Stream<Direction> stream() {
         return Arrays.stream(this.faces);
      }
   }
}