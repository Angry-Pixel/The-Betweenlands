package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class GameRules {
   public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<GameRules.Key<?>, GameRules.Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((p_46218_) -> {
      return p_46218_.id;
   }));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOFIRETICK = register("doFireTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOBGRIEFING = register("mobGriefing", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEPINVENTORY = register("keepInventory", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBSPAWNING = register("doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBLOOT = register("doMobLoot", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOBLOCKDROPS = register("doTileDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOENTITYDROPS = register("doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_COMMANDBLOCKOUTPUT = register("commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_NATURAL_REGENERATION = register("naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DAYLIGHT = register("doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LOGADMINCOMMANDS = register("logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SHOWDEATHMESSAGES = register("showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_RANDOMTICKING = register("randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntegerValue.create(3));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SENDCOMMANDFEEDBACK = register("sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_REDUCEDDEBUGINFO = register("reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (p_46212_, p_46213_) -> {
      byte b0 = (byte)(p_46213_.get() ? 22 : 23);

      for(ServerPlayer serverplayer : p_46212_.getPlayerList().getPlayers()) {
         serverplayer.connection.send(new ClientboundEntityEventPacket(serverplayer, b0));
      }

   }));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPECTATORSGENERATECHUNKS = register("spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SPAWN_RADIUS = register("spawnRadius", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.Category.MOBS, GameRules.IntegerValue.create(24));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WEATHER_CYCLE = register("doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.Category.MISC, GameRules.IntegerValue.create(65536));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_RAIDS = register("disableRaids", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOINSOMNIA = register("doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, (p_46200_, p_46201_) -> {
      for(ServerPlayer serverplayer : p_46200_.getPlayerList().getPlayers()) {
         serverplayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, p_46201_.get() ? 1.0F : 0.0F));
      }

   }));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DROWNING_DAMAGE = register("drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FALL_DAMAGE = register("fallDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FIRE_DAMAGE = register("fireDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FREEZE_DAMAGE = register("freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_UNIVERSAL_ANGER = register("universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE = register("playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100));
   private final Map<GameRules.Key<?>, GameRules.Value<?>> rules;

   public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String p_46190_, GameRules.Category p_46191_, GameRules.Type<T> p_46192_) {
      GameRules.Key<T> key = new GameRules.Key<>(p_46190_, p_46191_);
      GameRules.Type<?> type = GAME_RULE_TYPES.put(key, p_46192_);
      if (type != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + p_46190_);
      } else {
         return key;
      }
   }

   public GameRules(DynamicLike<?> p_46160_) {
      this();
      this.loadFromTag(p_46160_);
   }

   public GameRules() {
      this.rules = GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_46210_) -> {
         return p_46210_.getValue().createRule();
      }));
   }

   private GameRules(Map<GameRules.Key<?>, GameRules.Value<?>> p_46162_) {
      this.rules = p_46162_;
   }

   public <T extends GameRules.Value<T>> T getRule(GameRules.Key<T> p_46171_) {
      return (T)(this.rules.get(p_46171_));
   }

   public CompoundTag createTag() {
      CompoundTag compoundtag = new CompoundTag();
      this.rules.forEach((p_46197_, p_46198_) -> {
         compoundtag.putString(p_46197_.id, p_46198_.serialize());
      });
      return compoundtag;
   }

   private void loadFromTag(DynamicLike<?> p_46184_) {
      this.rules.forEach((p_46187_, p_46188_) -> {
         p_46184_.get(p_46187_.id).asString().result().ifPresent(p_46188_::deserialize);
      });
   }

   public GameRules copy() {
      return new GameRules(this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_46194_) -> {
         return p_46194_.getValue().copy();
      })));
   }

   public static void visitGameRuleTypes(GameRules.GameRuleTypeVisitor p_46165_) {
      GAME_RULE_TYPES.forEach((p_46205_, p_46206_) -> {
         callVisitorCap(p_46165_, p_46205_, p_46206_);
      });
   }

   private static <T extends GameRules.Value<T>> void callVisitorCap(GameRules.GameRuleTypeVisitor p_46167_, GameRules.Key<?> p_46168_, GameRules.Type<?> p_46169_) {
      p_46167_.visit((GameRules.Key<T>)p_46168_, (GameRules.Type<T>)p_46169_);
      ((GameRules.Type<T>)p_46169_).callVisitor(p_46167_, (GameRules.Key<T>)p_46168_);
   }

   public void assignFrom(GameRules p_46177_, @Nullable MinecraftServer p_46178_) {
      p_46177_.rules.keySet().forEach((p_46182_) -> {
         this.assignCap(p_46182_, p_46177_, p_46178_);
      });
   }

   private <T extends GameRules.Value<T>> void assignCap(GameRules.Key<T> p_46173_, GameRules p_46174_, @Nullable MinecraftServer p_46175_) {
      T t = p_46174_.getRule(p_46173_);
      this.<T>getRule(p_46173_).setFrom(t, p_46175_);
   }

   public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> p_46208_) {
      return this.getRule(p_46208_).get();
   }

   public int getInt(GameRules.Key<GameRules.IntegerValue> p_46216_) {
      return this.getRule(p_46216_).get();
   }

   public static class BooleanValue extends GameRules.Value<GameRules.BooleanValue> {
      private boolean value;

      static GameRules.Type<GameRules.BooleanValue> create(boolean p_46253_, BiConsumer<MinecraftServer, GameRules.BooleanValue> p_46254_) {
         return new GameRules.Type<>(BoolArgumentType::bool, (p_46242_) -> {
            return new GameRules.BooleanValue(p_46242_, p_46253_);
         }, p_46254_, GameRules.GameRuleTypeVisitor::visitBoolean);
      }

      static GameRules.Type<GameRules.BooleanValue> create(boolean p_46251_) {
         return create(p_46251_, (p_46236_, p_46237_) -> {
         });
      }

      public BooleanValue(GameRules.Type<GameRules.BooleanValue> p_46221_, boolean p_46222_) {
         super(p_46221_);
         this.value = p_46222_;
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> p_46231_, String p_46232_) {
         this.value = BoolArgumentType.getBool(p_46231_, p_46232_);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean p_46247_, @Nullable MinecraftServer p_46248_) {
         this.value = p_46247_;
         this.onChanged(p_46248_);
      }

      public String serialize() {
         return Boolean.toString(this.value);
      }

      protected void deserialize(String p_46234_) {
         this.value = Boolean.parseBoolean(p_46234_);
      }

      public int getCommandResult() {
         return this.value ? 1 : 0;
      }

      protected GameRules.BooleanValue getSelf() {
         return this;
      }

      protected GameRules.BooleanValue copy() {
         return new GameRules.BooleanValue(this.type, this.value);
      }

      public void setFrom(GameRules.BooleanValue p_46225_, @Nullable MinecraftServer p_46226_) {
         this.value = p_46225_.value;
         this.onChanged(p_46226_);
      }
   }

   public static enum Category {
      PLAYER("gamerule.category.player"),
      MOBS("gamerule.category.mobs"),
      SPAWNING("gamerule.category.spawning"),
      DROPS("gamerule.category.drops"),
      UPDATES("gamerule.category.updates"),
      CHAT("gamerule.category.chat"),
      MISC("gamerule.category.misc");

      private final String descriptionId;

      private Category(String p_46273_) {
         this.descriptionId = p_46273_;
      }

      public String getDescriptionId() {
         return this.descriptionId;
      }
   }

   public interface GameRuleTypeVisitor {
      default <T extends GameRules.Value<T>> void visit(GameRules.Key<T> p_46278_, GameRules.Type<T> p_46279_) {
      }

      default void visitBoolean(GameRules.Key<GameRules.BooleanValue> p_46280_, GameRules.Type<GameRules.BooleanValue> p_46281_) {
      }

      default void visitInteger(GameRules.Key<GameRules.IntegerValue> p_46282_, GameRules.Type<GameRules.IntegerValue> p_46283_) {
      }
   }

   public static class IntegerValue extends GameRules.Value<GameRules.IntegerValue> {
      private int value;

      private static GameRules.Type<GameRules.IntegerValue> create(int p_46295_, BiConsumer<MinecraftServer, GameRules.IntegerValue> p_46296_) {
         return new GameRules.Type<>(IntegerArgumentType::integer, (p_46293_) -> {
            return new GameRules.IntegerValue(p_46293_, p_46295_);
         }, p_46296_, GameRules.GameRuleTypeVisitor::visitInteger);
      }

      static GameRules.Type<GameRules.IntegerValue> create(int p_46313_) {
         return create(p_46313_, (p_46309_, p_46310_) -> {
         });
      }

      public IntegerValue(GameRules.Type<GameRules.IntegerValue> p_46286_, int p_46287_) {
         super(p_46286_);
         this.value = p_46287_;
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> p_46304_, String p_46305_) {
         this.value = IntegerArgumentType.getInteger(p_46304_, p_46305_);
      }

      public int get() {
         return this.value;
      }

      public void set(int p_151490_, @Nullable MinecraftServer p_151491_) {
         this.value = p_151490_;
         this.onChanged(p_151491_);
      }

      public String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String p_46307_) {
         this.value = safeParse(p_46307_);
      }

      public boolean tryDeserialize(String p_46315_) {
         try {
            this.value = Integer.parseInt(p_46315_);
            return true;
         } catch (NumberFormatException numberformatexception) {
            return false;
         }
      }

      private static int safeParse(String p_46318_) {
         if (!p_46318_.isEmpty()) {
            try {
               return Integer.parseInt(p_46318_);
            } catch (NumberFormatException numberformatexception) {
               GameRules.LOGGER.warn("Failed to parse integer {}", (Object)p_46318_);
            }
         }

         return 0;
      }

      public int getCommandResult() {
         return this.value;
      }

      protected GameRules.IntegerValue getSelf() {
         return this;
      }

      protected GameRules.IntegerValue copy() {
         return new GameRules.IntegerValue(this.type, this.value);
      }

      public void setFrom(GameRules.IntegerValue p_46298_, @Nullable MinecraftServer p_46299_) {
         this.value = p_46298_.value;
         this.onChanged(p_46299_);
      }
   }

   public static final class Key<T extends GameRules.Value<T>> {
      final String id;
      private final GameRules.Category category;

      public Key(String p_46326_, GameRules.Category p_46327_) {
         this.id = p_46326_;
         this.category = p_46327_;
      }

      public String toString() {
         return this.id;
      }

      public boolean equals(Object p_46334_) {
         if (this == p_46334_) {
            return true;
         } else {
            return p_46334_ instanceof GameRules.Key && ((GameRules.Key)p_46334_).id.equals(this.id);
         }
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public String getId() {
         return this.id;
      }

      public String getDescriptionId() {
         return "gamerule." + this.id;
      }

      public GameRules.Category getCategory() {
         return this.category;
      }
   }

   public static class Type<T extends GameRules.Value<T>> {
      private final Supplier<ArgumentType<?>> argument;
      private final Function<GameRules.Type<T>, T> constructor;
      final BiConsumer<MinecraftServer, T> callback;
      private final GameRules.VisitorCaller<T> visitorCaller;

      Type(Supplier<ArgumentType<?>> p_46342_, Function<GameRules.Type<T>, T> p_46343_, BiConsumer<MinecraftServer, T> p_46344_, GameRules.VisitorCaller<T> p_46345_) {
         this.argument = p_46342_;
         this.constructor = p_46343_;
         this.callback = p_46344_;
         this.visitorCaller = p_46345_;
      }

      public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String p_46359_) {
         return Commands.argument(p_46359_, this.argument.get());
      }

      public T createRule() {
         return this.constructor.apply(this);
      }

      public void callVisitor(GameRules.GameRuleTypeVisitor p_46354_, GameRules.Key<T> p_46355_) {
         this.visitorCaller.call(p_46354_, p_46355_, this);
      }
   }

   public abstract static class Value<T extends GameRules.Value<T>> {
      protected final GameRules.Type<T> type;

      public Value(GameRules.Type<T> p_46362_) {
         this.type = p_46362_;
      }

      protected abstract void updateFromArgument(CommandContext<CommandSourceStack> p_46365_, String p_46366_);

      public void setFromArgument(CommandContext<CommandSourceStack> p_46371_, String p_46372_) {
         this.updateFromArgument(p_46371_, p_46372_);
         this.onChanged(p_46371_.getSource().getServer());
      }

      protected void onChanged(@Nullable MinecraftServer p_46369_) {
         if (p_46369_ != null) {
            this.type.callback.accept(p_46369_, this.getSelf());
         }

      }

      protected abstract void deserialize(String p_46367_);

      public abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();

      protected abstract T copy();

      public abstract void setFrom(T p_46363_, @Nullable MinecraftServer p_46364_);
   }

   interface VisitorCaller<T extends GameRules.Value<T>> {
      void call(GameRules.GameRuleTypeVisitor p_46375_, GameRules.Key<T> p_46376_, GameRules.Type<T> p_46377_);
   }
}