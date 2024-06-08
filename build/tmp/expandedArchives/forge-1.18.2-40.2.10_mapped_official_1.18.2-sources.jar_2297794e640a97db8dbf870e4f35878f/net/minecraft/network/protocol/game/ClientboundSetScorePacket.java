package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ServerScoreboard;

public class ClientboundSetScorePacket implements Packet<ClientGamePacketListener> {
   private final String owner;
   @Nullable
   private final String objectiveName;
   private final int score;
   private final ServerScoreboard.Method method;

   public ClientboundSetScorePacket(ServerScoreboard.Method p_133329_, @Nullable String p_133330_, String p_133331_, int p_133332_) {
      if (p_133329_ != ServerScoreboard.Method.REMOVE && p_133330_ == null) {
         throw new IllegalArgumentException("Need an objective name");
      } else {
         this.owner = p_133331_;
         this.objectiveName = p_133330_;
         this.score = p_133332_;
         this.method = p_133329_;
      }
   }

   public ClientboundSetScorePacket(FriendlyByteBuf p_179373_) {
      this.owner = p_179373_.readUtf();
      this.method = p_179373_.readEnum(ServerScoreboard.Method.class);
      String s = p_179373_.readUtf();
      this.objectiveName = Objects.equals(s, "") ? null : s;
      if (this.method != ServerScoreboard.Method.REMOVE) {
         this.score = p_179373_.readVarInt();
      } else {
         this.score = 0;
      }

   }

   public void write(FriendlyByteBuf p_133341_) {
      p_133341_.writeUtf(this.owner);
      p_133341_.writeEnum(this.method);
      p_133341_.writeUtf(this.objectiveName == null ? "" : this.objectiveName);
      if (this.method != ServerScoreboard.Method.REMOVE) {
         p_133341_.writeVarInt(this.score);
      }

   }

   public void handle(ClientGamePacketListener p_133338_) {
      p_133338_.handleSetScore(this);
   }

   public String getOwner() {
      return this.owner;
   }

   @Nullable
   public String getObjectiveName() {
      return this.objectiveName;
   }

   public int getScore() {
      return this.score;
   }

   public ServerScoreboard.Method getMethod() {
      return this.method;
   }
}