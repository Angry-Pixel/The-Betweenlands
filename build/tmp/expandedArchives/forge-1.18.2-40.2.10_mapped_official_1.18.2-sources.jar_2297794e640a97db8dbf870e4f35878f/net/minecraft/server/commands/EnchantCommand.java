package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((p_137029_) -> {
      return new TranslatableComponent("commands.enchant.failed.entity", p_137029_);
   });
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((p_137027_) -> {
      return new TranslatableComponent("commands.enchant.failed.itemless", p_137027_);
   });
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((p_137020_) -> {
      return new TranslatableComponent("commands.enchant.failed.incompatible", p_137020_);
   });
   private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((p_137022_, p_137023_) -> {
      return new TranslatableComponent("commands.enchant.failed.level", p_137022_, p_137023_);
   });
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(new TranslatableComponent("commands.enchant.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_137009_) {
      p_137009_.register(Commands.literal("enchant").requires((p_137013_) -> {
         return p_137013_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("enchantment", ItemEnchantmentArgument.enchantment()).executes((p_137025_) -> {
         return enchant(p_137025_.getSource(), EntityArgument.getEntities(p_137025_, "targets"), ItemEnchantmentArgument.getEnchantment(p_137025_, "enchantment"), 1);
      }).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((p_137011_) -> {
         return enchant(p_137011_.getSource(), EntityArgument.getEntities(p_137011_, "targets"), ItemEnchantmentArgument.getEnchantment(p_137011_, "enchantment"), IntegerArgumentType.getInteger(p_137011_, "level"));
      })))));
   }

   private static int enchant(CommandSourceStack p_137015_, Collection<? extends Entity> p_137016_, Enchantment p_137017_, int p_137018_) throws CommandSyntaxException {
      if (p_137018_ > p_137017_.getMaxLevel()) {
         throw ERROR_LEVEL_TOO_HIGH.create(p_137018_, p_137017_.getMaxLevel());
      } else {
         int i = 0;

         for(Entity entity : p_137016_) {
            if (entity instanceof LivingEntity) {
               LivingEntity livingentity = (LivingEntity)entity;
               ItemStack itemstack = livingentity.getMainHandItem();
               if (!itemstack.isEmpty()) {
                  if (p_137017_.canEnchant(itemstack) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(itemstack).keySet(), p_137017_)) {
                     itemstack.enchant(p_137017_, p_137018_);
                     ++i;
                  } else if (p_137016_.size() == 1) {
                     throw ERROR_INCOMPATIBLE.create(itemstack.getItem().getName(itemstack).getString());
                  }
               } else if (p_137016_.size() == 1) {
                  throw ERROR_NO_ITEM.create(livingentity.getName().getString());
               }
            } else if (p_137016_.size() == 1) {
               throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
            }
         }

         if (i == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
         } else {
            if (p_137016_.size() == 1) {
               p_137015_.sendSuccess(new TranslatableComponent("commands.enchant.success.single", p_137017_.getFullname(p_137018_), p_137016_.iterator().next().getDisplayName()), true);
            } else {
               p_137015_.sendSuccess(new TranslatableComponent("commands.enchant.success.multiple", p_137017_.getFullname(p_137018_), p_137016_.size()), true);
            }

            return i;
         }
      }
   }
}