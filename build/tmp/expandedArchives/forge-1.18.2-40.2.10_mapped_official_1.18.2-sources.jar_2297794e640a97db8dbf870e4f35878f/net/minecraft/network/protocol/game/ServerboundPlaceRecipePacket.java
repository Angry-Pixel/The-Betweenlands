package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundPlaceRecipePacket implements Packet<ServerGamePacketListener> {
   private final int containerId;
   private final ResourceLocation recipe;
   private final boolean shiftDown;

   public ServerboundPlaceRecipePacket(int p_134240_, Recipe<?> p_134241_, boolean p_134242_) {
      this.containerId = p_134240_;
      this.recipe = p_134241_.getId();
      this.shiftDown = p_134242_;
   }

   public ServerboundPlaceRecipePacket(FriendlyByteBuf p_179706_) {
      this.containerId = p_179706_.readByte();
      this.recipe = p_179706_.readResourceLocation();
      this.shiftDown = p_179706_.readBoolean();
   }

   public void write(FriendlyByteBuf p_134251_) {
      p_134251_.writeByte(this.containerId);
      p_134251_.writeResourceLocation(this.recipe);
      p_134251_.writeBoolean(this.shiftDown);
   }

   public void handle(ServerGamePacketListener p_134248_) {
      p_134248_.handlePlaceRecipe(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isShiftDown() {
      return this.shiftDown;
   }
}