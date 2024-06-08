package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.slf4j.Logger;

public class ReloadableServerResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private final Commands commands;
   private final RecipeManager recipes;
   private final TagManager tagManager;
   private final PredicateManager predicateManager = new PredicateManager();
   private final LootTables lootTables = new LootTables(this.predicateManager);
   private final ItemModifierManager itemModifierManager = new ItemModifierManager(this.predicateManager, this.lootTables);
   private final ServerAdvancementManager advancements;
   private final ServerFunctionLibrary functionLibrary;

   public ReloadableServerResources(RegistryAccess.Frozen p_206857_, Commands.CommandSelection p_206858_, int p_206859_) {
      this.tagManager = new TagManager(p_206857_);
      this.commands = new Commands(p_206858_);
      this.functionLibrary = new ServerFunctionLibrary(p_206859_, this.commands.getDispatcher());
      // Forge: Create context object and pass it to the recipe manager.
      this.context = new net.minecraftforge.common.crafting.conditions.ConditionContext(this.tagManager);
      this.recipes = new RecipeManager(context);
      this.advancements = new ServerAdvancementManager(this.predicateManager, context);
   }

   public ServerFunctionLibrary getFunctionLibrary() {
      return this.functionLibrary;
   }

   public PredicateManager getPredicateManager() {
      return this.predicateManager;
   }

   public LootTables getLootTables() {
      return this.lootTables;
   }

   public ItemModifierManager getItemModifierManager() {
      return this.itemModifierManager;
   }

   public RecipeManager getRecipeManager() {
      return this.recipes;
   }

   public Commands getCommands() {
      return this.commands;
   }

   public ServerAdvancementManager getAdvancements() {
      return this.advancements;
   }

   public List<PreparableReloadListener> listeners() {
      return List.of(this.tagManager, this.predicateManager, this.recipes, this.lootTables, this.itemModifierManager, this.functionLibrary, this.advancements);
   }

   public static CompletableFuture<ReloadableServerResources> loadResources(ResourceManager p_206862_, RegistryAccess.Frozen p_206863_, Commands.CommandSelection p_206864_, int p_206865_, Executor p_206866_, Executor p_206867_) {
      ReloadableServerResources reloadableserverresources = new ReloadableServerResources(p_206863_, p_206864_, p_206865_);
      List<PreparableReloadListener> listeners = new java.util.ArrayList<>(reloadableserverresources.listeners());
      listeners.addAll(net.minecraftforge.event.ForgeEventFactory.onResourceReload(reloadableserverresources));
      return SimpleReloadInstance.create(p_206862_, listeners, p_206866_, p_206867_, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled()).done().thenApply((p_206880_) -> {
         return reloadableserverresources;
      });
   }

   public void updateRegistryTags(RegistryAccess p_206869_) {
      this.tagManager.getResult().forEach((p_206884_) -> {
         updateRegistryTags(p_206869_, p_206884_);
      });
      Blocks.rebuildCache();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent(p_206869_, false, false));
   }

   private static <T> void updateRegistryTags(RegistryAccess p_206871_, TagManager.LoadResult<T> p_206872_) {
      ResourceKey<? extends Registry<T>> resourcekey = p_206872_.key();
      Map<TagKey<T>, List<Holder<T>>> map = p_206872_.tags().entrySet().stream().collect(Collectors.toUnmodifiableMap((p_206877_) -> {
         return TagKey.create(resourcekey, p_206877_.getKey());
      }, (p_206874_) -> {
         return p_206874_.getValue().getValues();
      }));
      p_206871_.registryOrThrow(resourcekey).bindTags(map);
   }

   private final net.minecraftforge.common.crafting.conditions.ICondition.IContext context;

   /**
    * Exposes the current condition context for usage in other reload listeners.<br>
    * This is not useful outside the reloading stage.
    * @return The condition context for the currently active reload.
    */
   public net.minecraftforge.common.crafting.conditions.ICondition.IContext getConditionContext() {
      return this.context;
   }
}
