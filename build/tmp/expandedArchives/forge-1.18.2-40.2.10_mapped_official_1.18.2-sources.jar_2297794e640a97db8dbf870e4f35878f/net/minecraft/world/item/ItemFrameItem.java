package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;

public class ItemFrameItem extends HangingEntityItem {
   public ItemFrameItem(EntityType<? extends HangingEntity> p_150904_, Item.Properties p_150905_) {
      super(p_150904_, p_150905_);
   }

   protected boolean mayPlace(Player p_41551_, Direction p_41552_, ItemStack p_41553_, BlockPos p_41554_) {
      return !p_41551_.level.isOutsideBuildHeight(p_41554_) && p_41551_.mayUseItemAt(p_41554_, p_41552_, p_41553_);
   }
}