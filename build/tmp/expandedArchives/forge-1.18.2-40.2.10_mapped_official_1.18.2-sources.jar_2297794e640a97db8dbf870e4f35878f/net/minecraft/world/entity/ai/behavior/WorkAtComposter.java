package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WorkAtComposter extends WorkAtPoi {
   private static final List<Item> COMPOSTABLE_ITEMS = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS);

   protected void useWorkstation(ServerLevel p_24790_, Villager p_24791_) {
      Optional<GlobalPos> optional = p_24791_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (optional.isPresent()) {
         GlobalPos globalpos = optional.get();
         BlockState blockstate = p_24790_.getBlockState(globalpos.pos());
         if (blockstate.is(Blocks.COMPOSTER)) {
            this.makeBread(p_24791_);
            this.compostItems(p_24790_, p_24791_, globalpos, blockstate);
         }

      }
   }

   private void compostItems(ServerLevel p_24793_, Villager p_24794_, GlobalPos p_24795_, BlockState p_24796_) {
      BlockPos blockpos = p_24795_.pos();
      if (p_24796_.getValue(ComposterBlock.LEVEL) == 8) {
         p_24796_ = ComposterBlock.extractProduce(p_24796_, p_24793_, blockpos);
      }

      int i = 20;
      int j = 10;
      int[] aint = new int[COMPOSTABLE_ITEMS.size()];
      SimpleContainer simplecontainer = p_24794_.getInventory();
      int k = simplecontainer.getContainerSize();
      BlockState blockstate = p_24796_;

      for(int l = k - 1; l >= 0 && i > 0; --l) {
         ItemStack itemstack = simplecontainer.getItem(l);
         int i1 = COMPOSTABLE_ITEMS.indexOf(itemstack.getItem());
         if (i1 != -1) {
            int j1 = itemstack.getCount();
            int k1 = aint[i1] + j1;
            aint[i1] = k1;
            int l1 = Math.min(Math.min(k1 - 10, i), j1);
            if (l1 > 0) {
               i -= l1;

               for(int i2 = 0; i2 < l1; ++i2) {
                  blockstate = ComposterBlock.insertItem(blockstate, p_24793_, itemstack, blockpos);
                  if (blockstate.getValue(ComposterBlock.LEVEL) == 7) {
                     this.spawnComposterFillEffects(p_24793_, p_24796_, blockpos, blockstate);
                     return;
                  }
               }
            }
         }
      }

      this.spawnComposterFillEffects(p_24793_, p_24796_, blockpos, blockstate);
   }

   private void spawnComposterFillEffects(ServerLevel p_24798_, BlockState p_24799_, BlockPos p_24800_, BlockState p_24801_) {
      p_24798_.levelEvent(1500, p_24800_, p_24801_ != p_24799_ ? 1 : 0);
   }

   private void makeBread(Villager p_24803_) {
      SimpleContainer simplecontainer = p_24803_.getInventory();
      if (simplecontainer.countItem(Items.BREAD) <= 36) {
         int i = simplecontainer.countItem(Items.WHEAT);
         int j = 3;
         int k = 3;
         int l = Math.min(3, i / 3);
         if (l != 0) {
            int i1 = l * 3;
            simplecontainer.removeItemType(Items.WHEAT, i1);
            ItemStack itemstack = simplecontainer.addItem(new ItemStack(Items.BREAD, l));
            if (!itemstack.isEmpty()) {
               p_24803_.spawnAtLocation(itemstack, 0.5F);
            }

         }
      }
   }
}