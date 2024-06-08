package net.minecraft.world.entity.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GlowItemFrame extends ItemFrame {
   public GlowItemFrame(EntityType<? extends ItemFrame> p_149607_, Level p_149608_) {
      super(p_149607_, p_149608_);
   }

   public GlowItemFrame(Level p_149610_, BlockPos p_149611_, Direction p_149612_) {
      super(EntityType.GLOW_ITEM_FRAME, p_149610_, p_149611_, p_149612_);
   }

   public SoundEvent getRemoveItemSound() {
      return SoundEvents.GLOW_ITEM_FRAME_REMOVE_ITEM;
   }

   public SoundEvent getBreakSound() {
      return SoundEvents.GLOW_ITEM_FRAME_BREAK;
   }

   public SoundEvent getPlaceSound() {
      return SoundEvents.GLOW_ITEM_FRAME_PLACE;
   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM;
   }

   public SoundEvent getRotateItemSound() {
      return SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM;
   }

   protected ItemStack getFrameItemStack() {
      return new ItemStack(Items.GLOW_ITEM_FRAME);
   }
}