package net.minecraft.world.entity.monster;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public interface CrossbowAttackMob extends RangedAttackMob {
   void setChargingCrossbow(boolean p_32339_);

   void shootCrossbowProjectile(LivingEntity p_32328_, ItemStack p_32329_, Projectile p_32330_, float p_32331_);

   @Nullable
   LivingEntity getTarget();

   void onCrossbowAttackPerformed();

   default void performCrossbowAttack(LivingEntity p_32337_, float p_32338_) {
      InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(p_32337_, item -> item instanceof CrossbowItem);
      ItemStack itemstack = p_32337_.getItemInHand(interactionhand);
      if (p_32337_.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
         CrossbowItem.performShooting(p_32337_.level, p_32337_, interactionhand, itemstack, p_32338_, (float)(14 - p_32337_.level.getDifficulty().getId() * 4));
      }

      this.onCrossbowAttackPerformed();
   }

   default void shootCrossbowProjectile(LivingEntity p_32323_, LivingEntity p_32324_, Projectile p_32325_, float p_32326_, float p_32327_) {
      double d0 = p_32324_.getX() - p_32323_.getX();
      double d1 = p_32324_.getZ() - p_32323_.getZ();
      double d2 = Math.sqrt(d0 * d0 + d1 * d1);
      double d3 = p_32324_.getY(0.3333333333333333D) - p_32325_.getY() + d2 * (double)0.2F;
      Vector3f vector3f = this.getProjectileShotVector(p_32323_, new Vec3(d0, d3, d1), p_32326_);
      p_32325_.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_32327_, (float)(14 - p_32323_.level.getDifficulty().getId() * 4));
      p_32323_.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (p_32323_.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   default Vector3f getProjectileShotVector(LivingEntity p_32333_, Vec3 p_32334_, float p_32335_) {
      Vec3 vec3 = p_32334_.normalize();
      Vec3 vec31 = vec3.cross(new Vec3(0.0D, 1.0D, 0.0D));
      if (vec31.lengthSqr() <= 1.0E-7D) {
         vec31 = vec3.cross(p_32333_.getUpVector(1.0F));
      }

      Quaternion quaternion = new Quaternion(new Vector3f(vec31), 90.0F, true);
      Vector3f vector3f = new Vector3f(vec3);
      vector3f.transform(quaternion);
      Quaternion quaternion1 = new Quaternion(vector3f, p_32335_, true);
      Vector3f vector3f1 = new Vector3f(vec3);
      vector3f1.transform(quaternion1);
      return vector3f1;
   }
}
