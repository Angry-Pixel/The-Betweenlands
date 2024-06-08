package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ClipBlockStateContext {
   private final Vec3 from;
   private final Vec3 to;
   private final Predicate<BlockState> block;

   public ClipBlockStateContext(Vec3 p_151401_, Vec3 p_151402_, Predicate<BlockState> p_151403_) {
      this.from = p_151401_;
      this.to = p_151402_;
      this.block = p_151403_;
   }

   public Vec3 getTo() {
      return this.to;
   }

   public Vec3 getFrom() {
      return this.from;
   }

   public Predicate<BlockState> isTargetBlock() {
      return this.block;
   }
}