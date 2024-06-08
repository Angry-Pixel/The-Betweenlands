package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookComponent extends GuiComponent implements Widget, GuiEventListener, NarratableEntry, RecipeShownListener, PlaceRecipe<Ingredient> {
   protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final Component SEARCH_HINT = (new TranslatableComponent("gui.recipebook.search_hint")).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
   public static final int IMAGE_WIDTH = 147;
   public static final int IMAGE_HEIGHT = 166;
   private static final int OFFSET_X_POSITION = 86;
   private static final Component ONLY_CRAFTABLES_TOOLTIP = new TranslatableComponent("gui.recipebook.toggleRecipes.craftable");
   private static final Component ALL_RECIPES_TOOLTIP = new TranslatableComponent("gui.recipebook.toggleRecipes.all");
   private int xOffset;
   private int width;
   private int height;
   protected final GhostRecipe ghostRecipe = new GhostRecipe();
   private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
   @Nullable
   private RecipeBookTabButton selectedTab;
   protected StateSwitchingButton filterButton;
   protected RecipeBookMenu<?> menu;
   protected Minecraft minecraft;
   @Nullable
   private EditBox searchBox;
   private String lastSearch = "";
   private ClientRecipeBook book;
   private final RecipeBookPage recipeBookPage = new RecipeBookPage();
   private final StackedContents stackedContents = new StackedContents();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;
   private boolean visible;
   private boolean widthTooNarrow;

   public void init(int p_100310_, int p_100311_, Minecraft p_100312_, boolean p_100313_, RecipeBookMenu<?> p_100314_) {
      this.minecraft = p_100312_;
      this.width = p_100310_;
      this.height = p_100311_;
      this.menu = p_100314_;
      this.widthTooNarrow = p_100313_;
      p_100312_.player.containerMenu = p_100314_;
      this.book = p_100312_.player.getRecipeBook();
      this.timesInventoryChanged = p_100312_.player.getInventory().getTimesChanged();
      this.visible = this.isVisibleAccordingToBookData();
      if (this.visible) {
         this.initVisuals();
      }

      p_100312_.keyboardHandler.setSendRepeatsToGui(true);
   }

   public void initVisuals() {
      this.xOffset = this.widthTooNarrow ? 0 : 86;
      int i = (this.width - 147) / 2 - this.xOffset;
      int j = (this.height - 166) / 2;
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String s = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.minecraft.font, i + 25, j + 14, 80, 9 + 5, new TranslatableComponent("itemGroup.search"));
      this.searchBox.setMaxLength(50);
      this.searchBox.setBordered(false);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(s);
      this.recipeBookPage.init(this.minecraft, i, j);
      this.recipeBookPage.addListener(this);
      this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, this.book.isFiltering(this.menu));
      this.initFilterButtonTextures();
      this.tabButtons.clear();

      for(RecipeBookCategories recipebookcategories : this.menu.getRecipeBookCategories()) {
         this.tabButtons.add(new RecipeBookTabButton(recipebookcategories));
      }

      if (this.selectedTab != null) {
         this.selectedTab = this.tabButtons.stream().filter((p_100329_) -> {
            return p_100329_.getCategory().equals(this.selectedTab.getCategory());
         }).findFirst().orElse((RecipeBookTabButton)null);
      }

      if (this.selectedTab == null) {
         this.selectedTab = this.tabButtons.get(0);
      }

      this.selectedTab.setStateTriggered(true);
      this.updateCollections(false);
      this.updateTabs();
   }

   public boolean changeFocus(boolean p_100372_) {
      return false;
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 41, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public int updateScreenPosition(int p_181402_, int p_181403_) {
      int i;
      if (this.isVisible() && !this.widthTooNarrow) {
         i = 177 + (p_181402_ - p_181403_ - 200) / 2;
      } else {
         i = (p_181402_ - p_181403_) / 2;
      }

      return i;
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   public boolean isVisible() {
      return this.visible;
   }

   private boolean isVisibleAccordingToBookData() {
      return this.book.isOpen(this.menu.getRecipeBookType());
   }

   protected void setVisible(boolean p_100370_) {
      if (p_100370_) {
         this.initVisuals();
      }

      this.visible = p_100370_;
      this.book.setOpen(this.menu.getRecipeBookType(), p_100370_);
      if (!p_100370_) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   public void slotClicked(@Nullable Slot p_100315_) {
      if (p_100315_ != null && p_100315_.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
         if (this.isVisible()) {
            this.updateStackedContents();
         }
      }

   }

   private void updateCollections(boolean p_100383_) {
      List<RecipeCollection> list = this.book.getCollection(this.selectedTab.getCategory());
      list.forEach((p_100381_) -> {
         p_100381_.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book);
      });
      List<RecipeCollection> list1 = Lists.newArrayList(list);
      list1.removeIf((p_100368_) -> {
         return !p_100368_.hasKnownRecipes();
      });
      list1.removeIf((p_100360_) -> {
         return !p_100360_.hasFitting();
      });
      String s = this.searchBox.getValue();
      if (!s.isEmpty()) {
         ObjectSet<RecipeCollection> objectset = new ObjectLinkedOpenHashSet<>(this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(s.toLowerCase(Locale.ROOT)));
         list1.removeIf((p_100334_) -> {
            return !objectset.contains(p_100334_);
         });
      }

      if (this.book.isFiltering(this.menu)) {
         list1.removeIf((p_100331_) -> {
            return !p_100331_.hasCraftable();
         });
      }

      this.recipeBookPage.updateCollections(list1, p_100383_);
   }

   private void updateTabs() {
      int i = (this.width - 147) / 2 - this.xOffset - 30;
      int j = (this.height - 166) / 2 + 3;
      int k = 27;
      int l = 0;

      for(RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
         RecipeBookCategories recipebookcategories = recipebooktabbutton.getCategory();
         if (recipebookcategories != RecipeBookCategories.CRAFTING_SEARCH && recipebookcategories != RecipeBookCategories.FURNACE_SEARCH) {
            if (recipebooktabbutton.updateVisibility(this.book)) {
               recipebooktabbutton.setPosition(i, j + 27 * l++);
               recipebooktabbutton.startAnimation(this.minecraft);
            }
         } else {
            recipebooktabbutton.visible = true;
            recipebooktabbutton.setPosition(i, j + 27 * l++);
         }
      }

   }

   public void tick() {
      boolean flag = this.isVisibleAccordingToBookData();
      if (this.isVisible() != flag) {
         this.setVisible(flag);
      }

      if (this.isVisible()) {
         if (this.timesInventoryChanged != this.minecraft.player.getInventory().getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.getInventory().getTimesChanged();
         }

         this.searchBox.tick();
      }
   }

   private void updateStackedContents() {
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      this.updateCollections(false);
   }

   public void render(PoseStack p_100319_, int p_100320_, int p_100321_, float p_100322_) {
      if (this.isVisible()) {
         p_100319_.pushPose();
         p_100319_.translate(0.0D, 0.0D, 100.0D);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, RECIPE_BOOK_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int i = (this.width - 147) / 2 - this.xOffset;
         int j = (this.height - 166) / 2;
         this.blit(p_100319_, i, j, 1, 1, 147, 166);
         if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
            drawString(p_100319_, this.minecraft.font, SEARCH_HINT, i + 25, j + 14, -1);
         } else {
            this.searchBox.render(p_100319_, p_100320_, p_100321_, p_100322_);
         }

         for(RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
            recipebooktabbutton.render(p_100319_, p_100320_, p_100321_, p_100322_);
         }

         this.filterButton.render(p_100319_, p_100320_, p_100321_, p_100322_);
         this.recipeBookPage.render(p_100319_, i, j, p_100320_, p_100321_, p_100322_);
         p_100319_.popPose();
      }
   }

   public void renderTooltip(PoseStack p_100362_, int p_100363_, int p_100364_, int p_100365_, int p_100366_) {
      if (this.isVisible()) {
         this.recipeBookPage.renderTooltip(p_100362_, p_100365_, p_100366_);
         if (this.filterButton.isHoveredOrFocused()) {
            Component component = this.getFilterButtonTooltip();
            if (this.minecraft.screen != null) {
               this.minecraft.screen.renderTooltip(p_100362_, component, p_100365_, p_100366_);
            }
         }

         this.renderGhostRecipeTooltip(p_100362_, p_100363_, p_100364_, p_100365_, p_100366_);
      }
   }

   private Component getFilterButtonTooltip() {
      return this.filterButton.isStateTriggered() ? this.getRecipeFilterName() : ALL_RECIPES_TOOLTIP;
   }

   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   private void renderGhostRecipeTooltip(PoseStack p_100375_, int p_100376_, int p_100377_, int p_100378_, int p_100379_) {
      ItemStack itemstack = null;

      for(int i = 0; i < this.ghostRecipe.size(); ++i) {
         GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
         int j = ghostrecipe$ghostingredient.getX() + p_100376_;
         int k = ghostrecipe$ghostingredient.getY() + p_100377_;
         if (p_100378_ >= j && p_100379_ >= k && p_100378_ < j + 16 && p_100379_ < k + 16) {
            itemstack = ghostrecipe$ghostingredient.getItem();
         }
      }

      if (itemstack != null && this.minecraft.screen != null) {
         this.minecraft.screen.renderComponentTooltip(p_100375_, this.minecraft.screen.getTooltipFromItem(itemstack), p_100378_, p_100379_, itemstack);
      }

   }

   public void renderGhostRecipe(PoseStack p_100323_, int p_100324_, int p_100325_, boolean p_100326_, float p_100327_) {
      this.ghostRecipe.render(p_100323_, this.minecraft, p_100324_, p_100325_, p_100326_, p_100327_);
   }

   public boolean mouseClicked(double p_100294_, double p_100295_, int p_100296_) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.recipeBookPage.mouseClicked(p_100294_, p_100295_, p_100296_, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            Recipe<?> recipe = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection recipecollection = this.recipeBookPage.getLastClickedRecipeCollection();
            if (recipe != null && recipecollection != null) {
               if (!recipecollection.isCraftable(recipe) && this.ghostRecipe.getRecipe() == recipe) {
                  return false;
               }

               this.ghostRecipe.clear();
               this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, recipe, Screen.hasShiftDown());
               if (!this.isOffsetNextToMainGUI()) {
                  this.setVisible(false);
               }
            }

            return true;
         } else if (this.searchBox.mouseClicked(p_100294_, p_100295_, p_100296_)) {
            return true;
         } else if (this.filterButton.mouseClicked(p_100294_, p_100295_, p_100296_)) {
            boolean flag = this.toggleFiltering();
            this.filterButton.setStateTriggered(flag);
            this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
         } else {
            for(RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
               if (recipebooktabbutton.mouseClicked(p_100294_, p_100295_, p_100296_)) {
                  if (this.selectedTab != recipebooktabbutton) {
                     if (this.selectedTab != null) {
                        this.selectedTab.setStateTriggered(false);
                     }

                     this.selectedTab = recipebooktabbutton;
                     this.selectedTab.setStateTriggered(true);
                     this.updateCollections(true);
                  }

                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private boolean toggleFiltering() {
      RecipeBookType recipebooktype = this.menu.getRecipeBookType();
      boolean flag = !this.book.isFiltering(recipebooktype);
      this.book.setFiltering(recipebooktype, flag);
      return flag;
   }

   public boolean hasClickedOutside(double p_100298_, double p_100299_, int p_100300_, int p_100301_, int p_100302_, int p_100303_, int p_100304_) {
      if (!this.isVisible()) {
         return true;
      } else {
         boolean flag = p_100298_ < (double)p_100300_ || p_100299_ < (double)p_100301_ || p_100298_ >= (double)(p_100300_ + p_100302_) || p_100299_ >= (double)(p_100301_ + p_100303_);
         boolean flag1 = (double)(p_100300_ - 147) < p_100298_ && p_100298_ < (double)p_100300_ && (double)p_100301_ < p_100299_ && p_100299_ < (double)(p_100301_ + p_100303_);
         return flag && !flag1 && !this.selectedTab.isHoveredOrFocused();
      }
   }

   public boolean keyPressed(int p_100306_, int p_100307_, int p_100308_) {
      this.ignoreTextInput = false;
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (p_100306_ == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
         } else if (this.searchBox.keyPressed(p_100306_, p_100307_, p_100308_)) {
            this.checkSearchStringUpdate();
            return true;
         } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && p_100306_ != 256) {
            return true;
         } else if (this.minecraft.options.keyChat.matches(p_100306_, p_100307_) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocus(true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean keyReleased(int p_100356_, int p_100357_, int p_100358_) {
      this.ignoreTextInput = false;
      return GuiEventListener.super.keyReleased(p_100356_, p_100357_, p_100358_);
   }

   public boolean charTyped(char p_100291_, int p_100292_) {
      if (this.ignoreTextInput) {
         return false;
      } else if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.searchBox.charTyped(p_100291_, p_100292_)) {
            this.checkSearchStringUpdate();
            return true;
         } else {
            return GuiEventListener.super.charTyped(p_100291_, p_100292_);
         }
      } else {
         return false;
      }
   }

   public boolean isMouseOver(double p_100353_, double p_100354_) {
      return false;
   }

   private void checkSearchStringUpdate() {
      String s = this.searchBox.getValue().toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(s);
      if (!s.equals(this.lastSearch)) {
         this.updateCollections(false);
         this.lastSearch = s;
      }

   }

   private void pirateSpeechForThePeople(String p_100336_) {
      if ("excitedze".equals(p_100336_)) {
         LanguageManager languagemanager = this.minecraft.getLanguageManager();
         LanguageInfo languageinfo = languagemanager.getLanguage("en_pt");
         if (languagemanager.getSelected().compareTo(languageinfo) == 0) {
            return;
         }

         languagemanager.setSelected(languageinfo);
         this.minecraft.options.languageCode = languageinfo.getCode();
         this.minecraft.reloadResourcePacks();
         this.minecraft.options.save();
      }

   }

   private boolean isOffsetNextToMainGUI() {
      return this.xOffset == 86;
   }

   public void recipesUpdated() {
      this.updateTabs();
      if (this.isVisible()) {
         this.updateCollections(false);
      }

   }

   public void recipesShown(List<Recipe<?>> p_100344_) {
      for(Recipe<?> recipe : p_100344_) {
         this.minecraft.player.removeRecipeHighlight(recipe);
      }

   }

   public void setupGhostRecipe(Recipe<?> p_100316_, List<Slot> p_100317_) {
      ItemStack itemstack = p_100316_.getResultItem();
      this.ghostRecipe.setRecipe(p_100316_);
      this.ghostRecipe.addIngredient(Ingredient.of(itemstack), (p_100317_.get(0)).x, (p_100317_.get(0)).y);
      this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), p_100316_, p_100316_.getIngredients().iterator(), 0);
   }

   public void addItemToSlot(Iterator<Ingredient> p_100338_, int p_100339_, int p_100340_, int p_100341_, int p_100342_) {
      Ingredient ingredient = p_100338_.next();
      if (!ingredient.isEmpty()) {
         Slot slot = this.menu.slots.get(p_100339_);
         this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
      }

   }

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         RecipeBookType recipebooktype = this.menu.getRecipeBookType();
         boolean flag = this.book.getBookSettings().isOpen(recipebooktype);
         boolean flag1 = this.book.getBookSettings().isFiltering(recipebooktype);
         this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(recipebooktype, flag, flag1));
      }

   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
   }

   public void updateNarration(NarrationElementOutput p_170046_) {
      List<NarratableEntry> list = Lists.newArrayList();
      this.recipeBookPage.listButtons((p_170049_) -> {
         if (p_170049_.isActive()) {
            list.add(p_170049_);
         }

      });
      list.add(this.searchBox);
      list.add(this.filterButton);
      list.addAll(this.tabButtons);
      Screen.NarratableSearchResult screen$narratablesearchresult = Screen.findNarratableWidget(list, (NarratableEntry)null);
      if (screen$narratablesearchresult != null) {
         screen$narratablesearchresult.entry.updateNarration(p_170046_.nest());
      }

   }
}
