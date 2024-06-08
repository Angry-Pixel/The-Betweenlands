package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.List;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSlotOptionsScreen extends RealmsScreen {
   private static final int DEFAULT_DIFFICULTY = 2;
   public static final List<Difficulty> DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
   private static final int DEFAULT_GAME_MODE = 0;
   public static final List<GameType> GAME_MODES = ImmutableList.of(GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE);
   private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.edit.slot.name");
   static final Component SPAWN_PROTECTION_TEXT = new TranslatableComponent("mco.configure.world.spawnProtection");
   private EditBox nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1X;
   private int columnWidth;
   private final RealmsWorldOptions options;
   private final RealmsServer.WorldType worldType;
   private final int activeSlot;
   private Difficulty difficulty;
   private GameType gameMode;
   private boolean pvp;
   private boolean spawnNPCs;
   private boolean spawnAnimals;
   private boolean spawnMonsters;
   int spawnProtection;
   private boolean commandBlocks;
   private boolean forceGameMode;
   RealmsSlotOptionsScreen.SettingsSlider spawnProtectionButton;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen p_89886_, RealmsWorldOptions p_89887_, RealmsServer.WorldType p_89888_, int p_89889_) {
      super(new TranslatableComponent("mco.configure.world.buttons.options"));
      this.parent = p_89886_;
      this.options = p_89887_;
      this.worldType = p_89888_;
      this.activeSlot = p_89889_;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void tick() {
      this.nameEdit.tick();
   }

   public boolean keyPressed(int p_89891_, int p_89892_, int p_89893_) {
      if (p_89891_ == 256) {
         this.minecraft.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(p_89891_, p_89892_, p_89893_);
      }
   }

   private static <T> T findByIndex(List<T> p_167525_, int p_167526_, int p_167527_) {
      try {
         return p_167525_.get(p_167526_);
      } catch (IndexOutOfBoundsException indexoutofboundsexception) {
         return p_167525_.get(p_167527_);
      }
   }

   private static <T> int findIndex(List<T> p_167529_, T p_167530_, int p_167531_) {
      int i = p_167529_.indexOf(p_167530_);
      return i == -1 ? p_167531_ : i;
   }

   public void init() {
      this.columnWidth = 170;
      this.column1X = this.width / 2 - this.columnWidth;
      int i = this.width / 2 + 10;
      this.difficulty = findByIndex(DIFFICULTIES, this.options.difficulty, 2);
      this.gameMode = findByIndex(GAME_MODES, this.options.gameMode, 0);
      if (this.worldType == RealmsServer.WorldType.NORMAL) {
         this.pvp = this.options.pvp;
         this.spawnProtection = this.options.spawnProtection;
         this.forceGameMode = this.options.forceGameMode;
         this.spawnAnimals = this.options.spawnAnimals;
         this.spawnMonsters = this.options.spawnMonsters;
         this.spawnNPCs = this.options.spawnNPCs;
         this.commandBlocks = this.options.commandBlocks;
      } else {
         Component component;
         if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
            component = new TranslatableComponent("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
            component = new TranslatableComponent("mco.configure.world.edit.subscreen.inspiration");
         } else {
            component = new TranslatableComponent("mco.configure.world.edit.subscreen.experience");
         }

         this.addLabel(new RealmsLabel(component, this.width / 2, 26, 16711680));
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnAnimals = true;
         this.spawnMonsters = true;
         this.spawnNPCs = true;
         this.commandBlocks = true;
      }

      this.nameEdit = new EditBox(this.minecraft.font, this.column1X + 2, row(1), this.columnWidth - 4, 20, (EditBox)null, new TranslatableComponent("mco.configure.world.edit.slot.name"));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
      this.magicalSpecialHackyFocus(this.nameEdit);
      CycleButton<Boolean> cyclebutton5 = this.addRenderableWidget(CycleButton.onOffBuilder(this.pvp).create(i, row(1), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.pvp"), (p_167546_, p_167547_) -> {
         this.pvp = p_167547_;
      }));
      this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues(GAME_MODES).withInitialValue(this.gameMode).create(this.column1X, row(3), this.columnWidth, 20, new TranslatableComponent("selectWorld.gameMode"), (p_167515_, p_167516_) -> {
         this.gameMode = p_167516_;
      }));
      CycleButton<Boolean> cyclebutton = this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnAnimals).create(i, row(3), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.spawnAnimals"), (p_167543_, p_167544_) -> {
         this.spawnAnimals = p_167544_;
      }));
      CycleButton<Boolean> cyclebutton1 = CycleButton.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).create(i, row(5), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.spawnMonsters"), (p_167540_, p_167541_) -> {
         this.spawnMonsters = p_167541_;
      });
      this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues(DIFFICULTIES).withInitialValue(this.difficulty).create(this.column1X, row(5), this.columnWidth, 20, new TranslatableComponent("options.difficulty"), (p_167519_, p_167520_) -> {
         this.difficulty = p_167520_;
         if (this.worldType == RealmsServer.WorldType.NORMAL) {
            boolean flag = this.difficulty != Difficulty.PEACEFUL;
            cyclebutton1.active = flag;
            cyclebutton1.setValue(flag && this.spawnMonsters);
         }

      }));
      this.addRenderableWidget(cyclebutton1);
      this.spawnProtectionButton = this.addRenderableWidget(new RealmsSlotOptionsScreen.SettingsSlider(this.column1X, row(7), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      CycleButton<Boolean> cyclebutton2 = this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnNPCs).create(i, row(7), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.spawnNPCs"), (p_167537_, p_167538_) -> {
         this.spawnNPCs = p_167538_;
      }));
      CycleButton<Boolean> cyclebutton3 = this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(this.column1X, row(9), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.forceGameMode"), (p_167534_, p_167535_) -> {
         this.forceGameMode = p_167535_;
      }));
      CycleButton<Boolean> cyclebutton4 = this.addRenderableWidget(CycleButton.onOffBuilder(this.commandBlocks).create(i, row(9), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.commandBlocks"), (p_167522_, p_167523_) -> {
         this.commandBlocks = p_167523_;
      }));
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         cyclebutton5.active = false;
         cyclebutton.active = false;
         cyclebutton2.active = false;
         cyclebutton1.active = false;
         this.spawnProtectionButton.active = false;
         cyclebutton4.active = false;
         cyclebutton3.active = false;
      }

      if (this.difficulty == Difficulty.PEACEFUL) {
         cyclebutton1.active = false;
      }

      this.addRenderableWidget(new Button(this.column1X, row(13), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.buttons.done"), (p_89910_) -> {
         this.saveSettings();
      }));
      this.addRenderableWidget(new Button(i, row(13), this.columnWidth, 20, CommonComponents.GUI_CANCEL, (p_89905_) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.addWidget(this.nameEdit);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void render(PoseStack p_89895_, int p_89896_, int p_89897_, float p_89898_) {
      this.renderBackground(p_89895_);
      drawCenteredString(p_89895_, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(p_89895_, NAME_LABEL, (float)(this.column1X + this.columnWidth / 2 - this.font.width(NAME_LABEL) / 2), (float)(row(0) - 5), 16777215);
      this.nameEdit.render(p_89895_, p_89896_, p_89897_, p_89898_);
      super.render(p_89895_, p_89896_, p_89897_, p_89898_);
   }

   private String getSlotName() {
      return this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getValue();
   }

   private void saveSettings() {
      int i = findIndex(DIFFICULTIES, this.difficulty, 2);
      int j = findIndex(GAME_MODES, this.gameMode, 0);
      if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP && this.worldType != RealmsServer.WorldType.EXPERIENCE && this.worldType != RealmsServer.WorldType.INSPIRATION) {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, i, j, this.forceGameMode, this.getSlotName()));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, i, j, this.options.forceGameMode, this.getSlotName()));
      }

   }

   @OnlyIn(Dist.CLIENT)
   class SettingsSlider extends AbstractSliderButton {
      private final double minValue;
      private final double maxValue;

      public SettingsSlider(int p_89946_, int p_89947_, int p_89948_, int p_89949_, float p_89950_, float p_89951_) {
         super(p_89946_, p_89947_, p_89948_, 20, TextComponent.EMPTY, 0.0D);
         this.minValue = (double)p_89950_;
         this.maxValue = (double)p_89951_;
         this.value = (double)((Mth.clamp((float)p_89949_, p_89950_, p_89951_) - p_89950_) / (p_89951_ - p_89950_));
         this.updateMessage();
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
            RealmsSlotOptionsScreen.this.spawnProtection = (int)Mth.lerp(Mth.clamp(this.value, 0.0D, 1.0D), this.minValue, this.maxValue);
         }
      }

      protected void updateMessage() {
         this.setMessage(CommonComponents.optionNameValue(RealmsSlotOptionsScreen.SPAWN_PROTECTION_TEXT, (Component)(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? CommonComponents.OPTION_OFF : new TextComponent(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection)))));
      }

      public void onClick(double p_89954_, double p_89955_) {
      }

      public void onRelease(double p_89957_, double p_89958_) {
      }
   }
}