package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSeenAdvancementsPacket implements Packet<ServerGamePacketListener> {
   private final ServerboundSeenAdvancementsPacket.Action action;
   @Nullable
   private final ResourceLocation tab;

   public ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action p_134434_, @Nullable ResourceLocation p_134435_) {
      this.action = p_134434_;
      this.tab = p_134435_;
   }

   public static ServerboundSeenAdvancementsPacket openedTab(Advancement p_134443_) {
      return new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.OPENED_TAB, p_134443_.getId());
   }

   public static ServerboundSeenAdvancementsPacket closedScreen() {
      return new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.CLOSED_SCREEN, (ResourceLocation)null);
   }

   public ServerboundSeenAdvancementsPacket(FriendlyByteBuf p_179744_) {
      this.action = p_179744_.readEnum(ServerboundSeenAdvancementsPacket.Action.class);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         this.tab = p_179744_.readResourceLocation();
      } else {
         this.tab = null;
      }

   }

   public void write(FriendlyByteBuf p_134446_) {
      p_134446_.writeEnum(this.action);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         p_134446_.writeResourceLocation(this.tab);
      }

   }

   public void handle(ServerGamePacketListener p_134441_) {
      p_134441_.handleSeenAdvancements(this);
   }

   public ServerboundSeenAdvancementsPacket.Action getAction() {
      return this.action;
   }

   @Nullable
   public ResourceLocation getTab() {
      return this.tab;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;
   }
}