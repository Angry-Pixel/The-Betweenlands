package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;

public class MemoryServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private final MinecraftServer server;
   private final Connection connection;

   public MemoryServerHandshakePacketListenerImpl(MinecraftServer p_9691_, Connection p_9692_) {
      this.server = p_9691_;
      this.connection = p_9692_;
   }

   public void handleIntention(ClientIntentionPacket p_9697_) {
      if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerLogin(p_9697_, this.connection)) return;
      this.connection.setProtocol(p_9697_.getIntention());
      this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
   }

   public void onDisconnect(Component p_9695_) {
   }

   public Connection getConnection() {
      return this.connection;
   }
}
