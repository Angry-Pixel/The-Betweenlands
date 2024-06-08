package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacketData {
   private static final int TWO_MEGABYTES = 2097152;
   private final CompoundTag heightmaps;
   private final byte[] buffer;
   private final List<ClientboundLevelChunkPacketData.BlockEntityInfo> blockEntitiesData;

   public ClientboundLevelChunkPacketData(LevelChunk p_195651_) {
      this.heightmaps = new CompoundTag();

      for(Entry<Heightmap.Types, Heightmap> entry : p_195651_.getHeightmaps()) {
         if (entry.getKey().sendToClient()) {
            this.heightmaps.put(entry.getKey().getSerializationKey(), new LongArrayTag(entry.getValue().getRawData()));
         }
      }

      this.buffer = new byte[calculateChunkSize(p_195651_)];
      extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), p_195651_);
      this.blockEntitiesData = Lists.newArrayList();

      for(Entry<BlockPos, BlockEntity> entry1 : p_195651_.getBlockEntities().entrySet()) {
         this.blockEntitiesData.add(ClientboundLevelChunkPacketData.BlockEntityInfo.create(entry1.getValue()));
      }

   }

   public ClientboundLevelChunkPacketData(FriendlyByteBuf p_195653_, int p_195654_, int p_195655_) {
      this.heightmaps = p_195653_.readNbt();
      if (this.heightmaps == null) {
         throw new RuntimeException("Can't read heightmap in packet for [" + p_195654_ + ", " + p_195655_ + "]");
      } else {
         int i = p_195653_.readVarInt();
         if (i > 2097152) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
         } else {
            this.buffer = new byte[i];
            p_195653_.readBytes(this.buffer);
            this.blockEntitiesData = p_195653_.readList(ClientboundLevelChunkPacketData.BlockEntityInfo::new);
         }
      }
   }

   public void write(FriendlyByteBuf p_195667_) {
      p_195667_.writeNbt(this.heightmaps);
      p_195667_.writeVarInt(this.buffer.length);
      p_195667_.writeBytes(this.buffer);
      p_195667_.writeCollection(this.blockEntitiesData, (p_195672_, p_195673_) -> {
         p_195673_.write(p_195672_);
      });
   }

   private static int calculateChunkSize(LevelChunk p_195665_) {
      int i = 0;

      for(LevelChunkSection levelchunksection : p_195665_.getSections()) {
         i += levelchunksection.getSerializedSize();
      }

      return i;
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
      bytebuf.writerIndex(0);
      return bytebuf;
   }

   public static void extractChunkData(FriendlyByteBuf p_195669_, LevelChunk p_195670_) {
      for(LevelChunkSection levelchunksection : p_195670_.getSections()) {
         levelchunksection.write(p_195669_);
      }

   }

   public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int p_195658_, int p_195659_) {
      return (p_195663_) -> {
         this.getBlockEntitiesTags(p_195663_, p_195658_, p_195659_);
      };
   }

   private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput p_195675_, int p_195676_, int p_195677_) {
      int i = 16 * p_195676_;
      int j = 16 * p_195677_;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(ClientboundLevelChunkPacketData.BlockEntityInfo clientboundlevelchunkpacketdata$blockentityinfo : this.blockEntitiesData) {
         int k = i + SectionPos.sectionRelative(clientboundlevelchunkpacketdata$blockentityinfo.packedXZ >> 4);
         int l = j + SectionPos.sectionRelative(clientboundlevelchunkpacketdata$blockentityinfo.packedXZ);
         blockpos$mutableblockpos.set(k, clientboundlevelchunkpacketdata$blockentityinfo.y, l);
         p_195675_.accept(blockpos$mutableblockpos, clientboundlevelchunkpacketdata$blockentityinfo.type, clientboundlevelchunkpacketdata$blockentityinfo.tag);
      }

   }

   public FriendlyByteBuf getReadBuffer() {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
   }

   public CompoundTag getHeightmaps() {
      return this.heightmaps;
   }

   static class BlockEntityInfo {
      final int packedXZ;
      final int y;
      final BlockEntityType<?> type;
      @Nullable
      final CompoundTag tag;

      private BlockEntityInfo(int p_195685_, int p_195686_, BlockEntityType<?> p_195687_, @Nullable CompoundTag p_195688_) {
         this.packedXZ = p_195685_;
         this.y = p_195686_;
         this.type = p_195687_;
         this.tag = p_195688_;
      }

      private BlockEntityInfo(FriendlyByteBuf p_195690_) {
         this.packedXZ = p_195690_.readByte();
         this.y = p_195690_.readShort();
         int i = p_195690_.readVarInt();
         this.type = Registry.BLOCK_ENTITY_TYPE.byId(i);
         this.tag = p_195690_.readNbt();
      }

      void write(FriendlyByteBuf p_195694_) {
         p_195694_.writeByte(this.packedXZ);
         p_195694_.writeShort(this.y);
         p_195694_.writeVarInt(Registry.BLOCK_ENTITY_TYPE.getId(this.type));
         p_195694_.writeNbt(this.tag);
      }

      static ClientboundLevelChunkPacketData.BlockEntityInfo create(BlockEntity p_195692_) {
         CompoundTag compoundtag = p_195692_.getUpdateTag();
         BlockPos blockpos = p_195692_.getBlockPos();
         int i = SectionPos.sectionRelative(blockpos.getX()) << 4 | SectionPos.sectionRelative(blockpos.getZ());
         return new ClientboundLevelChunkPacketData.BlockEntityInfo(i, blockpos.getY(), p_195692_.getType(), compoundtag.isEmpty() ? null : compoundtag);
      }
   }

   @FunctionalInterface
   public interface BlockEntityTagOutput {
      void accept(BlockPos p_195696_, BlockEntityType<?> p_195697_, @Nullable CompoundTag p_195698_);
   }
}