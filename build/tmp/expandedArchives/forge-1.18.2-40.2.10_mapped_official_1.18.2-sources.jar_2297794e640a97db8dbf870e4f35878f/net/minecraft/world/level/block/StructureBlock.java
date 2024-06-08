package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.phys.BlockHitResult;

public class StructureBlock extends BaseEntityBlock implements GameMasterBlock {
   public static final EnumProperty<StructureMode> MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;

   public StructureBlock(BlockBehaviour.Properties p_57113_) {
      super(p_57113_);
      this.registerDefaultState(this.stateDefinition.any().setValue(MODE, StructureMode.LOAD));
   }

   public BlockEntity newBlockEntity(BlockPos p_154732_, BlockState p_154733_) {
      return new StructureBlockEntity(p_154732_, p_154733_);
   }

   public InteractionResult use(BlockState p_57128_, Level p_57129_, BlockPos p_57130_, Player p_57131_, InteractionHand p_57132_, BlockHitResult p_57133_) {
      BlockEntity blockentity = p_57129_.getBlockEntity(p_57130_);
      if (blockentity instanceof StructureBlockEntity) {
         return ((StructureBlockEntity)blockentity).usedBy(p_57131_) ? InteractionResult.sidedSuccess(p_57129_.isClientSide) : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void setPlacedBy(Level p_57122_, BlockPos p_57123_, BlockState p_57124_, @Nullable LivingEntity p_57125_, ItemStack p_57126_) {
      if (!p_57122_.isClientSide) {
         if (p_57125_ != null) {
            BlockEntity blockentity = p_57122_.getBlockEntity(p_57123_);
            if (blockentity instanceof StructureBlockEntity) {
               ((StructureBlockEntity)blockentity).createdBy(p_57125_);
            }
         }

      }
   }

   public RenderShape getRenderShape(BlockState p_57144_) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57142_) {
      p_57142_.add(MODE);
   }

   public void neighborChanged(BlockState p_57135_, Level p_57136_, BlockPos p_57137_, Block p_57138_, BlockPos p_57139_, boolean p_57140_) {
      if (p_57136_ instanceof ServerLevel) {
         BlockEntity blockentity = p_57136_.getBlockEntity(p_57137_);
         if (blockentity instanceof StructureBlockEntity) {
            StructureBlockEntity structureblockentity = (StructureBlockEntity)blockentity;
            boolean flag = p_57136_.hasNeighborSignal(p_57137_);
            boolean flag1 = structureblockentity.isPowered();
            if (flag && !flag1) {
               structureblockentity.setPowered(true);
               this.trigger((ServerLevel)p_57136_, structureblockentity);
            } else if (!flag && flag1) {
               structureblockentity.setPowered(false);
            }

         }
      }
   }

   private void trigger(ServerLevel p_57115_, StructureBlockEntity p_57116_) {
      switch(p_57116_.getMode()) {
      case SAVE:
         p_57116_.saveStructure(false);
         break;
      case LOAD:
         p_57116_.loadStructure(p_57115_, false);
         break;
      case CORNER:
         p_57116_.unloadStructure();
      case DATA:
      }

   }
}