package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class ClientboundSectionBlocksUpdatePacket implements Packet<ClientGamePacketListener> {
   private static final int POS_IN_SECTION_BITS = 12;
   private final SectionPos sectionPos;
   private final short[] positions;
   private final BlockState[] states;
   private final boolean suppressLightUpdates;

   public ClientboundSectionBlocksUpdatePacket(SectionPos p_132986_, ShortSet p_132987_, LevelChunkSection p_132988_, boolean p_132989_) {
      this.sectionPos = p_132986_;
      this.suppressLightUpdates = p_132989_;
      int i = p_132987_.size();
      this.positions = new short[i];
      this.states = new BlockState[i];
      int j = 0;

      for(short short1 : p_132987_) {
         this.positions[j] = short1;
         this.states[j] = p_132988_.getBlockState(SectionPos.sectionRelativeX(short1), SectionPos.sectionRelativeY(short1), SectionPos.sectionRelativeZ(short1));
         ++j;
      }

   }

   public ClientboundSectionBlocksUpdatePacket(FriendlyByteBuf p_179196_) {
      this.sectionPos = SectionPos.of(p_179196_.readLong());
      this.suppressLightUpdates = p_179196_.readBoolean();
      int i = p_179196_.readVarInt();
      this.positions = new short[i];
      this.states = new BlockState[i];

      for(int j = 0; j < i; ++j) {
         long k = p_179196_.readVarLong();
         this.positions[j] = (short)((int)(k & 4095L));
         this.states[j] = Block.BLOCK_STATE_REGISTRY.byId((int)(k >>> 12));
      }

   }

   public void write(FriendlyByteBuf p_133002_) {
      p_133002_.writeLong(this.sectionPos.asLong());
      p_133002_.writeBoolean(this.suppressLightUpdates);
      p_133002_.writeVarInt(this.positions.length);

      for(int i = 0; i < this.positions.length; ++i) {
         p_133002_.writeVarLong((long)(Block.getId(this.states[i]) << 12 | this.positions[i]));
      }

   }

   public void handle(ClientGamePacketListener p_132999_) {
      p_132999_.handleChunkBlocksUpdate(this);
   }

   public void runUpdates(BiConsumer<BlockPos, BlockState> p_132993_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < this.positions.length; ++i) {
         short short1 = this.positions[i];
         blockpos$mutableblockpos.set(this.sectionPos.relativeToBlockX(short1), this.sectionPos.relativeToBlockY(short1), this.sectionPos.relativeToBlockZ(short1));
         p_132993_.accept(blockpos$mutableblockpos, this.states[i]);
      }

   }

   public boolean shouldSuppressLightUpdates() {
      return this.suppressLightUpdates;
   }
}