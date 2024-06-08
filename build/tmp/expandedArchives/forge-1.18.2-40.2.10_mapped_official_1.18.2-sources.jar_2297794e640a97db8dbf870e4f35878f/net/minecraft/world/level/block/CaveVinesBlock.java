package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class CaveVinesBlock extends GrowingPlantHeadBlock implements BonemealableBlock, CaveVines {
   private static final float CHANCE_OF_BERRIES_ON_GROWTH = 0.11F;

   public CaveVinesBlock(BlockBehaviour.Properties p_152959_) {
      super(p_152959_, Direction.DOWN, SHAPE, false, 0.1D);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(BERRIES, Boolean.valueOf(false)));
   }

   protected int getBlocksToGrowWhenBonemealed(Random p_152995_) {
      return 1;
   }

   protected boolean canGrowInto(BlockState p_152998_) {
      return p_152998_.isAir();
   }

   protected Block getBodyBlock() {
      return Blocks.CAVE_VINES_PLANT;
   }

   protected BlockState updateBodyAfterConvertedFromHead(BlockState p_152987_, BlockState p_152988_) {
      return p_152988_.setValue(BERRIES, p_152987_.getValue(BERRIES));
   }

   protected BlockState getGrowIntoState(BlockState p_152990_, Random p_152991_) {
      return super.getGrowIntoState(p_152990_, p_152991_).setValue(BERRIES, Boolean.valueOf(p_152991_.nextFloat() < 0.11F));
   }

   public ItemStack getCloneItemStack(BlockGetter p_152966_, BlockPos p_152967_, BlockState p_152968_) {
      return new ItemStack(Items.GLOW_BERRIES);
   }

   public InteractionResult use(BlockState p_152980_, Level p_152981_, BlockPos p_152982_, Player p_152983_, InteractionHand p_152984_, BlockHitResult p_152985_) {
      return CaveVines.use(p_152980_, p_152981_, p_152982_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_152993_) {
      super.createBlockStateDefinition(p_152993_);
      p_152993_.add(BERRIES);
   }

   public boolean isValidBonemealTarget(BlockGetter p_152970_, BlockPos p_152971_, BlockState p_152972_, boolean p_152973_) {
      return !p_152972_.getValue(BERRIES);
   }

   public boolean isBonemealSuccess(Level p_152975_, Random p_152976_, BlockPos p_152977_, BlockState p_152978_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_152961_, Random p_152962_, BlockPos p_152963_, BlockState p_152964_) {
      p_152961_.setBlock(p_152963_, p_152964_.setValue(BERRIES, Boolean.valueOf(true)), 2);
   }
}