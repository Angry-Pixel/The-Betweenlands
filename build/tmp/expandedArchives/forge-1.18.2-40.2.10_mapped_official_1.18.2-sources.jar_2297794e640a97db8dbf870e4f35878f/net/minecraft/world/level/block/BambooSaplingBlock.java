package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooSaplingBlock extends Block implements BonemealableBlock {
   protected static final float SAPLING_AABB_OFFSET = 4.0F;
   protected static final VoxelShape SAPLING_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

   public BambooSaplingBlock(BlockBehaviour.Properties p_48957_) {
      super(p_48957_);
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XZ;
   }

   public VoxelShape getShape(BlockState p_49003_, BlockGetter p_49004_, BlockPos p_49005_, CollisionContext p_49006_) {
      Vec3 vec3 = p_49003_.getOffset(p_49004_, p_49005_);
      return SAPLING_SHAPE.move(vec3.x, vec3.y, vec3.z);
   }

   public void randomTick(BlockState p_48998_, ServerLevel p_48999_, BlockPos p_49000_, Random p_49001_) {
      if (p_49001_.nextInt(3) == 0 && p_48999_.isEmptyBlock(p_49000_.above()) && p_48999_.getRawBrightness(p_49000_.above(), 0) >= 9) {
         this.growBamboo(p_48999_, p_49000_);
      }

   }

   public boolean canSurvive(BlockState p_48986_, LevelReader p_48987_, BlockPos p_48988_) {
      return p_48987_.getBlockState(p_48988_.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState p_48990_, Direction p_48991_, BlockState p_48992_, LevelAccessor p_48993_, BlockPos p_48994_, BlockPos p_48995_) {
      if (!p_48990_.canSurvive(p_48993_, p_48994_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_48991_ == Direction.UP && p_48992_.is(Blocks.BAMBOO)) {
            p_48993_.setBlock(p_48994_, Blocks.BAMBOO.defaultBlockState(), 2);
         }

         return super.updateShape(p_48990_, p_48991_, p_48992_, p_48993_, p_48994_, p_48995_);
      }
   }

   public ItemStack getCloneItemStack(BlockGetter p_48964_, BlockPos p_48965_, BlockState p_48966_) {
      return new ItemStack(Items.BAMBOO);
   }

   public boolean isValidBonemealTarget(BlockGetter p_48968_, BlockPos p_48969_, BlockState p_48970_, boolean p_48971_) {
      return p_48968_.getBlockState(p_48969_.above()).isAir();
   }

   public boolean isBonemealSuccess(Level p_48976_, Random p_48977_, BlockPos p_48978_, BlockState p_48979_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_48959_, Random p_48960_, BlockPos p_48961_, BlockState p_48962_) {
      this.growBamboo(p_48959_, p_48961_);
   }

   public float getDestroyProgress(BlockState p_48981_, Player p_48982_, BlockGetter p_48983_, BlockPos p_48984_) {
      return p_48982_.getMainHandItem().canPerformAction(net.minecraftforge.common.ToolActions.SWORD_DIG) ? 1.0F : super.getDestroyProgress(p_48981_, p_48982_, p_48983_, p_48984_);
   }

   protected void growBamboo(Level p_48973_, BlockPos p_48974_) {
      p_48973_.setBlock(p_48974_.above(), Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
   }
}
