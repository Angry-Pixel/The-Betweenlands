package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record ClientboundLoginPacket(int playerId, boolean hardcore, GameType gameType, @Nullable GameType previousGameType, Set<ResourceKey<Level>> levels, RegistryAccess.Frozen registryHolder, Holder<DimensionType> dimensionType, ResourceKey<Level> dimension, long seed, int maxPlayers, int chunkRadius, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean isDebug, boolean isFlat) implements Packet<ClientGamePacketListener> {
   public ClientboundLoginPacket(FriendlyByteBuf p_178960_) {
      this(p_178960_.readInt(), p_178960_.readBoolean(), GameType.byId(p_178960_.readByte()), GameType.byNullableId(p_178960_.readByte()), p_178960_.readCollection(Sets::newHashSetWithExpectedSize, (p_178965_) -> {
         return ResourceKey.create(Registry.DIMENSION_REGISTRY, p_178965_.readResourceLocation());
      }), p_178960_.readWithCodec(RegistryAccess.NETWORK_CODEC).freeze(), p_178960_.readWithCodec(DimensionType.CODEC), ResourceKey.create(Registry.DIMENSION_REGISTRY, p_178960_.readResourceLocation()), p_178960_.readLong(), p_178960_.readVarInt(), p_178960_.readVarInt(), p_178960_.readVarInt(), p_178960_.readBoolean(), p_178960_.readBoolean(), p_178960_.readBoolean(), p_178960_.readBoolean());
   }

   public void write(FriendlyByteBuf p_132400_) {
      p_132400_.writeInt(this.playerId);
      p_132400_.writeBoolean(this.hardcore);
      p_132400_.writeByte(this.gameType.getId());
      p_132400_.writeByte(GameType.getNullableId(this.previousGameType));
      p_132400_.writeCollection(this.levels, (p_178962_, p_178963_) -> {
         p_178962_.writeResourceLocation(p_178963_.location());
      });
      p_132400_.writeWithCodec(RegistryAccess.NETWORK_CODEC, this.registryHolder);
      p_132400_.writeWithCodec(DimensionType.CODEC, this.dimensionType);
      p_132400_.writeResourceLocation(this.dimension.location());
      p_132400_.writeLong(this.seed);
      p_132400_.writeVarInt(this.maxPlayers);
      p_132400_.writeVarInt(this.chunkRadius);
      p_132400_.writeVarInt(this.simulationDistance);
      p_132400_.writeBoolean(this.reducedDebugInfo);
      p_132400_.writeBoolean(this.showDeathScreen);
      p_132400_.writeBoolean(this.isDebug);
      p_132400_.writeBoolean(this.isFlat);
   }

   public void handle(ClientGamePacketListener p_132397_) {
      p_132397_.handleLogin(this);
   }
}