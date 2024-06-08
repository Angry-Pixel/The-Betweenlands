package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class AxeItem extends DiggerItem {
   protected static final Map<Block, Block> STRIPPABLES = (new Builder<Block, Block>()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).build();

   public AxeItem(Tier p_40521_, float p_40522_, float p_40523_, Item.Properties p_40524_) {
      super(p_40522_, p_40523_, p_40521_, BlockTags.MINEABLE_WITH_AXE, p_40524_);
   }

   public InteractionResult useOn(UseOnContext p_40529_) {
      Level level = p_40529_.getLevel();
      BlockPos blockpos = p_40529_.getClickedPos();
      Player player = p_40529_.getPlayer();
      BlockState blockstate = level.getBlockState(blockpos);
      Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(p_40529_, net.minecraftforge.common.ToolActions.AXE_STRIP, false));
      Optional<BlockState> optional1 = optional.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(p_40529_, net.minecraftforge.common.ToolActions.AXE_SCRAPE, false));
      Optional<BlockState> optional2 = optional.isPresent() || optional1.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(p_40529_, net.minecraftforge.common.ToolActions.AXE_WAX_OFF, false));
      ItemStack itemstack = p_40529_.getItemInHand();
      Optional<BlockState> optional3 = Optional.empty();
      if (optional.isPresent()) {
         level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
         optional3 = optional;
      } else if (optional1.isPresent()) {
         level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.levelEvent(player, 3005, blockpos, 0);
         optional3 = optional1;
      } else if (optional2.isPresent()) {
         level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.levelEvent(player, 3004, blockpos, 0);
         optional3 = optional2;
      }

      if (optional3.isPresent()) {
         if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
         }

         level.setBlock(blockpos, optional3.get(), 11);
         if (player != null) {
            itemstack.hurtAndBreak(1, player, (p_150686_) -> {
               p_150686_.broadcastBreakEvent(p_40529_.getHand());
            });
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   @javax.annotation.Nullable
   public static BlockState getAxeStrippingState(BlockState originalState) {
      Block block = STRIPPABLES.get(originalState.getBlock());
      return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)) : null;
   }

   private Optional<BlockState> getStripped(BlockState p_150691_) {
      return Optional.ofNullable(STRIPPABLES.get(p_150691_.getBlock())).map((p_150689_) -> {
         return p_150689_.defaultBlockState().setValue(RotatedPillarBlock.AXIS, p_150691_.getValue(RotatedPillarBlock.AXIS));
      });
   }

   @Override
   public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
      return net.minecraftforge.common.ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
   }
}
