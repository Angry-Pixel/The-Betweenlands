package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SelectWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Screen lastScreen;
   @Nullable
   private List<FormattedCharSequence> toolTip;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   protected EditBox searchBox;
   private WorldSelectionList list;

   public SelectWorldScreen(Screen p_101338_) {
      super(new TranslatableComponent("selectWorld.title"));
      this.lastScreen = p_101338_;
   }

   public boolean mouseScrolled(double p_101343_, double p_101344_, double p_101345_) {
      return super.mouseScrolled(p_101343_, p_101344_, p_101345_);
   }

   public void tick() {
      this.searchBox.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, new TranslatableComponent("selectWorld.search"));
      this.searchBox.setResponder((p_101362_) -> {
         this.list.refreshList(() -> {
            return p_101362_;
         }, false);
      });
      this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.searchBox.getValue();
      }, this.list);
      this.addWidget(this.searchBox);
      this.addWidget(this.list);
      this.selectButton = this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 52, 150, 20, new TranslatableComponent("selectWorld.select"), (p_101378_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 52, 150, 20, new TranslatableComponent("selectWorld.create"), (p_101376_) -> {
         this.minecraft.setScreen(CreateWorldScreen.createFresh(this));
      }));
      this.renameButton = this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.edit"), (p_101373_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld);
      }));
      this.deleteButton = this.addRenderableWidget(new Button(this.width / 2 - 76, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.delete"), (p_101366_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld);
      }));
      this.copyButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.recreate"), (p_101360_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 82, this.height - 28, 72, 20, CommonComponents.GUI_CANCEL, (p_101356_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.updateButtonStatus(false);
      this.setInitialFocus(this.searchBox);
   }

   public boolean keyPressed(int p_101347_, int p_101348_, int p_101349_) {
      return super.keyPressed(p_101347_, p_101348_, p_101349_) ? true : this.searchBox.keyPressed(p_101347_, p_101348_, p_101349_);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public boolean charTyped(char p_101340_, int p_101341_) {
      return this.searchBox.charTyped(p_101340_, p_101341_);
   }

   public void render(PoseStack p_101351_, int p_101352_, int p_101353_, float p_101354_) {
      this.toolTip = null;
      this.list.render(p_101351_, p_101352_, p_101353_, p_101354_);
      this.searchBox.render(p_101351_, p_101352_, p_101353_, p_101354_);
      drawCenteredString(p_101351_, this.font, this.title, this.width / 2, 8, 16777215);
      super.render(p_101351_, p_101352_, p_101353_, p_101354_);
      if (this.toolTip != null) {
         this.renderTooltip(p_101351_, this.toolTip, p_101352_, p_101353_);
      }

   }

   public void setToolTip(List<FormattedCharSequence> p_101364_) {
      this.toolTip = p_101364_;
   }

   public void updateButtonStatus(boolean p_101370_) {
      this.selectButton.active = p_101370_;
      this.deleteButton.active = p_101370_;
      this.renameButton.active = p_101370_;
      this.copyButton.active = p_101370_;
   }

   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.WorldListEntry::close);
      }

   }
}