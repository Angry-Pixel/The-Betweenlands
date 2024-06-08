package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final String RECIPE_TAG = "Recipes";
   private static final Logger LOGGER = LogUtils.getLogger();

   public KnowledgeBookItem(Item.Properties p_42822_) {
      super(p_42822_);
   }

   public InteractionResultHolder<ItemStack> use(Level p_42824_, Player p_42825_, InteractionHand p_42826_) {
      ItemStack itemstack = p_42825_.getItemInHand(p_42826_);
      CompoundTag compoundtag = itemstack.getTag();
      if (!p_42825_.getAbilities().instabuild) {
         p_42825_.setItemInHand(p_42826_, ItemStack.EMPTY);
      }

      if (compoundtag != null && compoundtag.contains("Recipes", 9)) {
         if (!p_42824_.isClientSide) {
            ListTag listtag = compoundtag.getList("Recipes", 8);
            List<Recipe<?>> list = Lists.newArrayList();
            RecipeManager recipemanager = p_42824_.getServer().getRecipeManager();

            for(int i = 0; i < listtag.size(); ++i) {
               String s = listtag.getString(i);
               Optional<? extends Recipe<?>> optional = recipemanager.byKey(new ResourceLocation(s));
               if (!optional.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", (Object)s);
                  return InteractionResultHolder.fail(itemstack);
               }

               list.add(optional.get());
            }

            p_42825_.awardRecipes(list);
            p_42825_.awardStat(Stats.ITEM_USED.get(this));
         }

         return InteractionResultHolder.sidedSuccess(itemstack, p_42824_.isClientSide());
      } else {
         LOGGER.error("Tag not valid: {}", (Object)compoundtag);
         return InteractionResultHolder.fail(itemstack);
      }
   }
}