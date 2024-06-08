package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   public static final String RECIPE_BOOK_TAG = "recipeBook";
   private static final Logger LOGGER = LogUtils.getLogger();

   public int addRecipes(Collection<Recipe<?>> p_12792_, ServerPlayer p_12793_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(Recipe<?> recipe : p_12792_) {
         ResourceLocation resourcelocation = recipe.getId();
         if (!this.known.contains(resourcelocation) && !recipe.isSpecial()) {
            this.add(resourcelocation);
            this.addHighlight(resourcelocation);
            list.add(resourcelocation);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(p_12793_, recipe);
            ++i;
         }
      }

      this.sendRecipes(ClientboundRecipePacket.State.ADD, p_12793_, list);
      return i;
   }

   public int removeRecipes(Collection<Recipe<?>> p_12807_, ServerPlayer p_12808_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(Recipe<?> recipe : p_12807_) {
         ResourceLocation resourcelocation = recipe.getId();
         if (this.known.contains(resourcelocation)) {
            this.remove(resourcelocation);
            list.add(resourcelocation);
            ++i;
         }
      }

      this.sendRecipes(ClientboundRecipePacket.State.REMOVE, p_12808_, list);
      return i;
   }

   private void sendRecipes(ClientboundRecipePacket.State p_12802_, ServerPlayer p_12803_, List<ResourceLocation> p_12804_) {
      p_12803_.connection.send(new ClientboundRecipePacket(p_12802_, p_12804_, Collections.emptyList(), this.getBookSettings()));
   }

   public CompoundTag toNbt() {
      CompoundTag compoundtag = new CompoundTag();
      this.getBookSettings().write(compoundtag);
      ListTag listtag = new ListTag();

      for(ResourceLocation resourcelocation : this.known) {
         listtag.add(StringTag.valueOf(resourcelocation.toString()));
      }

      compoundtag.put("recipes", listtag);
      ListTag listtag1 = new ListTag();

      for(ResourceLocation resourcelocation1 : this.highlight) {
         listtag1.add(StringTag.valueOf(resourcelocation1.toString()));
      }

      compoundtag.put("toBeDisplayed", listtag1);
      return compoundtag;
   }

   public void fromNbt(CompoundTag p_12795_, RecipeManager p_12796_) {
      this.setBookSettings(RecipeBookSettings.read(p_12795_));
      ListTag listtag = p_12795_.getList("recipes", 8);
      this.loadRecipes(listtag, this::add, p_12796_);
      ListTag listtag1 = p_12795_.getList("toBeDisplayed", 8);
      this.loadRecipes(listtag1, this::addHighlight, p_12796_);
   }

   private void loadRecipes(ListTag p_12798_, Consumer<Recipe<?>> p_12799_, RecipeManager p_12800_) {
      for(int i = 0; i < p_12798_.size(); ++i) {
         String s = p_12798_.getString(i);

         try {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            Optional<? extends Recipe<?>> optional = p_12800_.byKey(resourcelocation);
            if (!optional.isPresent()) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation);
            } else {
               p_12799_.accept(optional.get());
            }
         } catch (ResourceLocationException resourcelocationexception) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)s);
         }
      }

   }

   public void sendInitialRecipeBook(ServerPlayer p_12790_) {
      p_12790_.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, this.known, this.highlight, this.getBookSettings()));
   }
}