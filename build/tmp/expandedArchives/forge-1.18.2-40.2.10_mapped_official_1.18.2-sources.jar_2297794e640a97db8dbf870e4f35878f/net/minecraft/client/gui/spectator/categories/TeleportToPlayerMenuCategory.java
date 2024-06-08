package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToPlayerMenuCategory implements SpectatorMenuCategory, SpectatorMenuItem {
   private static final Ordering<PlayerInfo> PROFILE_ORDER = Ordering.from((p_101870_, p_101871_) -> {
      return ComparisonChain.start().compare(p_101870_.getProfile().getId(), p_101871_.getProfile().getId()).result();
   });
   private static final Component TELEPORT_TEXT = new TranslatableComponent("spectatorMenu.teleport");
   private static final Component TELEPORT_PROMPT = new TranslatableComponent("spectatorMenu.teleport.prompt");
   private final List<SpectatorMenuItem> items = Lists.newArrayList();

   public TeleportToPlayerMenuCategory() {
      this(PROFILE_ORDER.sortedCopy(Minecraft.getInstance().getConnection().getOnlinePlayers()));
   }

   public TeleportToPlayerMenuCategory(Collection<PlayerInfo> p_101861_) {
      for(PlayerInfo playerinfo : PROFILE_ORDER.sortedCopy(p_101861_)) {
         if (playerinfo.getGameMode() != GameType.SPECTATOR) {
            this.items.add(new PlayerMenuItem(playerinfo.getProfile()));
         }
      }

   }

   public List<SpectatorMenuItem> getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return TELEPORT_PROMPT;
   }

   public void selectItem(SpectatorMenu p_101868_) {
      p_101868_.selectCategory(this);
   }

   public Component getName() {
      return TELEPORT_TEXT;
   }

   public void renderIcon(PoseStack p_101864_, float p_101865_, int p_101866_) {
      RenderSystem.setShaderTexture(0, SpectatorGui.SPECTATOR_LOCATION);
      GuiComponent.blit(p_101864_, 0, 0, 0.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      return !this.items.isEmpty();
   }
}