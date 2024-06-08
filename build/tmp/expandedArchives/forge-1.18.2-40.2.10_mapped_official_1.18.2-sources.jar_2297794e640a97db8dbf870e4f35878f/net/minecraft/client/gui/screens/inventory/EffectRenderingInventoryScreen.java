package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EffectRenderingInventoryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
   public EffectRenderingInventoryScreen(T p_98701_, Inventory p_98702_, Component p_98703_) {
      super(p_98701_, p_98702_, p_98703_);
   }

   public void render(PoseStack p_98705_, int p_98706_, int p_98707_, float p_98708_) {
      super.render(p_98705_, p_98706_, p_98707_, p_98708_);
      this.renderEffects(p_98705_, p_98706_, p_98707_);
   }

   public boolean canSeeEffects() {
      int i = this.leftPos + this.imageWidth + 2;
      int j = this.width - i;
      return j >= 32;
   }

   private void renderEffects(PoseStack p_194015_, int p_194016_, int p_194017_) {
      int i = this.leftPos + this.imageWidth + 2;
      int j = this.width - i;
      Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
      if (!collection.isEmpty() && j >= 32) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = j >= 120;
         var event = net.minecraftforge.client.ForgeHooksClient.onScreenPotionSize(this);
         if (event != net.minecraftforge.eventbus.api.Event.Result.DEFAULT) flag = event == net.minecraftforge.eventbus.api.Event.Result.DENY; // true means classic mode
         int k = 33;
         if (collection.size() > 5) {
            k = 132 / (collection.size() - 1);
         }


         Iterable<MobEffectInstance> iterable = collection.stream().filter(net.minecraftforge.client.ForgeHooksClient::shouldRenderEffect).sorted().collect(java.util.stream.Collectors.toList());
         this.renderBackgrounds(p_194015_, i, k, iterable, flag);
         this.renderIcons(p_194015_, i, k, iterable, flag);
         if (flag) {
            this.renderLabels(p_194015_, i, k, iterable);
         } else if (p_194016_ >= i && p_194016_ <= i + 33) {
            int l = this.topPos;
            MobEffectInstance mobeffectinstance = null;

            for(MobEffectInstance mobeffectinstance1 : iterable) {
               if (p_194017_ >= l && p_194017_ <= l + k) {
                  mobeffectinstance = mobeffectinstance1;
               }

               l += k;
            }

            if (mobeffectinstance != null) {
               List<Component> list = List.of(this.getEffectName(mobeffectinstance), new TextComponent(MobEffectUtil.formatDuration(mobeffectinstance, 1.0F)));
               this.renderTooltip(p_194015_, list, Optional.empty(), p_194016_, p_194017_);
            }
         }

      }
   }

   private void renderBackgrounds(PoseStack p_194003_, int p_194004_, int p_194005_, Iterable<MobEffectInstance> p_194006_, boolean p_194007_) {
      RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
      int i = this.topPos;

      for(MobEffectInstance mobeffectinstance : p_194006_) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         if (p_194007_) {
            this.blit(p_194003_, p_194004_, i, 0, 166, 120, 32);
         } else {
            this.blit(p_194003_, p_194004_, i, 0, 198, 32, 32);
         }

         i += p_194005_;
      }

   }

   private void renderIcons(PoseStack p_194009_, int p_194010_, int p_194011_, Iterable<MobEffectInstance> p_194012_, boolean p_194013_) {
      MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
      int i = this.topPos;

      for(MobEffectInstance mobeffectinstance : p_194012_) {
         MobEffect mobeffect = mobeffectinstance.getEffect();
         TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
         RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
         blit(p_194009_, p_194010_ + (p_194013_ ? 6 : 7), i + 7, this.getBlitOffset(), 18, 18, textureatlassprite);
         i += p_194011_;
      }

   }

   private void renderLabels(PoseStack p_98723_, int p_98724_, int p_98725_, Iterable<MobEffectInstance> p_98726_) {
      int i = this.topPos;

      for(MobEffectInstance mobeffectinstance : p_98726_) {
         net.minecraftforge.client.EffectRenderer renderer = net.minecraftforge.client.RenderProperties.getEffectRenderer(mobeffectinstance);
         renderer.renderInventoryEffect(mobeffectinstance, this, p_98723_, p_98724_, i, this.getBlitOffset());
         if (!renderer.shouldRenderInvText(mobeffectinstance)) {
            i += p_98725_;
            continue;
         }
         Component component = this.getEffectName(mobeffectinstance);
         this.font.drawShadow(p_98723_, component, (float)(p_98724_ + 10 + 18), (float)(i + 6), 16777215);
         String s = MobEffectUtil.formatDuration(mobeffectinstance, 1.0F);
         this.font.drawShadow(p_98723_, s, (float)(p_98724_ + 10 + 18), (float)(i + 6 + 10), 8355711);
         i += p_98725_;
      }

   }

   private Component getEffectName(MobEffectInstance p_194001_) {
      MutableComponent mutablecomponent = p_194001_.getEffect().getDisplayName().copy();
      if (p_194001_.getAmplifier() >= 1 && p_194001_.getAmplifier() <= 9) {
         mutablecomponent.append(" ").append(new TranslatableComponent("enchantment.level." + (p_194001_.getAmplifier() + 1)));
      }

      return mutablecomponent;
   }
}
