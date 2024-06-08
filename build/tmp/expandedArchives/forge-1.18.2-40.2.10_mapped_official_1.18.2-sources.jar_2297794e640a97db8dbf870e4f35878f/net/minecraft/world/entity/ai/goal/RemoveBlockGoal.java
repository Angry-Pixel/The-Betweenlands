package net.minecraft.world.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class RemoveBlockGoal extends MoveToBlockGoal {
   private final Block blockToRemove;
   private final Mob removerMob;
   private int ticksSinceReachedGoal;
   private static final int WAIT_AFTER_BLOCK_FOUND = 20;

   public RemoveBlockGoal(Block p_25840_, PathfinderMob p_25841_, double p_25842_, int p_25843_) {
      super(p_25841_, p_25842_, 24, p_25843_);
      this.blockToRemove = p_25840_;
      this.removerMob = p_25841_;
   }

   public boolean canUse() {
      if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.removerMob.level, this.removerMob)) {
         return false;
      } else if (this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else if (this.tryFindBlock()) {
         this.nextStartTick = reducedTickDelay(20);
         return true;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         return false;
      }
   }

   private boolean tryFindBlock() {
      return this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos) ? true : this.findNearestBlock();
   }

   public void stop() {
      super.stop();
      this.removerMob.fallDistance = 1.0F;
   }

   public void start() {
      super.start();
      this.ticksSinceReachedGoal = 0;
   }

   public void playDestroyProgressSound(LevelAccessor p_25847_, BlockPos p_25848_) {
   }

   public void playBreakSound(Level p_25845_, BlockPos p_25846_) {
   }

   public void tick() {
      super.tick();
      Level level = this.removerMob.level;
      BlockPos blockpos = this.removerMob.blockPosition();
      BlockPos blockpos1 = this.getPosWithBlock(blockpos, level);
      Random random = this.removerMob.getRandom();
      if (this.isReachedTarget() && blockpos1 != null) {
         if (this.ticksSinceReachedGoal > 0) {
            Vec3 vec3 = this.removerMob.getDeltaMovement();
            this.removerMob.setDeltaMovement(vec3.x, 0.3D, vec3.z);
            if (!level.isClientSide) {
               double d0 = 0.08D;
               ((ServerLevel)level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.7D, (double)blockpos1.getZ() + 0.5D, 3, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, (double)0.15F);
            }
         }

         if (this.ticksSinceReachedGoal % 2 == 0) {
            Vec3 vec31 = this.removerMob.getDeltaMovement();
            this.removerMob.setDeltaMovement(vec31.x, -0.3D, vec31.z);
            if (this.ticksSinceReachedGoal % 6 == 0) {
               this.playDestroyProgressSound(level, this.blockPos);
            }
         }

         if (this.ticksSinceReachedGoal > 60) {
            level.removeBlock(blockpos1, false);
            if (!level.isClientSide) {
               for(int i = 0; i < 20; ++i) {
                  double d3 = random.nextGaussian() * 0.02D;
                  double d1 = random.nextGaussian() * 0.02D;
                  double d2 = random.nextGaussian() * 0.02D;
                  ((ServerLevel)level).sendParticles(ParticleTypes.POOF, (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY(), (double)blockpos1.getZ() + 0.5D, 1, d3, d1, d2, (double)0.15F);
               }

               this.playBreakSound(level, blockpos1);
            }
         }

         ++this.ticksSinceReachedGoal;
      }

   }

   @Nullable
   private BlockPos getPosWithBlock(BlockPos p_25853_, BlockGetter p_25854_) {
      if (p_25854_.getBlockState(p_25853_).is(this.blockToRemove)) {
         return p_25853_;
      } else {
         BlockPos[] ablockpos = new BlockPos[]{p_25853_.below(), p_25853_.west(), p_25853_.east(), p_25853_.north(), p_25853_.south(), p_25853_.below().below()};

         for(BlockPos blockpos : ablockpos) {
            if (p_25854_.getBlockState(blockpos).is(this.blockToRemove)) {
               return blockpos;
            }
         }

         return null;
      }
   }

   protected boolean isValidTarget(LevelReader p_25850_, BlockPos p_25851_) {
      ChunkAccess chunkaccess = p_25850_.getChunk(SectionPos.blockToSectionCoord(p_25851_.getX()), SectionPos.blockToSectionCoord(p_25851_.getZ()), ChunkStatus.FULL, false);
      if (chunkaccess == null) {
         return false;
      } else {
         net.minecraft.world.level.block.state.BlockState state = chunkaccess.getBlockState(p_25851_);
         if (!(state.canEntityDestroy(p_25850_, p_25851_, this.removerMob) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this.removerMob, p_25851_, state))) return false;
         return state.is(this.blockToRemove) && chunkaccess.getBlockState(p_25851_.above()).isAir() && chunkaccess.getBlockState(p_25851_.above(2)).isAir();
      }
   }
}
