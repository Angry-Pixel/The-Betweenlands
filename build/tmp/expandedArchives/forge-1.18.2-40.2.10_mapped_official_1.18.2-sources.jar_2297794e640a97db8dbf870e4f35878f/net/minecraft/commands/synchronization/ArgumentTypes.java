package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ArgumentTypes {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<Class<?>, ArgumentTypes.Entry<?>> BY_CLASS = Maps.newHashMap();
   private static final Map<ResourceLocation, ArgumentTypes.Entry<?>> BY_NAME = Maps.newHashMap();

   public static <T extends ArgumentType<?>> void register(String p_121602_, Class<T> p_121603_, ArgumentSerializer<T> p_121604_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_121602_);
      if (BY_CLASS.containsKey(p_121603_)) {
         throw new IllegalArgumentException("Class " + p_121603_.getName() + " already has a serializer!");
      } else if (BY_NAME.containsKey(resourcelocation)) {
         throw new IllegalArgumentException("'" + resourcelocation + "' is already a registered serializer!");
      } else {
         ArgumentTypes.Entry<T> entry = new ArgumentTypes.Entry<>(p_121604_, resourcelocation);
         BY_CLASS.put(p_121603_, entry);
         BY_NAME.put(resourcelocation, entry);
      }
   }

   public static void bootStrap() {
      BrigadierArgumentSerializers.bootstrap();
      register("entity", EntityArgument.class, new EntityArgument.Serializer());
      register("game_profile", GameProfileArgument.class, new EmptyArgumentSerializer<>(GameProfileArgument::gameProfile));
      register("block_pos", BlockPosArgument.class, new EmptyArgumentSerializer<>(BlockPosArgument::blockPos));
      register("column_pos", ColumnPosArgument.class, new EmptyArgumentSerializer<>(ColumnPosArgument::columnPos));
      register("vec3", Vec3Argument.class, new EmptyArgumentSerializer<>(Vec3Argument::vec3));
      register("vec2", Vec2Argument.class, new EmptyArgumentSerializer<>(Vec2Argument::vec2));
      register("block_state", BlockStateArgument.class, new EmptyArgumentSerializer<>(BlockStateArgument::block));
      register("block_predicate", BlockPredicateArgument.class, new EmptyArgumentSerializer<>(BlockPredicateArgument::blockPredicate));
      register("item_stack", ItemArgument.class, new EmptyArgumentSerializer<>(ItemArgument::item));
      register("item_predicate", ItemPredicateArgument.class, new EmptyArgumentSerializer<>(ItemPredicateArgument::itemPredicate));
      register("color", ColorArgument.class, new EmptyArgumentSerializer<>(ColorArgument::color));
      register("component", ComponentArgument.class, new EmptyArgumentSerializer<>(ComponentArgument::textComponent));
      register("message", MessageArgument.class, new EmptyArgumentSerializer<>(MessageArgument::message));
      register("nbt_compound_tag", CompoundTagArgument.class, new EmptyArgumentSerializer<>(CompoundTagArgument::compoundTag));
      register("nbt_tag", NbtTagArgument.class, new EmptyArgumentSerializer<>(NbtTagArgument::nbtTag));
      register("nbt_path", NbtPathArgument.class, new EmptyArgumentSerializer<>(NbtPathArgument::nbtPath));
      register("objective", ObjectiveArgument.class, new EmptyArgumentSerializer<>(ObjectiveArgument::objective));
      register("objective_criteria", ObjectiveCriteriaArgument.class, new EmptyArgumentSerializer<>(ObjectiveCriteriaArgument::criteria));
      register("operation", OperationArgument.class, new EmptyArgumentSerializer<>(OperationArgument::operation));
      register("particle", ParticleArgument.class, new EmptyArgumentSerializer<>(ParticleArgument::particle));
      register("angle", AngleArgument.class, new EmptyArgumentSerializer<>(AngleArgument::angle));
      register("rotation", RotationArgument.class, new EmptyArgumentSerializer<>(RotationArgument::rotation));
      register("scoreboard_slot", ScoreboardSlotArgument.class, new EmptyArgumentSerializer<>(ScoreboardSlotArgument::displaySlot));
      register("score_holder", ScoreHolderArgument.class, new ScoreHolderArgument.Serializer());
      register("swizzle", SwizzleArgument.class, new EmptyArgumentSerializer<>(SwizzleArgument::swizzle));
      register("team", TeamArgument.class, new EmptyArgumentSerializer<>(TeamArgument::team));
      register("item_slot", SlotArgument.class, new EmptyArgumentSerializer<>(SlotArgument::slot));
      register("resource_location", ResourceLocationArgument.class, new EmptyArgumentSerializer<>(ResourceLocationArgument::id));
      register("mob_effect", MobEffectArgument.class, new EmptyArgumentSerializer<>(MobEffectArgument::effect));
      register("function", FunctionArgument.class, new EmptyArgumentSerializer<>(FunctionArgument::functions));
      register("entity_anchor", EntityAnchorArgument.class, new EmptyArgumentSerializer<>(EntityAnchorArgument::anchor));
      register("int_range", RangeArgument.Ints.class, new EmptyArgumentSerializer<>(RangeArgument::intRange));
      register("float_range", RangeArgument.Floats.class, new EmptyArgumentSerializer<>(RangeArgument::floatRange));
      register("item_enchantment", ItemEnchantmentArgument.class, new EmptyArgumentSerializer<>(ItemEnchantmentArgument::enchantment));
      register("entity_summon", EntitySummonArgument.class, new EmptyArgumentSerializer<>(EntitySummonArgument::id));
      register("dimension", DimensionArgument.class, new EmptyArgumentSerializer<>(DimensionArgument::dimension));
      register("time", TimeArgument.class, new EmptyArgumentSerializer<>(TimeArgument::time));
      register("uuid", UuidArgument.class, new EmptyArgumentSerializer<>(UuidArgument::uuid));
      register("resource", fixClassType(ResourceKeyArgument.class), new ResourceKeyArgument.Serializer());
      register("resource_or_tag", fixClassType(ResourceOrTagLocationArgument.class), new ResourceOrTagLocationArgument.Serializer());
      if (net.minecraftforge.gametest.ForgeGameTestHooks.isGametestEnabled()) {
         register("test_argument", TestFunctionArgument.class, new EmptyArgumentSerializer<>(TestFunctionArgument::testFunctionArgument));
         register("test_class", TestClassNameArgument.class, new EmptyArgumentSerializer<>(TestClassNameArgument::testClassName));
      }

   }

   private static <T extends ArgumentType<?>> Class<T> fixClassType(Class<? super T> p_211032_) {
      return (Class<T>) p_211032_;
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ResourceLocation p_121615_) {
      return BY_NAME.get(p_121615_);
   }

   @Nullable
   private static ArgumentTypes.Entry<?> get(ArgumentType<?> p_121617_) {
      return BY_CLASS.get(p_121617_.getClass());
   }

   public static <T extends ArgumentType<?>> void serialize(FriendlyByteBuf p_121612_, T p_121613_) {
      ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)get(p_121613_);
      if (entry == null) {
         LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", p_121613_, p_121613_.getClass());
         p_121612_.writeResourceLocation(new ResourceLocation(""));
      } else {
         p_121612_.writeResourceLocation(entry.name);
         entry.serializer.serializeToNetwork(p_121613_, p_121612_);
      }
   }

   @Nullable
   public static ArgumentType<?> deserialize(FriendlyByteBuf p_121610_) {
      ResourceLocation resourcelocation = p_121610_.readResourceLocation();
      ArgumentTypes.Entry<?> entry = get(resourcelocation);
      if (entry == null) {
         LOGGER.error("Could not deserialize {}", (Object)resourcelocation);
         return null;
      } else {
         return entry.serializer.deserializeFromNetwork(p_121610_);
      }
   }

   private static <T extends ArgumentType<?>> void serializeToJson(JsonObject p_121588_, T p_121589_) {
      ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)get(p_121589_);
      if (entry == null) {
         LOGGER.error("Could not serialize argument {} ({})!", p_121589_, p_121589_.getClass());
         p_121588_.addProperty("type", "unknown");
      } else {
         p_121588_.addProperty("type", "argument");
         p_121588_.addProperty("parser", entry.name.toString());
         JsonObject jsonobject = new JsonObject();
         entry.serializer.serializeToJson(p_121589_, jsonobject);
         if (jsonobject.size() > 0) {
            p_121588_.add("properties", jsonobject);
         }
      }

   }

   public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> p_121591_, CommandNode<S> p_121592_) {
      JsonObject jsonobject = new JsonObject();
      if (p_121592_ instanceof RootCommandNode) {
         jsonobject.addProperty("type", "root");
      } else if (p_121592_ instanceof LiteralCommandNode) {
         jsonobject.addProperty("type", "literal");
      } else if (p_121592_ instanceof ArgumentCommandNode) {
         serializeToJson(jsonobject, ((ArgumentCommandNode)p_121592_).getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", p_121592_, p_121592_.getClass());
         jsonobject.addProperty("type", "unknown");
      }

      JsonObject jsonobject1 = new JsonObject();

      for(CommandNode<S> commandnode : p_121592_.getChildren()) {
         jsonobject1.add(commandnode.getName(), serializeNodeToJson(p_121591_, commandnode));
      }

      if (jsonobject1.size() > 0) {
         jsonobject.add("children", jsonobject1);
      }

      if (p_121592_.getCommand() != null) {
         jsonobject.addProperty("executable", true);
      }

      if (p_121592_.getRedirect() != null) {
         Collection<String> collection = p_121591_.getPath(p_121592_.getRedirect());
         if (!collection.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(String s : collection) {
               jsonarray.add(s);
            }

            jsonobject.add("redirect", jsonarray);
         }
      }

      return jsonobject;
   }

   public static boolean isTypeRegistered(ArgumentType<?> p_121594_) {
      return get(p_121594_) != null;
   }

   public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> p_121596_) {
      Set<CommandNode<T>> set = Sets.newIdentityHashSet();
      Set<ArgumentType<?>> set1 = Sets.newHashSet();
      findUsedArgumentTypes(p_121596_, set1, set);
      return set1;
   }

   private static <T> void findUsedArgumentTypes(CommandNode<T> p_121598_, Set<ArgumentType<?>> p_121599_, Set<CommandNode<T>> p_121600_) {
      if (p_121600_.add(p_121598_)) {
         if (p_121598_ instanceof ArgumentCommandNode) {
            p_121599_.add(((ArgumentCommandNode)p_121598_).getType());
         }

         p_121598_.getChildren().forEach((p_121608_) -> {
            findUsedArgumentTypes(p_121608_, p_121599_, p_121600_);
         });
         CommandNode<T> commandnode = p_121598_.getRedirect();
         if (commandnode != null) {
            findUsedArgumentTypes(commandnode, p_121599_, p_121600_);
         }

      }
   }

   static class Entry<T extends ArgumentType<?>> {
      public final ArgumentSerializer<T> serializer;
      public final ResourceLocation name;

      Entry(ArgumentSerializer<T> p_211034_, ResourceLocation p_211035_) {
         this.serializer = p_211034_;
         this.name = p_211035_;
      }
   }
   @javax.annotation.Nullable public static ResourceLocation getId(ArgumentType<?> type) {
      Entry<?> entry = get(type);
      return entry == null ? null : entry.name;
   }
}
