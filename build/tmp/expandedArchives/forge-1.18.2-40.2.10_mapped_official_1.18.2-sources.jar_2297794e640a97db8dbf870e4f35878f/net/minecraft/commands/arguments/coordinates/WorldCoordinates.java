package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates implements Coordinates {
   private final WorldCoordinate x;
   private final WorldCoordinate y;
   private final WorldCoordinate z;

   public WorldCoordinates(WorldCoordinate p_120883_, WorldCoordinate p_120884_, WorldCoordinate p_120885_) {
      this.x = p_120883_;
      this.y = p_120884_;
      this.z = p_120885_;
   }

   public Vec3 getPosition(CommandSourceStack p_120893_) {
      Vec3 vec3 = p_120893_.getPosition();
      return new Vec3(this.x.get(vec3.x), this.y.get(vec3.y), this.z.get(vec3.z));
   }

   public Vec2 getRotation(CommandSourceStack p_120896_) {
      Vec2 vec2 = p_120896_.getRotation();
      return new Vec2((float)this.x.get((double)vec2.x), (float)this.y.get((double)vec2.y));
   }

   public boolean isXRelative() {
      return this.x.isRelative();
   }

   public boolean isYRelative() {
      return this.y.isRelative();
   }

   public boolean isZRelative() {
      return this.z.isRelative();
   }

   public boolean equals(Object p_120900_) {
      if (this == p_120900_) {
         return true;
      } else if (!(p_120900_ instanceof WorldCoordinates)) {
         return false;
      } else {
         WorldCoordinates worldcoordinates = (WorldCoordinates)p_120900_;
         if (!this.x.equals(worldcoordinates.x)) {
            return false;
         } else {
            return !this.y.equals(worldcoordinates.y) ? false : this.z.equals(worldcoordinates.z);
         }
      }
   }

   public static WorldCoordinates parseInt(StringReader p_120888_) throws CommandSyntaxException {
      int i = p_120888_.getCursor();
      WorldCoordinate worldcoordinate = WorldCoordinate.parseInt(p_120888_);
      if (p_120888_.canRead() && p_120888_.peek() == ' ') {
         p_120888_.skip();
         WorldCoordinate worldcoordinate1 = WorldCoordinate.parseInt(p_120888_);
         if (p_120888_.canRead() && p_120888_.peek() == ' ') {
            p_120888_.skip();
            WorldCoordinate worldcoordinate2 = WorldCoordinate.parseInt(p_120888_);
            return new WorldCoordinates(worldcoordinate, worldcoordinate1, worldcoordinate2);
         } else {
            p_120888_.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_120888_);
         }
      } else {
         p_120888_.setCursor(i);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_120888_);
      }
   }

   public static WorldCoordinates parseDouble(StringReader p_120890_, boolean p_120891_) throws CommandSyntaxException {
      int i = p_120890_.getCursor();
      WorldCoordinate worldcoordinate = WorldCoordinate.parseDouble(p_120890_, p_120891_);
      if (p_120890_.canRead() && p_120890_.peek() == ' ') {
         p_120890_.skip();
         WorldCoordinate worldcoordinate1 = WorldCoordinate.parseDouble(p_120890_, false);
         if (p_120890_.canRead() && p_120890_.peek() == ' ') {
            p_120890_.skip();
            WorldCoordinate worldcoordinate2 = WorldCoordinate.parseDouble(p_120890_, p_120891_);
            return new WorldCoordinates(worldcoordinate, worldcoordinate1, worldcoordinate2);
         } else {
            p_120890_.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_120890_);
         }
      } else {
         p_120890_.setCursor(i);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(p_120890_);
      }
   }

   public static WorldCoordinates absolute(double p_175086_, double p_175087_, double p_175088_) {
      return new WorldCoordinates(new WorldCoordinate(false, p_175086_), new WorldCoordinate(false, p_175087_), new WorldCoordinate(false, p_175088_));
   }

   public static WorldCoordinates absolute(Vec2 p_175090_) {
      return new WorldCoordinates(new WorldCoordinate(false, (double)p_175090_.x), new WorldCoordinate(false, (double)p_175090_.y), new WorldCoordinate(true, 0.0D));
   }

   public static WorldCoordinates current() {
      return new WorldCoordinates(new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D));
   }

   public int hashCode() {
      int i = this.x.hashCode();
      i = 31 * i + this.y.hashCode();
      return 31 * i + this.z.hashCode();
   }
}