package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CartographyTableBlock extends Block {
   private static final Component CONTAINER_TITLE = new TranslatableComponent("container.cartography_table");

   public CartographyTableBlock(BlockBehaviour.Properties p_51349_) {
      super(p_51349_);
   }

   public InteractionResult use(BlockState p_51357_, Level p_51358_, BlockPos p_51359_, Player p_51360_, InteractionHand p_51361_, BlockHitResult p_51362_) {
      if (p_51358_.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         p_51360_.openMenu(p_51357_.getMenuProvider(p_51358_, p_51359_));
         p_51360_.awardStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState p_51364_, Level p_51365_, BlockPos p_51366_) {
      return new SimpleMenuProvider((p_51353_, p_51354_, p_51355_) -> {
         return new CartographyTableMenu(p_51353_, p_51354_, ContainerLevelAccess.create(p_51365_, p_51366_));
      }, CONTAINER_TITLE);
   }
}