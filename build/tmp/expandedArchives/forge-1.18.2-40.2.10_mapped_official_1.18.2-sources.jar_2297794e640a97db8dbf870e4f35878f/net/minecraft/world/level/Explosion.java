package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Explosion {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
   private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
   private final boolean fire;
   private final Explosion.BlockInteraction blockInteraction;
   private final Random random = new Random();
   private final Level level;
   private final double x;
   private final double y;
   private final double z;
   @Nullable
   private final Entity source;
   private final float radius;
   private final DamageSource damageSource;
   private final ExplosionDamageCalculator damageCalculator;
   private final List<BlockPos> toBlow = Lists.newArrayList();
   private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();
   private final Vec3 position;

   public Explosion(Level p_151471_, @Nullable Entity p_151472_, double p_151473_, double p_151474_, double p_151475_, float p_151476_) {
      this(p_151471_, p_151472_, p_151473_, p_151474_, p_151475_, p_151476_, false, Explosion.BlockInteraction.DESTROY);
   }

   public Explosion(Level p_46024_, @Nullable Entity p_46025_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, List<BlockPos> p_46030_) {
      this(p_46024_, p_46025_, p_46026_, p_46027_, p_46028_, p_46029_, false, Explosion.BlockInteraction.DESTROY, p_46030_);
   }

   public Explosion(Level p_46041_, @Nullable Entity p_46042_, double p_46043_, double p_46044_, double p_46045_, float p_46046_, boolean p_46047_, Explosion.BlockInteraction p_46048_, List<BlockPos> p_46049_) {
      this(p_46041_, p_46042_, p_46043_, p_46044_, p_46045_, p_46046_, p_46047_, p_46048_);
      this.toBlow.addAll(p_46049_);
   }

   public Explosion(Level p_46032_, @Nullable Entity p_46033_, double p_46034_, double p_46035_, double p_46036_, float p_46037_, boolean p_46038_, Explosion.BlockInteraction p_46039_) {
      this(p_46032_, p_46033_, (DamageSource)null, (ExplosionDamageCalculator)null, p_46034_, p_46035_, p_46036_, p_46037_, p_46038_, p_46039_);
   }

   public Explosion(Level p_46051_, @Nullable Entity p_46052_, @Nullable DamageSource p_46053_, @Nullable ExplosionDamageCalculator p_46054_, double p_46055_, double p_46056_, double p_46057_, float p_46058_, boolean p_46059_, Explosion.BlockInteraction p_46060_) {
      this.level = p_46051_;
      this.source = p_46052_;
      this.radius = p_46058_;
      this.x = p_46055_;
      this.y = p_46056_;
      this.z = p_46057_;
      this.fire = p_46059_;
      this.blockInteraction = p_46060_;
      this.damageSource = p_46053_ == null ? DamageSource.explosion(this) : p_46053_;
      this.damageCalculator = p_46054_ == null ? this.makeDamageCalculator(p_46052_) : p_46054_;
      this.position = new Vec3(this.x, this.y, this.z);
   }

   private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity p_46063_) {
      return (ExplosionDamageCalculator)(p_46063_ == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(p_46063_));
   }

   public static float getSeenPercent(Vec3 p_46065_, Entity p_46066_) {
      AABB aabb = p_46066_.getBoundingBox();
      double d0 = 1.0D / ((aabb.maxX - aabb.minX) * 2.0D + 1.0D);
      double d1 = 1.0D / ((aabb.maxY - aabb.minY) * 2.0D + 1.0D);
      double d2 = 1.0D / ((aabb.maxZ - aabb.minZ) * 2.0D + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
         int i = 0;
         int j = 0;

         for(double d5 = 0.0D; d5 <= 1.0D; d5 += d0) {
            for(double d6 = 0.0D; d6 <= 1.0D; d6 += d1) {
               for(double d7 = 0.0D; d7 <= 1.0D; d7 += d2) {
                  double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                  double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                  double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                  Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);
                  if (p_46066_.level.clip(new ClipContext(vec3, p_46065_, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p_46066_)).getType() == HitResult.Type.MISS) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (float)i / (float)j;
      } else {
         return 0.0F;
      }
   }

   public void explode() {
      this.level.gameEvent(this.source, GameEvent.EXPLODE, new BlockPos(this.x, this.y, this.z));
      Set<BlockPos> set = Sets.newHashSet();
      int i = 16;

      for(int j = 0; j < 16; ++j) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
               if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                  double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                  double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                  double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  d0 /= d3;
                  d1 /= d3;
                  d2 /= d3;
                  float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                  double d4 = this.x;
                  double d6 = this.y;
                  double d8 = this.z;

                  for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                     BlockPos blockpos = new BlockPos(d4, d6, d8);
                     BlockState blockstate = this.level.getBlockState(blockpos);
                     FluidState fluidstate = this.level.getFluidState(blockpos);
                     if (!this.level.isInWorldBounds(blockpos)) {
                        break;
                     }

                     Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
                     if (optional.isPresent()) {
                        f -= (optional.get() + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, f)) {
                        set.add(blockpos);
                     }

                     d4 += d0 * (double)0.3F;
                     d6 += d1 * (double)0.3F;
                     d8 += d2 * (double)0.3F;
                  }
               }
            }
         }
      }

      this.toBlow.addAll(set);
      float f2 = this.radius * 2.0F;
      int k1 = Mth.floor(this.x - (double)f2 - 1.0D);
      int l1 = Mth.floor(this.x + (double)f2 + 1.0D);
      int i2 = Mth.floor(this.y - (double)f2 - 1.0D);
      int i1 = Mth.floor(this.y + (double)f2 + 1.0D);
      int j2 = Mth.floor(this.z - (double)f2 - 1.0D);
      int j1 = Mth.floor(this.z + (double)f2 + 1.0D);
      List<Entity> list = this.level.getEntities(this.source, new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
      net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, f2);
      Vec3 vec3 = new Vec3(this.x, this.y, this.z);

      for(int k2 = 0; k2 < list.size(); ++k2) {
         Entity entity = list.get(k2);
         if (!entity.ignoreExplosion()) {
            double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
            if (d12 <= 1.0D) {
               double d5 = entity.getX() - this.x;
               double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
               double d9 = entity.getZ() - this.z;
               double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
               if (d13 != 0.0D) {
                  d5 /= d13;
                  d7 /= d13;
                  d9 /= d13;
                  double d14 = (double)getSeenPercent(vec3, entity);
                  double d10 = (1.0D - d12) * d14;
                  entity.hurt(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f2 + 1.0D)));
                  double d11 = d10;
                  if (entity instanceof LivingEntity) {
                     d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, d10);
                  }

                  entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
                  if (entity instanceof Player) {
                     Player player = (Player)entity;
                     if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        this.hitPlayers.put(player, new Vec3(d5 * d10, d7 * d10, d9 * d10));
                     }
                  }
               }
            }
         }
      }

   }

   public void finalizeExplosion(boolean p_46076_) {
      if (this.level.isClientSide) {
         this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
      }

      boolean flag = this.blockInteraction != Explosion.BlockInteraction.NONE;
      if (p_46076_) {
         if (!(this.radius < 2.0F) && flag) {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
         } else {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
         }
      }

      if (flag) {
         ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
         Collections.shuffle(this.toBlow, this.level.random);

         for(BlockPos blockpos : this.toBlow) {
            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (!blockstate.isAir()) {
               BlockPos blockpos1 = blockpos.immutable();
               this.level.getProfiler().push("explosion_blocks");
               if (blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerLevel) {
                  BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
                  LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                  if (this.blockInteraction == Explosion.BlockInteraction.DESTROY) {
                     lootcontext$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
                  }

                  blockstate.getDrops(lootcontext$builder).forEach((p_46074_) -> {
                     addBlockDrops(objectarraylist, p_46074_, blockpos1);
                  });
               }

               blockstate.onBlockExploded(this.level, blockpos, this);
               this.level.getProfiler().pop();
            }
         }

         for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
            Block.popResource(this.level, pair.getSecond(), pair.getFirst());
         }
      }

      if (this.fire) {
         for(BlockPos blockpos2 : this.toBlow) {
            if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
               this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
            }
         }
      }

   }

   private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> p_46068_, ItemStack p_46069_, BlockPos p_46070_) {
      int i = p_46068_.size();

      for(int j = 0; j < i; ++j) {
         Pair<ItemStack, BlockPos> pair = p_46068_.get(j);
         ItemStack itemstack = pair.getFirst();
         if (ItemEntity.areMergable(itemstack, p_46069_)) {
            ItemStack itemstack1 = ItemEntity.merge(itemstack, p_46069_, 16);
            p_46068_.set(j, Pair.of(itemstack1, pair.getSecond()));
            if (p_46069_.isEmpty()) {
               return;
            }
         }
      }

      p_46068_.add(Pair.of(p_46069_, p_46070_));
   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public Map<Player, Vec3> getHitPlayers() {
      return this.hitPlayers;
   }

   @Nullable
   public LivingEntity getSourceMob() {
      if (this.source == null) {
         return null;
      } else if (this.source instanceof PrimedTnt) {
         return ((PrimedTnt)this.source).getOwner();
      } else if (this.source instanceof LivingEntity) {
         return (LivingEntity)this.source;
      } else {
         if (this.source instanceof Projectile) {
            Entity entity = ((Projectile)this.source).getOwner();
            if (entity instanceof LivingEntity) {
               return (LivingEntity)entity;
            }
         }

         return null;
      }
   }

   public void clearToBlow() {
      this.toBlow.clear();
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }

   public Vec3 getPosition() {
      return this.position;
   }

   @Nullable
   public Entity getExploder() {
      return this.source;
   }

   public static enum BlockInteraction {
      NONE,
      BREAK,
      DESTROY;
   }
}
