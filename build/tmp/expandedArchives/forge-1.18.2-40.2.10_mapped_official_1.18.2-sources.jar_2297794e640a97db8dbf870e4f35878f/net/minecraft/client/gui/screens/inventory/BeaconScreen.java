package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeaconScreen extends AbstractContainerScreen<BeaconMenu> {
   static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
   private static final Component PRIMARY_EFFECT_LABEL = new TranslatableComponent("block.minecraft.beacon.primary");
   private static final Component SECONDARY_EFFECT_LABEL = new TranslatableComponent("block.minecraft.beacon.secondary");
   private final List<BeaconScreen.BeaconButton> beaconButtons = Lists.newArrayList();
   @Nullable
   MobEffect primary;
   @Nullable
   MobEffect secondary;

   public BeaconScreen(final BeaconMenu p_97912_, Inventory p_97913_, Component p_97914_) {
      super(p_97912_, p_97913_, p_97914_);
      this.imageWidth = 230;
      this.imageHeight = 219;
      p_97912_.addSlotListener(new ContainerListener() {
         public void slotChanged(AbstractContainerMenu p_97973_, int p_97974_, ItemStack p_97975_) {
         }

         public void dataChanged(AbstractContainerMenu p_169628_, int p_169629_, int p_169630_) {
            BeaconScreen.this.primary = p_97912_.getPrimaryEffect();
            BeaconScreen.this.secondary = p_97912_.getSecondaryEffect();
         }
      });
   }

   private <T extends AbstractWidget & BeaconScreen.BeaconButton> void addBeaconButton(T p_169617_) {
      this.addRenderableWidget(p_169617_);
      this.beaconButtons.add(p_169617_);
   }

   protected void init() {
      super.init();
      this.beaconButtons.clear();
      this.addBeaconButton(new BeaconScreen.BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addBeaconButton(new BeaconScreen.BeaconCancelButton(this.leftPos + 190, this.topPos + 107));

      for(int i = 0; i <= 2; ++i) {
         int j = BeaconBlockEntity.BEACON_EFFECTS[i].length;
         int k = j * 22 + (j - 1) * 2;

         for(int l = 0; l < j; ++l) {
            MobEffect mobeffect = BeaconBlockEntity.BEACON_EFFECTS[i][l];
            BeaconScreen.BeaconPowerButton beaconscreen$beaconpowerbutton = new BeaconScreen.BeaconPowerButton(this.leftPos + 76 + l * 24 - k / 2, this.topPos + 22 + i * 25, mobeffect, true, i);
            beaconscreen$beaconpowerbutton.active = false;
            this.addBeaconButton(beaconscreen$beaconpowerbutton);
         }
      }

      int i1 = 3;
      int j1 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
      int k1 = j1 * 22 + (j1 - 1) * 2;

      for(int l1 = 0; l1 < j1 - 1; ++l1) {
         MobEffect mobeffect1 = BeaconBlockEntity.BEACON_EFFECTS[3][l1];
         BeaconScreen.BeaconPowerButton beaconscreen$beaconpowerbutton2 = new BeaconScreen.BeaconPowerButton(this.leftPos + 167 + l1 * 24 - k1 / 2, this.topPos + 47, mobeffect1, false, 3);
         beaconscreen$beaconpowerbutton2.active = false;
         this.addBeaconButton(beaconscreen$beaconpowerbutton2);
      }

      BeaconScreen.BeaconPowerButton beaconscreen$beaconpowerbutton1 = new BeaconScreen.BeaconUpgradePowerButton(this.leftPos + 167 + (j1 - 1) * 24 - k1 / 2, this.topPos + 47, BeaconBlockEntity.BEACON_EFFECTS[0][0]);
      beaconscreen$beaconpowerbutton1.visible = false;
      this.addBeaconButton(beaconscreen$beaconpowerbutton1);
   }

   public void containerTick() {
      super.containerTick();
      this.updateButtons();
   }

   void updateButtons() {
      int i = this.menu.getLevels();
      this.beaconButtons.forEach((p_169615_) -> {
         p_169615_.updateStatus(i);
      });
   }

   protected void renderLabels(PoseStack p_97935_, int p_97936_, int p_97937_) {
      drawCenteredString(p_97935_, this.font, PRIMARY_EFFECT_LABEL, 62, 10, 14737632);
      drawCenteredString(p_97935_, this.font, SECONDARY_EFFECT_LABEL, 169, 10, 14737632);

      for(BeaconScreen.BeaconButton beaconscreen$beaconbutton : this.beaconButtons) {
         if (beaconscreen$beaconbutton.isShowingTooltip()) {
            beaconscreen$beaconbutton.renderToolTip(p_97935_, p_97936_ - this.leftPos, p_97937_ - this.topPos);
            break;
         }
      }

   }

   protected void renderBg(PoseStack p_97916_, float p_97917_, int p_97918_, int p_97919_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BEACON_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_97916_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.itemRenderer.blitOffset = 100.0F;
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), i + 41, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public void render(PoseStack p_97921_, int p_97922_, int p_97923_, float p_97924_) {
      this.renderBackground(p_97921_);
      super.render(p_97921_, p_97922_, p_97923_, p_97924_);
      this.renderTooltip(p_97921_, p_97922_, p_97923_);
   }

   @OnlyIn(Dist.CLIENT)
   interface BeaconButton {
      boolean isShowingTooltip();

      void renderToolTip(PoseStack p_169632_, int p_169633_, int p_169634_);

      void updateStatus(int p_169631_);
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconCancelButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconCancelButton(int p_97982_, int p_97983_) {
         super(p_97982_, p_97983_, 112, 220, CommonComponents.GUI_CANCEL);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      public void updateStatus(int p_169636_) {
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconConfirmButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconConfirmButton(int p_97992_, int p_97993_) {
         super(p_97992_, p_97993_, 90, 220, CommonComponents.GUI_DONE);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(MobEffect.getId(BeaconScreen.this.primary), MobEffect.getId(BeaconScreen.this.secondary)));
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      public void updateStatus(int p_169638_) {
         this.active = BeaconScreen.this.menu.hasPayment() && BeaconScreen.this.primary != null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconPowerButton extends BeaconScreen.BeaconScreenButton {
      private final boolean isPrimary;
      protected final int tier;
      private MobEffect effect;
      private TextureAtlasSprite sprite;
      private Component tooltip;

      public BeaconPowerButton(int p_169642_, int p_169643_, MobEffect p_169644_, boolean p_169645_, int p_169646_) {
         super(p_169642_, p_169643_);
         this.isPrimary = p_169645_;
         this.tier = p_169646_;
         this.setEffect(p_169644_);
      }

      protected void setEffect(MobEffect p_169650_) {
         this.effect = p_169650_;
         this.sprite = Minecraft.getInstance().getMobEffectTextures().get(p_169650_);
         this.tooltip = this.createEffectDescription(p_169650_);
      }

      protected MutableComponent createEffectDescription(MobEffect p_169652_) {
         return new TranslatableComponent(p_169652_.getDescriptionId());
      }

      public void onPress() {
         if (!this.isSelected()) {
            if (this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.updateButtons();
         }
      }

      public void renderToolTip(PoseStack p_98016_, int p_98017_, int p_98018_) {
         BeaconScreen.this.renderTooltip(p_98016_, this.tooltip, p_98017_, p_98018_);
      }

      protected void renderIcon(PoseStack p_98014_) {
         RenderSystem.setShaderTexture(0, this.sprite.atlas().location());
         blit(p_98014_, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
      }

      public void updateStatus(int p_169648_) {
         this.active = this.tier < p_169648_;
         this.setSelected(this.effect == (this.isPrimary ? BeaconScreen.this.primary : BeaconScreen.this.secondary));
      }

      protected MutableComponent createNarrationMessage() {
         return this.createEffectDescription(this.effect);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class BeaconScreenButton extends AbstractButton implements BeaconScreen.BeaconButton {
      private boolean selected;

      protected BeaconScreenButton(int p_98022_, int p_98023_) {
         super(p_98022_, p_98023_, 22, 22, TextComponent.EMPTY);
      }

      protected BeaconScreenButton(int p_169654_, int p_169655_, Component p_169656_) {
         super(p_169654_, p_169655_, 22, 22, p_169656_);
      }

      public void renderButton(PoseStack p_98027_, int p_98028_, int p_98029_, float p_98030_) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, BeaconScreen.BEACON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int i = 219;
         int j = 0;
         if (!this.active) {
            j += this.width * 2;
         } else if (this.selected) {
            j += this.width * 1;
         } else if (this.isHoveredOrFocused()) {
            j += this.width * 3;
         }

         this.blit(p_98027_, this.x, this.y, j, 219, this.width, this.height);
         this.renderIcon(p_98027_);
      }

      protected abstract void renderIcon(PoseStack p_98025_);

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean p_98032_) {
         this.selected = p_98032_;
      }

      public boolean isShowingTooltip() {
         return this.isHovered;
      }

      public void updateNarration(NarrationElementOutput p_169659_) {
         this.defaultButtonNarrationText(p_169659_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract class BeaconSpriteScreenButton extends BeaconScreen.BeaconScreenButton {
      private final int iconX;
      private final int iconY;

      protected BeaconSpriteScreenButton(int p_169663_, int p_169664_, int p_169665_, int p_169666_, Component p_169667_) {
         super(p_169663_, p_169664_, p_169667_);
         this.iconX = p_169665_;
         this.iconY = p_169666_;
      }

      protected void renderIcon(PoseStack p_98041_) {
         this.blit(p_98041_, this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
      }

      public void renderToolTip(PoseStack p_169669_, int p_169670_, int p_169671_) {
         BeaconScreen.this.renderTooltip(p_169669_, BeaconScreen.this.title, p_169670_, p_169671_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class BeaconUpgradePowerButton extends BeaconScreen.BeaconPowerButton {
      public BeaconUpgradePowerButton(int p_169675_, int p_169676_, MobEffect p_169677_) {
         super(p_169675_, p_169676_, p_169677_, false, 3);
      }

      protected MutableComponent createEffectDescription(MobEffect p_169681_) {
         return (new TranslatableComponent(p_169681_.getDescriptionId())).append(" II");
      }

      public void updateStatus(int p_169679_) {
         if (BeaconScreen.this.primary != null) {
            this.visible = true;
            this.setEffect(BeaconScreen.this.primary);
            super.updateStatus(p_169679_);
         } else {
            this.visible = false;
         }

      }
   }
}