package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace extends AbstractMinecart {
   private static final EntityDataAccessor<Boolean> DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
   private int fuel;
   public double xPush;
   public double zPush;
   private static final Ingredient INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);

   public MinecartFurnace(EntityType<? extends MinecartFurnace> p_38552_, Level p_38553_) {
      super(p_38552_, p_38553_);
   }

   public MinecartFurnace(Level p_38555_, double p_38556_, double p_38557_, double p_38558_) {
      super(EntityType.FURNACE_MINECART, p_38555_, p_38556_, p_38557_, p_38558_);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.FURNACE;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FUEL, false);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide()) {
         if (this.fuel > 0) {
            --this.fuel;
         }

         if (this.fuel <= 0) {
            this.xPush = 0.0D;
            this.zPush = 0.0D;
         }

         this.setHasFuel(this.fuel > 0);
      }

      if (this.hasFuel() && this.random.nextInt(4) == 0) {
         this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected double getMaxSpeed() {
      return (this.isInWater() ? 3.0D : 4.0D) / 20.0D;
   }

   @Override
   public float getMaxCartSpeedOnRail() {
      return 0.2f;
   }

   public void destroy(DamageSource p_38560_) {
      super.destroy(p_38560_);
      if (!p_38560_.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos p_38569_, BlockState p_38570_) {
      double d0 = 1.0E-4D;
      double d1 = 0.001D;
      super.moveAlongTrack(p_38569_, p_38570_);
      Vec3 vec3 = this.getDeltaMovement();
      double d2 = vec3.horizontalDistanceSqr();
      double d3 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (d3 > 1.0E-4D && d2 > 0.001D) {
         double d4 = Math.sqrt(d2);
         double d5 = Math.sqrt(d3);
         this.xPush = vec3.x / d4 * d5;
         this.zPush = vec3.z / d4 * d5;
      }

   }

   protected void applyNaturalSlowdown() {
      double d0 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (d0 > 1.0E-7D) {
         d0 = Math.sqrt(d0);
         this.xPush /= d0;
         this.zPush /= d0;
         Vec3 vec3 = this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush);
         if (this.isInWater()) {
            vec3 = vec3.scale(0.1D);
         }

         this.setDeltaMovement(vec3);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.0D, 0.98D));
      }

      super.applyNaturalSlowdown();
   }

   public InteractionResult interact(Player p_38562_, InteractionHand p_38563_) {
      InteractionResult ret = super.interact(p_38562_, p_38563_);
      if (ret.consumesAction()) return ret;
      ItemStack itemstack = p_38562_.getItemInHand(p_38563_);
      if (INGREDIENT.test(itemstack) && this.fuel + 3600 <= 32000) {
         if (!p_38562_.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.xPush = this.getX() - p_38562_.getX();
         this.zPush = this.getZ() - p_38562_.getZ();
      }

      return InteractionResult.sidedSuccess(this.level.isClientSide);
   }

   protected void addAdditionalSaveData(CompoundTag p_38567_) {
      super.addAdditionalSaveData(p_38567_);
      p_38567_.putDouble("PushX", this.xPush);
      p_38567_.putDouble("PushZ", this.zPush);
      p_38567_.putShort("Fuel", (short)this.fuel);
   }

   protected void readAdditionalSaveData(CompoundTag p_38565_) {
      super.readAdditionalSaveData(p_38565_);
      this.xPush = p_38565_.getDouble("PushX");
      this.zPush = p_38565_.getDouble("PushZ");
      this.fuel = p_38565_.getShort("Fuel");
   }

   protected boolean hasFuel() {
      return this.entityData.get(DATA_ID_FUEL);
   }

   protected void setHasFuel(boolean p_38577_) {
      this.entityData.set(DATA_ID_FUEL, p_38577_);
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH).setValue(FurnaceBlock.LIT, Boolean.valueOf(this.hasFuel()));
   }
}
