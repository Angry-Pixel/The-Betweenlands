package net.minecraft.world.item;

import net.minecraft.world.level.block.Block;

public class ItemNameBlockItem extends BlockItem {
   public ItemNameBlockItem(Block p_41579_, Item.Properties p_41580_) {
      super(p_41579_, p_41580_);
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }
}