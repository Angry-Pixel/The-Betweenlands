package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket implements Packet<ClientGamePacketListener> {
   private final int slot;
   private final String objectiveName;

   public ClientboundSetDisplayObjectivePacket(int p_133131_, @Nullable Objective p_133132_) {
      this.slot = p_133131_;
      if (p_133132_ == null) {
         this.objectiveName = "";
      } else {
         this.objectiveName = p_133132_.getName();
      }

   }

   public ClientboundSetDisplayObjectivePacket(FriendlyByteBuf p_179288_) {
      this.slot = p_179288_.readByte();
      this.objectiveName = p_179288_.readUtf();
   }

   public void write(FriendlyByteBuf p_133141_) {
      p_133141_.writeByte(this.slot);
      p_133141_.writeUtf(this.objectiveName);
   }

   public void handle(ClientGamePacketListener p_133138_) {
      p_133138_.handleSetDisplayObjective(this);
   }

   public int getSlot() {
      return this.slot;
   }

   @Nullable
   public String getObjectiveName() {
      return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
   }
}