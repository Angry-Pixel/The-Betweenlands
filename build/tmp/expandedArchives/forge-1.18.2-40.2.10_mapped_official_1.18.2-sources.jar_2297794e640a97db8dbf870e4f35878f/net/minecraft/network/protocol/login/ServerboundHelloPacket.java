package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundHelloPacket implements Packet<ServerLoginPacketListener> {
   private final GameProfile gameProfile;

   public ServerboundHelloPacket(GameProfile p_134842_) {
      this.gameProfile = p_134842_;
   }

   public ServerboundHelloPacket(FriendlyByteBuf p_179827_) {
      this.gameProfile = new GameProfile((UUID)null, p_179827_.readUtf(16));
   }

   public void write(FriendlyByteBuf p_134851_) {
      p_134851_.writeUtf(this.gameProfile.getName());
   }

   public void handle(ServerLoginPacketListener p_134848_) {
      p_134848_.handleHello(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}