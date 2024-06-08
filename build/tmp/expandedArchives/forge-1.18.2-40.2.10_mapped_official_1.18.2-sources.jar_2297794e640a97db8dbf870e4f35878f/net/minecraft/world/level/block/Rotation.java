package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import com.mojang.math.OctahedralGroup;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.Direction;

public enum Rotation {
   NONE(OctahedralGroup.IDENTITY),
   CLOCKWISE_90(OctahedralGroup.ROT_90_Y_NEG),
   CLOCKWISE_180(OctahedralGroup.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90(OctahedralGroup.ROT_90_Y_POS);

   private final OctahedralGroup rotation;

   private Rotation(OctahedralGroup p_55947_) {
      this.rotation = p_55947_;
   }

   public Rotation getRotated(Rotation p_55953_) {
      switch(p_55953_) {
      case CLOCKWISE_180:
         switch(this) {
         case NONE:
            return CLOCKWISE_180;
         case CLOCKWISE_90:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_180:
            return NONE;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_90;
         }
      case COUNTERCLOCKWISE_90:
         switch(this) {
         case NONE:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_90:
            return NONE;
         case CLOCKWISE_180:
            return CLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_180;
         }
      case CLOCKWISE_90:
         switch(this) {
         case NONE:
            return CLOCKWISE_90;
         case CLOCKWISE_90:
            return CLOCKWISE_180;
         case CLOCKWISE_180:
            return COUNTERCLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return NONE;
         }
      default:
         return this;
      }
   }

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Direction rotate(Direction p_55955_) {
      if (p_55955_.getAxis() == Direction.Axis.Y) {
         return p_55955_;
      } else {
         switch(this) {
         case CLOCKWISE_90:
            return p_55955_.getClockWise();
         case CLOCKWISE_180:
            return p_55955_.getOpposite();
         case COUNTERCLOCKWISE_90:
            return p_55955_.getCounterClockWise();
         default:
            return p_55955_;
         }
      }
   }

   public int rotate(int p_55950_, int p_55951_) {
      switch(this) {
      case CLOCKWISE_90:
         return (p_55950_ + p_55951_ / 4) % p_55951_;
      case CLOCKWISE_180:
         return (p_55950_ + p_55951_ / 2) % p_55951_;
      case COUNTERCLOCKWISE_90:
         return (p_55950_ + p_55951_ * 3 / 4) % p_55951_;
      default:
         return p_55950_;
      }
   }

   public static Rotation getRandom(Random p_55957_) {
      return Util.getRandom(values(), p_55957_);
   }

   public static List<Rotation> getShuffled(Random p_55959_) {
      List<Rotation> list = Lists.newArrayList(values());
      Collections.shuffle(list, p_55959_);
      return list;
   }
}