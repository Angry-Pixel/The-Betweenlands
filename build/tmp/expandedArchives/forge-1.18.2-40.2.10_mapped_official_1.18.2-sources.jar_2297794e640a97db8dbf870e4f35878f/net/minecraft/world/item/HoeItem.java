package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoeItem extends DiggerItem {
   /**
    * @deprecated Forge: This map is patched out of vanilla code.
    * Listen to {@link net.minecraftforge.event.world.BlockEvent.BlockToolModificationEvent}
    * or override {@link net.minecraftforge.common.extensions.IForgeBlock#getToolModifiedState(BlockState, UseOnContext, net.minecraftforge.common.ToolAction, boolean)}.
    */
   @Deprecated
   protected static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT_PATH, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.COARSE_DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())), Blocks.ROOTED_DIRT, Pair.of((p_150861_) -> {
      return true;
   }, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))));

   public HoeItem(Tier p_41336_, int p_41337_, float p_41338_, Item.Properties p_41339_) {
      super((float)p_41337_, p_41338_, p_41336_, BlockTags.MINEABLE_WITH_HOE, p_41339_);
   }

   public InteractionResult useOn(UseOnContext p_41341_) {
      int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(p_41341_);
      if (hook != 0) return hook > 0 ? InteractionResult.SUCCESS : InteractionResult.FAIL;
      Level level = p_41341_.getLevel();
      BlockPos blockpos = p_41341_.getClickedPos();
      BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(p_41341_, net.minecraftforge.common.ToolActions.HOE_TILL, false);
      Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(toolModifiedState));
      if (pair == null) {
         return InteractionResult.PASS;
      } else {
         Predicate<UseOnContext> predicate = pair.getFirst();
         Consumer<UseOnContext> consumer = pair.getSecond();
         if (predicate.test(p_41341_)) {
            Player player = p_41341_.getPlayer();
            level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide) {
               consumer.accept(p_41341_);
               if (player != null) {
                  p_41341_.getItemInHand().hurtAndBreak(1, player, (p_150845_) -> {
                     p_150845_.broadcastBreakEvent(p_41341_.getHand());
                  });
               }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static Consumer<UseOnContext> changeIntoState(BlockState p_150859_) {
      return (p_150848_) -> {
         p_150848_.getLevel().setBlock(p_150848_.getClickedPos(), p_150859_, 11);
      };
   }

   public static Consumer<UseOnContext> changeIntoStateAndDropItem(BlockState p_150850_, ItemLike p_150851_) {
      return (p_150855_) -> {
         p_150855_.getLevel().setBlock(p_150855_.getClickedPos(), p_150850_, 11);
         Block.popResourceFromFace(p_150855_.getLevel(), p_150855_.getClickedPos(), p_150855_.getClickedFace(), new ItemStack(p_150851_));
      };
   }

   public static boolean onlyIfAirAbove(UseOnContext p_150857_) {
      return p_150857_.getClickedFace() != Direction.DOWN && p_150857_.getLevel().getBlockState(p_150857_.getClickedPos().above()).isAir();
   }

   @Override
   public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
      return net.minecraftforge.common.ToolActions.DEFAULT_HOE_ACTIONS.contains(toolAction);
   }
}
