package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSuggestionProvider implements SharedSuggestionProvider {
   private final ClientPacketListener connection;
   private final Minecraft minecraft;
   private int pendingSuggestionsId = -1;
   @Nullable
   private CompletableFuture<Suggestions> pendingSuggestionsFuture;

   public ClientSuggestionProvider(ClientPacketListener p_105165_, Minecraft p_105166_) {
      this.connection = p_105165_;
      this.minecraft = p_105166_;
   }

   public Collection<String> getOnlinePlayerNames() {
      List<String> list = Lists.newArrayList();

      for(PlayerInfo playerinfo : this.connection.getOnlinePlayers()) {
         list.add(playerinfo.getProfile().getName());
      }

      return list;
   }

   public Collection<String> getSelectedEntities() {
      return (Collection<String>)(this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY ? Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID()) : Collections.emptyList());
   }

   public Collection<String> getAllTeams() {
      return this.connection.getLevel().getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getAvailableSoundEvents() {
      return this.minecraft.getSoundManager().getAvailableSounds();
   }

   public Stream<ResourceLocation> getRecipeNames() {
      return this.connection.getRecipeManager().getRecipeIds();
   }

   public boolean hasPermission(int p_105178_) {
      LocalPlayer localplayer = this.minecraft.player;
      return localplayer != null ? localplayer.hasPermissions(p_105178_) : p_105178_ == 0;
   }

   public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> p_212429_, SharedSuggestionProvider.ElementSuggestionType p_212430_, SuggestionsBuilder p_212431_, CommandContext<?> p_212432_) {
      return this.registryAccess().registry(p_212429_).map((p_212427_) -> {
         this.suggestRegistryElements(p_212427_, p_212430_, p_212431_);
         return p_212431_.buildFuture();
      }).orElseGet(() -> {
         return this.customSuggestion(p_212432_);
      });
   }

   public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> p_212423_) {
      if (this.pendingSuggestionsFuture != null) {
         this.pendingSuggestionsFuture.cancel(false);
      }

      this.pendingSuggestionsFuture = new CompletableFuture<>();
      int i = ++this.pendingSuggestionsId;
      this.connection.send(new ServerboundCommandSuggestionPacket(i, p_212423_.getInput()));
      return this.pendingSuggestionsFuture;
   }

   private static String prettyPrint(double p_105168_) {
      return String.format(Locale.ROOT, "%.2f", p_105168_);
   }

   private static String prettyPrint(int p_105170_) {
      return Integer.toString(p_105170_);
   }

   public Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
      HitResult hitresult = this.minecraft.hitResult;
      if (hitresult != null && hitresult.getType() == HitResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(blockpos.getX()), prettyPrint(blockpos.getY()), prettyPrint(blockpos.getZ())));
      } else {
         return SharedSuggestionProvider.super.getRelevantCoordinates();
      }
   }

   public Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
      HitResult hitresult = this.minecraft.hitResult;
      if (hitresult != null && hitresult.getType() == HitResult.Type.BLOCK) {
         Vec3 vec3 = hitresult.getLocation();
         return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint(vec3.x), prettyPrint(vec3.y), prettyPrint(vec3.z)));
      } else {
         return SharedSuggestionProvider.super.getAbsoluteCoordinates();
      }
   }

   public Set<ResourceKey<Level>> levels() {
      return this.connection.levels();
   }

   public RegistryAccess registryAccess() {
      return this.connection.registryAccess();
   }

   public void completeCustomSuggestions(int p_105172_, Suggestions p_105173_) {
      if (p_105172_ == this.pendingSuggestionsId) {
         this.pendingSuggestionsFuture.complete(p_105173_);
         this.pendingSuggestionsFuture = null;
         this.pendingSuggestionsId = -1;
      }

   }
}