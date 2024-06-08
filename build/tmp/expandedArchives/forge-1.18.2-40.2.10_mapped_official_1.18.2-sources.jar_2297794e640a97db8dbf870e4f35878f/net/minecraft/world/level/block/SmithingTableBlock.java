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
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SmithingTableBlock extends CraftingTableBlock {
   private static final Component CONTAINER_TITLE = new TranslatableComponent("container.upgrade");

   public SmithingTableBlock(BlockBehaviour.Properties p_56420_) {
      super(p_56420_);
   }

   public MenuProvider getMenuProvider(BlockState p_56435_, Level p_56436_, BlockPos p_56437_) {
      return new SimpleMenuProvider((p_56424_, p_56425_, p_56426_) -> {
         return new SmithingMenu(p_56424_, p_56425_, ContainerLevelAccess.create(p_56436_, p_56437_));
      }, CONTAINER_TITLE);
   }

   public InteractionResult use(BlockState p_56428_, Level p_56429_, BlockPos p_56430_, Player p_56431_, InteractionHand p_56432_, BlockHitResult p_56433_) {
      if (p_56429_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         p_56431_.openMenu(p_56428_.getMenuProvider(p_56429_, p_56430_));
         p_56431_.awardStat(Stats.INTERACT_WITH_SMITHING_TABLE);
         return InteractionResult.CONSUME;
      }
   }
}