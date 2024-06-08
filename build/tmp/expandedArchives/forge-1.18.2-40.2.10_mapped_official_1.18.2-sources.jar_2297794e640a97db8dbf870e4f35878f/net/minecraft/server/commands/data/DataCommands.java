package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class DataCommands {
   private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType((p_139491_) -> {
      return new TranslatableComponent("commands.data.get.invalid", p_139491_);
   });
   private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType((p_139481_) -> {
      return new TranslatableComponent("commands.data.get.unknown", p_139481_);
   });
   private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.get.multiple"));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType((p_139468_) -> {
      return new TranslatableComponent("commands.data.modify.expected_list", p_139468_);
   });
   private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType((p_139448_) -> {
      return new TranslatableComponent("commands.data.modify.expected_object", p_139448_);
   });
   private static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType((p_139402_) -> {
      return new TranslatableComponent("commands.data.modify.invalid_index", p_139402_);
   });
   public static final List<Function<String, DataCommands.DataProvider>> ALL_PROVIDERS = ImmutableList.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER, StorageDataAccessor.PROVIDER);
   public static final List<DataCommands.DataProvider> TARGET_PROVIDERS = ALL_PROVIDERS.stream().map((p_139450_) -> {
      return p_139450_.apply("target");
   }).collect(ImmutableList.toImmutableList());
   public static final List<DataCommands.DataProvider> SOURCE_PROVIDERS = ALL_PROVIDERS.stream().map((p_139410_) -> {
      return p_139410_.apply("source");
   }).collect(ImmutableList.toImmutableList());

   public static void register(CommandDispatcher<CommandSourceStack> p_139366_) {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("data").requires((p_139381_) -> {
         return p_139381_.hasPermission(2);
      });

      for(DataCommands.DataProvider datacommands$dataprovider : TARGET_PROVIDERS) {
         literalargumentbuilder.then(datacommands$dataprovider.wrap(Commands.literal("merge"), (p_139471_) -> {
            return p_139471_.then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_142857_) -> {
               return mergeData(p_142857_.getSource(), datacommands$dataprovider.access(p_142857_), CompoundTagArgument.getCompoundTag(p_142857_, "nbt"));
            }));
         })).then(datacommands$dataprovider.wrap(Commands.literal("get"), (p_139453_) -> {
            return p_139453_.executes((p_142849_) -> {
               return getData(p_142849_.getSource(), datacommands$dataprovider.access(p_142849_));
            }).then(Commands.argument("path", NbtPathArgument.nbtPath()).executes((p_142841_) -> {
               return getData(p_142841_.getSource(), datacommands$dataprovider.access(p_142841_), NbtPathArgument.getPath(p_142841_, "path"));
            }).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((p_142833_) -> {
               return getNumeric(p_142833_.getSource(), datacommands$dataprovider.access(p_142833_), NbtPathArgument.getPath(p_142833_, "path"), DoubleArgumentType.getDouble(p_142833_, "scale"));
            })));
         })).then(datacommands$dataprovider.wrap(Commands.literal("remove"), (p_139413_) -> {
            return p_139413_.then(Commands.argument("path", NbtPathArgument.nbtPath()).executes((p_142820_) -> {
               return removeData(p_142820_.getSource(), datacommands$dataprovider.access(p_142820_), NbtPathArgument.getPath(p_142820_, "path"));
            }));
         })).then(decorateModification((p_139368_, p_139369_) -> {
            p_139368_.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(p_139369_.create((p_142859_, p_142860_, p_142861_, p_142862_) -> {
               int i = IntegerArgumentType.getInteger(p_142859_, "index");
               return insertAtIndex(i, p_142860_, p_142861_, p_142862_);
            })))).then(Commands.literal("prepend").then(p_139369_.create((p_142851_, p_142852_, p_142853_, p_142854_) -> {
               return insertAtIndex(0, p_142852_, p_142853_, p_142854_);
            }))).then(Commands.literal("append").then(p_139369_.create((p_142843_, p_142844_, p_142845_, p_142846_) -> {
               return insertAtIndex(-1, p_142844_, p_142845_, p_142846_);
            }))).then(Commands.literal("set").then(p_139369_.create((p_142835_, p_142836_, p_142837_, p_142838_) -> {
               return p_142837_.set(p_142836_, Iterables.getLast(p_142838_)::copy);
            }))).then(Commands.literal("merge").then(p_139369_.create((p_142822_, p_142823_, p_142824_, p_142825_) -> {
               Collection<Tag> collection = p_142824_.getOrCreate(p_142823_, CompoundTag::new);
               int i = 0;

               for(Tag tag : collection) {
                  if (!(tag instanceof CompoundTag)) {
                     throw ERROR_EXPECTED_OBJECT.create(tag);
                  }

                  CompoundTag compoundtag = (CompoundTag)tag;
                  CompoundTag compoundtag1 = compoundtag.copy();

                  for(Tag tag1 : p_142825_) {
                     if (!(tag1 instanceof CompoundTag)) {
                        throw ERROR_EXPECTED_OBJECT.create(tag1);
                     }

                     compoundtag.merge((CompoundTag)tag1);
                  }

                  i += compoundtag1.equals(compoundtag) ? 0 : 1;
               }

               return i;
            })));
         }));
      }

      p_139366_.register(literalargumentbuilder);
   }

   private static int insertAtIndex(int p_139361_, CompoundTag p_139362_, NbtPathArgument.NbtPath p_139363_, List<Tag> p_139364_) throws CommandSyntaxException {
      Collection<Tag> collection = p_139363_.getOrCreate(p_139362_, ListTag::new);
      int i = 0;

      for(Tag tag : collection) {
         if (!(tag instanceof CollectionTag)) {
            throw ERROR_EXPECTED_LIST.create(tag);
         }

         boolean flag = false;
         CollectionTag<?> collectiontag = (CollectionTag)tag;
         int j = p_139361_ < 0 ? collectiontag.size() + p_139361_ + 1 : p_139361_;

         for(Tag tag1 : p_139364_) {
            try {
               if (collectiontag.addTag(j, tag1.copy())) {
                  ++j;
                  flag = true;
               }
            } catch (IndexOutOfBoundsException indexoutofboundsexception) {
               throw ERROR_INVALID_INDEX.create(j);
            }
         }

         i += flag ? 1 : 0;
      }

      return i;
   }

   private static ArgumentBuilder<CommandSourceStack, ?> decorateModification(BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataCommands.DataManipulatorDecorator> p_139404_) {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("modify");

      for(DataCommands.DataProvider datacommands$dataprovider : TARGET_PROVIDERS) {
         datacommands$dataprovider.wrap(literalargumentbuilder, (p_139408_) -> {
            ArgumentBuilder<CommandSourceStack, ?> argumentbuilder = Commands.argument("targetPath", NbtPathArgument.nbtPath());

            for(DataCommands.DataProvider datacommands$dataprovider1 : SOURCE_PROVIDERS) {
               p_139404_.accept(argumentbuilder, (p_142807_) -> {
                  return datacommands$dataprovider1.wrap(Commands.literal("from"), (p_142812_) -> {
                     return p_142812_.executes((p_142830_) -> {
                        List<Tag> list = Collections.singletonList(datacommands$dataprovider1.access(p_142830_).getData());
                        return manipulateData(p_142830_, datacommands$dataprovider, p_142807_, list);
                     }).then(Commands.argument("sourcePath", NbtPathArgument.nbtPath()).executes((p_142817_) -> {
                        DataAccessor dataaccessor = datacommands$dataprovider1.access(p_142817_);
                        NbtPathArgument.NbtPath nbtpathargument$nbtpath = NbtPathArgument.getPath(p_142817_, "sourcePath");
                        List<Tag> list = nbtpathargument$nbtpath.get(dataaccessor.getData());
                        return manipulateData(p_142817_, datacommands$dataprovider, p_142807_, list);
                     }));
                  });
               });
            }

            p_139404_.accept(argumentbuilder, (p_142799_) -> {
               return Commands.literal("value").then(Commands.argument("value", NbtTagArgument.nbtTag()).executes((p_142803_) -> {
                  List<Tag> list = Collections.singletonList(NbtTagArgument.getNbtTag(p_142803_, "value"));
                  return manipulateData(p_142803_, datacommands$dataprovider, p_142799_, list);
               }));
            });
            return p_139408_.then(argumentbuilder);
         });
      }

      return literalargumentbuilder;
   }

   private static int manipulateData(CommandContext<CommandSourceStack> p_139376_, DataCommands.DataProvider p_139377_, DataCommands.DataManipulator p_139378_, List<Tag> p_139379_) throws CommandSyntaxException {
      DataAccessor dataaccessor = p_139377_.access(p_139376_);
      NbtPathArgument.NbtPath nbtpathargument$nbtpath = NbtPathArgument.getPath(p_139376_, "targetPath");
      CompoundTag compoundtag = dataaccessor.getData();
      int i = p_139378_.modify(p_139376_, compoundtag, nbtpathargument$nbtpath, p_139379_);
      if (i == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         dataaccessor.setData(compoundtag);
         p_139376_.getSource().sendSuccess(dataaccessor.getModifiedSuccess(), true);
         return i;
      }
   }

   private static int removeData(CommandSourceStack p_139386_, DataAccessor p_139387_, NbtPathArgument.NbtPath p_139388_) throws CommandSyntaxException {
      CompoundTag compoundtag = p_139387_.getData();
      int i = p_139388_.remove(compoundtag);
      if (i == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         p_139387_.setData(compoundtag);
         p_139386_.sendSuccess(p_139387_.getModifiedSuccess(), true);
         return i;
      }
   }

   private static Tag getSingleTag(NbtPathArgument.NbtPath p_139399_, DataAccessor p_139400_) throws CommandSyntaxException {
      Collection<Tag> collection = p_139399_.get(p_139400_.getData());
      Iterator<Tag> iterator = collection.iterator();
      Tag tag = iterator.next();
      if (iterator.hasNext()) {
         throw ERROR_MULTIPLE_TAGS.create();
      } else {
         return tag;
      }
   }

   private static int getData(CommandSourceStack p_139444_, DataAccessor p_139445_, NbtPathArgument.NbtPath p_139446_) throws CommandSyntaxException {
      Tag tag = getSingleTag(p_139446_, p_139445_);
      int i;
      if (tag instanceof NumericTag) {
         i = Mth.floor(((NumericTag)tag).getAsDouble());
      } else if (tag instanceof CollectionTag) {
         i = ((CollectionTag)tag).size();
      } else if (tag instanceof CompoundTag) {
         i = ((CompoundTag)tag).size();
      } else {
         if (!(tag instanceof StringTag)) {
            throw ERROR_GET_NON_EXISTENT.create(p_139446_.toString());
         }

         i = tag.getAsString().length();
      }

      p_139444_.sendSuccess(p_139445_.getPrintSuccess(tag), false);
      return i;
   }

   private static int getNumeric(CommandSourceStack p_139390_, DataAccessor p_139391_, NbtPathArgument.NbtPath p_139392_, double p_139393_) throws CommandSyntaxException {
      Tag tag = getSingleTag(p_139392_, p_139391_);
      if (!(tag instanceof NumericTag)) {
         throw ERROR_GET_NOT_NUMBER.create(p_139392_.toString());
      } else {
         int i = Mth.floor(((NumericTag)tag).getAsDouble() * p_139393_);
         p_139390_.sendSuccess(p_139391_.getPrintSuccess(p_139392_, p_139393_, i), false);
         return i;
      }
   }

   private static int getData(CommandSourceStack p_139383_, DataAccessor p_139384_) throws CommandSyntaxException {
      p_139383_.sendSuccess(p_139384_.getPrintSuccess(p_139384_.getData()), false);
      return 1;
   }

   private static int mergeData(CommandSourceStack p_139395_, DataAccessor p_139396_, CompoundTag p_139397_) throws CommandSyntaxException {
      CompoundTag compoundtag = p_139396_.getData();
      CompoundTag compoundtag1 = compoundtag.copy().merge(p_139397_);
      if (compoundtag.equals(compoundtag1)) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         p_139396_.setData(compoundtag1);
         p_139395_.sendSuccess(p_139396_.getModifiedSuccess(), true);
         return 1;
      }
   }

   interface DataManipulator {
      int modify(CommandContext<CommandSourceStack> p_139496_, CompoundTag p_139497_, NbtPathArgument.NbtPath p_139498_, List<Tag> p_139499_) throws CommandSyntaxException;
   }

   interface DataManipulatorDecorator {
      ArgumentBuilder<CommandSourceStack, ?> create(DataCommands.DataManipulator p_139501_);
   }

   public interface DataProvider {
      DataAccessor access(CommandContext<CommandSourceStack> p_139504_) throws CommandSyntaxException;

      ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> p_139502_, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> p_139503_);
   }
}