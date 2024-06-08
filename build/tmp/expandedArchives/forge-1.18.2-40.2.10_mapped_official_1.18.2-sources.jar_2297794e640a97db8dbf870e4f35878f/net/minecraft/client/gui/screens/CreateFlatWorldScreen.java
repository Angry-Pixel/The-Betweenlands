package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateFlatWorldScreen extends Screen {
   private static final int SLOT_TEX_SIZE = 128;
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   protected final CreateWorldScreen parent;
   private final Consumer<FlatLevelGeneratorSettings> applySettings;
   FlatLevelGeneratorSettings generator;
   private Component columnType;
   private Component columnHeight;
   private CreateFlatWorldScreen.DetailsList list;
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen p_95822_, Consumer<FlatLevelGeneratorSettings> p_95823_, FlatLevelGeneratorSettings p_95824_) {
      super(new TranslatableComponent("createWorld.customize.flat.title"));
      this.parent = p_95822_;
      this.applySettings = p_95823_;
      this.generator = p_95824_;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.generator;
   }

   public void setConfig(FlatLevelGeneratorSettings p_95826_) {
      this.generator = p_95826_;
   }

   protected void init() {
      this.columnType = new TranslatableComponent("createWorld.customize.flat.tile");
      this.columnHeight = new TranslatableComponent("createWorld.customize.flat.height");
      this.list = new CreateFlatWorldScreen.DetailsList();
      this.addWidget(this.list);
      this.deleteLayerButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 52, 150, 20, new TranslatableComponent("createWorld.customize.flat.removeLayer"), (p_95845_) -> {
         if (this.hasValidSelection()) {
            List<FlatLayerInfo> list = this.generator.getLayersInfo();
            int i = this.list.children().indexOf(this.list.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.list.setSelected(list.isEmpty() ? null : this.list.children().get(Math.min(i, list.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
         }
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableComponent("createWorld.customize.presets"), (p_95843_) -> {
         this.minecraft.setScreen(new PresetFlatWorldScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 28, 150, 20, CommonComponents.GUI_DONE, (p_95839_) -> {
         this.applySettings.accept(this.generator);
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (p_95833_) -> {
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }));
      this.generator.updateLayers();
      this.updateButtonValidity();
   }

   void updateButtonValidity() {
      this.deleteLayerButton.active = this.hasValidSelection();
   }

   private boolean hasValidSelection() {
      return this.list.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(PoseStack p_95828_, int p_95829_, int p_95830_, float p_95831_) {
      this.renderBackground(p_95828_);
      this.list.render(p_95828_, p_95829_, p_95830_, p_95831_);
      drawCenteredString(p_95828_, this.font, this.title, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      drawString(p_95828_, this.font, this.columnType, i, 32, 16777215);
      drawString(p_95828_, this.font, this.columnHeight, i + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
      super.render(p_95828_, p_95829_, p_95830_, p_95831_);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsList extends ObjectSelectionList<CreateFlatWorldScreen.DetailsList.Entry> {
      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int i = 0; i < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++i) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.Entry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.Entry p_95855_) {
         super.setSelected(p_95855_);
         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      protected boolean isFocused() {
         return CreateFlatWorldScreen.this.getFocused() == this;
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void resetRows() {
         int i = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int j = 0; j < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++j) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.Entry());
         }

         List<CreateFlatWorldScreen.DetailsList.Entry> list = this.children();
         if (i >= 0 && i < list.size()) {
            this.setSelected(list.get(i));
         }

      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ObjectSelectionList.Entry<CreateFlatWorldScreen.DetailsList.Entry> {
         public void render(PoseStack p_95876_, int p_95877_, int p_95878_, int p_95879_, int p_95880_, int p_95881_, int p_95882_, int p_95883_, boolean p_95884_, float p_95885_) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - p_95877_ - 1);
            BlockState blockstate = flatlayerinfo.getBlockState();
            ItemStack itemstack = this.getDisplayItem(blockstate);
            this.blitSlot(p_95876_, p_95879_, p_95878_, itemstack);
            CreateFlatWorldScreen.this.font.draw(p_95876_, itemstack.getHoverName(), (float)(p_95879_ + 18 + 5), (float)(p_95878_ + 3), 16777215);
            Component component;
            if (p_95877_ == 0) {
               component = new TranslatableComponent("createWorld.customize.flat.layer.top", flatlayerinfo.getHeight());
            } else if (p_95877_ == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               component = new TranslatableComponent("createWorld.customize.flat.layer.bottom", flatlayerinfo.getHeight());
            } else {
               component = new TranslatableComponent("createWorld.customize.flat.layer", flatlayerinfo.getHeight());
            }

            CreateFlatWorldScreen.this.font.draw(p_95876_, component, (float)(p_95879_ + 2 + 213 - CreateFlatWorldScreen.this.font.width(component)), (float)(p_95878_ + 3), 16777215);
         }

         private ItemStack getDisplayItem(BlockState p_169294_) {
            Item item = p_169294_.getBlock().asItem();
            if (item == Items.AIR) {
               if (p_169294_.is(Blocks.WATER)) {
                  item = Items.WATER_BUCKET;
               } else if (p_169294_.is(Blocks.LAVA)) {
                  item = Items.LAVA_BUCKET;
               }
            }

            return new ItemStack(item);
         }

         public Component getNarration() {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - DetailsList.this.children().indexOf(this) - 1);
            ItemStack itemstack = this.getDisplayItem(flatlayerinfo.getBlockState());
            return (Component)(!itemstack.isEmpty() ? new TranslatableComponent("narrator.select", itemstack.getHoverName()) : TextComponent.EMPTY);
         }

         public boolean mouseClicked(double p_95868_, double p_95869_, int p_95870_) {
            if (p_95870_ == 0) {
               DetailsList.this.setSelected(this);
               return true;
            } else {
               return false;
            }
         }

         private void blitSlot(PoseStack p_95887_, int p_95888_, int p_95889_, ItemStack p_95890_) {
            this.blitSlotBg(p_95887_, p_95888_ + 1, p_95889_ + 1);
            if (!p_95890_.isEmpty()) {
               CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(p_95890_, p_95888_ + 2, p_95889_ + 2);
            }

         }

         private void blitSlotBg(PoseStack p_95872_, int p_95873_, int p_95874_) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(p_95872_, p_95873_, p_95874_, CreateFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}