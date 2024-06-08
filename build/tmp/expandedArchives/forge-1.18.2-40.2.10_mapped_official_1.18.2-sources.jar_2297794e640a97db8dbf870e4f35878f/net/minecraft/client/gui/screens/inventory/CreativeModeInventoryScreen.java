package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeModeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
   private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final String GUI_CREATIVE_TAB_PREFIX = "textures/gui/container/creative_inventory/tab_";
   private static final String CUSTOM_SLOT_LOCK = "CustomCreativeLock";
   private static final int NUM_ROWS = 5;
   private static final int NUM_COLS = 9;
   private static final int TAB_WIDTH = 28;
   private static final int TAB_HEIGHT = 32;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static final Component TRASH_SLOT_TOOLTIP = new TranslatableComponent("inventory.binSlot");
   private static final int TEXT_COLOR = 16777215;
   private static int selectedTab = CreativeModeTab.TAB_BUILDING_BLOCKS.getId();
   private float scrollOffs;
   private boolean scrolling;
   private EditBox searchBox;
   @Nullable
   private List<Slot> originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeInventoryListener listener;
   private boolean ignoreTextInput;
   private static int tabPage = 0;
   private int maxPages = 0;
   private boolean hasClickedOutside;
   private final Set<TagKey<Item>> visibleTags = new HashSet<>();

   public CreativeModeInventoryScreen(Player p_98519_) {
      super(new CreativeModeInventoryScreen.ItemPickerMenu(p_98519_), p_98519_.getInventory(), TextComponent.EMPTY);
      p_98519_.containerMenu = this.menu;
      this.passEvents = true;
      this.imageHeight = 136;
      this.imageWidth = 195;
   }

   public void containerTick() {
      super.containerTick();
      if (!this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      } else if (this.searchBox != null) {
         this.searchBox.tick();
      }

   }

   protected void slotClicked(@Nullable Slot p_98556_, int p_98557_, int p_98558_, ClickType p_98559_) {
      if (this.isCreativeSlot(p_98556_)) {
         this.searchBox.moveCursorToEnd();
         this.searchBox.setHighlightPos(0);
      }

      boolean flag = p_98559_ == ClickType.QUICK_MOVE;
      p_98559_ = p_98557_ == -999 && p_98559_ == ClickType.PICKUP ? ClickType.THROW : p_98559_;
      if (p_98556_ == null && selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && p_98559_ != ClickType.QUICK_CRAFT) {
         if (!this.menu.getCarried().isEmpty() && this.hasClickedOutside) {
            if (p_98558_ == 0) {
               this.minecraft.player.drop(this.menu.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
               this.menu.setCarried(ItemStack.EMPTY);
            }

            if (p_98558_ == 1) {
               ItemStack itemstack5 = this.menu.getCarried().split(1);
               this.minecraft.player.drop(itemstack5, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack5);
            }
         }
      } else {
         if (p_98556_ != null && !p_98556_.mayPickup(this.minecraft.player)) {
            return;
         }

         if (p_98556_ == this.destroyItemSlot && flag) {
            for(int j = 0; j < this.minecraft.player.inventoryMenu.getItems().size(); ++j) {
               this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, j);
            }
         } else if (selectedTab == CreativeModeTab.TAB_INVENTORY.getId()) {
            if (p_98556_ == this.destroyItemSlot) {
               this.menu.setCarried(ItemStack.EMPTY);
            } else if (p_98559_ == ClickType.THROW && p_98556_ != null && p_98556_.hasItem()) {
               ItemStack itemstack = p_98556_.remove(p_98558_ == 0 ? 1 : p_98556_.getItem().getMaxStackSize());
               ItemStack itemstack1 = p_98556_.getItem();
               this.minecraft.player.drop(itemstack, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack);
               this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack1, ((CreativeModeInventoryScreen.SlotWrapper)p_98556_).target.index);
            } else if (p_98559_ == ClickType.THROW && !this.menu.getCarried().isEmpty()) {
               this.minecraft.player.drop(this.menu.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
               this.menu.setCarried(ItemStack.EMPTY);
            } else {
               this.minecraft.player.inventoryMenu.clicked(p_98556_ == null ? p_98557_ : ((CreativeModeInventoryScreen.SlotWrapper)p_98556_).target.index, p_98558_, p_98559_, this.minecraft.player);
               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         } else if (p_98559_ != ClickType.QUICK_CRAFT && p_98556_.container == CONTAINER) {
            ItemStack itemstack4 = this.menu.getCarried();
            ItemStack itemstack7 = p_98556_.getItem();
            if (p_98559_ == ClickType.SWAP) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack10 = itemstack7.copy();
                  itemstack10.setCount(itemstack10.getMaxStackSize());
                  this.minecraft.player.getInventory().setItem(p_98558_, itemstack10);
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }

               return;
            }

            if (p_98559_ == ClickType.CLONE) {
               if (this.menu.getCarried().isEmpty() && p_98556_.hasItem()) {
                  ItemStack itemstack9 = p_98556_.getItem().copy();
                  itemstack9.setCount(itemstack9.getMaxStackSize());
                  this.menu.setCarried(itemstack9);
               }

               return;
            }

            if (p_98559_ == ClickType.THROW) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack8 = itemstack7.copy();
                  itemstack8.setCount(p_98558_ == 0 ? 1 : itemstack8.getMaxStackSize());
                  this.minecraft.player.drop(itemstack8, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack8);
               }

               return;
            }

            if (!itemstack4.isEmpty() && !itemstack7.isEmpty() && itemstack4.sameItem(itemstack7) && ItemStack.tagMatches(itemstack4, itemstack7)) {
               if (p_98558_ == 0) {
                  if (flag) {
                     itemstack4.setCount(itemstack4.getMaxStackSize());
                  } else if (itemstack4.getCount() < itemstack4.getMaxStackSize()) {
                     itemstack4.grow(1);
                  }
               } else {
                  itemstack4.shrink(1);
               }
            } else if (!itemstack7.isEmpty() && itemstack4.isEmpty()) {
               this.menu.setCarried(itemstack7.copy());
               itemstack4 = this.menu.getCarried();
               if (flag) {
                  itemstack4.setCount(itemstack4.getMaxStackSize());
               }
            } else if (p_98558_ == 0) {
               this.menu.setCarried(ItemStack.EMPTY);
            } else {
               this.menu.getCarried().shrink(1);
            }
         } else if (this.menu != null) {
            ItemStack itemstack3 = p_98556_ == null ? ItemStack.EMPTY : this.menu.getSlot(p_98556_.index).getItem();
            this.menu.clicked(p_98556_ == null ? p_98557_ : p_98556_.index, p_98558_, p_98559_, this.minecraft.player);
            if (AbstractContainerMenu.getQuickcraftHeader(p_98558_) == 2) {
               for(int k = 0; k < 9; ++k) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(this.menu.getSlot(45 + k).getItem(), 36 + k);
               }
            } else if (p_98556_ != null) {
               ItemStack itemstack6 = this.menu.getSlot(p_98556_.index).getItem();
               this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack6, p_98556_.index - (this.menu).slots.size() + 9 + 36);
               int i = 45 + p_98558_;
               if (p_98559_ == ClickType.SWAP) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack3, i - (this.menu).slots.size() + 9 + 36);
               } else if (p_98559_ == ClickType.THROW && !itemstack3.isEmpty()) {
                  ItemStack itemstack2 = itemstack3.copy();
                  itemstack2.setCount(p_98558_ == 0 ? 1 : itemstack2.getMaxStackSize());
                  this.minecraft.player.drop(itemstack2, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack2);
               }

               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         }
      }

   }

   private boolean isCreativeSlot(@Nullable Slot p_98554_) {
      return p_98554_ != null && p_98554_.container == CONTAINER;
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         int tabCount = CreativeModeTab.TABS.length;
         if (tabCount > 12) {
            addRenderableWidget(new net.minecraft.client.gui.components.Button(leftPos,              topPos - 50, 20, 20, new TextComponent("<"), b -> tabPage = Math.max(tabPage - 1, 0       )));
            addRenderableWidget(new net.minecraft.client.gui.components.Button(leftPos + imageWidth - 20, topPos - 50, 20, 20, new TextComponent(">"), b -> tabPage = Math.min(tabPage + 1, maxPages)));
            maxPages = (int) Math.ceil((tabCount - 12) / 10D);
         }
         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, new TranslatableComponent("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.addWidget(this.searchBox);
         int i = selectedTab;
         selectedTab = -1;
         this.selectTab(CreativeModeTab.TABS[i]);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeInventoryListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
      } else {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft p_98595_, int p_98596_, int p_98597_) {
      String s = this.searchBox.getValue();
      this.init(p_98595_, p_98596_, p_98597_);
      this.searchBox.setValue(s);
      if (!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }

   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean charTyped(char p_98521_, int p_98522_) {
      if (this.ignoreTextInput) {
         return false;
      } else if (!CreativeModeTab.TABS[selectedTab].hasSearchBar()) {
         return false;
      } else {
         String s = this.searchBox.getValue();
         if (this.searchBox.charTyped(p_98521_, p_98522_)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
               this.refreshSearchResults();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
      this.ignoreTextInput = false;
      if (!CreativeModeTab.TABS[selectedTab].hasSearchBar()) {
         if (this.minecraft.options.keyChat.matches(p_98547_, p_98548_)) {
            this.ignoreTextInput = true;
            this.selectTab(CreativeModeTab.TAB_SEARCH);
            return true;
         } else {
            return super.keyPressed(p_98547_, p_98548_, p_98549_);
         }
      } else {
         boolean flag = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
         boolean flag1 = InputConstants.getKey(p_98547_, p_98548_).getNumericKeyValue().isPresent();
         if (flag && flag1 && this.checkHotbarKeyPressed(p_98547_, p_98548_)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
               if (!Objects.equals(s, this.searchBox.getValue())) {
                  this.refreshSearchResults();
               }

               return true;
            } else {
               return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 ? true : super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
         }
      }
   }

   public boolean keyReleased(int p_98612_, int p_98613_, int p_98614_) {
      this.ignoreTextInput = false;
      return super.keyReleased(p_98612_, p_98613_, p_98614_);
   }

   private void refreshSearchResults() {
      (this.menu).items.clear();
      this.visibleTags.clear();

      CreativeModeTab tab = CreativeModeTab.TABS[selectedTab];
      if (tab.hasSearchBar() && tab != CreativeModeTab.TAB_SEARCH) {
         tab.fillItemList(menu.items);
         if (!this.searchBox.getValue().isEmpty()) {
            //TODO: Make this a SearchTree not a manual search
            String search = this.searchBox.getValue().toLowerCase(Locale.ROOT);
            java.util.Iterator<ItemStack> itr = menu.items.iterator();
            while (itr.hasNext()) {
               ItemStack stack = itr.next();
               boolean matches = false;
               for (Component line : stack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL)) {
                  if (ChatFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                     matches = true;
                     break;
                  }
               }
               if (!matches)
                  itr.remove();
            }
         }
         this.scrollOffs = 0.0F;
         menu.scrollTo(0.0F);
         return;
      }

      String s = this.searchBox.getValue();
      if (s.isEmpty()) {
         for(Item item : Registry.ITEM) {
            item.fillItemCategory(CreativeModeTab.TAB_SEARCH, (this.menu).items);
         }
      } else {
         SearchTree<ItemStack> searchtree;
         if (s.startsWith("#")) {
            s = s.substring(1);
            searchtree = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
            this.updateVisibleTags(s);
         } else {
            searchtree = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
         }

         (this.menu).items.addAll(searchtree.search(s.toLowerCase(Locale.ROOT)));
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   private void updateVisibleTags(String p_98620_) {
      int i = p_98620_.indexOf(58);
      Predicate<ResourceLocation> predicate;
      if (i == -1) {
         predicate = (p_98609_) -> {
            return p_98609_.getPath().contains(p_98620_);
         };
      } else {
         String s = p_98620_.substring(0, i).trim();
         String s1 = p_98620_.substring(i + 1).trim();
         predicate = (p_98606_) -> {
            return p_98606_.getNamespace().contains(s) && p_98606_.getPath().contains(s1);
         };
      }

      Registry.ITEM.getTagNames().filter((p_205410_) -> {
         return predicate.test(p_205410_.location());
      }).forEach(this.visibleTags::add);
   }

   protected void renderLabels(PoseStack p_98616_, int p_98617_, int p_98618_) {
      CreativeModeTab creativemodetab = CreativeModeTab.TABS[selectedTab];
      if (creativemodetab != null && creativemodetab.showTitle()) {
         RenderSystem.disableBlend();
         this.font.draw(p_98616_, creativemodetab.getDisplayName(), 8.0F, 6.0F, creativemodetab.getLabelColor());
      }

   }

   public boolean mouseClicked(double p_98531_, double p_98532_, int p_98533_) {
      if (p_98533_ == 0) {
         double d0 = p_98531_ - (double)this.leftPos;
         double d1 = p_98532_ - (double)this.topPos;

         for(CreativeModeTab creativemodetab : CreativeModeTab.TABS) {
            if (creativemodetab != null && this.checkTabClicked(creativemodetab, d0, d1)) {
               return true;
            }
         }

         if (selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && this.insideScrollbar(p_98531_, p_98532_)) {
            this.scrolling = this.canScroll();
            return true;
         }
      }

      return super.mouseClicked(p_98531_, p_98532_, p_98533_);
   }

   public boolean mouseReleased(double p_98622_, double p_98623_, int p_98624_) {
      if (p_98624_ == 0) {
         double d0 = p_98622_ - (double)this.leftPos;
         double d1 = p_98623_ - (double)this.topPos;
         this.scrolling = false;

         for(CreativeModeTab creativemodetab : CreativeModeTab.TABS) {
            if (creativemodetab != null && this.checkTabClicked(creativemodetab, d0, d1)) {
               this.selectTab(creativemodetab);
               return true;
            }
         }
      }

      return super.mouseReleased(p_98622_, p_98623_, p_98624_);
   }

   private boolean canScroll() {
      if (CreativeModeTab.TABS[selectedTab] == null) return false;
      return selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && CreativeModeTab.TABS[selectedTab].canScroll() && this.menu.canScroll();
   }

   private void selectTab(CreativeModeTab p_98561_) {
      if (p_98561_ == null) return;
      int i = selectedTab;
      selectedTab = p_98561_.getId();
      slotColor = p_98561_.getSlotColor();
      this.quickCraftSlots.clear();
      (this.menu).items.clear();
      if (p_98561_ == CreativeModeTab.TAB_HOTBAR) {
         HotbarManager hotbarmanager = this.minecraft.getHotbarManager();

         for(int j = 0; j < 9; ++j) {
            Hotbar hotbar = hotbarmanager.get(j);
            if (hotbar.isEmpty()) {
               for(int k = 0; k < 9; ++k) {
                  if (k == j) {
                     ItemStack itemstack = new ItemStack(Items.PAPER);
                     itemstack.getOrCreateTagElement("CustomCreativeLock");
                     Component component = this.minecraft.options.keyHotbarSlots[j].getTranslatedKeyMessage();
                     Component component1 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     itemstack.setHoverName(new TranslatableComponent("inventory.hotbarInfo", component1, component));
                     (this.menu).items.add(itemstack);
                  } else {
                     (this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               (this.menu).items.addAll(hotbar);
            }
         }
      } else if (p_98561_ != CreativeModeTab.TAB_SEARCH) {
         p_98561_.fillItemList((this.menu).items);
      }

      if (p_98561_ == CreativeModeTab.TAB_INVENTORY) {
         AbstractContainerMenu abstractcontainermenu = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf((this.menu).slots);
         }

         (this.menu).slots.clear();

         for(int l = 0; l < abstractcontainermenu.slots.size(); ++l) {
            int i1;
            int j1;
            if (l >= 5 && l < 9) {
               int l1 = l - 5;
               int j2 = l1 / 2;
               int l2 = l1 % 2;
               i1 = 54 + j2 * 54;
               j1 = 6 + l2 * 27;
            } else if (l >= 0 && l < 5) {
               i1 = -2000;
               j1 = -2000;
            } else if (l == 45) {
               i1 = 35;
               j1 = 20;
            } else {
               int k1 = l - 9;
               int i2 = k1 % 9;
               int k2 = k1 / 9;
               i1 = 9 + i2 * 18;
               if (l >= 36) {
                  j1 = 112;
               } else {
                  j1 = 54 + k2 * 18;
               }
            }

            Slot slot = new CreativeModeInventoryScreen.SlotWrapper(abstractcontainermenu.slots.get(l), l, i1, j1);
            (this.menu).slots.add(slot);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         (this.menu).slots.add(this.destroyItemSlot);
      } else if (i == CreativeModeTab.TAB_INVENTORY.getId()) {
         (this.menu).slots.clear();
         (this.menu).slots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if (this.searchBox != null) {
         if (p_98561_.hasSearchBar()) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocus(true);
            if (i != p_98561_.getId()) {
               this.searchBox.setValue("");
            }
            this.searchBox.setWidth(p_98561_.getSearchbarWidth());
            this.searchBox.x = this.leftPos + (82 /*default left*/ + 89 /*default width*/) - this.searchBox.getWidth();

            this.refreshSearchResults();
         } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocus(false);
            this.searchBox.setValue("");
         }
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_) {
      if (!this.canScroll()) {
         return false;
      } else {
         int i = ((this.menu).items.size() + 9 - 1) / 9 - 5;
         float f = (float)(p_98529_ / (double)i);
         this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_98541_, double p_98542_, int p_98543_, int p_98544_, int p_98545_) {
      boolean flag = p_98541_ < (double)p_98543_ || p_98542_ < (double)p_98544_ || p_98541_ >= (double)(p_98543_ + this.imageWidth) || p_98542_ >= (double)(p_98544_ + this.imageHeight);
      this.hasClickedOutside = flag && !this.checkTabClicked(CreativeModeTab.TABS[selectedTab], p_98541_, p_98542_);
      return this.hasClickedOutside;
   }

   protected boolean insideScrollbar(double p_98524_, double p_98525_) {
      int i = this.leftPos;
      int j = this.topPos;
      int k = i + 175;
      int l = j + 18;
      int i1 = k + 14;
      int j1 = l + 112;
      return p_98524_ >= (double)k && p_98525_ >= (double)l && p_98524_ < (double)i1 && p_98525_ < (double)j1;
   }

   public boolean mouseDragged(double p_98535_, double p_98536_, int p_98537_, double p_98538_, double p_98539_) {
      if (this.scrolling) {
         int i = this.topPos + 18;
         int j = i + 112;
         this.scrollOffs = ((float)p_98536_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(p_98535_, p_98536_, p_98537_, p_98538_, p_98539_);
      }
   }

   public void render(PoseStack p_98577_, int p_98578_, int p_98579_, float p_98580_) {
      this.renderBackground(p_98577_);
      super.render(p_98577_, p_98578_, p_98579_, p_98580_);

      int start = tabPage * 10;
      int end = Math.min(CreativeModeTab.TABS.length, ((tabPage + 1) * 10) + 2);
      if (tabPage != 0) start += 2;
      boolean rendered = false;

      for (int x = start; x < end; x++) {
         CreativeModeTab creativemodetab = CreativeModeTab.TABS[x];
         if (creativemodetab != null && this.checkTabHovering(p_98577_, creativemodetab, p_98578_, p_98579_)) {
            rendered = true;
            break;
         }
      }
      if (!rendered && !this.checkTabHovering(p_98577_, CreativeModeTab.TAB_SEARCH, p_98578_, p_98579_))
         this.checkTabHovering(p_98577_, CreativeModeTab.TAB_INVENTORY, p_98578_, p_98579_);

      if (this.destroyItemSlot != null && selectedTab == CreativeModeTab.TAB_INVENTORY.getId() && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)p_98578_, (double)p_98579_)) {
         this.renderTooltip(p_98577_, TRASH_SLOT_TOOLTIP, p_98578_, p_98579_);
      }

      if (maxPages != 0) {
          Component page = new TextComponent(String.format("%d / %d", tabPage + 1, maxPages + 1));
          this.setBlitOffset(300);
          this.itemRenderer.blitOffset = 300.0F;
          font.drawShadow(p_98577_, page.getVisualOrderText(), leftPos + (imageWidth / 2) - (font.width(page) / 2), topPos - 44, -1);
          this.setBlitOffset(0);
          this.itemRenderer.blitOffset = 0.0F;
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderTooltip(p_98577_, p_98578_, p_98579_);
   }

   protected void renderTooltip(PoseStack p_98590_, ItemStack p_98591_, int p_98592_, int p_98593_) {
      if (selectedTab == CreativeModeTab.TAB_SEARCH.getId()) {
         List<Component> list = p_98591_.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
         List<Component> list1 = Lists.newArrayList(list);
         Item item = p_98591_.getItem();
         CreativeModeTab creativemodetab = item.getItemCategory();
         if (creativemodetab == null && p_98591_.is(Items.ENCHANTED_BOOK)) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_98591_);
            if (map.size() == 1) {
               Enchantment enchantment = map.keySet().iterator().next();

               for(CreativeModeTab creativemodetab1 : CreativeModeTab.TABS) {
                  if (creativemodetab1.hasEnchantmentCategory(enchantment.category)) {
                     creativemodetab = creativemodetab1;
                     break;
                  }
               }
            }
         }

         this.visibleTags.forEach((p_205407_) -> {
            if (p_98591_.is(p_205407_)) {
               list1.add(1, (new TextComponent("#" + p_205407_.location())).withStyle(ChatFormatting.DARK_PURPLE));
            }

         });
         if (creativemodetab != null) {
            list1.add(1, creativemodetab.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
         }

         this.renderTooltip(p_98590_, list1, p_98591_.getTooltipImage(), p_98592_, p_98593_, p_98591_);
      } else {
         super.renderTooltip(p_98590_, p_98591_, p_98592_, p_98593_);
      }

   }

   protected void renderBg(PoseStack p_98572_, float p_98573_, int p_98574_, int p_98575_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      CreativeModeTab creativemodetab = CreativeModeTab.TABS[selectedTab];

      int start = tabPage * 10;
      int end = Math.min(CreativeModeTab.TABS.length, ((tabPage + 1) * 10 + 2));
      if (tabPage != 0) start += 2;

      for (int idx = start; idx < end; idx++) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         CreativeModeTab creativemodetab1 = CreativeModeTab.TABS[idx];
         if (creativemodetab1 != null && creativemodetab1.getId() != selectedTab) {
            RenderSystem.setShaderTexture(0, creativemodetab1.getTabsImage());
            this.renderTabButton(p_98572_, creativemodetab1);
         }
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      if (tabPage != 0) {
         if (creativemodetab != CreativeModeTab.TAB_SEARCH) {
            RenderSystem.setShaderTexture(0, CreativeModeTab.TAB_SEARCH.getTabsImage());
            renderTabButton(p_98572_, CreativeModeTab.TAB_SEARCH);
         }
         if (creativemodetab != CreativeModeTab.TAB_INVENTORY) {
            RenderSystem.setShaderTexture(0, CreativeModeTab.TAB_INVENTORY.getTabsImage());
            renderTabButton(p_98572_, CreativeModeTab.TAB_INVENTORY);
         }
      }

      RenderSystem.setShaderTexture(0, creativemodetab.getBackgroundImage());
      this.blit(p_98572_, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.searchBox.render(p_98572_, p_98574_, p_98575_, p_98573_);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.leftPos + 175;
      int j = this.topPos + 18;
      int k = j + 112;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, creativemodetab.getTabsImage());
      if (creativemodetab.canScroll()) {
         this.blit(p_98572_, i, j + (int)((float)(k - j - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
      }

      if ((creativemodetab == null || creativemodetab.getTabPage() != tabPage) && (creativemodetab != CreativeModeTab.TAB_SEARCH && creativemodetab != CreativeModeTab.TAB_INVENTORY))
         return;

      this.renderTabButton(p_98572_, creativemodetab);
      if (creativemodetab == CreativeModeTab.TAB_INVENTORY) {
         InventoryScreen.renderEntityInInventory(this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - p_98574_), (float)(this.topPos + 45 - 30 - p_98575_), this.minecraft.player);
      }

   }

   protected boolean checkTabClicked(CreativeModeTab p_98563_, double p_98564_, double p_98565_) {
      if (p_98563_.getTabPage() != tabPage && p_98563_ != CreativeModeTab.TAB_SEARCH && p_98563_ != CreativeModeTab.TAB_INVENTORY) return false;
      int i = p_98563_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_98563_.isAlignedRight()) {
         j = this.imageWidth - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (p_98563_.isTopRow()) {
         k -= 32;
      } else {
         k += this.imageHeight;
      }

      return p_98564_ >= (double)j && p_98564_ <= (double)(j + 28) && p_98565_ >= (double)k && p_98565_ <= (double)(k + 32);
   }

   protected boolean checkTabHovering(PoseStack p_98585_, CreativeModeTab p_98586_, int p_98587_, int p_98588_) {
      int i = p_98586_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_98586_.isAlignedRight()) {
         j = this.imageWidth - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (p_98586_.isTopRow()) {
         k -= 32;
      } else {
         k += this.imageHeight;
      }

      if (this.isHovering(j + 3, k + 3, 23, 27, (double)p_98587_, (double)p_98588_)) {
         this.renderTooltip(p_98585_, p_98586_.getDisplayName(), p_98587_, p_98588_);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(PoseStack p_98582_, CreativeModeTab p_98583_) {
      boolean flag = p_98583_.getId() == selectedTab;
      boolean flag1 = p_98583_.isTopRow();
      int i = p_98583_.getColumn();
      int j = i * 28;
      int k = 0;
      int l = this.leftPos + 28 * i;
      int i1 = this.topPos;
      int j1 = 32;
      if (flag) {
         k += 32;
      }

      if (p_98583_.isAlignedRight()) {
         l = this.leftPos + this.imageWidth - 28 * (6 - i);
      } else if (i > 0) {
         l += i;
      }

      if (flag1) {
         i1 -= 28;
      } else {
         k += 64;
         i1 += this.imageHeight - 4;
      }

      RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
      this.blit(p_98582_, l, i1, j, k, 28, 32);
      this.itemRenderer.blitOffset = 100.0F;
      l += 6;
      i1 += 8 + (flag1 ? 1 : -1);
      ItemStack itemstack = p_98583_.getIconItem();
      this.itemRenderer.renderAndDecorateItem(itemstack, l, i1);
      this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, l, i1);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public int getSelectedTab() {
      return selectedTab;
   }

   public static void handleHotbarLoadOrSave(Minecraft p_98599_, int p_98600_, boolean p_98601_, boolean p_98602_) {
      LocalPlayer localplayer = p_98599_.player;
      HotbarManager hotbarmanager = p_98599_.getHotbarManager();
      Hotbar hotbar = hotbarmanager.get(p_98600_);
      if (p_98601_) {
         for(int i = 0; i < Inventory.getSelectionSize(); ++i) {
            ItemStack itemstack = hotbar.get(i).copy();
            localplayer.getInventory().setItem(i, itemstack);
            p_98599_.gameMode.handleCreativeModeItemAdd(itemstack, 36 + i);
         }

         localplayer.inventoryMenu.broadcastChanges();
      } else if (p_98602_) {
         for(int j = 0; j < Inventory.getSelectionSize(); ++j) {
            hotbar.set(j, localplayer.getInventory().getItem(j).copy());
         }

         Component component = p_98599_.options.keyHotbarSlots[p_98600_].getTranslatedKeyMessage();
         Component component1 = p_98599_.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         p_98599_.gui.setOverlayMessage(new TranslatableComponent("inventory.hotbarSaved", component1, component), false);
         hotbarmanager.save();
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(Container p_98633_, int p_98634_, int p_98635_, int p_98636_) {
         super(p_98633_, p_98634_, p_98635_, p_98636_);
      }

      public boolean mayPickup(Player p_98638_) {
         if (super.mayPickup(p_98638_) && this.hasItem()) {
            return this.getItem().getTagElement("CustomCreativeLock") == null;
         } else {
            return !this.hasItem();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList<ItemStack> items = NonNullList.create();
      private final AbstractContainerMenu inventoryMenu;

      public ItemPickerMenu(Player p_98641_) {
         super((MenuType<?>)null, 0);
         this.inventoryMenu = p_98641_.inventoryMenu;
         Inventory inventory = p_98641_.getInventory();

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 9; ++j) {
               this.addSlot(new CreativeModeInventoryScreen.CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean stillValid(Player p_98645_) {
         return true;
      }

      public void scrollTo(float p_98643_) {
         int i = (this.items.size() + 9 - 1) / 9 - 5;
         int j = (int)((double)(p_98643_ * (float)i) + 0.5D);
         if (j < 0) {
            j = 0;
         }

         for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 9; ++l) {
               int i1 = l + (k + j) * 9;
               if (i1 >= 0 && i1 < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(l + k * 9, this.items.get(i1));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(l + k * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      public ItemStack quickMoveStack(Player p_98650_, int p_98651_) {
         if (p_98651_ >= this.slots.size() - 9 && p_98651_ < this.slots.size()) {
            Slot slot = this.slots.get(p_98651_);
            if (slot != null && slot.hasItem()) {
               slot.set(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canTakeItemForPickAll(ItemStack p_98647_, Slot p_98648_) {
         return p_98648_.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public boolean canDragTo(Slot p_98653_) {
         return p_98653_.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public ItemStack getCarried() {
         return this.inventoryMenu.getCarried();
      }

      public void setCarried(ItemStack p_169751_) {
         this.inventoryMenu.setCarried(p_169751_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class SlotWrapper extends Slot {
      final Slot target;

      public SlotWrapper(Slot p_98657_, int p_98658_, int p_98659_, int p_98660_) {
         super(p_98657_.container, p_98658_, p_98659_, p_98660_);
         this.target = p_98657_;
      }

      public void onTake(Player p_169754_, ItemStack p_169755_) {
         this.target.onTake(p_169754_, p_169755_);
      }

      public boolean mayPlace(ItemStack p_98670_) {
         return this.target.mayPlace(p_98670_);
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public boolean hasItem() {
         return this.target.hasItem();
      }

      public void set(ItemStack p_98679_) {
         this.target.set(p_98679_);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack p_98675_) {
         return this.target.getMaxStackSize(p_98675_);
      }

      @Nullable
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      public ItemStack remove(int p_98663_) {
         return this.target.remove(p_98663_);
      }

      public boolean isActive() {
         return this.target.isActive();
      }

      public boolean mayPickup(Player p_98665_) {
         return this.target.mayPickup(p_98665_);
      }

      @Override
      public int getSlotIndex() {
         return this.target.getSlotIndex();
      }

      @Override
      public boolean isSameInventory(Slot other) {
         return this.target.isSameInventory(other);
      }

      @Override
      public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
         this.target.setBackground(atlas, sprite);
         return this;
      }
   }
}
