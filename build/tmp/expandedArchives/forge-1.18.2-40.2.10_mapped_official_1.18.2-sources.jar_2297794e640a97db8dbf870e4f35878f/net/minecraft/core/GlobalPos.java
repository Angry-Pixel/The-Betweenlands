package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class GlobalPos {
   public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create((p_122642_) -> {
      return p_122642_.group(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply(p_122642_, GlobalPos::of);
   });
   private final ResourceKey<Level> dimension;
   private final BlockPos pos;

   private GlobalPos(ResourceKey<Level> p_122638_, BlockPos p_122639_) {
      this.dimension = p_122638_;
      this.pos = p_122639_;
   }

   public static GlobalPos of(ResourceKey<Level> p_122644_, BlockPos p_122645_) {
      return new GlobalPos(p_122644_, p_122645_);
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean equals(Object p_122648_) {
      if (this == p_122648_) {
         return true;
      } else if (p_122648_ != null && this.getClass() == p_122648_.getClass()) {
         GlobalPos globalpos = (GlobalPos)p_122648_;
         return Objects.equals(this.dimension, globalpos.dimension) && Objects.equals(this.pos, globalpos.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.dimension, this.pos);
   }

   public String toString() {
      return this.dimension + " " + this.pos;
   }
}