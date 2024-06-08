package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeCommand {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.recipe.give.failed"));
   private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.recipe.take.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138201_) {
      p_138201_.register(Commands.literal("recipe").requires((p_138205_) -> {
         return p_138205_.hasPermission(2);
      }).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_138219_) -> {
         return giveRecipes(p_138219_.getSource(), EntityArgument.getPlayers(p_138219_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_138219_, "recipe")));
      })).then(Commands.literal("*").executes((p_138217_) -> {
         return giveRecipes(p_138217_.getSource(), EntityArgument.getPlayers(p_138217_, "targets"), p_138217_.getSource().getServer().getRecipeManager().getRecipes());
      })))).then(Commands.literal("take").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_138211_) -> {
         return takeRecipes(p_138211_.getSource(), EntityArgument.getPlayers(p_138211_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_138211_, "recipe")));
      })).then(Commands.literal("*").executes((p_138203_) -> {
         return takeRecipes(p_138203_.getSource(), EntityArgument.getPlayers(p_138203_, "targets"), p_138203_.getSource().getServer().getRecipeManager().getRecipes());
      })))));
   }

   private static int giveRecipes(CommandSourceStack p_138207_, Collection<ServerPlayer> p_138208_, Collection<Recipe<?>> p_138209_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayer serverplayer : p_138208_) {
         i += serverplayer.awardRecipes(p_138209_);
      }

      if (i == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (p_138208_.size() == 1) {
            p_138207_.sendSuccess(new TranslatableComponent("commands.recipe.give.success.single", p_138209_.size(), p_138208_.iterator().next().getDisplayName()), true);
         } else {
            p_138207_.sendSuccess(new TranslatableComponent("commands.recipe.give.success.multiple", p_138209_.size(), p_138208_.size()), true);
         }

         return i;
      }
   }

   private static int takeRecipes(CommandSourceStack p_138213_, Collection<ServerPlayer> p_138214_, Collection<Recipe<?>> p_138215_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayer serverplayer : p_138214_) {
         i += serverplayer.resetRecipes(p_138215_);
      }

      if (i == 0) {
         throw ERROR_TAKE_FAILED.create();
      } else {
         if (p_138214_.size() == 1) {
            p_138213_.sendSuccess(new TranslatableComponent("commands.recipe.take.success.single", p_138215_.size(), p_138214_.iterator().next().getDisplayName()), true);
         } else {
            p_138213_.sendSuccess(new TranslatableComponent("commands.recipe.take.success.multiple", p_138215_.size(), p_138214_.size()), true);
         }

         return i;
      }
   }
}