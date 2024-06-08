package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class FireworkStarItem extends Item {
   public FireworkStarItem(Item.Properties p_41248_) {
      super(p_41248_);
   }

   public void appendHoverText(ItemStack p_41252_, @Nullable Level p_41253_, List<Component> p_41254_, TooltipFlag p_41255_) {
      CompoundTag compoundtag = p_41252_.getTagElement("Explosion");
      if (compoundtag != null) {
         appendHoverText(compoundtag, p_41254_);
      }

   }

   public static void appendHoverText(CompoundTag p_41257_, List<Component> p_41258_) {
      FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.byId(p_41257_.getByte("Type"));
      p_41258_.add((new TranslatableComponent("item.minecraft.firework_star.shape." + fireworkrocketitem$shape.getName())).withStyle(ChatFormatting.GRAY));
      int[] aint = p_41257_.getIntArray("Colors");
      if (aint.length > 0) {
         p_41258_.add(appendColors((new TextComponent("")).withStyle(ChatFormatting.GRAY), aint));
      }

      int[] aint1 = p_41257_.getIntArray("FadeColors");
      if (aint1.length > 0) {
         p_41258_.add(appendColors((new TranslatableComponent("item.minecraft.firework_star.fade_to")).append(" ").withStyle(ChatFormatting.GRAY), aint1));
      }

      if (p_41257_.getBoolean("Trail")) {
         p_41258_.add((new TranslatableComponent("item.minecraft.firework_star.trail")).withStyle(ChatFormatting.GRAY));
      }

      if (p_41257_.getBoolean("Flicker")) {
         p_41258_.add((new TranslatableComponent("item.minecraft.firework_star.flicker")).withStyle(ChatFormatting.GRAY));
      }

   }

   private static Component appendColors(MutableComponent p_41260_, int[] p_41261_) {
      for(int i = 0; i < p_41261_.length; ++i) {
         if (i > 0) {
            p_41260_.append(", ");
         }

         p_41260_.append(getColorName(p_41261_[i]));
      }

      return p_41260_;
   }

   private static Component getColorName(int p_41250_) {
      DyeColor dyecolor = DyeColor.byFireworkColor(p_41250_);
      return dyecolor == null ? new TranslatableComponent("item.minecraft.firework_star.custom_color") : new TranslatableComponent("item.minecraft.firework_star." + dyecolor.getName());
   }
}