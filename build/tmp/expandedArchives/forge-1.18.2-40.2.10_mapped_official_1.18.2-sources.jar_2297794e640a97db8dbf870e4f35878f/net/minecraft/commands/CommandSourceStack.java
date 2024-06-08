package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandSourceStack implements SharedSuggestionProvider, net.minecraftforge.common.extensions.IForgeCommandSourceStack {
   public static final SimpleCommandExceptionType ERROR_NOT_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.player"));
   public static final SimpleCommandExceptionType ERROR_NOT_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("permissions.requires.entity"));
   private final CommandSource source;
   private final Vec3 worldPosition;
   private final ServerLevel level;
   private final int permissionLevel;
   private final String textName;
   private final Component displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   @Nullable
   private final ResultConsumer<CommandSourceStack> consumer;
   private final EntityAnchorArgument.Anchor anchor;
   private final Vec2 rotation;

   public CommandSourceStack(CommandSource p_81302_, Vec3 p_81303_, Vec2 p_81304_, ServerLevel p_81305_, int p_81306_, String p_81307_, Component p_81308_, MinecraftServer p_81309_, @Nullable Entity p_81310_) {
      this(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_, false, (p_81361_, p_81362_, p_81363_) -> {
      }, EntityAnchorArgument.Anchor.FEET);
   }

   protected CommandSourceStack(CommandSource p_81312_, Vec3 p_81313_, Vec2 p_81314_, ServerLevel p_81315_, int p_81316_, String p_81317_, Component p_81318_, MinecraftServer p_81319_, @Nullable Entity p_81320_, boolean p_81321_, @Nullable ResultConsumer<CommandSourceStack> p_81322_, EntityAnchorArgument.Anchor p_81323_) {
      this.source = p_81312_;
      this.worldPosition = p_81313_;
      this.level = p_81315_;
      this.silent = p_81321_;
      this.entity = p_81320_;
      this.permissionLevel = p_81316_;
      this.textName = p_81317_;
      this.displayName = p_81318_;
      this.server = p_81319_;
      this.consumer = p_81322_;
      this.anchor = p_81323_;
      this.rotation = p_81314_;
   }

   public CommandSourceStack withSource(CommandSource p_165485_) {
      return this.source == p_165485_ ? this : new CommandSourceStack(p_165485_, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withEntity(Entity p_81330_) {
      return this.entity == p_81330_ ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, p_81330_.getName().getString(), p_81330_.getDisplayName(), this.server, p_81330_, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withPosition(Vec3 p_81349_) {
      return this.worldPosition.equals(p_81349_) ? this : new CommandSourceStack(this.source, p_81349_, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withRotation(Vec2 p_81347_) {
      return this.rotation.equals(p_81347_) ? this : new CommandSourceStack(this.source, this.worldPosition, p_81347_, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> p_81335_) {
      return Objects.equals(this.consumer, p_81335_) ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, p_81335_, this.anchor);
   }

   public CommandSourceStack withCallback(ResultConsumer<CommandSourceStack> p_81337_, BinaryOperator<ResultConsumer<CommandSourceStack>> p_81338_) {
      ResultConsumer<CommandSourceStack> resultconsumer = p_81338_.apply(this.consumer, p_81337_);
      return this.withCallback(resultconsumer);
   }

   public CommandSourceStack withSuppressedOutput() {
      return !this.silent && !this.source.alwaysAccepts() ? new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, true, this.consumer, this.anchor) : this;
   }

   public CommandSourceStack withPermission(int p_81326_) {
      return p_81326_ == this.permissionLevel ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, p_81326_, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withMaximumPermission(int p_81359_) {
      return p_81359_ <= this.permissionLevel ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, p_81359_, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
   }

   public CommandSourceStack withAnchor(EntityAnchorArgument.Anchor p_81351_) {
      return p_81351_ == this.anchor ? this : new CommandSourceStack(this.source, this.worldPosition, this.rotation, this.level, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, p_81351_);
   }

   public CommandSourceStack withLevel(ServerLevel p_81328_) {
      if (p_81328_ == this.level) {
         return this;
      } else {
         double d0 = DimensionType.getTeleportationScale(this.level.dimensionType(), p_81328_.dimensionType());
         Vec3 vec3 = new Vec3(this.worldPosition.x * d0, this.worldPosition.y, this.worldPosition.z * d0);
         return new CommandSourceStack(this.source, vec3, this.rotation, p_81328_, this.permissionLevel, this.textName, this.displayName, this.server, this.entity, this.silent, this.consumer, this.anchor);
      }
   }

   public CommandSourceStack facing(Entity p_81332_, EntityAnchorArgument.Anchor p_81333_) {
      return this.facing(p_81333_.apply(p_81332_));
   }

   public CommandSourceStack facing(Vec3 p_81365_) {
      Vec3 vec3 = this.anchor.apply(this);
      double d0 = p_81365_.x - vec3.x;
      double d1 = p_81365_.y - vec3.y;
      double d2 = p_81365_.z - vec3.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      float f = Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      float f1 = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      return this.withRotation(new Vec2(f, f1));
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public String getTextName() {
      return this.textName;
   }

   public boolean hasPermission(int p_81370_) {
      return this.permissionLevel >= p_81370_;
   }

   public Vec3 getPosition() {
      return this.worldPosition;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrException() throws CommandSyntaxException {
      if (this.entity == null) {
         throw ERROR_NOT_ENTITY.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayer getPlayerOrException() throws CommandSyntaxException {
      if (!(this.entity instanceof ServerPlayer)) {
         throw ERROR_NOT_PLAYER.create();
      } else {
         return (ServerPlayer)this.entity;
      }
   }

   public Vec2 getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Anchor getAnchor() {
      return this.anchor;
   }

   public void sendSuccess(Component p_81355_, boolean p_81356_) {
      if (this.source.acceptsSuccess() && !this.silent) {
         this.source.sendMessage(p_81355_, Util.NIL_UUID);
      }

      if (p_81356_ && this.source.shouldInformAdmins() && !this.silent) {
         this.broadcastToAdmins(p_81355_);
      }

   }

   private void broadcastToAdmins(Component p_81367_) {
      Component component = (new TranslatableComponent("chat.type.admin", this.getDisplayName(), p_81367_)).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
      if (this.server.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
         for(ServerPlayer serverplayer : this.server.getPlayerList().getPlayers()) {
            if (serverplayer != this.source && this.server.getPlayerList().isOp(serverplayer.getGameProfile())) {
               serverplayer.sendMessage(component, Util.NIL_UUID);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.RULE_LOGADMINCOMMANDS)) {
         this.server.sendMessage(component, Util.NIL_UUID);
      }

   }

   public void sendFailure(Component p_81353_) {
      if (this.source.acceptsFailure() && !this.silent) {
         this.source.sendMessage((new TextComponent("")).append(p_81353_).withStyle(ChatFormatting.RED), Util.NIL_UUID);
      }

   }

   public void onCommandComplete(CommandContext<CommandSourceStack> p_81343_, boolean p_81344_, int p_81345_) {
      if (this.consumer != null) {
         this.consumer.onCommandComplete(p_81343_, p_81344_, p_81345_);
      }

   }

   public Collection<String> getOnlinePlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   public Collection<String> getAllTeams() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getAvailableSoundEvents() {
      return Registry.SOUND_EVENT.keySet();
   }

   public Stream<ResourceLocation> getRecipeNames() {
      return this.server.getRecipeManager().getRecipeIds();
   }

   public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> p_212324_) {
      return Suggestions.empty();
   }

   public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> p_212330_, SharedSuggestionProvider.ElementSuggestionType p_212331_, SuggestionsBuilder p_212332_, CommandContext<?> p_212333_) {
      return this.registryAccess().registry(p_212330_).map((p_212328_) -> {
         this.suggestRegistryElements(p_212328_, p_212331_, p_212332_);
         return p_212332_.buildFuture();
      }).orElseGet(Suggestions::empty);
   }

   public Set<ResourceKey<Level>> levels() {
      return this.server.levelKeys();
   }

   public RegistryAccess registryAccess() {
      return this.server.registryAccess();
   }
}
