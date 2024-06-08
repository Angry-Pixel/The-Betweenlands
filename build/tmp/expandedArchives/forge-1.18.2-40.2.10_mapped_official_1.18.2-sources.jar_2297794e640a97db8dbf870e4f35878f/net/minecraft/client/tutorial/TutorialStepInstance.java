package net.minecraft.client.tutorial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface TutorialStepInstance {
   default void clear() {
   }

   default void tick() {
   }

   default void onInput(Input p_120623_) {
   }

   default void onMouse(double p_120614_, double p_120615_) {
   }

   default void onLookAt(ClientLevel p_120617_, HitResult p_120618_) {
   }

   default void onDestroyBlock(ClientLevel p_120619_, BlockPos p_120620_, BlockState p_120621_, float p_120622_) {
   }

   default void onOpenInventory() {
   }

   default void onGetItem(ItemStack p_120616_) {
   }
}