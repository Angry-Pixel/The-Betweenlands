package net.minecraft.network.protocol.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public class ClientboundStatusResponsePacket implements Packet<ClientStatusPacketListener> {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerStatus.Version.class, new ServerStatus.Version.Serializer()).registerTypeAdapter(ServerStatus.Players.class, new ServerStatus.Players.Serializer()).registerTypeAdapter(ServerStatus.class, new ServerStatus.Serializer()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory()).create();
   private final ServerStatus status;

   public ClientboundStatusResponsePacket(ServerStatus p_134890_) {
      this.status = p_134890_;
   }

   public ClientboundStatusResponsePacket(FriendlyByteBuf p_179834_) {
      this.status = GsonHelper.fromJson(GSON, p_179834_.readUtf(32767), ServerStatus.class);
   }

   public void write(FriendlyByteBuf p_134899_) {
      p_134899_.writeUtf(this.status.getJson()); //Forge: Let the response cache the JSON
   }

   public void handle(ClientStatusPacketListener p_134896_) {
      p_134896_.handleStatusResponse(this);
   }

   public ServerStatus getStatus() {
      return this.status;
   }
}
