package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.commons.lang3.tuple.Pair;

public class MushroomCow extends Cow implements Shearable, net.minecraftforge.common.IForgeShearable {
   private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
   private static final int MUTATE_CHANCE = 1024;
   @Nullable
   private MobEffect effect;
   private int effectDuration;
   @Nullable
   private UUID lastLightningBoltUUID;

   public MushroomCow(EntityType<? extends MushroomCow> p_28914_, Level p_28915_) {
      super(p_28914_, p_28915_);
   }

   public float getWalkTargetValue(BlockPos p_28933_, LevelReader p_28934_) {
      return p_28934_.getBlockState(p_28933_.below()).is(Blocks.MYCELIUM) ? 10.0F : p_28934_.getBrightness(p_28933_) - 0.5F;
   }

   public static boolean checkMushroomSpawnRules(EntityType<MushroomCow> p_28949_, LevelAccessor p_28950_, MobSpawnType p_28951_, BlockPos p_28952_, Random p_28953_) {
      return p_28950_.getBlockState(p_28952_.below()).is(BlockTags.MOOSHROOMS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_28950_, p_28952_);
   }

   public void thunderHit(ServerLevel p_28921_, LightningBolt p_28922_) {
      UUID uuid = p_28922_.getUUID();
      if (!uuid.equals(this.lastLightningBoltUUID)) {
         this.setMushroomType(this.getMushroomType() == MushroomCow.MushroomType.RED ? MushroomCow.MushroomType.BROWN : MushroomCow.MushroomType.RED);
         this.lastLightningBoltUUID = uuid;
         this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE, MushroomCow.MushroomType.RED.type);
   }

   public InteractionResult mobInteract(Player p_28941_, InteractionHand p_28942_) {
      ItemStack itemstack = p_28941_.getItemInHand(p_28942_);
      if (itemstack.is(Items.BOWL) && !this.isBaby()) {
         boolean flag = false;
         ItemStack itemstack1;
         if (this.effect != null) {
            flag = true;
            itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(itemstack1, this.effect, this.effectDuration);
            this.effect = null;
            this.effectDuration = 0;
         } else {
            itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
         }

         ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, p_28941_, itemstack1, false);
         p_28941_.setItemInHand(p_28942_, itemstack2);
         SoundEvent soundevent;
         if (flag) {
            soundevent = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
         } else {
            soundevent = SoundEvents.MOOSHROOM_MILK;
         }

         this.playSound(soundevent, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (false && itemstack.getItem() == Items.SHEARS && this.readyForShearing()) { //Forge: Moved to onSheared
         this.shear(SoundSource.PLAYERS);
         this.gameEvent(GameEvent.SHEAR, p_28941_);
         if (!this.level.isClientSide) {
            itemstack.hurtAndBreak(1, p_28941_, (p_28927_) -> {
               p_28927_.broadcastBreakEvent(p_28942_);
            });
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (this.getMushroomType() == MushroomCow.MushroomType.BROWN && itemstack.is(ItemTags.SMALL_FLOWERS)) {
         if (this.effect != null) {
            for(int i = 0; i < 2; ++i) {
               this.level.addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextDouble() / 2.0D, this.getY(0.5D), this.getZ() + this.random.nextDouble() / 2.0D, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
            }
         } else {
            Optional<Pair<MobEffect, Integer>> optional = this.getEffectFromItemStack(itemstack);
            if (!optional.isPresent()) {
               return InteractionResult.PASS;
            }

            Pair<MobEffect, Integer> pair = optional.get();
            if (!p_28941_.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            for(int j = 0; j < 4; ++j) {
               this.level.addParticle(ParticleTypes.EFFECT, this.getX() + this.random.nextDouble() / 2.0D, this.getY(0.5D), this.getZ() + this.random.nextDouble() / 2.0D, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
            }

            this.effect = pair.getLeft();
            this.effectDuration = pair.getRight();
            this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0F, 1.0F);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_28941_, p_28942_);
      }
   }

   @Override
   public java.util.List<ItemStack> onSheared(@javax.annotation.Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
      this.gameEvent(GameEvent.SHEAR, player);
      return shearInternal(player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS);
   }

   public void shear(SoundSource p_28924_) {
      shearInternal(p_28924_).forEach(s -> this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(1.0D), this.getZ(), s)));
   }

   private java.util.List<ItemStack> shearInternal(SoundSource p_28924_) {
      this.level.playSound((Player)null, this, SoundEvents.MOOSHROOM_SHEAR, p_28924_, 1.0F, 1.0F);
      if (!this.level.isClientSide()) {
         ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5D), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
         this.discard();
         Cow cow = EntityType.COW.create(this.level);
         cow.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
         cow.setHealth(this.getHealth());
         cow.yBodyRot = this.yBodyRot;
         if (this.hasCustomName()) {
            cow.setCustomName(this.getCustomName());
            cow.setCustomNameVisible(this.isCustomNameVisible());
         }

         if (this.isPersistenceRequired()) {
            cow.setPersistenceRequired();
         }

         cow.setInvulnerable(this.isInvulnerable());
         this.level.addFreshEntity(cow);

         java.util.List<ItemStack> items = new java.util.ArrayList<>();
         for(int i = 0; i < 5; ++i) {
            items.add(new ItemStack(this.getMushroomType().blockState.getBlock()));
         }
         return items;
      }
      return java.util.Collections.emptyList();

   }

   public boolean readyForShearing() {
      return this.isAlive() && !this.isBaby();
   }

   public void addAdditionalSaveData(CompoundTag p_28944_) {
      super.addAdditionalSaveData(p_28944_);
      p_28944_.putString("Type", this.getMushroomType().type);
      if (this.effect != null) {
         p_28944_.putByte("EffectId", (byte)MobEffect.getId(this.effect));
         net.minecraftforge.common.ForgeHooks.saveMobEffect(p_28944_, "forge:effect_id", this.effect);
         p_28944_.putInt("EffectDuration", this.effectDuration);
      }

   }

   public void readAdditionalSaveData(CompoundTag p_28936_) {
      super.readAdditionalSaveData(p_28936_);
      this.setMushroomType(MushroomCow.MushroomType.byType(p_28936_.getString("Type")));
      if (p_28936_.contains("EffectId", 1)) {
         this.effect = MobEffect.byId(p_28936_.getByte("EffectId"));
         this.effect = net.minecraftforge.common.ForgeHooks.loadMobEffect(p_28936_, "forge:effect_id", this.effect);
      }

      if (p_28936_.contains("EffectDuration", 3)) {
         this.effectDuration = p_28936_.getInt("EffectDuration");
      }

   }

   private Optional<Pair<MobEffect, Integer>> getEffectFromItemStack(ItemStack p_28957_) {
      Item item = p_28957_.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         if (block instanceof FlowerBlock) {
            FlowerBlock flowerblock = (FlowerBlock)block;
            return Optional.of(Pair.of(flowerblock.getSuspiciousStewEffect(), flowerblock.getEffectDuration()));
         }
      }

      return Optional.empty();
   }

   private void setMushroomType(MushroomCow.MushroomType p_28929_) {
      this.entityData.set(DATA_TYPE, p_28929_.type);
   }

   public MushroomCow.MushroomType getMushroomType() {
      return MushroomCow.MushroomType.byType(this.entityData.get(DATA_TYPE));
   }

   public MushroomCow getBreedOffspring(ServerLevel p_148942_, AgeableMob p_148943_) {
      MushroomCow mushroomcow = EntityType.MOOSHROOM.create(p_148942_);
      mushroomcow.setMushroomType(this.getOffspringType((MushroomCow)p_148943_));
      return mushroomcow;
   }

   private MushroomCow.MushroomType getOffspringType(MushroomCow p_28931_) {
      MushroomCow.MushroomType mushroomcow$mushroomtype = this.getMushroomType();
      MushroomCow.MushroomType mushroomcow$mushroomtype1 = p_28931_.getMushroomType();
      MushroomCow.MushroomType mushroomcow$mushroomtype2;
      if (mushroomcow$mushroomtype == mushroomcow$mushroomtype1 && this.random.nextInt(1024) == 0) {
         mushroomcow$mushroomtype2 = mushroomcow$mushroomtype == MushroomCow.MushroomType.BROWN ? MushroomCow.MushroomType.RED : MushroomCow.MushroomType.BROWN;
      } else {
         mushroomcow$mushroomtype2 = this.random.nextBoolean() ? mushroomcow$mushroomtype : mushroomcow$mushroomtype1;
      }

      return mushroomcow$mushroomtype2;
   }

   @Override
   public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
      return readyForShearing();
   }

   public static enum MushroomType {
      RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
      BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

      final String type;
      final BlockState blockState;

      private MushroomType(String p_28967_, BlockState p_28968_) {
         this.type = p_28967_;
         this.blockState = p_28968_;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }

      static MushroomCow.MushroomType byType(String p_28977_) {
         for(MushroomCow.MushroomType mushroomcow$mushroomtype : values()) {
            if (mushroomcow$mushroomtype.type.equals(p_28977_)) {
               return mushroomcow$mushroomtype;
            }
         }

         return RED;
      }
   }
}
