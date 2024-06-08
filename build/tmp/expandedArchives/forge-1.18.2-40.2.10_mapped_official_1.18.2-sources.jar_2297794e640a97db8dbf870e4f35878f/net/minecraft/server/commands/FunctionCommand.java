package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerFunctionManager;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (p_137719_, p_137720_) -> {
      ServerFunctionManager serverfunctionmanager = p_137719_.getSource().getServer().getFunctions();
      SharedSuggestionProvider.suggestResource(serverfunctionmanager.getTagNames(), p_137720_, "#");
      return SharedSuggestionProvider.suggestResource(serverfunctionmanager.getFunctionNames(), p_137720_);
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_137715_) {
      p_137715_.register(Commands.literal("function").requires((p_137722_) -> {
         return p_137722_.hasPermission(2);
      }).then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes((p_137717_) -> {
         return runFunction(p_137717_.getSource(), FunctionArgument.getFunctions(p_137717_, "name"));
      })));
   }

   private static int runFunction(CommandSourceStack p_137724_, Collection<CommandFunction> p_137725_) {
      int i = 0;

      for(CommandFunction commandfunction : p_137725_) {
         i += p_137724_.getServer().getFunctions().execute(commandfunction, p_137724_.withSuppressedOutput().withMaximumPermission(2));
      }

      if (p_137725_.size() == 1) {
         p_137724_.sendSuccess(new TranslatableComponent("commands.function.success.single", i, p_137725_.iterator().next().getId()), true);
      } else {
         p_137724_.sendSuccess(new TranslatableComponent("commands.function.success.multiple", i, p_137725_.size()), true);
      }

      return i;
   }
}