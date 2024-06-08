package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetBerryBushBlock extends BushBlock implements BonemealableBlock {
   private static final float HURT_SPEED_THRESHOLD = 0.003F;
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
   private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public SweetBerryBushBlock(BlockBehaviour.Properties p_57249_) {
      super(p_57249_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public ItemStack getCloneItemStack(BlockGetter p_57256_, BlockPos p_57257_, BlockState p_57258_) {
      return new ItemStack(Items.SWEET_BERRIES);
   }

   public VoxelShape getShape(BlockState p_57291_, BlockGetter p_57292_, BlockPos p_57293_, CollisionContext p_57294_) {
      if (p_57291_.getValue(AGE) == 0) {
         return SAPLING_SHAPE;
      } else {
         return p_57291_.getValue(AGE) < 3 ? MID_GROWTH_SHAPE : super.getShape(p_57291_, p_57292_, p_57293_, p_57294_);
      }
   }

   public boolean isRandomlyTicking(BlockState p_57284_) {
      return p_57284_.getValue(AGE) < 3;
   }

   public void randomTick(BlockState p_57286_, ServerLevel p_57287_, BlockPos p_57288_, Random p_57289_) {
      int i = p_57286_.getValue(AGE);
      if (i < 3 && p_57287_.getRawBrightness(p_57288_.above(), 0) >= 9 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_57287_, p_57288_, p_57286_,p_57289_.nextInt(5) == 0)) {
         p_57287_.setBlock(p_57288_, p_57286_.setValue(AGE, Integer.valueOf(i + 1)), 2);
         net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_57287_, p_57288_, p_57286_);
      }

   }

   public void entityInside(BlockState p_57270_, Level p_57271_, BlockPos p_57272_, Entity p_57273_) {
      if (p_57273_ instanceof LivingEntity && p_57273_.getType() != EntityType.FOX && p_57273_.getType() != EntityType.BEE) {
         p_57273_.makeStuckInBlock(p_57270_, new Vec3((double)0.8F, 0.75D, (double)0.8F));
         if (!p_57271_.isClientSide && p_57270_.getValue(AGE) > 0 && (p_57273_.xOld != p_57273_.getX() || p_57273_.zOld != p_57273_.getZ())) {
            double d0 = Math.abs(p_57273_.getX() - p_57273_.xOld);
            double d1 = Math.abs(p_57273_.getZ() - p_57273_.zOld);
            if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
               p_57273_.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0F);
            }
         }

      }
   }

   public InteractionResult use(BlockState p_57275_, Level p_57276_, BlockPos p_57277_, Player p_57278_, InteractionHand p_57279_, BlockHitResult p_57280_) {
      int i = p_57275_.getValue(AGE);
      boolean flag = i == 3;
      if (!flag && p_57278_.getItemInHand(p_57279_).is(Items.BONE_MEAL)) {
         return InteractionResult.PASS;
      } else if (i > 1) {
         int j = 1 + p_57276_.random.nextInt(2);
         popResource(p_57276_, p_57277_, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
         p_57276_.playSound((Player)null, p_57277_, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + p_57276_.random.nextFloat() * 0.4F);
         p_57276_.setBlock(p_57277_, p_57275_.setValue(AGE, Integer.valueOf(1)), 2);
         return InteractionResult.sidedSuccess(p_57276_.isClientSide);
      } else {
         return super.use(p_57275_, p_57276_, p_57277_, p_57278_, p_57279_, p_57280_);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57282_) {
      p_57282_.add(AGE);
   }

   public boolean isValidBonemealTarget(BlockGetter p_57260_, BlockPos p_57261_, BlockState p_57262_, boolean p_57263_) {
      return p_57262_.getValue(AGE) < 3;
   }

   public boolean isBonemealSuccess(Level p_57265_, Random p_57266_, BlockPos p_57267_, BlockState p_57268_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_57251_, Random p_57252_, BlockPos p_57253_, BlockState p_57254_) {
      int i = Math.min(3, p_57254_.getValue(AGE) + 1);
      p_57251_.setBlock(p_57253_, p_57254_.setValue(AGE, Integer.valueOf(i)), 2);
   }
}
