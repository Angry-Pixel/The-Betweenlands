package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.Result> {
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_SCORE_HOLDERS = (p_108221_, p_108222_) -> {
      StringReader stringreader = new StringReader(p_108222_.getInput());
      stringreader.setCursor(p_108222_.getStart());
      EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader);

      try {
         entityselectorparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return entityselectorparser.fillSuggestions(p_108222_, (p_171606_) -> {
         SharedSuggestionProvider.suggest(p_108221_.getSource().getOnlinePlayerNames(), p_171606_);
      });
   };
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(new TranslatableComponent("argument.scoreHolder.empty"));
   private static final byte FLAG_MULTIPLE = 1;
   final boolean multiple;

   public ScoreHolderArgument(boolean p_108216_) {
      this.multiple = p_108216_;
   }

   public static String getName(CommandContext<CommandSourceStack> p_108224_, String p_108225_) throws CommandSyntaxException {
      return getNames(p_108224_, p_108225_).iterator().next();
   }

   public static Collection<String> getNames(CommandContext<CommandSourceStack> p_108244_, String p_108245_) throws CommandSyntaxException {
      return getNames(p_108244_, p_108245_, Collections::emptyList);
   }

   public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandSourceStack> p_108247_, String p_108248_) throws CommandSyntaxException {
      return getNames(p_108247_, p_108248_, p_108247_.getSource().getServer().getScoreboard()::getTrackedPlayers);
   }

   public static Collection<String> getNames(CommandContext<CommandSourceStack> p_108227_, String p_108228_, Supplier<Collection<String>> p_108229_) throws CommandSyntaxException {
      Collection<String> collection = p_108227_.getArgument(p_108228_, ScoreHolderArgument.Result.class).getNames(p_108227_.getSource(), p_108229_);
      if (collection.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else {
         return collection;
      }
   }

   public static ScoreHolderArgument scoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument scoreHolders() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.Result parse(StringReader p_108219_) throws CommandSyntaxException {
      if (p_108219_.canRead() && p_108219_.peek() == '@') {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_108219_);
         EntitySelector entityselector = entityselectorparser.parse();
         if (!this.multiple && entityselector.getMaxResults() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
         } else {
            return new ScoreHolderArgument.SelectorResult(entityselector);
         }
      } else {
         int i = p_108219_.getCursor();

         while(p_108219_.canRead() && p_108219_.peek() != ' ') {
            p_108219_.skip();
         }

         String s = p_108219_.getString().substring(i, p_108219_.getCursor());
         if (s.equals("*")) {
            return (p_108231_, p_108232_) -> {
               Collection<String> collection1 = p_108232_.get();
               if (collection1.isEmpty()) {
                  throw ERROR_NO_RESULTS.create();
               } else {
                  return collection1;
               }
            };
         } else {
            Collection<String> collection = Collections.singleton(s);
            return (p_108237_, p_108238_) -> {
               return collection;
            };
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface Result {
      Collection<String> getNames(CommandSourceStack p_108252_, Supplier<Collection<String>> p_108253_) throws CommandSyntaxException;
   }

   public static class SelectorResult implements ScoreHolderArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector p_108256_) {
         this.selector = p_108256_;
      }

      public Collection<String> getNames(CommandSourceStack p_108258_, Supplier<Collection<String>> p_108259_) throws CommandSyntaxException {
         List<? extends Entity> list = this.selector.findEntities(p_108258_);
         if (list.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
         } else {
            List<String> list1 = Lists.newArrayList();

            for(Entity entity : list) {
               list1.add(entity.getScoreboardName());
            }

            return list1;
         }
      }
   }

   public static class Serializer implements ArgumentSerializer<ScoreHolderArgument> {
      public void serializeToNetwork(ScoreHolderArgument p_108271_, FriendlyByteBuf p_108272_) {
         byte b0 = 0;
         if (p_108271_.multiple) {
            b0 = (byte)(b0 | 1);
         }

         p_108272_.writeByte(b0);
      }

      public ScoreHolderArgument deserializeFromNetwork(FriendlyByteBuf p_108274_) {
         byte b0 = p_108274_.readByte();
         boolean flag = (b0 & 1) != 0;
         return new ScoreHolderArgument(flag);
      }

      public void serializeToJson(ScoreHolderArgument p_108268_, JsonObject p_108269_) {
         p_108269_.addProperty("amount", p_108268_.multiple ? "multiple" : "single");
      }
   }
}