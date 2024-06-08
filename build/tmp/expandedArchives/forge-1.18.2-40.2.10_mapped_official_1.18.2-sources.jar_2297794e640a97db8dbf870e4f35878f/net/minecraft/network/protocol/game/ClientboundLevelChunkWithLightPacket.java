package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;
   private final ClientboundLevelChunkPacketData chunkData;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLevelChunkWithLightPacket(LevelChunk p_195704_, LevelLightEngine p_195705_, @Nullable BitSet p_195706_, @Nullable BitSet p_195707_, boolean p_195708_) {
      ChunkPos chunkpos = p_195704_.getPos();
      this.x = chunkpos.x;
      this.z = chunkpos.z;
      this.chunkData = new ClientboundLevelChunkPacketData(p_195704_);
      this.lightData = new ClientboundLightUpdatePacketData(chunkpos, p_195705_, p_195706_, p_195707_, p_195708_);
   }

   public ClientboundLevelChunkWithLightPacket(FriendlyByteBuf p_195710_) {
      this.x = p_195710_.readInt();
      this.z = p_195710_.readInt();
      this.chunkData = new ClientboundLevelChunkPacketData(p_195710_, this.x, this.z);
      this.lightData = new ClientboundLightUpdatePacketData(p_195710_, this.x, this.z);
   }

   public void write(FriendlyByteBuf p_195712_) {
      p_195712_.writeInt(this.x);
      p_195712_.writeInt(this.z);
      this.chunkData.write(p_195712_);
      this.lightData.write(p_195712_);
   }

   public void handle(ClientGamePacketListener p_195716_) {
      p_195716_.handleLevelChunkWithLight(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public ClientboundLevelChunkPacketData getChunkData() {
      return this.chunkData;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}