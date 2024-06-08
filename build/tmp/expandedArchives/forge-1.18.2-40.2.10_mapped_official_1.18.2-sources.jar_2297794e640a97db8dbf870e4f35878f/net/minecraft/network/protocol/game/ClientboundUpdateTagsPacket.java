package net.minecraft.network.protocol.game;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;

public class ClientboundUpdateTagsPacket implements Packet<ClientGamePacketListener> {
   private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags;

   public ClientboundUpdateTagsPacket(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> p_179473_) {
      this.tags = p_179473_;
   }

   public ClientboundUpdateTagsPacket(FriendlyByteBuf p_179475_) {
      this.tags = p_179475_.readMap((p_179484_) -> {
         return ResourceKey.createRegistryKey(p_179484_.readResourceLocation());
      }, TagNetworkSerialization.NetworkPayload::read);
   }

   public void write(FriendlyByteBuf p_133661_) {
      p_133661_.writeMap(this.tags, (p_179480_, p_179481_) -> {
         p_179480_.writeResourceLocation(p_179481_.location());
      }, (p_206653_, p_206654_) -> {
         p_206654_.write(p_206653_);
      });
   }

   public void handle(ClientGamePacketListener p_133658_) {
      p_133658_.handleUpdateTags(this);
   }

   public Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> getTags() {
      return this.tags;
   }
}