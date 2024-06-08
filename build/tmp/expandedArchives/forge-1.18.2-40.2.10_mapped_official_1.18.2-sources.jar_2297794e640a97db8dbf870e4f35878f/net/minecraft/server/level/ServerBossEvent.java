package net.minecraft.server.level;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class ServerBossEvent extends BossEvent {
   private final Set<ServerPlayer> players = Sets.newHashSet();
   private final Set<ServerPlayer> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
   private boolean visible = true;

   public ServerBossEvent(Component p_8300_, BossEvent.BossBarColor p_8301_, BossEvent.BossBarOverlay p_8302_) {
      super(Mth.createInsecureUUID(), p_8300_, p_8301_, p_8302_);
   }

   public void setProgress(float p_143223_) {
      if (p_143223_ != this.progress) {
         super.setProgress(p_143223_);
         this.broadcast(ClientboundBossEventPacket::createUpdateProgressPacket);
      }

   }

   public void setColor(BossEvent.BossBarColor p_8307_) {
      if (p_8307_ != this.color) {
         super.setColor(p_8307_);
         this.broadcast(ClientboundBossEventPacket::createUpdateStylePacket);
      }

   }

   public void setOverlay(BossEvent.BossBarOverlay p_8309_) {
      if (p_8309_ != this.overlay) {
         super.setOverlay(p_8309_);
         this.broadcast(ClientboundBossEventPacket::createUpdateStylePacket);
      }

   }

   public BossEvent setDarkenScreen(boolean p_8315_) {
      if (p_8315_ != this.darkenScreen) {
         super.setDarkenScreen(p_8315_);
         this.broadcast(ClientboundBossEventPacket::createUpdatePropertiesPacket);
      }

      return this;
   }

   public BossEvent setPlayBossMusic(boolean p_8318_) {
      if (p_8318_ != this.playBossMusic) {
         super.setPlayBossMusic(p_8318_);
         this.broadcast(ClientboundBossEventPacket::createUpdatePropertiesPacket);
      }

      return this;
   }

   public BossEvent setCreateWorldFog(boolean p_8320_) {
      if (p_8320_ != this.createWorldFog) {
         super.setCreateWorldFog(p_8320_);
         this.broadcast(ClientboundBossEventPacket::createUpdatePropertiesPacket);
      }

      return this;
   }

   public void setName(Component p_8311_) {
      if (!Objects.equal(p_8311_, this.name)) {
         super.setName(p_8311_);
         this.broadcast(ClientboundBossEventPacket::createUpdateNamePacket);
      }

   }

   private void broadcast(Function<BossEvent, ClientboundBossEventPacket> p_143225_) {
      if (this.visible) {
         ClientboundBossEventPacket clientboundbosseventpacket = p_143225_.apply(this);

         for(ServerPlayer serverplayer : this.players) {
            serverplayer.connection.send(clientboundbosseventpacket);
         }
      }

   }

   public void addPlayer(ServerPlayer p_8305_) {
      if (this.players.add(p_8305_) && this.visible) {
         p_8305_.connection.send(ClientboundBossEventPacket.createAddPacket(this));
      }

   }

   public void removePlayer(ServerPlayer p_8316_) {
      if (this.players.remove(p_8316_) && this.visible) {
         p_8316_.connection.send(ClientboundBossEventPacket.createRemovePacket(this.getId()));
      }

   }

   public void removeAllPlayers() {
      if (!this.players.isEmpty()) {
         for(ServerPlayer serverplayer : Lists.newArrayList(this.players)) {
            this.removePlayer(serverplayer);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_8322_) {
      if (p_8322_ != this.visible) {
         this.visible = p_8322_;

         for(ServerPlayer serverplayer : this.players) {
            serverplayer.connection.send(p_8322_ ? ClientboundBossEventPacket.createAddPacket(this) : ClientboundBossEventPacket.createRemovePacket(this.getId()));
         }
      }

   }

   public Collection<ServerPlayer> getPlayers() {
      return this.unmodifiablePlayers;
   }
}