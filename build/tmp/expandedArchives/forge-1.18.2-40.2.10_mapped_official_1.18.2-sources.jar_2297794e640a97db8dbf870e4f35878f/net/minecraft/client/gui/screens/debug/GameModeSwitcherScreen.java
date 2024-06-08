package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameModeSwitcherScreen extends Screen {
   static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
   private static final int SPRITE_SHEET_WIDTH = 128;
   private static final int SPRITE_SHEET_HEIGHT = 128;
   private static final int SLOT_AREA = 26;
   private static final int SLOT_PADDING = 5;
   private static final int SLOT_AREA_PADDED = 31;
   private static final int HELP_TIPS_OFFSET_Y = 5;
   private static final int ALL_SLOTS_WIDTH = GameModeSwitcherScreen.GameModeIcon.values().length * 31 - 5;
   private static final Component SELECT_KEY = new TranslatableComponent("debug.gamemodes.select_next", (new TranslatableComponent("debug.gamemodes.press_f4")).withStyle(ChatFormatting.AQUA));
   private final Optional<GameModeSwitcherScreen.GameModeIcon> previousHovered;
   private Optional<GameModeSwitcherScreen.GameModeIcon> currentlyHovered = Optional.empty();
   private int firstMouseX;
   private int firstMouseY;
   private boolean setFirstMousePos;
   private final List<GameModeSwitcherScreen.GameModeSlot> slots = Lists.newArrayList();

   public GameModeSwitcherScreen() {
      super(NarratorChatListener.NO_TITLE);
      this.previousHovered = GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.getDefaultSelected());
   }

   private GameType getDefaultSelected() {
      MultiPlayerGameMode multiplayergamemode = Minecraft.getInstance().gameMode;
      GameType gametype = multiplayergamemode.getPreviousPlayerMode();
      if (gametype != null) {
         return gametype;
      } else {
         return multiplayergamemode.getPlayerMode() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
      }
   }

   protected void init() {
      super.init();
      this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.minecraft.gameMode.getPlayerMode());

      for(int i = 0; i < GameModeSwitcherScreen.GameModeIcon.VALUES.length; ++i) {
         GameModeSwitcherScreen.GameModeIcon gamemodeswitcherscreen$gamemodeicon = GameModeSwitcherScreen.GameModeIcon.VALUES[i];
         this.slots.add(new GameModeSwitcherScreen.GameModeSlot(gamemodeswitcherscreen$gamemodeicon, this.width / 2 - ALL_SLOTS_WIDTH / 2 + i * 31, this.height / 2 - 31));
      }

   }

   public void render(PoseStack p_97557_, int p_97558_, int p_97559_, float p_97560_) {
      if (!this.checkToClose()) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         p_97557_.pushPose();
         RenderSystem.enableBlend();
         RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
         int i = this.width / 2 - 62;
         int j = this.height / 2 - 31 - 27;
         blit(p_97557_, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
         p_97557_.popPose();
         super.render(p_97557_, p_97558_, p_97559_, p_97560_);
         this.currentlyHovered.ifPresent((p_97563_) -> {
            drawCenteredString(p_97557_, this.font, p_97563_.getName(), this.width / 2, this.height / 2 - 31 - 20, -1);
         });
         drawCenteredString(p_97557_, this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
         if (!this.setFirstMousePos) {
            this.firstMouseX = p_97558_;
            this.firstMouseY = p_97559_;
            this.setFirstMousePos = true;
         }

         boolean flag = this.firstMouseX == p_97558_ && this.firstMouseY == p_97559_;

         for(GameModeSwitcherScreen.GameModeSlot gamemodeswitcherscreen$gamemodeslot : this.slots) {
            gamemodeswitcherscreen$gamemodeslot.render(p_97557_, p_97558_, p_97559_, p_97560_);
            this.currentlyHovered.ifPresent((p_97569_) -> {
               gamemodeswitcherscreen$gamemodeslot.setSelected(p_97569_ == gamemodeswitcherscreen$gamemodeslot.icon);
            });
            if (!flag && gamemodeswitcherscreen$gamemodeslot.isHoveredOrFocused()) {
               this.currentlyHovered = Optional.of(gamemodeswitcherscreen$gamemodeslot.icon);
            }
         }

      }
   }

   private void switchToHoveredGameMode() {
      switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
   }

   private static void switchToHoveredGameMode(Minecraft p_97565_, Optional<GameModeSwitcherScreen.GameModeIcon> p_97566_) {
      if (p_97565_.gameMode != null && p_97565_.player != null && p_97566_.isPresent()) {
         Optional<GameModeSwitcherScreen.GameModeIcon> optional = GameModeSwitcherScreen.GameModeIcon.getFromGameType(p_97565_.gameMode.getPlayerMode());
         GameModeSwitcherScreen.GameModeIcon gamemodeswitcherscreen$gamemodeicon = p_97566_.get();
         if (optional.isPresent() && p_97565_.player.hasPermissions(2) && gamemodeswitcherscreen$gamemodeicon != optional.get()) {
            p_97565_.player.chat(gamemodeswitcherscreen$gamemodeicon.getCommand());
         }

      }
   }

   private boolean checkToClose() {
      if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
         this.switchToHoveredGameMode();
         this.minecraft.setScreen((Screen)null);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int p_97553_, int p_97554_, int p_97555_) {
      if (p_97553_ == 293 && this.currentlyHovered.isPresent()) {
         this.setFirstMousePos = false;
         this.currentlyHovered = this.currentlyHovered.get().getNext();
         return true;
      } else {
         return super.keyPressed(p_97553_, p_97554_, p_97555_);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   static enum GameModeIcon {
      CREATIVE(new TranslatableComponent("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(new TranslatableComponent("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(new TranslatableComponent("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
      SPECTATOR(new TranslatableComponent("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

      protected static final GameModeSwitcherScreen.GameModeIcon[] VALUES = values();
      private static final int ICON_AREA = 16;
      protected static final int ICON_TOP_LEFT = 5;
      final Component name;
      final String command;
      final ItemStack renderStack;

      private GameModeIcon(Component p_97594_, String p_97595_, ItemStack p_97596_) {
         this.name = p_97594_;
         this.command = p_97595_;
         this.renderStack = p_97596_;
      }

      void drawIcon(ItemRenderer p_97608_, int p_97609_, int p_97610_) {
         p_97608_.renderAndDecorateItem(this.renderStack, p_97609_, p_97610_);
      }

      Component getName() {
         return this.name;
      }

      String getCommand() {
         return this.command;
      }

      Optional<GameModeSwitcherScreen.GameModeIcon> getNext() {
         switch(this) {
         case CREATIVE:
            return Optional.of(SURVIVAL);
         case SURVIVAL:
            return Optional.of(ADVENTURE);
         case ADVENTURE:
            return Optional.of(SPECTATOR);
         default:
            return Optional.of(CREATIVE);
         }
      }

      static Optional<GameModeSwitcherScreen.GameModeIcon> getFromGameType(GameType p_97613_) {
         switch(p_97613_) {
         case SPECTATOR:
            return Optional.of(SPECTATOR);
         case SURVIVAL:
            return Optional.of(SURVIVAL);
         case CREATIVE:
            return Optional.of(CREATIVE);
         case ADVENTURE:
            return Optional.of(ADVENTURE);
         default:
            return Optional.empty();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class GameModeSlot extends AbstractWidget {
      final GameModeSwitcherScreen.GameModeIcon icon;
      private boolean isSelected;

      public GameModeSlot(GameModeSwitcherScreen.GameModeIcon p_97627_, int p_97628_, int p_97629_) {
         super(p_97628_, p_97629_, 26, 26, p_97627_.getName());
         this.icon = p_97627_;
      }

      public void renderButton(PoseStack p_97636_, int p_97637_, int p_97638_, float p_97639_) {
         Minecraft minecraft = Minecraft.getInstance();
         this.drawSlot(p_97636_, minecraft.getTextureManager());
         this.icon.drawIcon(GameModeSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
         if (this.isSelected) {
            this.drawSelection(p_97636_, minecraft.getTextureManager());
         }

      }

      public void updateNarration(NarrationElementOutput p_169594_) {
         this.defaultButtonNarrationText(p_169594_);
      }

      public boolean isHoveredOrFocused() {
         return super.isHoveredOrFocused() || this.isSelected;
      }

      public void setSelected(boolean p_97644_) {
         this.isSelected = p_97644_;
      }

      private void drawSlot(PoseStack p_97631_, TextureManager p_97632_) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION);
         p_97631_.pushPose();
         p_97631_.translate((double)this.x, (double)this.y, 0.0D);
         blit(p_97631_, 0, 0, 0.0F, 75.0F, 26, 26, 128, 128);
         p_97631_.popPose();
      }

      private void drawSelection(PoseStack p_97641_, TextureManager p_97642_) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION);
         p_97641_.pushPose();
         p_97641_.translate((double)this.x, (double)this.y, 0.0D);
         blit(p_97641_, 0, 0, 26.0F, 75.0F, 26, 26, 128, 128);
         p_97641_.popPose();
      }
   }
}