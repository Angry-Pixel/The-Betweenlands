package net.minecraft.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import org.slf4j.Logger;

public class RateKickingConnection extends Connection {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component EXCEED_REASON = new TranslatableComponent("disconnect.exceeded_packet_rate");
   private final int rateLimitPacketsPerSecond;

   public RateKickingConnection(int p_130558_) {
      super(PacketFlow.SERVERBOUND);
      this.rateLimitPacketsPerSecond = p_130558_;
   }

   protected void tickSecond() {
      super.tickSecond();
      float f = this.getAverageReceivedPackets();
      if (f > (float)this.rateLimitPacketsPerSecond) {
         LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", (float)f);
         this.send(new ClientboundDisconnectPacket(EXCEED_REASON), (p_130560_) -> {
            this.disconnect(EXCEED_REASON);
         });
         this.setReadOnly();
      }

   }
}