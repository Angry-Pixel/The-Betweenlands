package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;

public class DataPackCommand {
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((p_136868_) -> {
      return new TranslatableComponent("commands.datapack.unknown", p_136868_);
   });
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((p_136857_) -> {
      return new TranslatableComponent("commands.datapack.enable.failed", p_136857_);
   });
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((p_136833_) -> {
      return new TranslatableComponent("commands.datapack.disable.failed", p_136833_);
   });
   private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS = (p_136848_, p_136849_) -> {
      return SharedSuggestionProvider.suggest(p_136848_.getSource().getServer().getPackRepository().getSelectedIds().stream().map(StringArgumentType::escapeIfRequired), p_136849_);
   };
   private static final SuggestionProvider<CommandSourceStack> UNSELECTED_PACKS = (p_136813_, p_136814_) -> {
      PackRepository packrepository = p_136813_.getSource().getServer().getPackRepository();
      Collection<String> collection = packrepository.getSelectedIds();
      return SharedSuggestionProvider.suggest(packrepository.getAvailableIds().stream().filter((p_180050_) -> {
         return !collection.contains(p_180050_);
      }).map(StringArgumentType::escapeIfRequired), p_136814_);
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_136809_) {
      p_136809_.register(Commands.literal("datapack").requires((p_136872_) -> {
         return p_136872_.hasPermission(2);
      }).then(Commands.literal("enable").then(Commands.argument("name", StringArgumentType.string()).suggests(UNSELECTED_PACKS).executes((p_136882_) -> {
         return enablePack(p_136882_.getSource(), getPack(p_136882_, "name", true), (p_180059_, p_180060_) -> {
            p_180060_.getDefaultPosition().insert(p_180059_, p_180060_, (p_180062_) -> {
               return p_180062_;
            }, false);
         });
      }).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((p_136880_) -> {
         return enablePack(p_136880_.getSource(), getPack(p_136880_, "name", true), (p_180056_, p_180057_) -> {
            p_180056_.add(p_180056_.indexOf(getPack(p_136880_, "existing", false)) + 1, p_180057_);
         });
      }))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((p_136878_) -> {
         return enablePack(p_136878_.getSource(), getPack(p_136878_, "name", true), (p_180046_, p_180047_) -> {
            p_180046_.add(p_180046_.indexOf(getPack(p_136878_, "existing", false)), p_180047_);
         });
      }))).then(Commands.literal("last").executes((p_136876_) -> {
         return enablePack(p_136876_.getSource(), getPack(p_136876_, "name", true), List::add);
      })).then(Commands.literal("first").executes((p_136874_) -> {
         return enablePack(p_136874_.getSource(), getPack(p_136874_, "name", true), (p_180052_, p_180053_) -> {
            p_180052_.add(0, p_180053_);
         });
      })))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((p_136870_) -> {
         return disablePack(p_136870_.getSource(), getPack(p_136870_, "name", false));
      }))).then(Commands.literal("list").executes((p_136864_) -> {
         return listPacks(p_136864_.getSource());
      }).then(Commands.literal("available").executes((p_136846_) -> {
         return listAvailablePacks(p_136846_.getSource());
      })).then(Commands.literal("enabled").executes((p_136811_) -> {
         return listEnabledPacks(p_136811_.getSource());
      }))));
   }

   private static int enablePack(CommandSourceStack p_136829_, Pack p_136830_, DataPackCommand.Inserter p_136831_) throws CommandSyntaxException {
      PackRepository packrepository = p_136829_.getServer().getPackRepository();
      List<Pack> list = Lists.newArrayList(packrepository.getSelectedPacks());
      p_136831_.apply(list, p_136830_);
      p_136829_.sendSuccess(new TranslatableComponent("commands.datapack.modify.enable", p_136830_.getChatLink(true)), true);
      ReloadCommand.reloadPacks(list.stream().map(Pack::getId).collect(Collectors.toList()), p_136829_);
      return list.size();
   }

   private static int disablePack(CommandSourceStack p_136826_, Pack p_136827_) {
      PackRepository packrepository = p_136826_.getServer().getPackRepository();
      List<Pack> list = Lists.newArrayList(packrepository.getSelectedPacks());
      list.remove(p_136827_);
      p_136826_.sendSuccess(new TranslatableComponent("commands.datapack.modify.disable", p_136827_.getChatLink(true)), true);
      ReloadCommand.reloadPacks(list.stream().map(Pack::getId).collect(Collectors.toList()), p_136826_);
      return list.size();
   }

   private static int listPacks(CommandSourceStack p_136824_) {
      return listEnabledPacks(p_136824_) + listAvailablePacks(p_136824_);
   }

   private static int listAvailablePacks(CommandSourceStack p_136855_) {
      PackRepository packrepository = p_136855_.getServer().getPackRepository();
      packrepository.reload();
      Collection<? extends Pack> collection = packrepository.getSelectedPacks();
      Collection<? extends Pack> collection1 = packrepository.getAvailablePacks();
      List<Pack> list = collection1.stream().filter((p_136836_) -> {
         return !collection.contains(p_136836_);
      }).collect(Collectors.toList());
      if (list.isEmpty()) {
         p_136855_.sendSuccess(new TranslatableComponent("commands.datapack.list.available.none"), false);
      } else {
         p_136855_.sendSuccess(new TranslatableComponent("commands.datapack.list.available.success", list.size(), ComponentUtils.formatList(list, (p_136844_) -> {
            return p_136844_.getChatLink(false);
         })), false);
      }

      return list.size();
   }

   private static int listEnabledPacks(CommandSourceStack p_136866_) {
      PackRepository packrepository = p_136866_.getServer().getPackRepository();
      packrepository.reload();
      Collection<? extends Pack> collection = packrepository.getSelectedPacks();
      if (collection.isEmpty()) {
         p_136866_.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.none"), false);
      } else {
         p_136866_.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.success", collection.size(), ComponentUtils.formatList(collection, (p_136807_) -> {
            return p_136807_.getChatLink(true);
         })), false);
      }

      return collection.size();
   }

   private static Pack getPack(CommandContext<CommandSourceStack> p_136816_, String p_136817_, boolean p_136818_) throws CommandSyntaxException {
      String s = StringArgumentType.getString(p_136816_, p_136817_);
      PackRepository packrepository = p_136816_.getSource().getServer().getPackRepository();
      Pack pack = packrepository.getPack(s);
      if (pack == null) {
         throw ERROR_UNKNOWN_PACK.create(s);
      } else {
         boolean flag = packrepository.getSelectedPacks().contains(pack);
         if (p_136818_ && flag) {
            throw ERROR_PACK_ALREADY_ENABLED.create(s);
         } else if (!p_136818_ && !flag) {
            throw ERROR_PACK_ALREADY_DISABLED.create(s);
         } else {
            return pack;
         }
      }
   }

   interface Inserter {
      void apply(List<Pack> p_136884_, Pack p_136885_) throws CommandSyntaxException;
   }
}