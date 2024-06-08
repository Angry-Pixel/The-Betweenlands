package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ServerboundPlayerCommandPacket implements Packet<ServerGamePacketListener> {
   private final int id;
   private final ServerboundPlayerCommandPacket.Action action;
   private final int data;

   public ServerboundPlayerCommandPacket(Entity p_134306_, ServerboundPlayerCommandPacket.Action p_134307_) {
      this(p_134306_, p_134307_, 0);
   }

   public ServerboundPlayerCommandPacket(Entity p_134309_, ServerboundPlayerCommandPacket.Action p_134310_, int p_134311_) {
      this.id = p_134309_.getId();
      this.action = p_134310_;
      this.data = p_134311_;
   }

   public ServerboundPlayerCommandPacket(FriendlyByteBuf p_179714_) {
      this.id = p_179714_.readVarInt();
      this.action = p_179714_.readEnum(ServerboundPlayerCommandPacket.Action.class);
      this.data = p_179714_.readVarInt();
   }

   public void write(FriendlyByteBuf p_134319_) {
      p_134319_.writeVarInt(this.id);
      p_134319_.writeEnum(this.action);
      p_134319_.writeVarInt(this.data);
   }

   public void handle(ServerGamePacketListener p_134317_) {
      p_134317_.handlePlayerCommand(this);
   }

   public int getId() {
      return this.id;
   }

   public ServerboundPlayerCommandPacket.Action getAction() {
      return this.action;
   }

   public int getData() {
      return this.data;
   }

   public static enum Action {
      PRESS_SHIFT_KEY,
      RELEASE_SHIFT_KEY,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;
   }
}