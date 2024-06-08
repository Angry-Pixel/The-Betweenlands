package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private CompoundTag playerData;

   public IntegratedPlayerList(IntegratedServer p_205649_, RegistryAccess.Frozen p_205650_, PlayerDataStorage p_205651_) {
      super(p_205649_, p_205650_, p_205651_, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer p_120011_) {
      if (p_120011_.getName().getString().equals(this.getServer().getSingleplayerName())) {
         this.playerData = p_120011_.saveWithoutId(new CompoundTag());
      }

      super.save(p_120011_);
   }

   public Component canPlayerLogin(SocketAddress p_120007_, GameProfile p_120008_) {
      return (Component)(p_120008_.getName().equalsIgnoreCase(this.getServer().getSingleplayerName()) && this.getPlayerByName(p_120008_.getName()) != null ? new TranslatableComponent("multiplayer.disconnect.name_taken") : super.canPlayerLogin(p_120007_, p_120008_));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public CompoundTag getSingleplayerData() {
      return this.playerData;
   }
}