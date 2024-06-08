package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FletchingTableBlock extends CraftingTableBlock {
   public FletchingTableBlock(BlockBehaviour.Properties p_53499_) {
      super(p_53499_);
   }

   public InteractionResult use(BlockState p_53501_, Level p_53502_, BlockPos p_53503_, Player p_53504_, InteractionHand p_53505_, BlockHitResult p_53506_) {
      return InteractionResult.PASS;
   }
}