package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public interface DispensibleContainerItem extends net.minecraftforge.common.extensions.IForgeDispensibleContainerItem {
   default void checkExtraContent(@Nullable Player p_150817_, Level p_150818_, ItemStack p_150819_, BlockPos p_150820_) {
   }

   @Deprecated //Forge: use the ItemStack sensitive version
   boolean emptyContents(@Nullable Player p_150821_, Level p_150822_, BlockPos p_150823_, @Nullable BlockHitResult p_150824_);
}
