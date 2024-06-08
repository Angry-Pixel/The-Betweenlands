package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   public JukeboxBlock(BlockBehaviour.Properties p_54257_) {
      super(p_54257_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)));
   }

   public void setPlacedBy(Level p_54264_, BlockPos p_54265_, BlockState p_54266_, @Nullable LivingEntity p_54267_, ItemStack p_54268_) {
      super.setPlacedBy(p_54264_, p_54265_, p_54266_, p_54267_, p_54268_);
      CompoundTag compoundtag = BlockItem.getBlockEntityData(p_54268_);
      if (compoundtag != null && compoundtag.contains("RecordItem")) {
         p_54264_.setBlock(p_54265_, p_54266_.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }

   }

   public InteractionResult use(BlockState p_54281_, Level p_54282_, BlockPos p_54283_, Player p_54284_, InteractionHand p_54285_, BlockHitResult p_54286_) {
      if (p_54281_.getValue(HAS_RECORD)) {
         this.dropRecording(p_54282_, p_54283_);
         p_54281_ = p_54281_.setValue(HAS_RECORD, Boolean.valueOf(false));
         p_54282_.setBlock(p_54283_, p_54281_, 2);
         return InteractionResult.sidedSuccess(p_54282_.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public void setRecord(LevelAccessor p_54270_, BlockPos p_54271_, BlockState p_54272_, ItemStack p_54273_) {
      BlockEntity blockentity = p_54270_.getBlockEntity(p_54271_);
      if (blockentity instanceof JukeboxBlockEntity) {
         ((JukeboxBlockEntity)blockentity).setRecord(p_54273_.copy());
         p_54270_.setBlock(p_54271_, p_54272_.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   private void dropRecording(Level p_54261_, BlockPos p_54262_) {
      if (!p_54261_.isClientSide) {
         BlockEntity blockentity = p_54261_.getBlockEntity(p_54262_);
         if (blockentity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxblockentity = (JukeboxBlockEntity)blockentity;
            ItemStack itemstack = jukeboxblockentity.getRecord();
            if (!itemstack.isEmpty()) {
               p_54261_.levelEvent(1010, p_54262_, 0);
               jukeboxblockentity.clearContent();
               float f = 0.7F;
               double d0 = (double)(p_54261_.random.nextFloat() * 0.7F) + (double)0.15F;
               double d1 = (double)(p_54261_.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
               double d2 = (double)(p_54261_.random.nextFloat() * 0.7F) + (double)0.15F;
               ItemStack itemstack1 = itemstack.copy();
               ItemEntity itementity = new ItemEntity(p_54261_, (double)p_54262_.getX() + d0, (double)p_54262_.getY() + d1, (double)p_54262_.getZ() + d2, itemstack1);
               itementity.setDefaultPickUpDelay();
               p_54261_.addFreshEntity(itementity);
            }
         }
      }
   }

   public void onRemove(BlockState p_54288_, Level p_54289_, BlockPos p_54290_, BlockState p_54291_, boolean p_54292_) {
      if (!p_54288_.is(p_54291_.getBlock())) {
         this.dropRecording(p_54289_, p_54290_);
         super.onRemove(p_54288_, p_54289_, p_54290_, p_54291_, p_54292_);
      }
   }

   public BlockEntity newBlockEntity(BlockPos p_153451_, BlockState p_153452_) {
      return new JukeboxBlockEntity(p_153451_, p_153452_);
   }

   public boolean hasAnalogOutputSignal(BlockState p_54275_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_54277_, Level p_54278_, BlockPos p_54279_) {
      BlockEntity blockentity = p_54278_.getBlockEntity(p_54279_);
      if (blockentity instanceof JukeboxBlockEntity) {
         Item item = ((JukeboxBlockEntity)blockentity).getRecord().getItem();
         if (item instanceof RecordItem) {
            return ((RecordItem)item).getAnalogOutput();
         }
      }

      return 0;
   }

   public RenderShape getRenderShape(BlockState p_54296_) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54294_) {
      p_54294_.add(HAS_RECORD);
   }
}