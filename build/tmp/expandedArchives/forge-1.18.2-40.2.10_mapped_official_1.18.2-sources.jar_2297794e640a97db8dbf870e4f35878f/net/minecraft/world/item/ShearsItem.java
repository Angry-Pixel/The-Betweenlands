package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties p_43074_) {
      super(p_43074_);
   }

   public boolean mineBlock(ItemStack p_43078_, Level p_43079_, BlockState p_43080_, BlockPos p_43081_, LivingEntity p_43082_) {
      if (!p_43079_.isClientSide && !p_43080_.is(BlockTags.FIRE)) {
         p_43078_.hurtAndBreak(1, p_43082_, (p_43076_) -> {
            p_43076_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return !p_43080_.is(BlockTags.LEAVES) && !p_43080_.is(Blocks.COBWEB) && !p_43080_.is(Blocks.GRASS) && !p_43080_.is(Blocks.FERN) && !p_43080_.is(Blocks.DEAD_BUSH) && !p_43080_.is(Blocks.HANGING_ROOTS) && !p_43080_.is(Blocks.VINE) && !p_43080_.is(Blocks.TRIPWIRE) && !p_43080_.is(BlockTags.WOOL) ? super.mineBlock(p_43078_, p_43079_, p_43080_, p_43081_, p_43082_) : true;
   }

   public boolean isCorrectToolForDrops(BlockState p_43087_) {
      return p_43087_.is(Blocks.COBWEB) || p_43087_.is(Blocks.REDSTONE_WIRE) || p_43087_.is(Blocks.TRIPWIRE);
   }

   public float getDestroySpeed(ItemStack p_43084_, BlockState p_43085_) {
      if (!p_43085_.is(Blocks.COBWEB) && !p_43085_.is(BlockTags.LEAVES)) {
         if (p_43085_.is(BlockTags.WOOL)) {
            return 5.0F;
         } else {
            return !p_43085_.is(Blocks.VINE) && !p_43085_.is(Blocks.GLOW_LICHEN) ? super.getDestroySpeed(p_43084_, p_43085_) : 2.0F;
         }
      } else {
         return 15.0F;
      }
   }

   @Override
   public net.minecraft.world.InteractionResult interactLivingEntity(ItemStack stack, net.minecraft.world.entity.player.Player playerIn, LivingEntity entity, net.minecraft.world.InteractionHand hand) {
      if (entity instanceof net.minecraftforge.common.IForgeShearable target) {
         if (entity.level.isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
         BlockPos pos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
         if (target.isShearable(stack, entity.level, pos)) {
            java.util.List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level, pos,
                    net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE, stack));
            java.util.Random rand = new java.util.Random();
            drops.forEach(d -> {
               net.minecraft.world.entity.item.ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
               ent.setDeltaMovement(ent.getDeltaMovement().add((double)((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(rand.nextFloat() * 0.05F), (double)((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
            });
            stack.hurtAndBreak(1, playerIn, e -> e.broadcastBreakEvent(hand));
         }
         return net.minecraft.world.InteractionResult.SUCCESS;
      }
      return net.minecraft.world.InteractionResult.PASS;
   }

   @Override
   public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
      return net.minecraftforge.common.ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction);
   }

   public InteractionResult useOn(UseOnContext p_186371_) {
      Level level = p_186371_.getLevel();
      BlockPos blockpos = p_186371_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block instanceof GrowingPlantHeadBlock) {
         GrowingPlantHeadBlock growingplantheadblock = (GrowingPlantHeadBlock)block;
         if (!growingplantheadblock.isMaxAge(blockstate)) {
            Player player = p_186371_.getPlayer();
            ItemStack itemstack = p_186371_.getItemInHand();
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            level.playSound(player, blockpos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(blockpos, growingplantheadblock.getMaxAgeState(blockstate));
            if (player != null) {
               itemstack.hurtAndBreak(1, player, (p_186374_) -> {
                  p_186374_.broadcastBreakEvent(p_186371_.getHand());
               });
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return super.useOn(p_186371_);
   }
}
