package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
   public FurnaceBlockEntity(BlockPos p_155545_, BlockState p_155546_) {
      super(BlockEntityType.FURNACE, p_155545_, p_155546_, RecipeType.SMELTING);
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.furnace");
   }

   protected AbstractContainerMenu createMenu(int p_59293_, Inventory p_59294_) {
      return new FurnaceMenu(p_59293_, p_59294_, this, this.dataAccess);
   }
}