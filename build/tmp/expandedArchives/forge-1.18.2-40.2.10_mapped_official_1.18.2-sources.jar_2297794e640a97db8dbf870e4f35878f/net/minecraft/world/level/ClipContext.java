package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClipContext {
   private final Vec3 from;
   private final Vec3 to;
   private final ClipContext.Block block;
   private final ClipContext.Fluid fluid;
   private final CollisionContext collisionContext;

   public ClipContext(Vec3 p_45688_, Vec3 p_45689_, ClipContext.Block p_45690_, ClipContext.Fluid p_45691_, @javax.annotation.Nullable Entity p_45692_) {
      this.from = p_45688_;
      this.to = p_45689_;
      this.block = p_45690_;
      this.fluid = p_45691_;
      this.collisionContext = p_45692_ == null ? CollisionContext.empty() : CollisionContext.of(p_45692_);
   }

   public Vec3 getTo() {
      return this.to;
   }

   public Vec3 getFrom() {
      return this.from;
   }

   public VoxelShape getBlockShape(BlockState p_45695_, BlockGetter p_45696_, BlockPos p_45697_) {
      return this.block.get(p_45695_, p_45696_, p_45697_, this.collisionContext);
   }

   public VoxelShape getFluidShape(FluidState p_45699_, BlockGetter p_45700_, BlockPos p_45701_) {
      return this.fluid.canPick(p_45699_) ? p_45699_.getShape(p_45700_, p_45701_) : Shapes.empty();
   }

   public static enum Block implements ClipContext.ShapeGetter {
      COLLIDER(BlockBehaviour.BlockStateBase::getCollisionShape),
      OUTLINE(BlockBehaviour.BlockStateBase::getShape),
      VISUAL(BlockBehaviour.BlockStateBase::getVisualShape),
      FALLDAMAGE_RESETTING((p_201982_, p_201983_, p_201984_, p_201985_) -> {
         return p_201982_.is(BlockTags.FALL_DAMAGE_RESETTING) ? Shapes.block() : Shapes.empty();
      });

      private final ClipContext.ShapeGetter shapeGetter;

      private Block(ClipContext.ShapeGetter p_45712_) {
         this.shapeGetter = p_45712_;
      }

      public VoxelShape get(BlockState p_45714_, BlockGetter p_45715_, BlockPos p_45716_, CollisionContext p_45717_) {
         return this.shapeGetter.get(p_45714_, p_45715_, p_45716_, p_45717_);
      }
   }

   public static enum Fluid {
      NONE((p_45736_) -> {
         return false;
      }),
      SOURCE_ONLY(FluidState::isSource),
      ANY((p_45734_) -> {
         return !p_45734_.isEmpty();
      }),
      WATER((p_201988_) -> {
         return p_201988_.is(FluidTags.WATER);
      });

      private final Predicate<FluidState> canPick;

      private Fluid(Predicate<FluidState> p_45730_) {
         this.canPick = p_45730_;
      }

      public boolean canPick(FluidState p_45732_) {
         return this.canPick.test(p_45732_);
      }
   }

   public interface ShapeGetter {
      VoxelShape get(BlockState p_45740_, BlockGetter p_45741_, BlockPos p_45742_, CollisionContext p_45743_);
   }
}
