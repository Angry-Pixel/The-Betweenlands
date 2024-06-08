package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatsScreen extends Screen implements StatsUpdateListener {
   private static final Component PENDING_TEXT = new TranslatableComponent("multiplayer.downloadingStats");
   protected final Screen lastScreen;
   private StatsScreen.GeneralStatisticsList statsList;
   StatsScreen.ItemStatisticsList itemStatsList;
   private StatsScreen.MobsStatisticsList mobsStatsList;
   final StatsCounter stats;
   @Nullable
   private ObjectSelectionList<?> activeList;
   private boolean isLoading = true;
   private static final int SLOT_TEX_SIZE = 128;
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   private static final int SLOT_LEFT_INSERT = 40;
   private static final int SLOT_TEXT_OFFSET = 5;
   private static final int SORT_NONE = 0;
   private static final int SORT_DOWN = -1;
   private static final int SORT_UP = 1;

   public StatsScreen(Screen p_96906_, StatsCounter p_96907_) {
      super(new TranslatableComponent("gui.stats"));
      this.lastScreen = p_96906_;
      this.stats = p_96907_;
   }

   protected void init() {
      this.isLoading = true;
      this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
   }

   public void initLists() {
      this.statsList = new StatsScreen.GeneralStatisticsList(this.minecraft);
      this.itemStatsList = new StatsScreen.ItemStatisticsList(this.minecraft);
      this.mobsStatsList = new StatsScreen.MobsStatisticsList(this.minecraft);
   }

   public void initButtons() {
      this.addRenderableWidget(new Button(this.width / 2 - 120, this.height - 52, 80, 20, new TranslatableComponent("stat.generalButton"), (p_96963_) -> {
         this.setActiveList(this.statsList);
      }));
      Button button = this.addRenderableWidget(new Button(this.width / 2 - 40, this.height - 52, 80, 20, new TranslatableComponent("stat.itemsButton"), (p_96959_) -> {
         this.setActiveList(this.itemStatsList);
      }));
      Button button1 = this.addRenderableWidget(new Button(this.width / 2 + 40, this.height - 52, 80, 20, new TranslatableComponent("stat.mobsButton"), (p_96949_) -> {
         this.setActiveList(this.mobsStatsList);
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 28, 200, 20, CommonComponents.GUI_DONE, (p_96923_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      if (this.itemStatsList.children().isEmpty()) {
         button.active = false;
      }

      if (this.mobsStatsList.children().isEmpty()) {
         button1.active = false;
      }

   }

   public void render(PoseStack p_96913_, int p_96914_, int p_96915_, float p_96916_) {
      if (this.isLoading) {
         this.renderBackground(p_96913_);
         drawCenteredString(p_96913_, this.font, PENDING_TEXT, this.width / 2, this.height / 2, 16777215);
         drawCenteredString(p_96913_, this.font, LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)], this.width / 2, this.height / 2 + 9 * 2, 16777215);
      } else {
         this.getActiveList().render(p_96913_, p_96914_, p_96915_, p_96916_);
         drawCenteredString(p_96913_, this.font, this.title, this.width / 2, 20, 16777215);
         super.render(p_96913_, p_96914_, p_96915_, p_96916_);
      }

   }

   public void onStatsUpdated() {
      if (this.isLoading) {
         this.initLists();
         this.initButtons();
         this.setActiveList(this.statsList);
         this.isLoading = false;
      }

   }

   public boolean isPauseScreen() {
      return !this.isLoading;
   }

   @Nullable
   public ObjectSelectionList<?> getActiveList() {
      return this.activeList;
   }

   public void setActiveList(@Nullable ObjectSelectionList<?> p_96925_) {
      if (this.activeList != null) {
         this.removeWidget(this.activeList);
      }

      if (p_96925_ != null) {
         this.addWidget(p_96925_);
         this.activeList = p_96925_;
      }

   }

   static String getTranslationKey(Stat<ResourceLocation> p_96947_) {
      return "stat." + p_96947_.getValue().toString().replace(':', '.');
   }

   int getColumnX(int p_96909_) {
      return 115 + 40 * p_96909_;
   }

   void blitSlot(PoseStack p_96918_, int p_96919_, int p_96920_, Item p_96921_) {
      this.blitSlotIcon(p_96918_, p_96919_ + 1, p_96920_ + 1, 0, 0);
      this.itemRenderer.renderGuiItem(p_96921_.getDefaultInstance(), p_96919_ + 2, p_96920_ + 2);
   }

   void blitSlotIcon(PoseStack p_96953_, int p_96954_, int p_96955_, int p_96956_, int p_96957_) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, STATS_ICON_LOCATION);
      blit(p_96953_, p_96954_, p_96955_, this.getBlitOffset(), (float)p_96956_, (float)p_96957_, 18, 18, 128, 128);
   }

   @OnlyIn(Dist.CLIENT)
   class GeneralStatisticsList extends ObjectSelectionList<StatsScreen.GeneralStatisticsList.Entry> {
      public GeneralStatisticsList(Minecraft p_96995_) {
         super(p_96995_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
         ObjectArrayList<Stat<ResourceLocation>> objectarraylist = new ObjectArrayList<>(Stats.CUSTOM.iterator());
         objectarraylist.sort(Comparator.comparing((p_96997_) -> {
            return I18n.get(StatsScreen.getTranslationKey(p_96997_));
         }));

         for(Stat<ResourceLocation> stat : objectarraylist) {
            this.addEntry(new StatsScreen.GeneralStatisticsList.Entry(stat));
         }

      }

      protected void renderBackground(PoseStack p_96999_) {
         StatsScreen.this.renderBackground(p_96999_);
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ObjectSelectionList.Entry<StatsScreen.GeneralStatisticsList.Entry> {
         private final Stat<ResourceLocation> stat;
         private final Component statDisplay;

         Entry(Stat<ResourceLocation> p_97005_) {
            this.stat = p_97005_;
            this.statDisplay = new TranslatableComponent(StatsScreen.getTranslationKey(p_97005_));
         }

         private String getValueText() {
            return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
         }

         public void render(PoseStack p_97011_, int p_97012_, int p_97013_, int p_97014_, int p_97015_, int p_97016_, int p_97017_, int p_97018_, boolean p_97019_, float p_97020_) {
            GuiComponent.drawString(p_97011_, StatsScreen.this.font, this.statDisplay, p_97014_ + 2, p_97013_ + 1, p_97012_ % 2 == 0 ? 16777215 : 9474192);
            String s = this.getValueText();
            GuiComponent.drawString(p_97011_, StatsScreen.this.font, s, p_97014_ + 2 + 213 - StatsScreen.this.font.width(s), p_97013_ + 1, p_97012_ % 2 == 0 ? 16777215 : 9474192);
         }

         public Component getNarration() {
            return new TranslatableComponent("narrator.select", (new TextComponent("")).append(this.statDisplay).append(" ").append(this.getValueText()));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ItemStatisticsList extends ObjectSelectionList<StatsScreen.ItemStatisticsList.ItemRow> {
      protected final List<StatType<Block>> blockColumns;
      protected final List<StatType<Item>> itemColumns;
      private final int[] iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
      protected int headerPressed = -1;
      protected final Comparator<StatsScreen.ItemStatisticsList.ItemRow> itemStatSorter = new StatsScreen.ItemStatisticsList.ItemRowComparator();
      @Nullable
      protected StatType<?> sortColumn;
      protected int sortOrder;

      public ItemStatisticsList(Minecraft p_97032_) {
         super(p_97032_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
         this.blockColumns = Lists.newArrayList();
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED);
         this.setRenderHeader(true, 20);
         Set<Item> set = Sets.newIdentityHashSet();

         for(Item item : Registry.ITEM) {
            boolean flag = false;

            for(StatType<Item> stattype : this.itemColumns) {
               if (stattype.contains(item) && StatsScreen.this.stats.getValue(stattype.get(item)) > 0) {
                  flag = true;
               }
            }

            if (flag) {
               set.add(item);
            }
         }

         for(Block block : Registry.BLOCK) {
            boolean flag1 = false;

            for(StatType<Block> stattype1 : this.blockColumns) {
               if (stattype1.contains(block) && StatsScreen.this.stats.getValue(stattype1.get(block)) > 0) {
                  flag1 = true;
               }
            }

            if (flag1) {
               set.add(block.asItem());
            }
         }

         set.remove(Items.AIR);

         for(Item item1 : set) {
            this.addEntry(new StatsScreen.ItemStatisticsList.ItemRow(item1));
         }

      }

      protected void renderHeader(PoseStack p_97049_, int p_97050_, int p_97051_, Tesselator p_97052_) {
         if (!this.minecraft.mouseHandler.isLeftPressed()) {
            this.headerPressed = -1;
         }

         for(int i = 0; i < this.iconOffsets.length; ++i) {
            StatsScreen.this.blitSlotIcon(p_97049_, p_97050_ + StatsScreen.this.getColumnX(i) - 18, p_97051_ + 1, 0, this.headerPressed == i ? 0 : 18);
         }

         if (this.sortColumn != null) {
            int k = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            int j = this.sortOrder == 1 ? 2 : 1;
            StatsScreen.this.blitSlotIcon(p_97049_, p_97050_ + k, p_97051_ + 1, 18 * j, 0);
         }

         for(int l = 0; l < this.iconOffsets.length; ++l) {
            int i1 = this.headerPressed == l ? 1 : 0;
            StatsScreen.this.blitSlotIcon(p_97049_, p_97050_ + StatsScreen.this.getColumnX(l) - 18 + i1, p_97051_ + 1 + i1, 18 * this.iconOffsets[l], 18);
         }

      }

      public int getRowWidth() {
         return 375;
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 140;
      }

      protected void renderBackground(PoseStack p_97043_) {
         StatsScreen.this.renderBackground(p_97043_);
      }

      protected void clickedHeader(int p_97036_, int p_97037_) {
         this.headerPressed = -1;

         for(int i = 0; i < this.iconOffsets.length; ++i) {
            int j = p_97036_ - StatsScreen.this.getColumnX(i);
            if (j >= -36 && j <= 0) {
               this.headerPressed = i;
               break;
            }
         }

         if (this.headerPressed >= 0) {
            this.sortByColumn(this.getColumn(this.headerPressed));
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

      }

      private StatType<?> getColumn(int p_97034_) {
         return p_97034_ < this.blockColumns.size() ? this.blockColumns.get(p_97034_) : this.itemColumns.get(p_97034_ - this.blockColumns.size());
      }

      private int getColumnIndex(StatType<?> p_97059_) {
         int i = this.blockColumns.indexOf(p_97059_);
         if (i >= 0) {
            return i;
         } else {
            int j = this.itemColumns.indexOf(p_97059_);
            return j >= 0 ? j + this.blockColumns.size() : -1;
         }
      }

      protected void renderDecorations(PoseStack p_97045_, int p_97046_, int p_97047_) {
         if (p_97047_ >= this.y0 && p_97047_ <= this.y1) {
            StatsScreen.ItemStatisticsList.ItemRow statsscreen$itemstatisticslist$itemrow = this.getHovered();
            int i = (this.width - this.getRowWidth()) / 2;
            if (statsscreen$itemstatisticslist$itemrow != null) {
               if (p_97046_ < i + 40 || p_97046_ > i + 40 + 20) {
                  return;
               }

               Item item = statsscreen$itemstatisticslist$itemrow.getItem();
               this.renderMousehoverTooltip(p_97045_, this.getString(item), p_97046_, p_97047_);
            } else {
               Component component = null;
               int j = p_97046_ - i;

               for(int k = 0; k < this.iconOffsets.length; ++k) {
                  int l = StatsScreen.this.getColumnX(k);
                  if (j >= l - 18 && j <= l) {
                     component = this.getColumn(k).getDisplayName();
                     break;
                  }
               }

               this.renderMousehoverTooltip(p_97045_, component, p_97046_, p_97047_);
            }

         }
      }

      protected void renderMousehoverTooltip(PoseStack p_97054_, @Nullable Component p_97055_, int p_97056_, int p_97057_) {
         if (p_97055_ != null) {
            int i = p_97056_ + 12;
            int j = p_97057_ - 12;
            int k = StatsScreen.this.font.width(p_97055_);
            this.fillGradient(p_97054_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            p_97054_.pushPose();
            p_97054_.translate(0.0D, 0.0D, 400.0D);
            StatsScreen.this.font.drawShadow(p_97054_, p_97055_, (float)i, (float)j, -1);
            p_97054_.popPose();
         }
      }

      protected Component getString(Item p_97041_) {
         return p_97041_.getDescription();
      }

      protected void sortByColumn(StatType<?> p_97039_) {
         if (p_97039_ != this.sortColumn) {
            this.sortColumn = p_97039_;
            this.sortOrder = -1;
         } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
         } else {
            this.sortColumn = null;
            this.sortOrder = 0;
         }

         this.children().sort(this.itemStatSorter);
      }

      @OnlyIn(Dist.CLIENT)
      class ItemRow extends ObjectSelectionList.Entry<StatsScreen.ItemStatisticsList.ItemRow> {
         private final Item item;

         ItemRow(Item p_169517_) {
            this.item = p_169517_;
         }

         public Item getItem() {
            return this.item;
         }

         public void render(PoseStack p_97081_, int p_97082_, int p_97083_, int p_97084_, int p_97085_, int p_97086_, int p_97087_, int p_97088_, boolean p_97089_, float p_97090_) {
            StatsScreen.this.blitSlot(p_97081_, p_97084_ + 40, p_97083_, this.item);

            for(int i = 0; i < StatsScreen.this.itemStatsList.blockColumns.size(); ++i) {
               Stat<Block> stat;
               if (this.item instanceof BlockItem) {
                  stat = StatsScreen.this.itemStatsList.blockColumns.get(i).get(((BlockItem)this.item).getBlock());
               } else {
                  stat = null;
               }

               this.renderStat(p_97081_, stat, p_97084_ + StatsScreen.this.getColumnX(i), p_97083_, p_97082_ % 2 == 0);
            }

            for(int j = 0; j < StatsScreen.this.itemStatsList.itemColumns.size(); ++j) {
               this.renderStat(p_97081_, StatsScreen.this.itemStatsList.itemColumns.get(j).get(this.item), p_97084_ + StatsScreen.this.getColumnX(j + StatsScreen.this.itemStatsList.blockColumns.size()), p_97083_, p_97082_ % 2 == 0);
            }

         }

         protected void renderStat(PoseStack p_97092_, @Nullable Stat<?> p_97093_, int p_97094_, int p_97095_, boolean p_97096_) {
            String s = p_97093_ == null ? "-" : p_97093_.format(StatsScreen.this.stats.getValue(p_97093_));
            GuiComponent.drawString(p_97092_, StatsScreen.this.font, s, p_97094_ - StatsScreen.this.font.width(s), p_97095_ + 5, p_97096_ ? 16777215 : 9474192);
         }

         public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.item.getDescription());
         }
      }

      @OnlyIn(Dist.CLIENT)
      class ItemRowComparator implements Comparator<StatsScreen.ItemStatisticsList.ItemRow> {
         public int compare(StatsScreen.ItemStatisticsList.ItemRow p_169524_, StatsScreen.ItemStatisticsList.ItemRow p_169525_) {
            Item item = p_169524_.getItem();
            Item item1 = p_169525_.getItem();
            int i;
            int j;
            if (ItemStatisticsList.this.sortColumn == null) {
               i = 0;
               j = 0;
            } else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
               StatType<Block> stattype = (StatType<Block>) ItemStatisticsList.this.sortColumn;
               i = item instanceof BlockItem ? StatsScreen.this.stats.getValue(stattype, ((BlockItem)item).getBlock()) : -1;
               j = item1 instanceof BlockItem ? StatsScreen.this.stats.getValue(stattype, ((BlockItem)item1).getBlock()) : -1;
            } else {
               StatType<Item> stattype1 = (StatType<Item>) ItemStatisticsList.this.sortColumn;
               i = StatsScreen.this.stats.getValue(stattype1, item);
               j = StatsScreen.this.stats.getValue(stattype1, item1);
            }

            return i == j ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(item), Item.getId(item1)) : ItemStatisticsList.this.sortOrder * Integer.compare(i, j);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class MobsStatisticsList extends ObjectSelectionList<StatsScreen.MobsStatisticsList.MobRow> {
      public MobsStatisticsList(Minecraft p_97100_) {
         super(p_97100_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);

         for(EntityType<?> entitytype : Registry.ENTITY_TYPE) {
            if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entitytype)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entitytype)) > 0) {
               this.addEntry(new StatsScreen.MobsStatisticsList.MobRow(entitytype));
            }
         }

      }

      protected void renderBackground(PoseStack p_97102_) {
         StatsScreen.this.renderBackground(p_97102_);
      }

      @OnlyIn(Dist.CLIENT)
      class MobRow extends ObjectSelectionList.Entry<StatsScreen.MobsStatisticsList.MobRow> {
         private final Component mobName;
         private final Component kills;
         private final boolean hasKills;
         private final Component killedBy;
         private final boolean wasKilledBy;

         public MobRow(EntityType<?> p_97112_) {
            this.mobName = p_97112_.getDescription();
            int i = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(p_97112_));
            if (i == 0) {
               this.kills = new TranslatableComponent("stat_type.minecraft.killed.none", this.mobName);
               this.hasKills = false;
            } else {
               this.kills = new TranslatableComponent("stat_type.minecraft.killed", i, this.mobName);
               this.hasKills = true;
            }

            int j = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(p_97112_));
            if (j == 0) {
               this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by.none", this.mobName);
               this.wasKilledBy = false;
            } else {
               this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by", this.mobName, j);
               this.wasKilledBy = true;
            }

         }

         public void render(PoseStack p_97114_, int p_97115_, int p_97116_, int p_97117_, int p_97118_, int p_97119_, int p_97120_, int p_97121_, boolean p_97122_, float p_97123_) {
            GuiComponent.drawString(p_97114_, StatsScreen.this.font, this.mobName, p_97117_ + 2, p_97116_ + 1, 16777215);
            GuiComponent.drawString(p_97114_, StatsScreen.this.font, this.kills, p_97117_ + 2 + 10, p_97116_ + 1 + 9, this.hasKills ? 9474192 : 6316128);
            GuiComponent.drawString(p_97114_, StatsScreen.this.font, this.killedBy, p_97117_ + 2 + 10, p_97116_ + 1 + 9 * 2, this.wasKilledBy ? 9474192 : 6316128);
         }

         public Component getNarration() {
            return new TranslatableComponent("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
         }
      }
   }
}