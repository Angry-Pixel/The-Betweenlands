package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEnderpearl extends ThrowableItemProjectile {
   public ThrownEnderpearl(EntityType<? extends ThrownEnderpearl> p_37491_, Level p_37492_) {
      super(p_37491_, p_37492_);
   }

   public ThrownEnderpearl(Level p_37499_, LivingEntity p_37500_) {
      super(EntityType.ENDER_PEARL, p_37500_, p_37499_);
   }

   protected Item getDefaultItem() {
      return Items.ENDER_PEARL;
   }

   protected void onHitEntity(EntityHitResult p_37502_) {
      super.onHitEntity(p_37502_);
      p_37502_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
   }

   protected void onHit(HitResult p_37504_) {
      super.onHit(p_37504_);

      for(int i = 0; i < 32; ++i) {
         this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
      }

      if (!this.level.isClientSide && !this.isRemoved()) {
         Entity entity = this.getOwner();
         if (entity instanceof ServerPlayer) {
            ServerPlayer serverplayer = (ServerPlayer)entity;
            if (serverplayer.connection.getConnection().isConnected() && serverplayer.level == this.level && !serverplayer.isSleeping()) {
               net.minecraftforge.event.entity.EntityTeleportEvent.EnderPearl event = net.minecraftforge.event.ForgeEventFactory.onEnderPearlLand(serverplayer, this.getX(), this.getY(), this.getZ(), this, 5.0F);
               if (!event.isCanceled()) { // Don't indent to lower patch size
               if (this.random.nextFloat() < 0.05F && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                  Endermite endermite = EntityType.ENDERMITE.create(this.level);
                  endermite.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                  this.level.addFreshEntity(endermite);
               }

               if (entity.isPassenger()) {
                  serverplayer.dismountTo(this.getX(), this.getY(), this.getZ());
               } else {
                  entity.teleportTo(this.getX(), this.getY(), this.getZ());
               }

               entity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               entity.resetFallDistance();
               entity.hurt(DamageSource.FALL, event.getAttackDamage());
               } //Forge: End
            }
         } else if (entity != null) {
            entity.teleportTo(this.getX(), this.getY(), this.getZ());
            entity.resetFallDistance();
         }

         this.discard();
      }

   }

   public void tick() {
      Entity entity = this.getOwner();
      if (entity instanceof Player && !entity.isAlive()) {
         this.discard();
      } else {
         super.tick();
      }

   }

   @Nullable
   public Entity changeDimension(ServerLevel p_37506_, net.minecraftforge.common.util.ITeleporter teleporter) {
      Entity entity = this.getOwner();
      if (entity != null && entity.level.dimension() != p_37506_.dimension()) {
         this.setOwner((Entity)null);
      }

      return super.changeDimension(p_37506_, teleporter);
   }
}
