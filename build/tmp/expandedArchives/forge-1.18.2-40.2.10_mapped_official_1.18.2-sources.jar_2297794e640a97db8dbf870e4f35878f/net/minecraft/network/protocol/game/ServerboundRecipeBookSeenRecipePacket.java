package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundRecipeBookSeenRecipePacket implements Packet<ServerGamePacketListener> {
   private final ResourceLocation recipe;

   public ServerboundRecipeBookSeenRecipePacket(Recipe<?> p_134383_) {
      this.recipe = p_134383_.getId();
   }

   public ServerboundRecipeBookSeenRecipePacket(FriendlyByteBuf p_179736_) {
      this.recipe = p_179736_.readResourceLocation();
   }

   public void write(FriendlyByteBuf p_134392_) {
      p_134392_.writeResourceLocation(this.recipe);
   }

   public void handle(ServerGamePacketListener p_134389_) {
      p_134389_.handleRecipeBookSeenRecipePacket(this);
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }
}