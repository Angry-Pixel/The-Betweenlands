package net.minecraft.core.cauldron;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface CauldronInteraction {
   Map<Item, CauldronInteraction> EMPTY = newInteractionMap();
   Map<Item, CauldronInteraction> WATER = newInteractionMap();
   Map<Item, CauldronInteraction> LAVA = newInteractionMap();
   Map<Item, CauldronInteraction> POWDER_SNOW = newInteractionMap();
   CauldronInteraction FILL_WATER = (p_175683_, p_175684_, p_175685_, p_175686_, p_175687_, p_175688_) -> {
      return emptyBucket(p_175684_, p_175685_, p_175686_, p_175687_, p_175688_, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Integer.valueOf(3)), SoundEvents.BUCKET_EMPTY);
   };
   CauldronInteraction FILL_LAVA = (p_175676_, p_175677_, p_175678_, p_175679_, p_175680_, p_175681_) -> {
      return emptyBucket(p_175677_, p_175678_, p_175679_, p_175680_, p_175681_, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA);
   };
   CauldronInteraction FILL_POWDER_SNOW = (p_175669_, p_175670_, p_175671_, p_175672_, p_175673_, p_175674_) -> {
      return emptyBucket(p_175670_, p_175671_, p_175672_, p_175673_, p_175674_, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Integer.valueOf(3)), SoundEvents.BUCKET_EMPTY_POWDER_SNOW);
   };
   CauldronInteraction SHULKER_BOX = (p_175662_, p_175663_, p_175664_, p_175665_, p_175666_, p_175667_) -> {
      Block block = Block.byItem(p_175667_.getItem());
      if (!(block instanceof ShulkerBoxBlock)) {
         return InteractionResult.PASS;
      } else {
         if (!p_175663_.isClientSide) {
            ItemStack itemstack = new ItemStack(Blocks.SHULKER_BOX);
            if (p_175667_.hasTag()) {
               itemstack.setTag(p_175667_.getTag().copy());
            }

            p_175665_.setItemInHand(p_175666_, itemstack);
            p_175665_.awardStat(Stats.CLEAN_SHULKER_BOX);
            LayeredCauldronBlock.lowerFillLevel(p_175662_, p_175663_, p_175664_);
         }

         return InteractionResult.sidedSuccess(p_175663_.isClientSide);
      }
   };
   CauldronInteraction BANNER = (p_175653_, p_175654_, p_175655_, p_175656_, p_175657_, p_175658_) -> {
      if (BannerBlockEntity.getPatternCount(p_175658_) <= 0) {
         return InteractionResult.PASS;
      } else {
         if (!p_175654_.isClientSide) {
            ItemStack itemstack = p_175658_.copy();
            itemstack.setCount(1);
            BannerBlockEntity.removeLastPattern(itemstack);
            if (!p_175656_.getAbilities().instabuild) {
               p_175658_.shrink(1);
            }

            if (p_175658_.isEmpty()) {
               p_175656_.setItemInHand(p_175657_, itemstack);
            } else if (p_175656_.getInventory().add(itemstack)) {
               p_175656_.inventoryMenu.sendAllDataToRemote();
            } else {
               p_175656_.drop(itemstack, false);
            }

            p_175656_.awardStat(Stats.CLEAN_BANNER);
            LayeredCauldronBlock.lowerFillLevel(p_175653_, p_175654_, p_175655_);
         }

         return InteractionResult.sidedSuccess(p_175654_.isClientSide);
      }
   };
   CauldronInteraction DYED_ITEM = (p_175629_, p_175630_, p_175631_, p_175632_, p_175633_, p_175634_) -> {
      Item item = p_175634_.getItem();
      if (!(item instanceof DyeableLeatherItem)) {
         return InteractionResult.PASS;
      } else {
         DyeableLeatherItem dyeableleatheritem = (DyeableLeatherItem)item;
         if (!dyeableleatheritem.hasCustomColor(p_175634_)) {
            return InteractionResult.PASS;
         } else {
            if (!p_175630_.isClientSide) {
               dyeableleatheritem.clearColor(p_175634_);
               p_175632_.awardStat(Stats.CLEAN_ARMOR);
               LayeredCauldronBlock.lowerFillLevel(p_175629_, p_175630_, p_175631_);
            }

            return InteractionResult.sidedSuccess(p_175630_.isClientSide);
         }
      }
   };

   static Object2ObjectOpenHashMap<Item, CauldronInteraction> newInteractionMap() {
      return Util.make(new Object2ObjectOpenHashMap<>(), (p_175646_) -> {
         p_175646_.defaultReturnValue((p_175739_, p_175740_, p_175741_, p_175742_, p_175743_, p_175744_) -> {
            return InteractionResult.PASS;
         });
      });
   }

   InteractionResult interact(BlockState p_175711_, Level p_175712_, BlockPos p_175713_, Player p_175714_, InteractionHand p_175715_, ItemStack p_175716_);

   static void bootStrap() {
      addDefaultInteractions(EMPTY);
      EMPTY.put(Items.POTION, (p_175732_, p_175733_, p_175734_, p_175735_, p_175736_, p_175737_) -> {
         if (PotionUtils.getPotion(p_175737_) != Potions.WATER) {
            return InteractionResult.PASS;
         } else {
            if (!p_175733_.isClientSide) {
               Item item = p_175737_.getItem();
               p_175735_.setItemInHand(p_175736_, ItemUtils.createFilledResult(p_175737_, p_175735_, new ItemStack(Items.GLASS_BOTTLE)));
               p_175735_.awardStat(Stats.USE_CAULDRON);
               p_175735_.awardStat(Stats.ITEM_USED.get(item));
               p_175733_.setBlockAndUpdate(p_175734_, Blocks.WATER_CAULDRON.defaultBlockState());
               p_175733_.playSound((Player)null, p_175734_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               p_175733_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_175734_);
            }

            return InteractionResult.sidedSuccess(p_175733_.isClientSide);
         }
      });
      addDefaultInteractions(WATER);
      WATER.put(Items.BUCKET, (p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_) -> {
         return fillBucket(p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_, new ItemStack(Items.WATER_BUCKET), (p_175660_) -> {
            return p_175660_.getValue(LayeredCauldronBlock.LEVEL) == 3;
         }, SoundEvents.BUCKET_FILL);
      });
      WATER.put(Items.GLASS_BOTTLE, (p_175718_, p_175719_, p_175720_, p_175721_, p_175722_, p_175723_) -> {
         if (!p_175719_.isClientSide) {
            Item item = p_175723_.getItem();
            p_175721_.setItemInHand(p_175722_, ItemUtils.createFilledResult(p_175723_, p_175721_, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            p_175721_.awardStat(Stats.USE_CAULDRON);
            p_175721_.awardStat(Stats.ITEM_USED.get(item));
            LayeredCauldronBlock.lowerFillLevel(p_175718_, p_175719_, p_175720_);
            p_175719_.playSound((Player)null, p_175720_, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            p_175719_.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, p_175720_);
         }

         return InteractionResult.sidedSuccess(p_175719_.isClientSide);
      });
      WATER.put(Items.POTION, (p_175704_, p_175705_, p_175706_, p_175707_, p_175708_, p_175709_) -> {
         if (p_175704_.getValue(LayeredCauldronBlock.LEVEL) != 3 && PotionUtils.getPotion(p_175709_) == Potions.WATER) {
            if (!p_175705_.isClientSide) {
               p_175707_.setItemInHand(p_175708_, ItemUtils.createFilledResult(p_175709_, p_175707_, new ItemStack(Items.GLASS_BOTTLE)));
               p_175707_.awardStat(Stats.USE_CAULDRON);
               p_175707_.awardStat(Stats.ITEM_USED.get(p_175709_.getItem()));
               p_175705_.setBlockAndUpdate(p_175706_, p_175704_.cycle(LayeredCauldronBlock.LEVEL));
               p_175705_.playSound((Player)null, p_175706_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               p_175705_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_175706_);
            }

            return InteractionResult.sidedSuccess(p_175705_.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      });
      WATER.put(Items.LEATHER_BOOTS, DYED_ITEM);
      WATER.put(Items.LEATHER_LEGGINGS, DYED_ITEM);
      WATER.put(Items.LEATHER_CHESTPLATE, DYED_ITEM);
      WATER.put(Items.LEATHER_HELMET, DYED_ITEM);
      WATER.put(Items.LEATHER_HORSE_ARMOR, DYED_ITEM);
      WATER.put(Items.WHITE_BANNER, BANNER);
      WATER.put(Items.GRAY_BANNER, BANNER);
      WATER.put(Items.BLACK_BANNER, BANNER);
      WATER.put(Items.BLUE_BANNER, BANNER);
      WATER.put(Items.BROWN_BANNER, BANNER);
      WATER.put(Items.CYAN_BANNER, BANNER);
      WATER.put(Items.GREEN_BANNER, BANNER);
      WATER.put(Items.LIGHT_BLUE_BANNER, BANNER);
      WATER.put(Items.LIGHT_GRAY_BANNER, BANNER);
      WATER.put(Items.LIME_BANNER, BANNER);
      WATER.put(Items.MAGENTA_BANNER, BANNER);
      WATER.put(Items.ORANGE_BANNER, BANNER);
      WATER.put(Items.PINK_BANNER, BANNER);
      WATER.put(Items.PURPLE_BANNER, BANNER);
      WATER.put(Items.RED_BANNER, BANNER);
      WATER.put(Items.YELLOW_BANNER, BANNER);
      WATER.put(Items.WHITE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.GRAY_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.BLACK_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.BLUE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.BROWN_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.CYAN_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.GREEN_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.LIME_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.MAGENTA_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.ORANGE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.PINK_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.PURPLE_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.RED_SHULKER_BOX, SHULKER_BOX);
      WATER.put(Items.YELLOW_SHULKER_BOX, SHULKER_BOX);
      LAVA.put(Items.BUCKET, (p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_) -> {
         return fillBucket(p_175697_, p_175698_, p_175699_, p_175700_, p_175701_, p_175702_, new ItemStack(Items.LAVA_BUCKET), (p_175651_) -> {
            return true;
         }, SoundEvents.BUCKET_FILL_LAVA);
      });
      addDefaultInteractions(LAVA);
      POWDER_SNOW.put(Items.BUCKET, (p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_) -> {
         return fillBucket(p_175690_, p_175691_, p_175692_, p_175693_, p_175694_, p_175695_, new ItemStack(Items.POWDER_SNOW_BUCKET), (p_175627_) -> {
            return p_175627_.getValue(LayeredCauldronBlock.LEVEL) == 3;
         }, SoundEvents.BUCKET_FILL_POWDER_SNOW);
      });
      addDefaultInteractions(POWDER_SNOW);
   }

   static void addDefaultInteractions(Map<Item, CauldronInteraction> p_175648_) {
      p_175648_.put(Items.LAVA_BUCKET, FILL_LAVA);
      p_175648_.put(Items.WATER_BUCKET, FILL_WATER);
      p_175648_.put(Items.POWDER_SNOW_BUCKET, FILL_POWDER_SNOW);
   }

   static InteractionResult fillBucket(BlockState p_175636_, Level p_175637_, BlockPos p_175638_, Player p_175639_, InteractionHand p_175640_, ItemStack p_175641_, ItemStack p_175642_, Predicate<BlockState> p_175643_, SoundEvent p_175644_) {
      if (!p_175643_.test(p_175636_)) {
         return InteractionResult.PASS;
      } else {
         if (!p_175637_.isClientSide) {
            Item item = p_175641_.getItem();
            p_175639_.setItemInHand(p_175640_, ItemUtils.createFilledResult(p_175641_, p_175639_, p_175642_));
            p_175639_.awardStat(Stats.USE_CAULDRON);
            p_175639_.awardStat(Stats.ITEM_USED.get(item));
            p_175637_.setBlockAndUpdate(p_175638_, Blocks.CAULDRON.defaultBlockState());
            p_175637_.playSound((Player)null, p_175638_, p_175644_, SoundSource.BLOCKS, 1.0F, 1.0F);
            p_175637_.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, p_175638_);
         }

         return InteractionResult.sidedSuccess(p_175637_.isClientSide);
      }
   }

   static InteractionResult emptyBucket(Level p_175619_, BlockPos p_175620_, Player p_175621_, InteractionHand p_175622_, ItemStack p_175623_, BlockState p_175624_, SoundEvent p_175625_) {
      if (!p_175619_.isClientSide) {
         Item item = p_175623_.getItem();
         p_175621_.setItemInHand(p_175622_, ItemUtils.createFilledResult(p_175623_, p_175621_, new ItemStack(Items.BUCKET)));
         p_175621_.awardStat(Stats.FILL_CAULDRON);
         p_175621_.awardStat(Stats.ITEM_USED.get(item));
         p_175619_.setBlockAndUpdate(p_175620_, p_175624_);
         p_175619_.playSound((Player)null, p_175620_, p_175625_, SoundSource.BLOCKS, 1.0F, 1.0F);
         p_175619_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_175620_);
      }

      return InteractionResult.sidedSuccess(p_175619_.isClientSide);
   }
}