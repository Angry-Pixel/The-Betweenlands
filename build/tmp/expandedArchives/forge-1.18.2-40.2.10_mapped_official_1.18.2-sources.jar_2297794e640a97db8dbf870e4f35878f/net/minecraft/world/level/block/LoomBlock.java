package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class LoomBlock extends HorizontalDirectionalBlock {
   private static final Component CONTAINER_TITLE = new TranslatableComponent("container.loom");

   public LoomBlock(BlockBehaviour.Properties p_54777_) {
      super(p_54777_);
   }

   public InteractionResult use(BlockState p_54787_, Level p_54788_, BlockPos p_54789_, Player p_54790_, InteractionHand p_54791_, BlockHitResult p_54792_) {
      if (p_54788_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         p_54790_.openMenu(p_54787_.getMenuProvider(p_54788_, p_54789_));
         p_54790_.awardStat(Stats.INTERACT_WITH_LOOM);
         return InteractionResult.CONSUME;
      }
   }

   public MenuProvider getMenuProvider(BlockState p_54796_, Level p_54797_, BlockPos p_54798_) {
      return new SimpleMenuProvider((p_54783_, p_54784_, p_54785_) -> {
         return new LoomMenu(p_54783_, p_54784_, ContainerLevelAccess.create(p_54797_, p_54798_));
      }, CONTAINER_TITLE);
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_54779_) {
      return this.defaultBlockState().setValue(FACING, p_54779_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54794_) {
      p_54794_.add(FACING);
   }
}