package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseItemBehavior {
   private static final Logger LOGGER = LogUtils.getLogger();

   protected ItemStack execute(BlockSource p_123587_, ItemStack p_123588_) {
      this.setSuccess(false);
      Item item = p_123588_.getItem();
      if (item instanceof BlockItem) {
         Direction direction = p_123587_.getBlockState().getValue(DispenserBlock.FACING);
         BlockPos blockpos = p_123587_.getPos().relative(direction);
         Direction direction1 = p_123587_.getLevel().isEmptyBlock(blockpos.below()) ? direction : Direction.UP;

         try {
            this.setSuccess(((BlockItem)item).place(new DirectionalPlaceContext(p_123587_.getLevel(), blockpos, direction, p_123588_, direction1)).consumesAction());
         } catch (Exception exception) {
            LOGGER.error("Error trying to place shulker box at {}", blockpos, exception);
         }
      }

      return p_123588_;
   }
}