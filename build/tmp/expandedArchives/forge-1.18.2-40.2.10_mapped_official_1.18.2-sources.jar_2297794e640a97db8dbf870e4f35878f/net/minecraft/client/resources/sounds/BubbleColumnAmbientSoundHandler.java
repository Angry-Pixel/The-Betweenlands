package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubbleColumnAmbientSoundHandler implements AmbientSoundHandler {
   private final LocalPlayer player;
   private boolean wasInBubbleColumn;
   private boolean firstTick = true;

   public BubbleColumnAmbientSoundHandler(LocalPlayer p_119666_) {
      this.player = p_119666_;
   }

   public void tick() {
      Level level = this.player.level;
      BlockState blockstate = level.getBlockStatesIfLoaded(this.player.getBoundingBox().inflate(0.0D, (double)-0.4F, 0.0D).deflate(1.0E-6D)).filter((p_119669_) -> {
         return p_119669_.is(Blocks.BUBBLE_COLUMN);
      }).findFirst().orElse((BlockState)null);
      if (blockstate != null) {
         if (!this.wasInBubbleColumn && !this.firstTick && blockstate.is(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
            boolean flag = blockstate.getValue(BubbleColumnBlock.DRAG_DOWN);
            if (flag) {
               this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
            } else {
               this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
            }
         }

         this.wasInBubbleColumn = true;
      } else {
         this.wasInBubbleColumn = false;
      }

      this.firstTick = false;
   }
}