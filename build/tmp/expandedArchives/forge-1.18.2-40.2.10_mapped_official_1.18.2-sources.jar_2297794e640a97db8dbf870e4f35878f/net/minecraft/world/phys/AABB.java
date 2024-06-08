package net.minecraft.world.phys;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AABB {
   private static final double EPSILON = 1.0E-7D;
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public AABB(double p_82295_, double p_82296_, double p_82297_, double p_82298_, double p_82299_, double p_82300_) {
      this.minX = Math.min(p_82295_, p_82298_);
      this.minY = Math.min(p_82296_, p_82299_);
      this.minZ = Math.min(p_82297_, p_82300_);
      this.maxX = Math.max(p_82295_, p_82298_);
      this.maxY = Math.max(p_82296_, p_82299_);
      this.maxZ = Math.max(p_82297_, p_82300_);
   }

   public AABB(BlockPos p_82305_) {
      this((double)p_82305_.getX(), (double)p_82305_.getY(), (double)p_82305_.getZ(), (double)(p_82305_.getX() + 1), (double)(p_82305_.getY() + 1), (double)(p_82305_.getZ() + 1));
   }

   public AABB(BlockPos p_82307_, BlockPos p_82308_) {
      this((double)p_82307_.getX(), (double)p_82307_.getY(), (double)p_82307_.getZ(), (double)p_82308_.getX(), (double)p_82308_.getY(), (double)p_82308_.getZ());
   }

   public AABB(Vec3 p_82302_, Vec3 p_82303_) {
      this(p_82302_.x, p_82302_.y, p_82302_.z, p_82303_.x, p_82303_.y, p_82303_.z);
   }

   public static AABB of(BoundingBox p_82322_) {
      return new AABB((double)p_82322_.minX(), (double)p_82322_.minY(), (double)p_82322_.minZ(), (double)(p_82322_.maxX() + 1), (double)(p_82322_.maxY() + 1), (double)(p_82322_.maxZ() + 1));
   }

   public static AABB unitCubeFromLowerCorner(Vec3 p_82334_) {
      return new AABB(p_82334_.x, p_82334_.y, p_82334_.z, p_82334_.x + 1.0D, p_82334_.y + 1.0D, p_82334_.z + 1.0D);
   }

   public AABB setMinX(double p_165881_) {
      return new AABB(p_165881_, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public AABB setMinY(double p_165888_) {
      return new AABB(this.minX, p_165888_, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public AABB setMinZ(double p_165890_) {
      return new AABB(this.minX, this.minY, p_165890_, this.maxX, this.maxY, this.maxZ);
   }

   public AABB setMaxX(double p_165892_) {
      return new AABB(this.minX, this.minY, this.minZ, p_165892_, this.maxY, this.maxZ);
   }

   public AABB setMaxY(double p_165894_) {
      return new AABB(this.minX, this.minY, this.minZ, this.maxX, p_165894_, this.maxZ);
   }

   public AABB setMaxZ(double p_165896_) {
      return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, p_165896_);
   }

   public double min(Direction.Axis p_82341_) {
      return p_82341_.choose(this.minX, this.minY, this.minZ);
   }

   public double max(Direction.Axis p_82375_) {
      return p_82375_.choose(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object p_82398_) {
      if (this == p_82398_) {
         return true;
      } else if (!(p_82398_ instanceof AABB)) {
         return false;
      } else {
         AABB aabb = (AABB)p_82398_;
         if (Double.compare(aabb.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(aabb.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(aabb.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(aabb.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(aabb.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(aabb.maxZ, this.maxZ) == 0;
         }
      }
   }

   public int hashCode() {
      long i = Double.doubleToLongBits(this.minX);
      int j = (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.minZ);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxX);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxY);
      j = 31 * j + (int)(i ^ i >>> 32);
      i = Double.doubleToLongBits(this.maxZ);
      return 31 * j + (int)(i ^ i >>> 32);
   }

   public AABB contract(double p_82311_, double p_82312_, double p_82313_) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (p_82311_ < 0.0D) {
         d0 -= p_82311_;
      } else if (p_82311_ > 0.0D) {
         d3 -= p_82311_;
      }

      if (p_82312_ < 0.0D) {
         d1 -= p_82312_;
      } else if (p_82312_ > 0.0D) {
         d4 -= p_82312_;
      }

      if (p_82313_ < 0.0D) {
         d2 -= p_82313_;
      } else if (p_82313_ > 0.0D) {
         d5 -= p_82313_;
      }

      return new AABB(d0, d1, d2, d3, d4, d5);
   }

   public AABB expandTowards(Vec3 p_82370_) {
      return this.expandTowards(p_82370_.x, p_82370_.y, p_82370_.z);
   }

   public AABB expandTowards(double p_82364_, double p_82365_, double p_82366_) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (p_82364_ < 0.0D) {
         d0 += p_82364_;
      } else if (p_82364_ > 0.0D) {
         d3 += p_82364_;
      }

      if (p_82365_ < 0.0D) {
         d1 += p_82365_;
      } else if (p_82365_ > 0.0D) {
         d4 += p_82365_;
      }

      if (p_82366_ < 0.0D) {
         d2 += p_82366_;
      } else if (p_82366_ > 0.0D) {
         d5 += p_82366_;
      }

      return new AABB(d0, d1, d2, d3, d4, d5);
   }

   public AABB inflate(double p_82378_, double p_82379_, double p_82380_) {
      double d0 = this.minX - p_82378_;
      double d1 = this.minY - p_82379_;
      double d2 = this.minZ - p_82380_;
      double d3 = this.maxX + p_82378_;
      double d4 = this.maxY + p_82379_;
      double d5 = this.maxZ + p_82380_;
      return new AABB(d0, d1, d2, d3, d4, d5);
   }

   public AABB inflate(double p_82401_) {
      return this.inflate(p_82401_, p_82401_, p_82401_);
   }

   public AABB intersect(AABB p_82324_) {
      double d0 = Math.max(this.minX, p_82324_.minX);
      double d1 = Math.max(this.minY, p_82324_.minY);
      double d2 = Math.max(this.minZ, p_82324_.minZ);
      double d3 = Math.min(this.maxX, p_82324_.maxX);
      double d4 = Math.min(this.maxY, p_82324_.maxY);
      double d5 = Math.min(this.maxZ, p_82324_.maxZ);
      return new AABB(d0, d1, d2, d3, d4, d5);
   }

   public AABB minmax(AABB p_82368_) {
      double d0 = Math.min(this.minX, p_82368_.minX);
      double d1 = Math.min(this.minY, p_82368_.minY);
      double d2 = Math.min(this.minZ, p_82368_.minZ);
      double d3 = Math.max(this.maxX, p_82368_.maxX);
      double d4 = Math.max(this.maxY, p_82368_.maxY);
      double d5 = Math.max(this.maxZ, p_82368_.maxZ);
      return new AABB(d0, d1, d2, d3, d4, d5);
   }

   public AABB move(double p_82387_, double p_82388_, double p_82389_) {
      return new AABB(this.minX + p_82387_, this.minY + p_82388_, this.minZ + p_82389_, this.maxX + p_82387_, this.maxY + p_82388_, this.maxZ + p_82389_);
   }

   public AABB move(BlockPos p_82339_) {
      return new AABB(this.minX + (double)p_82339_.getX(), this.minY + (double)p_82339_.getY(), this.minZ + (double)p_82339_.getZ(), this.maxX + (double)p_82339_.getX(), this.maxY + (double)p_82339_.getY(), this.maxZ + (double)p_82339_.getZ());
   }

   public AABB move(Vec3 p_82384_) {
      return this.move(p_82384_.x, p_82384_.y, p_82384_.z);
   }

   public boolean intersects(AABB p_82382_) {
      return this.intersects(p_82382_.minX, p_82382_.minY, p_82382_.minZ, p_82382_.maxX, p_82382_.maxY, p_82382_.maxZ);
   }

   public boolean intersects(double p_82315_, double p_82316_, double p_82317_, double p_82318_, double p_82319_, double p_82320_) {
      return this.minX < p_82318_ && this.maxX > p_82315_ && this.minY < p_82319_ && this.maxY > p_82316_ && this.minZ < p_82320_ && this.maxZ > p_82317_;
   }

   public boolean intersects(Vec3 p_82336_, Vec3 p_82337_) {
      return this.intersects(Math.min(p_82336_.x, p_82337_.x), Math.min(p_82336_.y, p_82337_.y), Math.min(p_82336_.z, p_82337_.z), Math.max(p_82336_.x, p_82337_.x), Math.max(p_82336_.y, p_82337_.y), Math.max(p_82336_.z, p_82337_.z));
   }

   public boolean contains(Vec3 p_82391_) {
      return this.contains(p_82391_.x, p_82391_.y, p_82391_.z);
   }

   public boolean contains(double p_82394_, double p_82395_, double p_82396_) {
      return p_82394_ >= this.minX && p_82394_ < this.maxX && p_82395_ >= this.minY && p_82395_ < this.maxY && p_82396_ >= this.minZ && p_82396_ < this.maxZ;
   }

   public double getSize() {
      double d0 = this.getXsize();
      double d1 = this.getYsize();
      double d2 = this.getZsize();
      return (d0 + d1 + d2) / 3.0D;
   }

   public double getXsize() {
      return this.maxX - this.minX;
   }

   public double getYsize() {
      return this.maxY - this.minY;
   }

   public double getZsize() {
      return this.maxZ - this.minZ;
   }

   public AABB deflate(double p_165898_, double p_165899_, double p_165900_) {
      return this.inflate(-p_165898_, -p_165899_, -p_165900_);
   }

   public AABB deflate(double p_82407_) {
      return this.inflate(-p_82407_);
   }

   public Optional<Vec3> clip(Vec3 p_82372_, Vec3 p_82373_) {
      double[] adouble = new double[]{1.0D};
      double d0 = p_82373_.x - p_82372_.x;
      double d1 = p_82373_.y - p_82372_.y;
      double d2 = p_82373_.z - p_82372_.z;
      Direction direction = getDirection(this, p_82372_, adouble, (Direction)null, d0, d1, d2);
      if (direction == null) {
         return Optional.empty();
      } else {
         double d3 = adouble[0];
         return Optional.of(p_82372_.add(d3 * d0, d3 * d1, d3 * d2));
      }
   }

   @Nullable
   public static BlockHitResult clip(Iterable<AABB> p_82343_, Vec3 p_82344_, Vec3 p_82345_, BlockPos p_82346_) {
      double[] adouble = new double[]{1.0D};
      Direction direction = null;
      double d0 = p_82345_.x - p_82344_.x;
      double d1 = p_82345_.y - p_82344_.y;
      double d2 = p_82345_.z - p_82344_.z;

      for(AABB aabb : p_82343_) {
         direction = getDirection(aabb.move(p_82346_), p_82344_, adouble, direction, d0, d1, d2);
      }

      if (direction == null) {
         return null;
      } else {
         double d3 = adouble[0];
         return new BlockHitResult(p_82344_.add(d3 * d0, d3 * d1, d3 * d2), direction, p_82346_, false);
      }
   }

   @Nullable
   private static Direction getDirection(AABB p_82326_, Vec3 p_82327_, double[] p_82328_, @Nullable Direction p_82329_, double p_82330_, double p_82331_, double p_82332_) {
      if (p_82330_ > 1.0E-7D) {
         p_82329_ = clipPoint(p_82328_, p_82329_, p_82330_, p_82331_, p_82332_, p_82326_.minX, p_82326_.minY, p_82326_.maxY, p_82326_.minZ, p_82326_.maxZ, Direction.WEST, p_82327_.x, p_82327_.y, p_82327_.z);
      } else if (p_82330_ < -1.0E-7D) {
         p_82329_ = clipPoint(p_82328_, p_82329_, p_82330_, p_82331_, p_82332_, p_82326_.maxX, p_82326_.minY, p_82326_.maxY, p_82326_.minZ, p_82326_.maxZ, Direction.EAST, p_82327_.x, p_82327_.y, p_82327_.z);
      }

      if (p_82331_ > 1.0E-7D) {
         p_82329_ = clipPoint(p_82328_, p_82329_, p_82331_, p_82332_, p_82330_, p_82326_.minY, p_82326_.minZ, p_82326_.maxZ, p_82326_.minX, p_82326_.maxX, Direction.DOWN, p_82327_.y, p_82327_.z, p_82327_.x);
      } else if (p_82331_ < -1.0E-7D) {
         p_82329_ = clipPoint(p_82328_, p_82329_, p_82331_, p_82332_, p_82330_, p_82326_.maxY, p_82326_.minZ, p_82326_.maxZ, p_82326_.minX, p_82326_.maxX, Direction.UP, p_82327_.y, p_82327_.z, p_82327_.x);
      }

      if (p_82332_ > 1.0E-7D) {
         p_82329_ = clipPoint(p_82328_, p_82329_, p_82332_, p_82330_, p_82331_, p_82326_.minZ, p_82326_.minX, p_82326_.maxX, p_82326_.minY, p_82326_.maxY, Direction.NORTH, p_82327_.z, p_82327_.x, p_82327_.y);
      } else if (p_82332_ < -1.0E-7D) {
         p_82329_ = clipPoint(p_82328_, p_82329_, p_82332_, p_82330_, p_82331_, p_82326_.maxZ, p_82326_.minX, p_82326_.maxX, p_82326_.minY, p_82326_.maxY, Direction.SOUTH, p_82327_.z, p_82327_.x, p_82327_.y);
      }

      return p_82329_;
   }

   @Nullable
   private static Direction clipPoint(double[] p_82348_, @Nullable Direction p_82349_, double p_82350_, double p_82351_, double p_82352_, double p_82353_, double p_82354_, double p_82355_, double p_82356_, double p_82357_, Direction p_82358_, double p_82359_, double p_82360_, double p_82361_) {
      double d0 = (p_82353_ - p_82359_) / p_82350_;
      double d1 = p_82360_ + d0 * p_82351_;
      double d2 = p_82361_ + d0 * p_82352_;
      if (0.0D < d0 && d0 < p_82348_[0] && p_82354_ - 1.0E-7D < d1 && d1 < p_82355_ + 1.0E-7D && p_82356_ - 1.0E-7D < d2 && d2 < p_82357_ + 1.0E-7D) {
         p_82348_[0] = d0;
         return p_82358_;
      } else {
         return p_82349_;
      }
   }

   public String toString() {
      return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public boolean hasNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   public Vec3 getCenter() {
      return new Vec3(Mth.lerp(0.5D, this.minX, this.maxX), Mth.lerp(0.5D, this.minY, this.maxY), Mth.lerp(0.5D, this.minZ, this.maxZ));
   }

   public static AABB ofSize(Vec3 p_165883_, double p_165884_, double p_165885_, double p_165886_) {
      return new AABB(p_165883_.x - p_165884_ / 2.0D, p_165883_.y - p_165885_ / 2.0D, p_165883_.z - p_165886_ / 2.0D, p_165883_.x + p_165884_ / 2.0D, p_165883_.y + p_165885_ / 2.0D, p_165883_.z + p_165886_ / 2.0D);
   }
}