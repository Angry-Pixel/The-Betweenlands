package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoneycombItem extends Item {
   public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() -> {
      return ImmutableBiMap.<Block, Block>builder().put(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK).put(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER).put(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER).put(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER).put(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER).put(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER).put(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).put(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB).put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB).put(Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS).put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS).build();
   });
   public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> {
      return WAXABLES.get().inverse();
   });

   public HoneycombItem(Item.Properties p_150867_) {
      super(p_150867_);
   }

   public InteractionResult useOn(UseOnContext p_150869_) {
      Level level = p_150869_.getLevel();
      BlockPos blockpos = p_150869_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      return getWaxed(blockstate).map((p_150874_) -> {
         Player player = p_150869_.getPlayer();
         ItemStack itemstack = p_150869_.getItemInHand();
         if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
         }

         itemstack.shrink(1);
         level.setBlock(blockpos, p_150874_, 11);
         level.levelEvent(player, 3003, blockpos, 0);
         return InteractionResult.sidedSuccess(level.isClientSide);
      }).orElse(InteractionResult.PASS);
   }

   public static Optional<BlockState> getWaxed(BlockState p_150879_) {
      return Optional.ofNullable(WAXABLES.get().get(p_150879_.getBlock())).map((p_150877_) -> {
         return p_150877_.withPropertiesOf(p_150879_);
      });
   }
}