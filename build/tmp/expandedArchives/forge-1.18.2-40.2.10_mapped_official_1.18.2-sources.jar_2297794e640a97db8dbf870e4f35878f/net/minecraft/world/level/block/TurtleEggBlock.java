package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock extends Block {
   public static final int MAX_HATCH_LEVEL = 2;
   public static final int MIN_EGGS = 1;
   public static final int MAX_EGGS = 4;
   private static final VoxelShape ONE_EGG_AABB = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

   public TurtleEggBlock(BlockBehaviour.Properties p_57759_) {
      super(p_57759_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, Integer.valueOf(0)).setValue(EGGS, Integer.valueOf(1)));
   }

   public void stepOn(Level p_154857_, BlockPos p_154858_, BlockState p_154859_, Entity p_154860_) {
      this.destroyEgg(p_154857_, p_154859_, p_154858_, p_154860_, 100);
      super.stepOn(p_154857_, p_154858_, p_154859_, p_154860_);
   }

   public void fallOn(Level p_154845_, BlockState p_154846_, BlockPos p_154847_, Entity p_154848_, float p_154849_) {
      if (!(p_154848_ instanceof Zombie)) {
         this.destroyEgg(p_154845_, p_154846_, p_154847_, p_154848_, 3);
      }

      super.fallOn(p_154845_, p_154846_, p_154847_, p_154848_, p_154849_);
   }

   private void destroyEgg(Level p_154851_, BlockState p_154852_, BlockPos p_154853_, Entity p_154854_, int p_154855_) {
      if (this.canDestroyEgg(p_154851_, p_154854_)) {
         if (!p_154851_.isClientSide && p_154851_.random.nextInt(p_154855_) == 0 && p_154852_.is(Blocks.TURTLE_EGG)) {
            this.decreaseEggs(p_154851_, p_154853_, p_154852_);
         }

      }
   }

   private void decreaseEggs(Level p_57792_, BlockPos p_57793_, BlockState p_57794_) {
      p_57792_.playSound((Player)null, p_57793_, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + p_57792_.random.nextFloat() * 0.2F);
      int i = p_57794_.getValue(EGGS);
      if (i <= 1) {
         p_57792_.destroyBlock(p_57793_, false);
      } else {
         p_57792_.setBlock(p_57793_, p_57794_.setValue(EGGS, Integer.valueOf(i - 1)), 2);
         p_57792_.levelEvent(2001, p_57793_, Block.getId(p_57794_));
      }

   }

   public void randomTick(BlockState p_57804_, ServerLevel p_57805_, BlockPos p_57806_, Random p_57807_) {
      if (this.shouldUpdateHatchLevel(p_57805_) && onSand(p_57805_, p_57806_)) {
         int i = p_57804_.getValue(HATCH);
         if (i < 2) {
            p_57805_.playSound((Player)null, p_57806_, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + p_57807_.nextFloat() * 0.2F);
            p_57805_.setBlock(p_57806_, p_57804_.setValue(HATCH, Integer.valueOf(i + 1)), 2);
         } else {
            p_57805_.playSound((Player)null, p_57806_, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + p_57807_.nextFloat() * 0.2F);
            p_57805_.removeBlock(p_57806_, false);

            for(int j = 0; j < p_57804_.getValue(EGGS); ++j) {
               p_57805_.levelEvent(2001, p_57806_, Block.getId(p_57804_));
               Turtle turtle = EntityType.TURTLE.create(p_57805_);
               turtle.setAge(-24000);
               turtle.setHomePos(p_57806_);
               turtle.moveTo((double)p_57806_.getX() + 0.3D + (double)j * 0.2D, (double)p_57806_.getY(), (double)p_57806_.getZ() + 0.3D, 0.0F, 0.0F);
               p_57805_.addFreshEntity(turtle);
            }
         }
      }

   }

   public static boolean onSand(BlockGetter p_57763_, BlockPos p_57764_) {
      return isSand(p_57763_, p_57764_.below());
   }

   public static boolean isSand(BlockGetter p_57801_, BlockPos p_57802_) {
      return p_57801_.getBlockState(p_57802_).is(BlockTags.SAND);
   }

   public void onPlace(BlockState p_57814_, Level p_57815_, BlockPos p_57816_, BlockState p_57817_, boolean p_57818_) {
      if (onSand(p_57815_, p_57816_) && !p_57815_.isClientSide) {
         p_57815_.levelEvent(2005, p_57816_, 0);
      }

   }

   private boolean shouldUpdateHatchLevel(Level p_57766_) {
      float f = p_57766_.getTimeOfDay(1.0F);
      if ((double)f < 0.69D && (double)f > 0.65D) {
         return true;
      } else {
         return p_57766_.random.nextInt(500) == 0;
      }
   }

   public void playerDestroy(Level p_57771_, Player p_57772_, BlockPos p_57773_, BlockState p_57774_, @Nullable BlockEntity p_57775_, ItemStack p_57776_) {
      super.playerDestroy(p_57771_, p_57772_, p_57773_, p_57774_, p_57775_, p_57776_);
      this.decreaseEggs(p_57771_, p_57773_, p_57774_);
   }

   public boolean canBeReplaced(BlockState p_57796_, BlockPlaceContext p_57797_) {
      return !p_57797_.isSecondaryUseActive() && p_57797_.getItemInHand().is(this.asItem()) && p_57796_.getValue(EGGS) < 4 ? true : super.canBeReplaced(p_57796_, p_57797_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_57761_) {
      BlockState blockstate = p_57761_.getLevel().getBlockState(p_57761_.getClickedPos());
      return blockstate.is(this) ? blockstate.setValue(EGGS, Integer.valueOf(Math.min(4, blockstate.getValue(EGGS) + 1))) : super.getStateForPlacement(p_57761_);
   }

   public VoxelShape getShape(BlockState p_57809_, BlockGetter p_57810_, BlockPos p_57811_, CollisionContext p_57812_) {
      return p_57809_.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57799_) {
      p_57799_.add(HATCH, EGGS);
   }

   private boolean canDestroyEgg(Level p_57768_, Entity p_57769_) {
      if (!(p_57769_ instanceof Turtle) && !(p_57769_ instanceof Bat)) {
         if (!(p_57769_ instanceof LivingEntity)) {
            return false;
         } else {
            return p_57769_ instanceof Player || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(p_57768_, p_57769_);
         }
      } else {
         return false;
      }
   }
}
