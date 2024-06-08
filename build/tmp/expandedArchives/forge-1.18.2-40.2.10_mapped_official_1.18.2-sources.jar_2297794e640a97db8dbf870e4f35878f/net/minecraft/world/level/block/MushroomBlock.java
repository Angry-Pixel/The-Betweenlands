package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends BushBlock implements BonemealableBlock {
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Supplier<Holder<? extends ConfiguredFeature<?, ?>>> featureSupplier;

   public MushroomBlock(BlockBehaviour.Properties p_153983_, Supplier<Holder<? extends ConfiguredFeature<?, ?>>> p_153984_) {
      super(p_153983_);
      this.featureSupplier = p_153984_;
   }

   public VoxelShape getShape(BlockState p_54889_, BlockGetter p_54890_, BlockPos p_54891_, CollisionContext p_54892_) {
      return SHAPE;
   }

   public void randomTick(BlockState p_54884_, ServerLevel p_54885_, BlockPos p_54886_, Random p_54887_) {
      if (p_54887_.nextInt(25) == 0) {
         int i = 5;
         int j = 4;

         for(BlockPos blockpos : BlockPos.betweenClosed(p_54886_.offset(-4, -1, -4), p_54886_.offset(4, 1, 4))) {
            if (p_54885_.getBlockState(blockpos).is(this)) {
               --i;
               if (i <= 0) {
                  return;
               }
            }
         }

         BlockPos blockpos1 = p_54886_.offset(p_54887_.nextInt(3) - 1, p_54887_.nextInt(2) - p_54887_.nextInt(2), p_54887_.nextInt(3) - 1);

         for(int k = 0; k < 4; ++k) {
            if (p_54885_.isEmptyBlock(blockpos1) && p_54884_.canSurvive(p_54885_, blockpos1)) {
               p_54886_ = blockpos1;
            }

            blockpos1 = p_54886_.offset(p_54887_.nextInt(3) - 1, p_54887_.nextInt(2) - p_54887_.nextInt(2), p_54887_.nextInt(3) - 1);
         }

         if (p_54885_.isEmptyBlock(blockpos1) && p_54884_.canSurvive(p_54885_, blockpos1)) {
            p_54885_.setBlock(blockpos1, p_54884_, 2);
         }
      }

   }

   protected boolean mayPlaceOn(BlockState p_54894_, BlockGetter p_54895_, BlockPos p_54896_) {
      return p_54894_.isSolidRender(p_54895_, p_54896_);
   }

   public boolean canSurvive(BlockState p_54880_, LevelReader p_54881_, BlockPos p_54882_) {
      BlockPos blockpos = p_54882_.below();
      BlockState blockstate = p_54881_.getBlockState(blockpos);
      if (blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
         return true;
      } else {
         return p_54881_.getRawBrightness(p_54882_, 0) < 13 && blockstate.canSustainPlant(p_54881_, blockpos, net.minecraft.core.Direction.UP, this);
      }
   }

   public boolean growMushroom(ServerLevel p_54860_, BlockPos p_54861_, BlockState p_54862_, Random p_54863_) {
      net.minecraftforge.event.world.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_54860_, p_54863_, p_54861_, this.featureSupplier.get());
      if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY)) return false;
      p_54860_.removeBlock(p_54861_, false);
      if (event.getFeature().value().place(p_54860_, p_54860_.getChunkSource().getGenerator(), p_54863_, p_54861_)) {
         return true;
      } else {
         p_54860_.setBlock(p_54861_, p_54862_, 3);
         return false;
      }
   }

   public boolean isValidBonemealTarget(BlockGetter p_54870_, BlockPos p_54871_, BlockState p_54872_, boolean p_54873_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_54875_, Random p_54876_, BlockPos p_54877_, BlockState p_54878_) {
      return (double)p_54876_.nextFloat() < 0.4D;
   }

   public void performBonemeal(ServerLevel p_54865_, Random p_54866_, BlockPos p_54867_, BlockState p_54868_) {
      this.growMushroom(p_54865_, p_54867_, p_54868_, p_54866_);
   }
}
