package net.minecraft.client.searchtree;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchRegistry implements ResourceManagerReloadListener {
   public static final SearchRegistry.Key<ItemStack> CREATIVE_NAMES = new SearchRegistry.Key<>();
   public static final SearchRegistry.Key<ItemStack> CREATIVE_TAGS = new SearchRegistry.Key<>();
   public static final SearchRegistry.Key<RecipeCollection> RECIPE_COLLECTIONS = new SearchRegistry.Key<>();
   private final Map<SearchRegistry.Key<?>, MutableSearchTree<?>> searchTrees = Maps.newHashMap();

   public void onResourceManagerReload(ResourceManager p_119948_) {
      for(MutableSearchTree<?> mutablesearchtree : this.searchTrees.values()) {
         mutablesearchtree.refresh();
      }

   }

   public <T> void register(SearchRegistry.Key<T> p_119952_, MutableSearchTree<T> p_119953_) {
      this.searchTrees.put(p_119952_, p_119953_);
   }

   public <T> MutableSearchTree<T> getTree(SearchRegistry.Key<T> p_119950_) {
      return (MutableSearchTree<T>)this.searchTrees.get(p_119950_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Key<T> {
   }
}