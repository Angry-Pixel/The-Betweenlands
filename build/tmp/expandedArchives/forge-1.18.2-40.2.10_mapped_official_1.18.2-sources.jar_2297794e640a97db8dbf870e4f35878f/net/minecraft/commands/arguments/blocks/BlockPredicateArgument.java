package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockPredicateArgument implements ArgumentType<BlockPredicateArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
   static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_115580_) -> {
      return new TranslatableComponent("arguments.block.tag.unknown", p_115580_);
   });

   public static BlockPredicateArgument blockPredicate() {
      return new BlockPredicateArgument();
   }

   public BlockPredicateArgument.Result parse(StringReader p_115572_) throws CommandSyntaxException {
      final BlockStateParser blockstateparser = (new BlockStateParser(p_115572_, true)).parse(true);
      if (blockstateparser.getState() != null) {
         final BlockPredicateArgument.BlockPredicate blockpredicateargument$blockpredicate = new BlockPredicateArgument.BlockPredicate(blockstateparser.getState(), blockstateparser.getProperties().keySet(), blockstateparser.getNbt());
         return new BlockPredicateArgument.Result() {
            public Predicate<BlockInWorld> create(Registry<Block> p_205581_) {
               return blockpredicateargument$blockpredicate;
            }

            public boolean requiresNbt() {
               return blockpredicateargument$blockpredicate.requiresNbt();
            }
         };
      } else {
         final TagKey<Block> tagkey = blockstateparser.getTag();
         return new BlockPredicateArgument.Result() {
            public Predicate<BlockInWorld> create(Registry<Block> p_205588_) throws CommandSyntaxException {
               if (!p_205588_.isKnownTagName(tagkey)) {
                  throw BlockPredicateArgument.ERROR_UNKNOWN_TAG.create(tagkey);
               } else {
                  return new BlockPredicateArgument.TagPredicate(tagkey, blockstateparser.getVagueProperties(), blockstateparser.getNbt());
               }
            }

            public boolean requiresNbt() {
               return blockstateparser.getNbt() != null;
            }
         };
      }
   }

   public static Predicate<BlockInWorld> getBlockPredicate(CommandContext<CommandSourceStack> p_115574_, String p_115575_) throws CommandSyntaxException {
      return p_115574_.getArgument(p_115575_, BlockPredicateArgument.Result.class).create(p_115574_.getSource().getServer().registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_115587_, SuggestionsBuilder p_115588_) {
      StringReader stringreader = new StringReader(p_115588_.getInput());
      stringreader.setCursor(p_115588_.getStart());
      BlockStateParser blockstateparser = new BlockStateParser(stringreader, true);

      try {
         blockstateparser.parse(true);
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return blockstateparser.fillSuggestions(p_115588_, Registry.BLOCK);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static class BlockPredicate implements Predicate<BlockInWorld> {
      private final BlockState state;
      private final Set<Property<?>> properties;
      @Nullable
      private final CompoundTag nbt;

      public BlockPredicate(BlockState p_115595_, Set<Property<?>> p_115596_, @Nullable CompoundTag p_115597_) {
         this.state = p_115595_;
         this.properties = p_115596_;
         this.nbt = p_115597_;
      }

      public boolean test(BlockInWorld p_115599_) {
         BlockState blockstate = p_115599_.getState();
         if (!blockstate.is(this.state.getBlock())) {
            return false;
         } else {
            for(Property<?> property : this.properties) {
               if (blockstate.getValue(property) != this.state.getValue(property)) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               BlockEntity blockentity = p_115599_.getEntity();
               return blockentity != null && NbtUtils.compareNbt(this.nbt, blockentity.saveWithFullMetadata(), true);
            }
         }
      }

      public boolean requiresNbt() {
         return this.nbt != null;
      }
   }

   public interface Result {
      Predicate<BlockInWorld> create(Registry<Block> p_205589_) throws CommandSyntaxException;

      boolean requiresNbt();
   }

   static class TagPredicate implements Predicate<BlockInWorld> {
      private final TagKey<Block> tag;
      @Nullable
      private final CompoundTag nbt;
      private final Map<String, String> vagueProperties;

      TagPredicate(TagKey<Block> p_205591_, Map<String, String> p_205592_, @Nullable CompoundTag p_205593_) {
         this.tag = p_205591_;
         this.vagueProperties = p_205592_;
         this.nbt = p_205593_;
      }

      public boolean test(BlockInWorld p_115617_) {
         BlockState blockstate = p_115617_.getState();
         if (!blockstate.is(this.tag)) {
            return false;
         } else {
            for(Entry<String, String> entry : this.vagueProperties.entrySet()) {
               Property<?> property = blockstate.getBlock().getStateDefinition().getProperty(entry.getKey());
               if (property == null) {
                  return false;
               }

               Comparable<?> comparable = (Comparable)property.getValue(entry.getValue()).orElse(null);
               if (comparable == null) {
                  return false;
               }

               if (blockstate.getValue(property) != comparable) {
                  return false;
               }
            }

            if (this.nbt == null) {
               return true;
            } else {
               BlockEntity blockentity = p_115617_.getEntity();
               return blockentity != null && NbtUtils.compareNbt(this.nbt, blockentity.saveWithFullMetadata(), true);
            }
         }
      }
   }
}