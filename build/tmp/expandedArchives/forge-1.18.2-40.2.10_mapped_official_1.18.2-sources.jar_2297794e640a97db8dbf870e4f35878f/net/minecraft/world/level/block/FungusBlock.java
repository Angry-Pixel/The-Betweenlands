package net.minecraft.world.level.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FungusBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
   private static final double BONEMEAL_SUCCESS_PROBABILITY = 0.4D;
   private final Supplier<Holder<ConfiguredFeature<HugeFungusConfiguration, ?>>> feature;

   public FungusBlock(BlockBehaviour.Properties p_53600_, Supplier<Holder<ConfiguredFeature<HugeFungusConfiguration, ?>>> p_53601_) {
      super(p_53600_);
      this.feature = p_53601_;
   }

   public VoxelShape getShape(BlockState p_53618_, BlockGetter p_53619_, BlockPos p_53620_, CollisionContext p_53621_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_53623_, BlockGetter p_53624_, BlockPos p_53625_) {
      return p_53623_.is(BlockTags.NYLIUM) || p_53623_.is(Blocks.MYCELIUM) || p_53623_.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(p_53623_, p_53624_, p_53625_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_53608_, BlockPos p_53609_, BlockState p_53610_, boolean p_53611_) {
      Block block = (this.feature.get().value().config()).validBaseState.getBlock();
      BlockState blockstate = p_53608_.getBlockState(p_53609_.below());
      return blockstate.is(block);
   }

   public boolean isBonemealSuccess(Level p_53613_, Random p_53614_, BlockPos p_53615_, BlockState p_53616_) {
      return (double)p_53614_.nextFloat() < 0.4D;
   }

   public void performBonemeal(ServerLevel p_53603_, Random p_53604_, BlockPos p_53605_, BlockState p_53606_) {
      net.minecraftforge.event.world.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_53603_, p_53604_, p_53605_, this.feature.get());
      if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY)) return;
      event.getFeature().value().place(p_53603_, p_53603_.getChunkSource().getGenerator(), p_53604_, p_53605_);
   }
}
