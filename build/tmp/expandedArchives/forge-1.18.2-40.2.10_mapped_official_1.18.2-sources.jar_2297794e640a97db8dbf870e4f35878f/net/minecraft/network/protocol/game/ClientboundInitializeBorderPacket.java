package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundInitializeBorderPacket implements Packet<ClientGamePacketListener> {
   private final double newCenterX;
   private final double newCenterZ;
   private final double oldSize;
   private final double newSize;
   private final long lerpTime;
   private final int newAbsoluteMaxSize;
   private final int warningBlocks;
   private final int warningTime;

   public ClientboundInitializeBorderPacket(FriendlyByteBuf p_178879_) {
      this.newCenterX = p_178879_.readDouble();
      this.newCenterZ = p_178879_.readDouble();
      this.oldSize = p_178879_.readDouble();
      this.newSize = p_178879_.readDouble();
      this.lerpTime = p_178879_.readVarLong();
      this.newAbsoluteMaxSize = p_178879_.readVarInt();
      this.warningBlocks = p_178879_.readVarInt();
      this.warningTime = p_178879_.readVarInt();
   }

   public ClientboundInitializeBorderPacket(WorldBorder p_178877_) {
      this.newCenterX = p_178877_.getCenterX();
      this.newCenterZ = p_178877_.getCenterZ();
      this.oldSize = p_178877_.getSize();
      this.newSize = p_178877_.getLerpTarget();
      this.lerpTime = p_178877_.getLerpRemainingTime();
      this.newAbsoluteMaxSize = p_178877_.getAbsoluteMaxSize();
      this.warningBlocks = p_178877_.getWarningBlocks();
      this.warningTime = p_178877_.getWarningTime();
   }

   public void write(FriendlyByteBuf p_178881_) {
      p_178881_.writeDouble(this.newCenterX);
      p_178881_.writeDouble(this.newCenterZ);
      p_178881_.writeDouble(this.oldSize);
      p_178881_.writeDouble(this.newSize);
      p_178881_.writeVarLong(this.lerpTime);
      p_178881_.writeVarInt(this.newAbsoluteMaxSize);
      p_178881_.writeVarInt(this.warningBlocks);
      p_178881_.writeVarInt(this.warningTime);
   }

   public void handle(ClientGamePacketListener p_178885_) {
      p_178885_.handleInitializeBorder(this);
   }

   public double getNewCenterX() {
      return this.newCenterX;
   }

   public double getNewCenterZ() {
      return this.newCenterZ;
   }

   public double getNewSize() {
      return this.newSize;
   }

   public double getOldSize() {
      return this.oldSize;
   }

   public long getLerpTime() {
      return this.lerpTime;
   }

   public int getNewAbsoluteMaxSize() {
      return this.newAbsoluteMaxSize;
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }
}