package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearInventoryCommands {
   private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((p_136717_) -> {
      return new TranslatableComponent("clear.failed.single", p_136717_);
   });
   private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((p_136711_) -> {
      return new TranslatableComponent("clear.failed.multiple", p_136711_);
   });

   public static void register(CommandDispatcher<CommandSourceStack> p_136700_) {
      p_136700_.register(Commands.literal("clear").requires((p_136704_) -> {
         return p_136704_.hasPermission(2);
      }).executes((p_136721_) -> {
         return clearInventory(p_136721_.getSource(), Collections.singleton(p_136721_.getSource().getPlayerOrException()), (p_180029_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_136719_) -> {
         return clearInventory(p_136719_.getSource(), EntityArgument.getPlayers(p_136719_, "targets"), (p_180027_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((p_136715_) -> {
         return clearInventory(p_136715_.getSource(), EntityArgument.getPlayers(p_136715_, "targets"), ItemPredicateArgument.getItemPredicate(p_136715_, "item"), -1);
      }).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_136702_) -> {
         return clearInventory(p_136702_.getSource(), EntityArgument.getPlayers(p_136702_, "targets"), ItemPredicateArgument.getItemPredicate(p_136702_, "item"), IntegerArgumentType.getInteger(p_136702_, "maxCount"));
      })))));
   }

   private static int clearInventory(CommandSourceStack p_136706_, Collection<ServerPlayer> p_136707_, Predicate<ItemStack> p_136708_, int p_136709_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayer serverplayer : p_136707_) {
         i += serverplayer.getInventory().clearOrCountMatchingItems(p_136708_, p_136709_, serverplayer.inventoryMenu.getCraftSlots());
         serverplayer.containerMenu.broadcastChanges();
         serverplayer.inventoryMenu.slotsChanged(serverplayer.getInventory());
      }

      if (i == 0) {
         if (p_136707_.size() == 1) {
            throw ERROR_SINGLE.create(p_136707_.iterator().next().getName());
         } else {
            throw ERROR_MULTIPLE.create(p_136707_.size());
         }
      } else {
         if (p_136709_ == 0) {
            if (p_136707_.size() == 1) {
               p_136706_.sendSuccess(new TranslatableComponent("commands.clear.test.single", i, p_136707_.iterator().next().getDisplayName()), true);
            } else {
               p_136706_.sendSuccess(new TranslatableComponent("commands.clear.test.multiple", i, p_136707_.size()), true);
            }
         } else if (p_136707_.size() == 1) {
            p_136706_.sendSuccess(new TranslatableComponent("commands.clear.success.single", i, p_136707_.iterator().next().getDisplayName()), true);
         } else {
            p_136706_.sendSuccess(new TranslatableComponent("commands.clear.success.multiple", i, p_136707_.size()), true);
         }

         return i;
      }
   }
}