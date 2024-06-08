package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SolidBucketItem extends BlockItem implements DispensibleContainerItem {
   private final SoundEvent placeSound;

   public SolidBucketItem(Block p_151187_, SoundEvent p_151188_, Item.Properties p_151189_) {
      super(p_151187_, p_151189_);
      this.placeSound = p_151188_;
   }

   public InteractionResult useOn(UseOnContext p_151197_) {
      InteractionResult interactionresult = super.useOn(p_151197_);
      Player player = p_151197_.getPlayer();
      if (interactionresult.consumesAction() && player != null && !player.isCreative()) {
         InteractionHand interactionhand = p_151197_.getHand();
         player.setItemInHand(interactionhand, Items.BUCKET.getDefaultInstance());
      }

      return interactionresult;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   protected SoundEvent getPlaceSound(BlockState p_151199_) {
      return this.placeSound;
   }

   public boolean emptyContents(@Nullable Player p_151192_, Level p_151193_, BlockPos p_151194_, @Nullable BlockHitResult p_151195_) {
      if (p_151193_.isInWorldBounds(p_151194_) && p_151193_.isEmptyBlock(p_151194_)) {
         if (!p_151193_.isClientSide) {
            p_151193_.setBlock(p_151194_, this.getBlock().defaultBlockState(), 3);
         }

         p_151193_.playSound(p_151192_, p_151194_, this.placeSound, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }
}