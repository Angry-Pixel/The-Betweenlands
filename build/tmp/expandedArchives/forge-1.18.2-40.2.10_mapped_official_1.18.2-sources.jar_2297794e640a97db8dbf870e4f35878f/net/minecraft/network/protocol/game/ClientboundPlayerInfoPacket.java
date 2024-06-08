package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoPacket implements Packet<ClientGamePacketListener> {
   private final ClientboundPlayerInfoPacket.Action action;
   private final List<ClientboundPlayerInfoPacket.PlayerUpdate> entries;

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action p_132724_, ServerPlayer... p_132725_) {
      this.action = p_132724_;
      this.entries = Lists.newArrayListWithCapacity(p_132725_.length);

      for(ServerPlayer serverplayer : p_132725_) {
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(serverplayer.getGameProfile(), serverplayer.latency, serverplayer.gameMode.getGameModeForPlayer(), serverplayer.getTabListDisplayName()));
      }

   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action p_179083_, Collection<ServerPlayer> p_179084_) {
      this.action = p_179083_;
      this.entries = Lists.newArrayListWithCapacity(p_179084_.size());

      for(ServerPlayer serverplayer : p_179084_) {
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(serverplayer.getGameProfile(), serverplayer.latency, serverplayer.gameMode.getGameModeForPlayer(), serverplayer.getTabListDisplayName()));
      }

   }

   public ClientboundPlayerInfoPacket(FriendlyByteBuf p_179081_) {
      this.action = p_179081_.readEnum(ClientboundPlayerInfoPacket.Action.class);
      this.entries = p_179081_.readList(this.action::read);
   }

   public void write(FriendlyByteBuf p_132734_) {
      p_132734_.writeEnum(this.action);
      p_132734_.writeCollection(this.entries, this.action::write);
   }

   public void handle(ClientGamePacketListener p_132731_) {
      p_132731_.handlePlayerInfo(this);
   }

   public List<ClientboundPlayerInfoPacket.PlayerUpdate> getEntries() {
      return this.entries;
   }

   public ClientboundPlayerInfoPacket.Action getAction() {
      return this.action;
   }

   @Nullable
   static Component readDisplayName(FriendlyByteBuf p_179089_) {
      return p_179089_.readBoolean() ? p_179089_.readComponent() : null;
   }

   static void writeDisplayName(FriendlyByteBuf p_179086_, @Nullable Component p_179087_) {
      if (p_179087_ == null) {
         p_179086_.writeBoolean(false);
      } else {
         p_179086_.writeBoolean(true);
         p_179086_.writeComponent(p_179087_);
      }

   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf p_179101_) {
            GameProfile gameprofile = new GameProfile(p_179101_.readUUID(), p_179101_.readUtf(16));
            PropertyMap propertymap = gameprofile.getProperties();
            p_179101_.readWithCount((p_179099_) -> {
               String s = p_179099_.readUtf();
               String s1 = p_179099_.readUtf();
               if (p_179099_.readBoolean()) {
                  String s2 = p_179099_.readUtf();
                  propertymap.put(s, new Property(s, s1, s2));
               } else {
                  propertymap.put(s, new Property(s, s1));
               }

            });
            GameType gametype = GameType.byId(p_179101_.readVarInt());
            int i = p_179101_.readVarInt();
            Component component = ClientboundPlayerInfoPacket.readDisplayName(p_179101_);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(gameprofile, i, gametype, component);
         }

         protected void write(FriendlyByteBuf p_179106_, ClientboundPlayerInfoPacket.PlayerUpdate p_179107_) {
            p_179106_.writeUUID(p_179107_.getProfile().getId());
            p_179106_.writeUtf(p_179107_.getProfile().getName());
            p_179106_.writeCollection(p_179107_.getProfile().getProperties().values(), (p_179103_, p_179104_) -> {
               p_179103_.writeUtf(p_179104_.getName());
               p_179103_.writeUtf(p_179104_.getValue());
               if (p_179104_.hasSignature()) {
                  p_179103_.writeBoolean(true);
                  p_179103_.writeUtf(p_179104_.getSignature());
               } else {
                  p_179103_.writeBoolean(false);
               }

            });
            p_179106_.writeVarInt(p_179107_.getGameMode().getId());
            p_179106_.writeVarInt(p_179107_.getLatency());
            ClientboundPlayerInfoPacket.writeDisplayName(p_179106_, p_179107_.getDisplayName());
         }
      },
      UPDATE_GAME_MODE {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf p_179112_) {
            GameProfile gameprofile = new GameProfile(p_179112_.readUUID(), (String)null);
            GameType gametype = GameType.byId(p_179112_.readVarInt());
            return new ClientboundPlayerInfoPacket.PlayerUpdate(gameprofile, 0, gametype, (Component)null);
         }

         protected void write(FriendlyByteBuf p_179114_, ClientboundPlayerInfoPacket.PlayerUpdate p_179115_) {
            p_179114_.writeUUID(p_179115_.getProfile().getId());
            p_179114_.writeVarInt(p_179115_.getGameMode().getId());
         }
      },
      UPDATE_LATENCY {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf p_179120_) {
            GameProfile gameprofile = new GameProfile(p_179120_.readUUID(), (String)null);
            int i = p_179120_.readVarInt();
            return new ClientboundPlayerInfoPacket.PlayerUpdate(gameprofile, i, (GameType)null, (Component)null);
         }

         protected void write(FriendlyByteBuf p_179122_, ClientboundPlayerInfoPacket.PlayerUpdate p_179123_) {
            p_179122_.writeUUID(p_179123_.getProfile().getId());
            p_179122_.writeVarInt(p_179123_.getLatency());
         }
      },
      UPDATE_DISPLAY_NAME {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf p_179128_) {
            GameProfile gameprofile = new GameProfile(p_179128_.readUUID(), (String)null);
            Component component = ClientboundPlayerInfoPacket.readDisplayName(p_179128_);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(gameprofile, 0, (GameType)null, component);
         }

         protected void write(FriendlyByteBuf p_179130_, ClientboundPlayerInfoPacket.PlayerUpdate p_179131_) {
            p_179130_.writeUUID(p_179131_.getProfile().getId());
            ClientboundPlayerInfoPacket.writeDisplayName(p_179130_, p_179131_.getDisplayName());
         }
      },
      REMOVE_PLAYER {
         protected ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf p_179136_) {
            GameProfile gameprofile = new GameProfile(p_179136_.readUUID(), (String)null);
            return new ClientboundPlayerInfoPacket.PlayerUpdate(gameprofile, 0, (GameType)null, (Component)null);
         }

         protected void write(FriendlyByteBuf p_179138_, ClientboundPlayerInfoPacket.PlayerUpdate p_179139_) {
            p_179138_.writeUUID(p_179139_.getProfile().getId());
         }
      };

      protected abstract ClientboundPlayerInfoPacket.PlayerUpdate read(FriendlyByteBuf p_179091_);

      protected abstract void write(FriendlyByteBuf p_179092_, ClientboundPlayerInfoPacket.PlayerUpdate p_179093_);
   }

   public static class PlayerUpdate {
      private final int latency;
      private final GameType gameMode;
      private final GameProfile profile;
      @Nullable
      private final Component displayName;

      public PlayerUpdate(GameProfile p_179141_, int p_179142_, @Nullable GameType p_179143_, @Nullable Component p_179144_) {
         this.profile = p_179141_;
         this.latency = p_179142_;
         this.gameMode = p_179143_;
         this.displayName = p_179144_;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getLatency() {
         return this.latency;
      }

      public GameType getGameMode() {
         return this.gameMode;
      }

      @Nullable
      public Component getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : Component.Serializer.toJson(this.displayName)).toString();
      }
   }
}