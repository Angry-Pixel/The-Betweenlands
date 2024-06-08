package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe extends CustomRecipe {
   private static final Ingredient SHAPE_INGREDIENT = Ingredient.of(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
   private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
   private static final Ingredient FLICKER_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
   private static final Map<Item, FireworkRocketItem.Shape> SHAPE_BY_ITEM = Util.make(Maps.newHashMap(), (p_43898_) -> {
      p_43898_.put(Items.FIRE_CHARGE, FireworkRocketItem.Shape.LARGE_BALL);
      p_43898_.put(Items.FEATHER, FireworkRocketItem.Shape.BURST);
      p_43898_.put(Items.GOLD_NUGGET, FireworkRocketItem.Shape.STAR);
      p_43898_.put(Items.SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
      p_43898_.put(Items.WITHER_SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
      p_43898_.put(Items.CREEPER_HEAD, FireworkRocketItem.Shape.CREEPER);
      p_43898_.put(Items.PLAYER_HEAD, FireworkRocketItem.Shape.CREEPER);
      p_43898_.put(Items.DRAGON_HEAD, FireworkRocketItem.Shape.CREEPER);
      p_43898_.put(Items.ZOMBIE_HEAD, FireworkRocketItem.Shape.CREEPER);
   });
   private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

   public FireworkStarRecipe(ResourceLocation p_43883_) {
      super(p_43883_);
   }

   public boolean matches(CraftingContainer p_43895_, Level p_43896_) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;

      for(int i = 0; i < p_43895_.getContainerSize(); ++i) {
         ItemStack itemstack = p_43895_.getItem(i);
         if (!itemstack.isEmpty()) {
            if (SHAPE_INGREDIENT.test(itemstack)) {
               if (flag2) {
                  return false;
               }

               flag2 = true;
            } else if (FLICKER_INGREDIENT.test(itemstack)) {
               if (flag4) {
                  return false;
               }

               flag4 = true;
            } else if (TRAIL_INGREDIENT.test(itemstack)) {
               if (flag3) {
                  return false;
               }

               flag3 = true;
            } else if (GUNPOWDER_INGREDIENT.test(itemstack)) {
               if (flag) {
                  return false;
               }

               flag = true;
            } else {
               if (!(itemstack.getItem() instanceof DyeItem)) {
                  return false;
               }

               flag1 = true;
            }
         }
      }

      return flag && flag1;
   }

   public ItemStack assemble(CraftingContainer p_43893_) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
      CompoundTag compoundtag = itemstack.getOrCreateTagElement("Explosion");
      FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.SMALL_BALL;
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_43893_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_43893_.getItem(i);
         if (!itemstack1.isEmpty()) {
            if (SHAPE_INGREDIENT.test(itemstack1)) {
               fireworkrocketitem$shape = SHAPE_BY_ITEM.get(itemstack1.getItem());
            } else if (FLICKER_INGREDIENT.test(itemstack1)) {
               compoundtag.putBoolean("Flicker", true);
            } else if (TRAIL_INGREDIENT.test(itemstack1)) {
               compoundtag.putBoolean("Trail", true);
            } else if (itemstack1.getItem() instanceof DyeItem) {
               list.add(((DyeItem)itemstack1.getItem()).getDyeColor().getFireworkColor());
            }
         }
      }

      compoundtag.putIntArray("Colors", list);
      compoundtag.putByte("Type", (byte)fireworkrocketitem$shape.getId());
      return itemstack;
   }

   public boolean canCraftInDimensions(int p_43885_, int p_43886_) {
      return p_43885_ * p_43886_ >= 2;
   }

   public ItemStack getResultItem() {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR;
   }
}