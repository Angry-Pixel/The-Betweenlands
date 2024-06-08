package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FindTreeTutorialStepInstance implements TutorialStepInstance {
   private static final int HINT_DELAY = 6000;
   private static final Set<Block> TREE_BLOCKS = Sets.newHashSet(Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.WARPED_STEM, Blocks.CRIMSON_STEM, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.WARPED_HYPHAE, Blocks.CRIMSON_HYPHAE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.AZALEA_LEAVES, Blocks.FLOWERING_AZALEA_LEAVES);
   private static final Component TITLE = new TranslatableComponent("tutorial.find_tree.title");
   private static final Component DESCRIPTION = new TranslatableComponent("tutorial.find_tree.description");
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public FindTreeTutorialStepInstance(Tutorial p_120496_) {
      this.tutorial = p_120496_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            LocalPlayer localplayer = this.tutorial.getMinecraft().player;
            if (localplayer != null) {
               for(Block block : TREE_BLOCKS) {
                  if (localplayer.getInventory().contains(new ItemStack(block))) {
                     this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                     return;
                  }
               }

               if (hasPunchedTreesPreviously(localplayer)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if (this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
         }

      }
   }

   public void clear() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onLookAt(ClientLevel p_120501_, HitResult p_120502_) {
      if (p_120502_.getType() == HitResult.Type.BLOCK) {
         BlockState blockstate = p_120501_.getBlockState(((BlockHitResult)p_120502_).getBlockPos());
         if (TREE_BLOCKS.contains(blockstate.getBlock())) {
            this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
         }
      }

   }

   public void onGetItem(ItemStack p_120499_) {
      for(Block block : TREE_BLOCKS) {
         if (p_120499_.is(block.asItem())) {
            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
            return;
         }
      }

   }

   public static boolean hasPunchedTreesPreviously(LocalPlayer p_120504_) {
      for(Block block : TREE_BLOCKS) {
         if (p_120504_.getStats().getValue(Stats.BLOCK_MINED.get(block)) > 0) {
            return true;
         }
      }

      return false;
   }
}