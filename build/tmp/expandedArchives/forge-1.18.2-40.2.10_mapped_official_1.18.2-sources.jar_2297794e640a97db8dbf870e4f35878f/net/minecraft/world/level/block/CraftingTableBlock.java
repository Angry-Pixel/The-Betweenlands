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
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CraftingTableBlock extends Block {
   private static final Component CONTAINER_TITLE = new TranslatableComponent("container.crafting");

   public CraftingTableBlock(BlockBehaviour.Properties p_52225_) {
      super(p_52225_);
   }

   public InteractionResult use(BlockState p_52233_, Level p_52234_, BlockPos p_52235_, Player p_52236_, InteractionHand p_52237_, BlockHitResult p_52238_) {
      if (p_52234_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         p_52236_.openMenu(p_52233_.getMenuProvider(p_52234_, p_52235_));
         p_52236_.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
         return InteractionResult.CONSUME;
      }
   }

   public MenuProvider getMenuProvider(BlockState p_52240_, Level p_52241_, BlockPos p_52242_) {
      return new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) -> {
         return new CraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(p_52241_, p_52242_));
      }, CONTAINER_TITLE);
   }
}