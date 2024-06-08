package net.minecraft.world.level.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final float AABB_OFFSET = 4.0F;
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   private final WoodType type;

   protected SignBlock(BlockBehaviour.Properties p_56273_, WoodType p_56274_) {
      super(p_56273_);
      this.type = p_56274_;
   }

   public BlockState updateShape(BlockState p_56285_, Direction p_56286_, BlockState p_56287_, LevelAccessor p_56288_, BlockPos p_56289_, BlockPos p_56290_) {
      if (p_56285_.getValue(WATERLOGGED)) {
         p_56288_.scheduleTick(p_56289_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56288_));
      }

      return super.updateShape(p_56285_, p_56286_, p_56287_, p_56288_, p_56289_, p_56290_);
   }

   public VoxelShape getShape(BlockState p_56293_, BlockGetter p_56294_, BlockPos p_56295_, CollisionContext p_56296_) {
      return SHAPE;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockEntity newBlockEntity(BlockPos p_154556_, BlockState p_154557_) {
      return new SignBlockEntity(p_154556_, p_154557_);
   }

   public InteractionResult use(BlockState p_56278_, Level p_56279_, BlockPos p_56280_, Player p_56281_, InteractionHand p_56282_, BlockHitResult p_56283_) {
      ItemStack itemstack = p_56281_.getItemInHand(p_56282_);
      Item item = itemstack.getItem();
      boolean flag = item instanceof DyeItem;
      boolean flag1 = itemstack.is(Items.GLOW_INK_SAC);
      boolean flag2 = itemstack.is(Items.INK_SAC);
      boolean flag3 = (flag1 || flag || flag2) && p_56281_.getAbilities().mayBuild;
      if (p_56279_.isClientSide) {
         return flag3 ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         BlockEntity blockentity = p_56279_.getBlockEntity(p_56280_);
         if (!(blockentity instanceof SignBlockEntity)) {
            return InteractionResult.PASS;
         } else {
            SignBlockEntity signblockentity = (SignBlockEntity)blockentity;
            boolean flag4 = signblockentity.hasGlowingText();
            if ((!flag1 || !flag4) && (!flag2 || flag4)) {
               if (flag3) {
                  boolean flag5;
                  if (flag1) {
                     p_56279_.playSound((Player)null, p_56280_, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     flag5 = signblockentity.setHasGlowingText(true);
                     if (p_56281_ instanceof ServerPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)p_56281_, p_56280_, itemstack);
                     }
                  } else if (flag2) {
                     p_56279_.playSound((Player)null, p_56280_, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     flag5 = signblockentity.setHasGlowingText(false);
                  } else {
                     p_56279_.playSound((Player)null, p_56280_, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     flag5 = signblockentity.setColor(((DyeItem)item).getDyeColor());
                  }

                  if (flag5) {
                     if (!p_56281_.isCreative()) {
                        itemstack.shrink(1);
                     }

                     p_56281_.awardStat(Stats.ITEM_USED.get(item));
                  }
               }

               return signblockentity.executeClickCommands((ServerPlayer)p_56281_) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            } else {
               return InteractionResult.PASS;
            }
         }
      }
   }

   public FluidState getFluidState(BlockState p_56299_) {
      return p_56299_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56299_);
   }

   public WoodType type() {
      return this.type;
   }
}