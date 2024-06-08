package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.PlayerTeam;

public class ClientboundSetPlayerTeamPacket implements Packet<ClientGamePacketListener> {
   private static final int METHOD_ADD = 0;
   private static final int METHOD_REMOVE = 1;
   private static final int METHOD_CHANGE = 2;
   private static final int METHOD_JOIN = 3;
   private static final int METHOD_LEAVE = 4;
   private static final int MAX_VISIBILITY_LENGTH = 40;
   private static final int MAX_COLLISION_LENGTH = 40;
   private final int method;
   private final String name;
   private final Collection<String> players;
   private final Optional<ClientboundSetPlayerTeamPacket.Parameters> parameters;

   private ClientboundSetPlayerTeamPacket(String p_179318_, int p_179319_, Optional<ClientboundSetPlayerTeamPacket.Parameters> p_179320_, Collection<String> p_179321_) {
      this.name = p_179318_;
      this.method = p_179319_;
      this.parameters = p_179320_;
      this.players = ImmutableList.copyOf(p_179321_);
   }

   public static ClientboundSetPlayerTeamPacket createAddOrModifyPacket(PlayerTeam p_179333_, boolean p_179334_) {
      return new ClientboundSetPlayerTeamPacket(p_179333_.getName(), p_179334_ ? 0 : 2, Optional.of(new ClientboundSetPlayerTeamPacket.Parameters(p_179333_)), (Collection<String>)(p_179334_ ? p_179333_.getPlayers() : ImmutableList.of()));
   }

   public static ClientboundSetPlayerTeamPacket createRemovePacket(PlayerTeam p_179327_) {
      return new ClientboundSetPlayerTeamPacket(p_179327_.getName(), 1, Optional.empty(), ImmutableList.of());
   }

   public static ClientboundSetPlayerTeamPacket createPlayerPacket(PlayerTeam p_179329_, String p_179330_, ClientboundSetPlayerTeamPacket.Action p_179331_) {
      return new ClientboundSetPlayerTeamPacket(p_179329_.getName(), p_179331_ == ClientboundSetPlayerTeamPacket.Action.ADD ? 3 : 4, Optional.empty(), ImmutableList.of(p_179330_));
   }

   public ClientboundSetPlayerTeamPacket(FriendlyByteBuf p_179323_) {
      this.name = p_179323_.readUtf();
      this.method = p_179323_.readByte();
      if (shouldHaveParameters(this.method)) {
         this.parameters = Optional.of(new ClientboundSetPlayerTeamPacket.Parameters(p_179323_));
      } else {
         this.parameters = Optional.empty();
      }

      if (shouldHavePlayerList(this.method)) {
         this.players = p_179323_.readList(FriendlyByteBuf::readUtf);
      } else {
         this.players = ImmutableList.of();
      }

   }

   public void write(FriendlyByteBuf p_133313_) {
      p_133313_.writeUtf(this.name);
      p_133313_.writeByte(this.method);
      if (shouldHaveParameters(this.method)) {
         this.parameters.orElseThrow(() -> {
            return new IllegalStateException("Parameters not present, but method is" + this.method);
         }).write(p_133313_);
      }

      if (shouldHavePlayerList(this.method)) {
         p_133313_.writeCollection(this.players, FriendlyByteBuf::writeUtf);
      }

   }

   private static boolean shouldHavePlayerList(int p_179325_) {
      return p_179325_ == 0 || p_179325_ == 3 || p_179325_ == 4;
   }

   private static boolean shouldHaveParameters(int p_179337_) {
      return p_179337_ == 0 || p_179337_ == 2;
   }

   @Nullable
   public ClientboundSetPlayerTeamPacket.Action getPlayerAction() {
      switch(this.method) {
      case 0:
      case 3:
         return ClientboundSetPlayerTeamPacket.Action.ADD;
      case 1:
      case 2:
      default:
         return null;
      case 4:
         return ClientboundSetPlayerTeamPacket.Action.REMOVE;
      }
   }

   @Nullable
   public ClientboundSetPlayerTeamPacket.Action getTeamAction() {
      switch(this.method) {
      case 0:
         return ClientboundSetPlayerTeamPacket.Action.ADD;
      case 1:
         return ClientboundSetPlayerTeamPacket.Action.REMOVE;
      default:
         return null;
      }
   }

   public void handle(ClientGamePacketListener p_133310_) {
      p_133310_.handleSetPlayerTeamPacket(this);
   }

   public String getName() {
      return this.name;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public Optional<ClientboundSetPlayerTeamPacket.Parameters> getParameters() {
      return this.parameters;
   }

   public static enum Action {
      ADD,
      REMOVE;
   }

   public static class Parameters {
      private final Component displayName;
      private final Component playerPrefix;
      private final Component playerSuffix;
      private final String nametagVisibility;
      private final String collisionRule;
      private final ChatFormatting color;
      private final int options;

      public Parameters(PlayerTeam p_179360_) {
         this.displayName = p_179360_.getDisplayName();
         this.options = p_179360_.packOptions();
         this.nametagVisibility = p_179360_.getNameTagVisibility().name;
         this.collisionRule = p_179360_.getCollisionRule().name;
         this.color = p_179360_.getColor();
         this.playerPrefix = p_179360_.getPlayerPrefix();
         this.playerSuffix = p_179360_.getPlayerSuffix();
      }

      public Parameters(FriendlyByteBuf p_179362_) {
         this.displayName = p_179362_.readComponent();
         this.options = p_179362_.readByte();
         this.nametagVisibility = p_179362_.readUtf(40);
         this.collisionRule = p_179362_.readUtf(40);
         this.color = p_179362_.readEnum(ChatFormatting.class);
         this.playerPrefix = p_179362_.readComponent();
         this.playerSuffix = p_179362_.readComponent();
      }

      public Component getDisplayName() {
         return this.displayName;
      }

      public int getOptions() {
         return this.options;
      }

      public ChatFormatting getColor() {
         return this.color;
      }

      public String getNametagVisibility() {
         return this.nametagVisibility;
      }

      public String getCollisionRule() {
         return this.collisionRule;
      }

      public Component getPlayerPrefix() {
         return this.playerPrefix;
      }

      public Component getPlayerSuffix() {
         return this.playerSuffix;
      }

      public void write(FriendlyByteBuf p_179365_) {
         p_179365_.writeComponent(this.displayName);
         p_179365_.writeByte(this.options);
         p_179365_.writeUtf(this.nametagVisibility);
         p_179365_.writeUtf(this.collisionRule);
         p_179365_.writeEnum(this.color);
         p_179365_.writeComponent(this.playerPrefix);
         p_179365_.writeComponent(this.playerSuffix);
      }
   }
}