package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerMenuItem implements SpectatorMenuItem {
   private final GameProfile profile;
   private final ResourceLocation location;
   private final Component name;

   public PlayerMenuItem(GameProfile p_101756_) {
      this.profile = p_101756_;
      Minecraft minecraft = Minecraft.getInstance();
      Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(p_101756_);
      if (map.containsKey(Type.SKIN)) {
         this.location = minecraft.getSkinManager().registerTexture(map.get(Type.SKIN), Type.SKIN);
      } else {
         this.location = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(p_101756_));
      }

      this.name = new TextComponent(p_101756_.getName());
   }

   public void selectItem(SpectatorMenu p_101762_) {
      Minecraft.getInstance().getConnection().send(new ServerboundTeleportToEntityPacket(this.profile.getId()));
   }

   public Component getName() {
      return this.name;
   }

   public void renderIcon(PoseStack p_101758_, float p_101759_, int p_101760_) {
      RenderSystem.setShaderTexture(0, this.location);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float)p_101760_ / 255.0F);
      GuiComponent.blit(p_101758_, 2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
      GuiComponent.blit(p_101758_, 2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
   }

   public boolean isEnabled() {
      return true;
   }
}