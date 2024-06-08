package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBookSettings;

public class ClientboundRecipePacket implements Packet<ClientGamePacketListener> {
   private final ClientboundRecipePacket.State state;
   private final List<ResourceLocation> recipes;
   private final List<ResourceLocation> toHighlight;
   private final RecipeBookSettings bookSettings;

   public ClientboundRecipePacket(ClientboundRecipePacket.State p_132855_, Collection<ResourceLocation> p_132856_, Collection<ResourceLocation> p_132857_, RecipeBookSettings p_132858_) {
      this.state = p_132855_;
      this.recipes = ImmutableList.copyOf(p_132856_);
      this.toHighlight = ImmutableList.copyOf(p_132857_);
      this.bookSettings = p_132858_;
   }

   public ClientboundRecipePacket(FriendlyByteBuf p_179162_) {
      this.state = p_179162_.readEnum(ClientboundRecipePacket.State.class);
      this.bookSettings = RecipeBookSettings.read(p_179162_);
      this.recipes = p_179162_.readList(FriendlyByteBuf::readResourceLocation);
      if (this.state == ClientboundRecipePacket.State.INIT) {
         this.toHighlight = p_179162_.readList(FriendlyByteBuf::readResourceLocation);
      } else {
         this.toHighlight = ImmutableList.of();
      }

   }

   public void write(FriendlyByteBuf p_132867_) {
      p_132867_.writeEnum(this.state);
      this.bookSettings.write(p_132867_);
      p_132867_.writeCollection(this.recipes, FriendlyByteBuf::writeResourceLocation);
      if (this.state == ClientboundRecipePacket.State.INIT) {
         p_132867_.writeCollection(this.toHighlight, FriendlyByteBuf::writeResourceLocation);
      }

   }

   public void handle(ClientGamePacketListener p_132864_) {
      p_132864_.handleAddOrRemoveRecipes(this);
   }

   public List<ResourceLocation> getRecipes() {
      return this.recipes;
   }

   public List<ResourceLocation> getHighlights() {
      return this.toHighlight;
   }

   public RecipeBookSettings getBookSettings() {
      return this.bookSettings;
   }

   public ClientboundRecipePacket.State getState() {
      return this.state;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;
   }
}