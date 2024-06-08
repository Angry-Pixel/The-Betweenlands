package net.minecraft.world.entity.decoration;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity extends Entity {
   protected static final Predicate<Entity> HANGING_ENTITY = (p_31734_) -> {
      return p_31734_ instanceof HangingEntity;
   };
   private int checkInterval;
   protected BlockPos pos;
   protected Direction direction = Direction.SOUTH;

   protected HangingEntity(EntityType<? extends HangingEntity> p_31703_, Level p_31704_) {
      super(p_31703_, p_31704_);
   }

   protected HangingEntity(EntityType<? extends HangingEntity> p_31706_, Level p_31707_, BlockPos p_31708_) {
      this(p_31706_, p_31707_);
      this.pos = p_31708_;
   }

   protected void defineSynchedData() {
   }

   protected void setDirection(Direction p_31728_) {
      Validate.notNull(p_31728_);
      Validate.isTrue(p_31728_.getAxis().isHorizontal());
      this.direction = p_31728_;
      this.setYRot((float)(this.direction.get2DDataValue() * 90));
      this.yRotO = this.getYRot();
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double d0 = (double)this.pos.getX() + 0.5D;
         double d1 = (double)this.pos.getY() + 0.5D;
         double d2 = (double)this.pos.getZ() + 0.5D;
         double d3 = 0.46875D;
         double d4 = this.offs(this.getWidth());
         double d5 = this.offs(this.getHeight());
         d0 -= (double)this.direction.getStepX() * 0.46875D;
         d2 -= (double)this.direction.getStepZ() * 0.46875D;
         d1 += d5;
         Direction direction = this.direction.getCounterClockWise();
         d0 += d4 * (double)direction.getStepX();
         d2 += d4 * (double)direction.getStepZ();
         this.setPosRaw(d0, d1, d2);
         double d6 = (double)this.getWidth();
         double d7 = (double)this.getHeight();
         double d8 = (double)this.getWidth();
         if (this.direction.getAxis() == Direction.Axis.Z) {
            d8 = 1.0D;
         } else {
            d6 = 1.0D;
         }

         d6 /= 32.0D;
         d7 /= 32.0D;
         d8 /= 32.0D;
         this.setBoundingBox(new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
      }
   }

   private double offs(int p_31710_) {
      return p_31710_ % 32 == 0 ? 0.5D : 0.0D;
   }

   public void tick() {
      if (!this.level.isClientSide) {
         this.checkOutOfWorld();
         if (this.checkInterval++ == 100) {
            this.checkInterval = 0;
            if (!this.isRemoved() && !this.survives()) {
               this.discard();
               this.dropItem((Entity)null);
            }
         }
      }

   }

   public boolean survives() {
      if (!this.level.noCollision(this)) {
         return false;
      } else {
         int i = Math.max(1, this.getWidth() / 16);
         int j = Math.max(1, this.getHeight() / 16);
         BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
         Direction direction = this.direction.getCounterClockWise();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = 0; k < i; ++k) {
            for(int l = 0; l < j; ++l) {
               int i1 = (i - 1) / -2;
               int j1 = (j - 1) / -2;
               blockpos$mutableblockpos.set(blockpos).move(direction, k + i1).move(Direction.UP, l + j1);
               BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
               if (net.minecraft.world.level.block.Block.canSupportCenter(this.level, blockpos$mutableblockpos, this.direction))
                  continue;
               if (!blockstate.getMaterial().isSolid() && !DiodeBlock.isDiode(blockstate)) {
                  return false;
               }
            }
         }

         return this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }

   public boolean isPickable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity p_31750_) {
      if (p_31750_ instanceof Player) {
         Player player = (Player)p_31750_;
         return !this.level.mayInteract(player, this.pos) ? true : this.hurt(DamageSource.playerAttack(player), 0.0F);
      } else {
         return false;
      }
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean hurt(DamageSource p_31715_, float p_31716_) {
      if (this.isInvulnerableTo(p_31715_)) {
         return false;
      } else {
         if (!this.isRemoved() && !this.level.isClientSide) {
            this.kill();
            this.markHurt();
            this.dropItem(p_31715_.getEntity());
         }

         return true;
      }
   }

   public void move(MoverType p_31719_, Vec3 p_31720_) {
      if (!this.level.isClientSide && !this.isRemoved() && p_31720_.lengthSqr() > 0.0D) {
         this.kill();
         this.dropItem((Entity)null);
      }

   }

   public void push(double p_31744_, double p_31745_, double p_31746_) {
      if (!this.level.isClientSide && !this.isRemoved() && p_31744_ * p_31744_ + p_31745_ * p_31745_ + p_31746_ * p_31746_ > 0.0D) {
         this.kill();
         this.dropItem((Entity)null);
      }

   }

   public void addAdditionalSaveData(CompoundTag p_31736_) {
      BlockPos blockpos = this.getPos();
      p_31736_.putInt("TileX", blockpos.getX());
      p_31736_.putInt("TileY", blockpos.getY());
      p_31736_.putInt("TileZ", blockpos.getZ());
   }

   public void readAdditionalSaveData(CompoundTag p_31730_) {
      this.pos = new BlockPos(p_31730_.getInt("TileX"), p_31730_.getInt("TileY"), p_31730_.getInt("TileZ"));
   }

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract void dropItem(@Nullable Entity p_31717_);

   public abstract void playPlacementSound();

   public ItemEntity spawnAtLocation(ItemStack p_31722_, float p_31723_) {
      ItemEntity itementity = new ItemEntity(this.level, this.getX() + (double)((float)this.direction.getStepX() * 0.15F), this.getY() + (double)p_31723_, this.getZ() + (double)((float)this.direction.getStepZ() * 0.15F), p_31722_);
      itementity.setDefaultPickUpDelay();
      this.level.addFreshEntity(itementity);
      return itementity;
   }

   protected boolean repositionEntityAfterLoad() {
      return false;
   }

   public void setPos(double p_31739_, double p_31740_, double p_31741_) {
      this.pos = new BlockPos(p_31739_, p_31740_, p_31741_);
      this.recalculateBoundingBox();
      this.hasImpulse = true;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public float rotate(Rotation p_31727_) {
      if (this.direction.getAxis() != Direction.Axis.Y) {
         switch(p_31727_) {
         case CLOCKWISE_180:
            this.direction = this.direction.getOpposite();
            break;
         case COUNTERCLOCKWISE_90:
            this.direction = this.direction.getCounterClockWise();
            break;
         case CLOCKWISE_90:
            this.direction = this.direction.getClockWise();
         }
      }

      float f = Mth.wrapDegrees(this.getYRot());
      switch(p_31727_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 90.0F;
      case CLOCKWISE_90:
         return f + 270.0F;
      default:
         return f;
      }
   }

   public float mirror(Mirror p_31725_) {
      return this.rotate(p_31725_.getRotation(this.direction));
   }

   public void thunderHit(ServerLevel p_31712_, LightningBolt p_31713_) {
   }

   public void refreshDimensions() {
   }
}
