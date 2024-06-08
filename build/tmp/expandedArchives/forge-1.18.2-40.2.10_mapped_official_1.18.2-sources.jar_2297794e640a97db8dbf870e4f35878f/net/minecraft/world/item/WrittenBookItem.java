package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WrittenBookItem extends Item {
   public static final int TITLE_LENGTH = 16;
   public static final int TITLE_MAX_LENGTH = 32;
   public static final int PAGE_EDIT_LENGTH = 1024;
   public static final int PAGE_LENGTH = 32767;
   public static final int MAX_PAGES = 100;
   public static final int MAX_GENERATION = 2;
   public static final String TAG_TITLE = "title";
   public static final String TAG_FILTERED_TITLE = "filtered_title";
   public static final String TAG_AUTHOR = "author";
   public static final String TAG_PAGES = "pages";
   public static final String TAG_FILTERED_PAGES = "filtered_pages";
   public static final String TAG_GENERATION = "generation";
   public static final String TAG_RESOLVED = "resolved";

   public WrittenBookItem(Item.Properties p_43455_) {
      super(p_43455_);
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundTag p_43472_) {
      if (!WritableBookItem.makeSureTagIsValid(p_43472_)) {
         return false;
      } else if (!p_43472_.contains("title", 8)) {
         return false;
      } else {
         String s = p_43472_.getString("title");
         return s.length() > 32 ? false : p_43472_.contains("author", 8);
      }
   }

   public static int getGeneration(ItemStack p_43474_) {
      return p_43474_.getTag().getInt("generation");
   }

   public static int getPageCount(ItemStack p_43478_) {
      CompoundTag compoundtag = p_43478_.getTag();
      return compoundtag != null ? compoundtag.getList("pages", 8).size() : 0;
   }

   public Component getName(ItemStack p_43480_) {
      CompoundTag compoundtag = p_43480_.getTag();
      if (compoundtag != null) {
         String s = compoundtag.getString("title");
         if (!StringUtil.isNullOrEmpty(s)) {
            return new TextComponent(s);
         }
      }

      return super.getName(p_43480_);
   }

   public void appendHoverText(ItemStack p_43457_, @Nullable Level p_43458_, List<Component> p_43459_, TooltipFlag p_43460_) {
      if (p_43457_.hasTag()) {
         CompoundTag compoundtag = p_43457_.getTag();
         String s = compoundtag.getString("author");
         if (!StringUtil.isNullOrEmpty(s)) {
            p_43459_.add((new TranslatableComponent("book.byAuthor", s)).withStyle(ChatFormatting.GRAY));
         }

         p_43459_.add((new TranslatableComponent("book.generation." + compoundtag.getInt("generation"))).withStyle(ChatFormatting.GRAY));
      }

   }

   public InteractionResult useOn(UseOnContext p_43466_) {
      Level level = p_43466_.getLevel();
      BlockPos blockpos = p_43466_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      if (blockstate.is(Blocks.LECTERN)) {
         return LecternBlock.tryPlaceBook(p_43466_.getPlayer(), level, blockpos, blockstate, p_43466_.getItemInHand()) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level p_43468_, Player p_43469_, InteractionHand p_43470_) {
      ItemStack itemstack = p_43469_.getItemInHand(p_43470_);
      p_43469_.openItemGui(itemstack, p_43470_);
      p_43469_.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.sidedSuccess(itemstack, p_43468_.isClientSide());
   }

   public static boolean resolveBookComponents(ItemStack p_43462_, @Nullable CommandSourceStack p_43463_, @Nullable Player p_43464_) {
      CompoundTag compoundtag = p_43462_.getTag();
      if (compoundtag != null && !compoundtag.getBoolean("resolved")) {
         compoundtag.putBoolean("resolved", true);
         if (!makeSureTagIsValid(compoundtag)) {
            return false;
         } else {
            ListTag listtag = compoundtag.getList("pages", 8);

            for(int i = 0; i < listtag.size(); ++i) {
               listtag.set(i, (Tag)StringTag.valueOf(resolvePage(p_43463_, p_43464_, listtag.getString(i))));
            }

            if (compoundtag.contains("filtered_pages", 10)) {
               CompoundTag compoundtag1 = compoundtag.getCompound("filtered_pages");

               for(String s : compoundtag1.getAllKeys()) {
                  compoundtag1.putString(s, resolvePage(p_43463_, p_43464_, compoundtag1.getString(s)));
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static String resolvePage(@Nullable CommandSourceStack p_151249_, @Nullable Player p_151250_, String p_151251_) {
      Component component;
      try {
         component = Component.Serializer.fromJsonLenient(p_151251_);
         component = ComponentUtils.updateForEntity(p_151249_, component, p_151250_, 0);
      } catch (Exception exception) {
         component = new TextComponent(p_151251_);
      }

      return Component.Serializer.toJson(component);
   }

   public boolean isFoil(ItemStack p_43476_) {
      return true;
   }
}