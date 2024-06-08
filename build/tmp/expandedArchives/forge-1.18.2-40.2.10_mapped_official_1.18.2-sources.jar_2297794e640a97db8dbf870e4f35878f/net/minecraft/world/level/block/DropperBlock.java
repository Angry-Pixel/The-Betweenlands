package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropperBlock extends DispenserBlock {
   private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

   public DropperBlock(BlockBehaviour.Properties p_52942_) {
      super(p_52942_);
   }

   protected DispenseItemBehavior getDispenseMethod(ItemStack p_52947_) {
      return DISPENSE_BEHAVIOUR;
   }

   public BlockEntity newBlockEntity(BlockPos p_153179_, BlockState p_153180_) {
      return new DropperBlockEntity(p_153179_, p_153180_);
   }

   protected void dispenseFrom(ServerLevel p_52944_, BlockPos p_52945_) {
      BlockSourceImpl blocksourceimpl = new BlockSourceImpl(p_52944_, p_52945_);
      DispenserBlockEntity dispenserblockentity = blocksourceimpl.getEntity();
      int i = dispenserblockentity.getRandomSlot();
      if (i < 0) {
         p_52944_.levelEvent(1001, p_52945_, 0);
      } else {
         ItemStack itemstack = dispenserblockentity.getItem(i);
         if (!itemstack.isEmpty() && net.minecraftforge.items.VanillaInventoryCodeHooks.dropperInsertHook(p_52944_, p_52945_, dispenserblockentity, i, itemstack)) {
            Direction direction = p_52944_.getBlockState(p_52945_).getValue(FACING);
            Container container = HopperBlockEntity.getContainerAt(p_52944_, p_52945_.relative(direction));
            ItemStack itemstack1;
            if (container == null) {
               itemstack1 = DISPENSE_BEHAVIOUR.dispense(blocksourceimpl, itemstack);
            } else {
               itemstack1 = HopperBlockEntity.addItem(dispenserblockentity, container, itemstack.copy().split(1), direction.getOpposite());
               if (itemstack1.isEmpty()) {
                  itemstack1 = itemstack.copy();
                  itemstack1.shrink(1);
               } else {
                  itemstack1 = itemstack.copy();
               }
            }

            dispenserblockentity.setItem(i, itemstack1);
         }
      }
   }
}
