package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxColoring extends CustomRecipe {
   public ShulkerBoxColoring(ResourceLocation p_44312_) {
      super(p_44312_);
   }

   public boolean matches(CraftingContainer p_44324_, Level p_44325_) {
      int i = 0;
      int j = 0;

      for(int k = 0; k < p_44324_.getContainerSize(); ++k) {
         ItemStack itemstack = p_44324_.getItem(k);
         if (!itemstack.isEmpty()) {
            if (Block.byItem(itemstack.getItem()) instanceof ShulkerBoxBlock) {
               ++i;
            } else {
               if (!itemstack.is(net.minecraftforge.common.Tags.Items.DYES)) {
                  return false;
               }

               ++j;
            }

            if (j > 1 || i > 1) {
               return false;
            }
         }
      }

      return i == 1 && j == 1;
   }

   public ItemStack assemble(CraftingContainer p_44322_) {
      ItemStack itemstack = ItemStack.EMPTY;
      net.minecraft.world.item.DyeColor dyecolor = net.minecraft.world.item.DyeColor.WHITE;

      for(int i = 0; i < p_44322_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_44322_.getItem(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (Block.byItem(item) instanceof ShulkerBoxBlock) {
               itemstack = itemstack1;
            } else {
               net.minecraft.world.item.DyeColor tmp = net.minecraft.world.item.DyeColor.getColor(itemstack1);
               if (tmp != null) dyecolor = tmp;
            }
         }
      }

      ItemStack itemstack2 = ShulkerBoxBlock.getColoredItemStack(dyecolor);
      if (itemstack.hasTag()) {
         itemstack2.setTag(itemstack.getTag().copy());
      }

      return itemstack2;
   }

   public boolean canCraftInDimensions(int p_44314_, int p_44315_) {
      return p_44314_ * p_44315_ >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHULKER_BOX_COLORING;
   }
}
