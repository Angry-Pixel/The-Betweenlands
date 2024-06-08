package net.minecraft.world.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity extends HangingEntity {
   public static final double OFFSET_Y = 0.375D;

   public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> p_31828_, Level p_31829_) {
      super(p_31828_, p_31829_);
   }

   public LeashFenceKnotEntity(Level p_31831_, BlockPos p_31832_) {
      super(EntityType.LEASH_KNOT, p_31831_, p_31832_);
      this.setPos((double)p_31832_.getX(), (double)p_31832_.getY(), (double)p_31832_.getZ());
   }

   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.375D, (double)this.pos.getZ() + 0.5D);
      double d0 = (double)this.getType().getWidth() / 2.0D;
      double d1 = (double)this.getType().getHeight();
      this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + d1, this.getZ() + d0));
   }

   public void setDirection(Direction p_31848_) {
   }

   public int getWidth() {
      return 9;
   }

   public int getHeight() {
      return 9;
   }

   protected float getEyeHeight(Pose p_31839_, EntityDimensions p_31840_) {
      return 0.0625F;
   }

   public boolean shouldRenderAtSqrDistance(double p_31835_) {
      return p_31835_ < 1024.0D;
   }

   public void dropItem(@Nullable Entity p_31837_) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag p_31852_) {
   }

   public void readAdditionalSaveData(CompoundTag p_31850_) {
   }

   public InteractionResult interact(Player p_31842_, InteractionHand p_31843_) {
      if (this.level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         boolean flag = false;
         double d0 = 7.0D;
         List<Mob> list = this.level.getEntitiesOfClass(Mob.class, new AABB(this.getX() - 7.0D, this.getY() - 7.0D, this.getZ() - 7.0D, this.getX() + 7.0D, this.getY() + 7.0D, this.getZ() + 7.0D));

         for(Mob mob : list) {
            if (mob.getLeashHolder() == p_31842_) {
               mob.setLeashedTo(this, true);
               flag = true;
            }
         }

         if (!flag) {
            this.discard();
            if (p_31842_.getAbilities().instabuild) {
               for(Mob mob1 : list) {
                  if (mob1.isLeashed() && mob1.getLeashHolder() == this) {
                     mob1.dropLeash(true, false);
                  }
               }
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   public boolean survives() {
      return this.level.getBlockState(this.pos).is(BlockTags.FENCES);
   }

   public static LeashFenceKnotEntity getOrCreateKnot(Level p_31845_, BlockPos p_31846_) {
      int i = p_31846_.getX();
      int j = p_31846_.getY();
      int k = p_31846_.getZ();

      for(LeashFenceKnotEntity leashfenceknotentity : p_31845_.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)i - 1.0D, (double)j - 1.0D, (double)k - 1.0D, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D))) {
         if (leashfenceknotentity.getPos().equals(p_31846_)) {
            return leashfenceknotentity;
         }
      }

      LeashFenceKnotEntity leashfenceknotentity1 = new LeashFenceKnotEntity(p_31845_, p_31846_);
      p_31845_.addFreshEntity(leashfenceknotentity1);
      return leashfenceknotentity1;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), 0, this.getPos());
   }

   public Vec3 getRopeHoldPosition(float p_31863_) {
      return this.getPosition(p_31863_).add(0.0D, 0.2D, 0.0D);
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.LEAD);
   }
}