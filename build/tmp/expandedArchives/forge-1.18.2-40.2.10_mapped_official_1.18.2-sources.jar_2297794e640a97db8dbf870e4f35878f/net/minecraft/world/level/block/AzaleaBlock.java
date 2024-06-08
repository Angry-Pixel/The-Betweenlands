package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.grower.AzaleaTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AzaleaBlock extends BushBlock implements BonemealableBlock {
   private static final AzaleaTreeGrower TREE_GROWER = new AzaleaTreeGrower();
   private static final VoxelShape SHAPE = Shapes.or(Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D));

   public AzaleaBlock(BlockBehaviour.Properties p_152067_) {
      super(p_152067_);
   }

   public VoxelShape getShape(BlockState p_152084_, BlockGetter p_152085_, BlockPos p_152086_, CollisionContext p_152087_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_152089_, BlockGetter p_152090_, BlockPos p_152091_) {
      return p_152089_.is(Blocks.CLAY) || super.mayPlaceOn(p_152089_, p_152090_, p_152091_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_152074_, BlockPos p_152075_, BlockState p_152076_, boolean p_152077_) {
      return p_152074_.getFluidState(p_152075_.above()).isEmpty();
   }

   public boolean isBonemealSuccess(Level p_152079_, Random p_152080_, BlockPos p_152081_, BlockState p_152082_) {
      return (double)p_152079_.random.nextFloat() < 0.45D;
   }

   public void performBonemeal(ServerLevel p_152069_, Random p_152070_, BlockPos p_152071_, BlockState p_152072_) {
      TREE_GROWER.growTree(p_152069_, p_152069_.getChunkSource().getGenerator(), p_152071_, p_152072_, p_152070_);
   }
}