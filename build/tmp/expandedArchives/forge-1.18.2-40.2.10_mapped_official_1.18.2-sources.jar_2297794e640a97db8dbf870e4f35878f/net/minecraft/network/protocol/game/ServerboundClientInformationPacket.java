package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public record ServerboundClientInformationPacket(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing) implements Packet<ServerGamePacketListener> {
   public static final int MAX_LANGUAGE_LENGTH = 16;

   public ServerboundClientInformationPacket(FriendlyByteBuf p_179560_) {
      this(p_179560_.readUtf(16), p_179560_.readByte(), p_179560_.readEnum(ChatVisiblity.class), p_179560_.readBoolean(), p_179560_.readUnsignedByte(), p_179560_.readEnum(HumanoidArm.class), p_179560_.readBoolean(), p_179560_.readBoolean());
   }

   public void write(FriendlyByteBuf p_133884_) {
      p_133884_.writeUtf(this.language);
      p_133884_.writeByte(this.viewDistance);
      p_133884_.writeEnum(this.chatVisibility);
      p_133884_.writeBoolean(this.chatColors);
      p_133884_.writeByte(this.modelCustomisation);
      p_133884_.writeEnum(this.mainHand);
      p_133884_.writeBoolean(this.textFilteringEnabled);
      p_133884_.writeBoolean(this.allowsListing);
   }

   public void handle(ServerGamePacketListener p_133882_) {
      p_133882_.handleClientInformation(this);
   }
}