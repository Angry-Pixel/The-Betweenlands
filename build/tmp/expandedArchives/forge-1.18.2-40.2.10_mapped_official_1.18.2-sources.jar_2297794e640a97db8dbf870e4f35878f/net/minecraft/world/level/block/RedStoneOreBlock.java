package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RedStoneOreBlock extends Block {
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedStoneOreBlock(BlockBehaviour.Properties p_55453_) {
      super(p_55453_);
      this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   public void attack(BlockState p_55467_, Level p_55468_, BlockPos p_55469_, Player p_55470_) {
      interact(p_55467_, p_55468_, p_55469_);
      super.attack(p_55467_, p_55468_, p_55469_, p_55470_);
   }

   public void stepOn(Level p_154299_, BlockPos p_154300_, BlockState p_154301_, Entity p_154302_) {
      interact(p_154301_, p_154299_, p_154300_);
      super.stepOn(p_154299_, p_154300_, p_154301_, p_154302_);
   }

   public InteractionResult use(BlockState p_55472_, Level p_55473_, BlockPos p_55474_, Player p_55475_, InteractionHand p_55476_, BlockHitResult p_55477_) {
      if (p_55473_.isClientSide) {
         spawnParticles(p_55473_, p_55474_);
      } else {
         interact(p_55472_, p_55473_, p_55474_);
      }

      ItemStack itemstack = p_55475_.getItemInHand(p_55476_);
      return itemstack.getItem() instanceof BlockItem && (new BlockPlaceContext(p_55475_, p_55476_, itemstack, p_55477_)).canPlace() ? InteractionResult.PASS : InteractionResult.SUCCESS;
   }

   private static void interact(BlockState p_55493_, Level p_55494_, BlockPos p_55495_) {
      spawnParticles(p_55494_, p_55495_);
      if (!p_55493_.getValue(LIT)) {
         p_55494_.setBlock(p_55495_, p_55493_.setValue(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public boolean isRandomlyTicking(BlockState p_55486_) {
      return p_55486_.getValue(LIT);
   }

   public void randomTick(BlockState p_55488_, ServerLevel p_55489_, BlockPos p_55490_, Random p_55491_) {
      if (p_55488_.getValue(LIT)) {
         p_55489_.setBlock(p_55490_, p_55488_.setValue(LIT, Boolean.valueOf(false)), 3);
      }

   }

   public void spawnAfterBreak(BlockState p_55462_, ServerLevel p_55463_, BlockPos p_55464_, ItemStack p_55465_) {
      super.spawnAfterBreak(p_55462_, p_55463_, p_55464_, p_55465_);
   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, int fortune, int silktouch) {
      return silktouch == 0 ? 1 + RANDOM.nextInt(5) : 0;
   }

   public void animateTick(BlockState p_55479_, Level p_55480_, BlockPos p_55481_, Random p_55482_) {
      if (p_55479_.getValue(LIT)) {
         spawnParticles(p_55480_, p_55481_);
      }

   }

   private static void spawnParticles(Level p_55455_, BlockPos p_55456_) {
      double d0 = 0.5625D;
      Random random = p_55455_.random;

      for(Direction direction : Direction.values()) {
         BlockPos blockpos = p_55456_.relative(direction);
         if (!p_55455_.getBlockState(blockpos).isSolidRender(p_55455_, blockpos)) {
            Direction.Axis direction$axis = direction.getAxis();
            double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double)direction.getStepX() : (double)random.nextFloat();
            double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double)direction.getStepY() : (double)random.nextFloat();
            double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double)direction.getStepZ() : (double)random.nextFloat();
            p_55455_.addParticle(DustParticleOptions.REDSTONE, (double)p_55456_.getX() + d1, (double)p_55456_.getY() + d2, (double)p_55456_.getZ() + d3, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55484_) {
      p_55484_.add(LIT);
   }
}
