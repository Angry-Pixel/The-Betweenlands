package net.minecraft.world.phys.shapes;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY = new EntityCollisionContext(false, -Double.MAX_VALUE, ItemStack.EMPTY, (p_205118_) -> {
      return false;
   }, (Entity)null) {
      public boolean isAbove(VoxelShape p_82898_, BlockPos p_82899_, boolean p_82900_) {
         return p_82900_;
      }
   };
   private final boolean descending;
   private final double entityBottom;
   private final ItemStack heldItem;
   private final Predicate<FluidState> canStandOnFluid;
   @Nullable
   private final Entity entity;

   protected EntityCollisionContext(boolean p_198916_, double p_198917_, ItemStack p_198918_, Predicate<FluidState> p_198919_, @Nullable Entity p_198920_) {
      this.descending = p_198916_;
      this.entityBottom = p_198917_;
      this.heldItem = p_198918_;
      this.canStandOnFluid = p_198919_;
      this.entity = p_198920_;
   }

   /** @deprecated */
   @Deprecated
   protected EntityCollisionContext(Entity p_82872_) {
      this(p_82872_.isDescending(), p_82872_.getY(), p_82872_ instanceof LivingEntity ? ((LivingEntity)p_82872_).getMainHandItem() : ItemStack.EMPTY, p_82872_ instanceof LivingEntity ? ((LivingEntity)p_82872_)::canStandOnFluid : (p_205113_) -> {
         return false;
      }, p_82872_);
   }

   public boolean isHoldingItem(Item p_82879_) {
      return this.heldItem.is(p_82879_);
   }

   public boolean canStandOnFluid(FluidState p_205115_, FluidState p_205116_) {
      return this.canStandOnFluid.test(p_205116_) && !p_205115_.getType().isSame(p_205116_.getType());
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape p_82886_, BlockPos p_82887_, boolean p_82888_) {
      return this.entityBottom > (double)p_82887_.getY() + p_82886_.max(Direction.Axis.Y) - (double)1.0E-5F;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }
}