package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ExecuteCommand {
   private static final int MAX_TEST_AREA = 32768;
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137129_, p_137130_) -> {
      return new TranslatableComponent("commands.execute.blocks.toobig", p_137129_, p_137130_);
   });
   private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.execute.conditional.fail"));
   private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType((p_137127_) -> {
      return new TranslatableComponent("commands.execute.conditional.fail_count", p_137127_);
   });
   private static final BinaryOperator<ResultConsumer<CommandSourceStack>> CALLBACK_CHAINER = (p_137045_, p_137046_) -> {
      return (p_180160_, p_180161_, p_180162_) -> {
         p_137045_.onCommandComplete(p_180160_, p_180161_, p_180162_);
         p_137046_.onCommandComplete(p_180160_, p_180161_, p_180162_);
      };
   };
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_PREDICATE = (p_137062_, p_137063_) -> {
      PredicateManager predicatemanager = p_137062_.getSource().getServer().getPredicateManager();
      return SharedSuggestionProvider.suggestResource(predicatemanager.getKeys(), p_137063_);
   };

   public static void register(CommandDispatcher<CommandSourceStack> p_137043_) {
      LiteralCommandNode<CommandSourceStack> literalcommandnode = p_137043_.register(Commands.literal("execute").requires((p_137197_) -> {
         return p_137197_.hasPermission(2);
      }));
      p_137043_.register(Commands.literal("execute").requires((p_137103_) -> {
         return p_137103_.hasPermission(2);
      }).then(Commands.literal("run").redirect(p_137043_.getRoot())).then(addConditionals(literalcommandnode, Commands.literal("if"), true)).then(addConditionals(literalcommandnode, Commands.literal("unless"), false)).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_137299_) -> {
         List<CommandSourceStack> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_137299_, "targets")) {
            list.add(p_137299_.getSource().withEntity(entity));
         }

         return list;
      }))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_137297_) -> {
         List<CommandSourceStack> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_137297_, "targets")) {
            list.add(p_137297_.getSource().withLevel((ServerLevel)entity.level).withPosition(entity.position()).withRotation(entity.getRotationVector()));
         }

         return list;
      }))).then(Commands.literal("store").then(wrapStores(literalcommandnode, Commands.literal("result"), true)).then(wrapStores(literalcommandnode, Commands.literal("success"), false))).then(Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_137295_) -> {
         return p_137295_.getSource().withPosition(Vec3Argument.getVec3(p_137295_, "pos")).withAnchor(EntityAnchorArgument.Anchor.FEET);
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_137293_) -> {
         List<CommandSourceStack> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_137293_, "targets")) {
            list.add(p_137293_.getSource().withPosition(entity.position()));
         }

         return list;
      })))).then(Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(literalcommandnode, (p_137291_) -> {
         return p_137291_.getSource().withRotation(RotationArgument.getRotation(p_137291_, "rot").getRotation(p_137291_.getSource()));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_137289_) -> {
         List<CommandSourceStack> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_137289_, "targets")) {
            list.add(p_137289_.getSource().withRotation(entity.getRotationVector()));
         }

         return list;
      })))).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork(literalcommandnode, (p_137287_) -> {
         List<CommandSourceStack> list = Lists.newArrayList();
         EntityAnchorArgument.Anchor entityanchorargument$anchor = EntityAnchorArgument.getAnchor(p_137287_, "anchor");

         for(Entity entity : EntityArgument.getOptionalEntities(p_137287_, "targets")) {
            list.add(p_137287_.getSource().facing(entity, entityanchorargument$anchor));
         }

         return list;
      })))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_137285_) -> {
         return p_137285_.getSource().facing(Vec3Argument.getVec3(p_137285_, "pos"));
      }))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(literalcommandnode, (p_137283_) -> {
         return p_137283_.getSource().withPosition(p_137283_.getSource().getPosition().align(SwizzleArgument.getSwizzle(p_137283_, "axes")));
      }))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect(literalcommandnode, (p_137281_) -> {
         return p_137281_.getSource().withAnchor(EntityAnchorArgument.getAnchor(p_137281_, "anchor"));
      }))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.dimension()).redirect(literalcommandnode, (p_137279_) -> {
         return p_137279_.getSource().withLevel(DimensionArgument.getDimension(p_137279_, "dimension"));
      }))));
   }

   private static ArgumentBuilder<CommandSourceStack, ?> wrapStores(LiteralCommandNode<CommandSourceStack> p_137094_, LiteralArgumentBuilder<CommandSourceStack> p_137095_, boolean p_137096_) {
      p_137095_.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(p_137094_, (p_137271_) -> {
         return storeValue(p_137271_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_137271_, "targets"), ObjectiveArgument.getObjective(p_137271_, "objective"), p_137096_);
      }))));
      p_137095_.then(Commands.literal("bossbar").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommands.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect(p_137094_, (p_137259_) -> {
         return storeValue(p_137259_.getSource(), BossBarCommands.getBossBar(p_137259_), true, p_137096_);
      })).then(Commands.literal("max").redirect(p_137094_, (p_137247_) -> {
         return storeValue(p_137247_.getSource(), BossBarCommands.getBossBar(p_137247_), false, p_137096_);
      }))));

      for(DataCommands.DataProvider datacommands$dataprovider : DataCommands.TARGET_PROVIDERS) {
         datacommands$dataprovider.wrap(p_137095_, (p_137101_) -> {
            return p_137101_.then(Commands.argument("path", NbtPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180216_) -> {
               return storeData(p_180216_.getSource(), datacommands$dataprovider.access(p_180216_), NbtPathArgument.getPath(p_180216_, "path"), (p_180219_) -> {
                  return IntTag.valueOf((int)((double)p_180219_ * DoubleArgumentType.getDouble(p_180216_, "scale")));
               }, p_137096_);
            }))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180209_) -> {
               return storeData(p_180209_.getSource(), datacommands$dataprovider.access(p_180209_), NbtPathArgument.getPath(p_180209_, "path"), (p_180212_) -> {
                  return FloatTag.valueOf((float)((double)p_180212_ * DoubleArgumentType.getDouble(p_180209_, "scale")));
               }, p_137096_);
            }))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180199_) -> {
               return storeData(p_180199_.getSource(), datacommands$dataprovider.access(p_180199_), NbtPathArgument.getPath(p_180199_, "path"), (p_180202_) -> {
                  return ShortTag.valueOf((short)((int)((double)p_180202_ * DoubleArgumentType.getDouble(p_180199_, "scale"))));
               }, p_137096_);
            }))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180189_) -> {
               return storeData(p_180189_.getSource(), datacommands$dataprovider.access(p_180189_), NbtPathArgument.getPath(p_180189_, "path"), (p_180192_) -> {
                  return LongTag.valueOf((long)((double)p_180192_ * DoubleArgumentType.getDouble(p_180189_, "scale")));
               }, p_137096_);
            }))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180179_) -> {
               return storeData(p_180179_.getSource(), datacommands$dataprovider.access(p_180179_), NbtPathArgument.getPath(p_180179_, "path"), (p_180182_) -> {
                  return DoubleTag.valueOf((double)p_180182_ * DoubleArgumentType.getDouble(p_180179_, "scale"));
               }, p_137096_);
            }))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180156_) -> {
               return storeData(p_180156_.getSource(), datacommands$dataprovider.access(p_180156_), NbtPathArgument.getPath(p_180156_, "path"), (p_180165_) -> {
                  return ByteTag.valueOf((byte)((int)((double)p_180165_ * DoubleArgumentType.getDouble(p_180156_, "scale"))));
               }, p_137096_);
            }))));
         });
      }

      return p_137095_;
   }

   private static CommandSourceStack storeValue(CommandSourceStack p_137108_, Collection<String> p_137109_, Objective p_137110_, boolean p_137111_) {
      Scoreboard scoreboard = p_137108_.getServer().getScoreboard();
      return p_137108_.withCallback((p_137136_, p_137137_, p_137138_) -> {
         for(String s : p_137109_) {
            Score score = scoreboard.getOrCreatePlayerScore(s, p_137110_);
            int i = p_137111_ ? p_137138_ : (p_137137_ ? 1 : 0);
            score.setScore(i);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSourceStack storeValue(CommandSourceStack p_137113_, CustomBossEvent p_137114_, boolean p_137115_, boolean p_137116_) {
      return p_137113_.withCallback((p_137185_, p_137186_, p_137187_) -> {
         int i = p_137116_ ? p_137187_ : (p_137186_ ? 1 : 0);
         if (p_137115_) {
            p_137114_.setValue(i);
         } else {
            p_137114_.setMax(i);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSourceStack storeData(CommandSourceStack p_137118_, DataAccessor p_137119_, NbtPathArgument.NbtPath p_137120_, IntFunction<Tag> p_137121_, boolean p_137122_) {
      return p_137118_.withCallback((p_137153_, p_137154_, p_137155_) -> {
         try {
            CompoundTag compoundtag = p_137119_.getData();
            int i = p_137122_ ? p_137155_ : (p_137154_ ? 1 : 0);
            p_137120_.set(compoundtag, () -> {
               return p_137121_.apply(i);
            });
            p_137119_.setData(compoundtag);
         } catch (CommandSyntaxException commandsyntaxexception) {
         }

      }, CALLBACK_CHAINER);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> addConditionals(CommandNode<CommandSourceStack> p_137085_, LiteralArgumentBuilder<CommandSourceStack> p_137086_, boolean p_137087_) {
      p_137086_.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(addConditional(p_137085_, Commands.argument("block", BlockPredicateArgument.blockPredicate()), p_137087_, (p_137277_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_137277_, "block").test(new BlockInWorld(p_137277_.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(p_137277_, "pos"), true));
      })))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_137085_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_137087_, (p_137275_) -> {
         return checkScore(p_137275_, Integer::equals);
      })))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_137085_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_137087_, (p_137273_) -> {
         return checkScore(p_137273_, (p_180204_, p_180205_) -> {
            return p_180204_ < p_180205_;
         });
      })))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_137085_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_137087_, (p_137261_) -> {
         return checkScore(p_137261_, (p_180194_, p_180195_) -> {
            return p_180194_ <= p_180195_;
         });
      })))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_137085_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_137087_, (p_137249_) -> {
         return checkScore(p_137249_, (p_180184_, p_180185_) -> {
            return p_180184_ > p_180185_;
         });
      })))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_137085_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_137087_, (p_137234_) -> {
         return checkScore(p_137234_, (p_180167_, p_180168_) -> {
            return p_180167_ >= p_180168_;
         });
      })))).then(Commands.literal("matches").then(addConditional(p_137085_, Commands.argument("range", RangeArgument.intRange()), p_137087_, (p_137216_) -> {
         return checkScore(p_137216_, RangeArgument.Ints.getRange(p_137216_, "range"));
      })))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).then(addIfBlocksConditional(p_137085_, Commands.literal("all"), p_137087_, false)).then(addIfBlocksConditional(p_137085_, Commands.literal("masked"), p_137087_, true)))))).then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).fork(p_137085_, (p_137232_) -> {
         return expect(p_137232_, p_137087_, !EntityArgument.getOptionalEntities(p_137232_, "entities").isEmpty());
      }).executes(createNumericConditionalHandler(p_137087_, (p_137189_) -> {
         return EntityArgument.getOptionalEntities(p_137189_, "entities").size();
      })))).then(Commands.literal("predicate").then(addConditional(p_137085_, Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE), p_137087_, (p_137054_) -> {
         return checkCustomPredicate(p_137054_.getSource(), ResourceLocationArgument.getPredicate(p_137054_, "predicate"));
      })));

      for(DataCommands.DataProvider datacommands$dataprovider : DataCommands.SOURCE_PROVIDERS) {
         p_137086_.then(datacommands$dataprovider.wrap(Commands.literal("data"), (p_137092_) -> {
            return p_137092_.then(Commands.argument("path", NbtPathArgument.nbtPath()).fork(p_137085_, (p_180175_) -> {
               return expect(p_180175_, p_137087_, checkMatchingData(datacommands$dataprovider.access(p_180175_), NbtPathArgument.getPath(p_180175_, "path")) > 0);
            }).executes(createNumericConditionalHandler(p_137087_, (p_180152_) -> {
               return checkMatchingData(datacommands$dataprovider.access(p_180152_), NbtPathArgument.getPath(p_180152_, "path"));
            })));
         }));
      }

      return p_137086_;
   }

   private static Command<CommandSourceStack> createNumericConditionalHandler(boolean p_137167_, ExecuteCommand.CommandNumericPredicate p_137168_) {
      return p_137167_ ? (p_137203_) -> {
         int i = p_137168_.test(p_137203_);
         if (i > 0) {
            p_137203_.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", i), false);
            return i;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      } : (p_137144_) -> {
         int i = p_137168_.test(p_137144_);
         if (i == 0) {
            p_137144_.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create(i);
         }
      };
   }

   private static int checkMatchingData(DataAccessor p_137146_, NbtPathArgument.NbtPath p_137147_) throws CommandSyntaxException {
      return p_137147_.countMatching(p_137146_.getData());
   }

   private static boolean checkScore(CommandContext<CommandSourceStack> p_137065_, BiPredicate<Integer, Integer> p_137066_) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getName(p_137065_, "target");
      Objective objective = ObjectiveArgument.getObjective(p_137065_, "targetObjective");
      String s1 = ScoreHolderArgument.getName(p_137065_, "source");
      Objective objective1 = ObjectiveArgument.getObjective(p_137065_, "sourceObjective");
      Scoreboard scoreboard = p_137065_.getSource().getServer().getScoreboard();
      if (scoreboard.hasPlayerScore(s, objective) && scoreboard.hasPlayerScore(s1, objective1)) {
         Score score = scoreboard.getOrCreatePlayerScore(s, objective);
         Score score1 = scoreboard.getOrCreatePlayerScore(s1, objective1);
         return p_137066_.test(score.getScore(), score1.getScore());
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext<CommandSourceStack> p_137059_, MinMaxBounds.Ints p_137060_) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getName(p_137059_, "target");
      Objective objective = ObjectiveArgument.getObjective(p_137059_, "targetObjective");
      Scoreboard scoreboard = p_137059_.getSource().getServer().getScoreboard();
      return !scoreboard.hasPlayerScore(s, objective) ? false : p_137060_.matches(scoreboard.getOrCreatePlayerScore(s, objective).getScore());
   }

   private static boolean checkCustomPredicate(CommandSourceStack p_137105_, LootItemCondition p_137106_) {
      ServerLevel serverlevel = p_137105_.getLevel();
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverlevel)).withParameter(LootContextParams.ORIGIN, p_137105_.getPosition()).withOptionalParameter(LootContextParams.THIS_ENTITY, p_137105_.getEntity());
      return p_137106_.test(lootcontext$builder.create(LootContextParamSets.COMMAND));
   }

   private static Collection<CommandSourceStack> expect(CommandContext<CommandSourceStack> p_137071_, boolean p_137072_, boolean p_137073_) {
      return (Collection<CommandSourceStack>)(p_137073_ == p_137072_ ? Collections.singleton(p_137071_.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> p_137075_, ArgumentBuilder<CommandSourceStack, ?> p_137076_, boolean p_137077_, ExecuteCommand.CommandPredicate p_137078_) {
      return p_137076_.fork(p_137075_, (p_137214_) -> {
         return expect(p_137214_, p_137077_, p_137078_.test(p_137214_));
      }).executes((p_137172_) -> {
         if (p_137077_ == p_137078_.test(p_137172_)) {
            p_137172_.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSourceStack, ?> addIfBlocksConditional(CommandNode<CommandSourceStack> p_137080_, ArgumentBuilder<CommandSourceStack, ?> p_137081_, boolean p_137082_, boolean p_137083_) {
      return p_137081_.fork(p_137080_, (p_137180_) -> {
         return expect(p_137180_, p_137082_, checkRegions(p_137180_, p_137083_).isPresent());
      }).executes(p_137082_ ? (p_137210_) -> {
         return checkIfRegions(p_137210_, p_137083_);
      } : (p_137165_) -> {
         return checkUnlessRegions(p_137165_, p_137083_);
      });
   }

   private static int checkIfRegions(CommandContext<CommandSourceStack> p_137068_, boolean p_137069_) throws CommandSyntaxException {
      OptionalInt optionalint = checkRegions(p_137068_, p_137069_);
      if (optionalint.isPresent()) {
         p_137068_.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", optionalint.getAsInt()), false);
         return optionalint.getAsInt();
      } else {
         throw ERROR_CONDITIONAL_FAILED.create();
      }
   }

   private static int checkUnlessRegions(CommandContext<CommandSourceStack> p_137194_, boolean p_137195_) throws CommandSyntaxException {
      OptionalInt optionalint = checkRegions(p_137194_, p_137195_);
      if (optionalint.isPresent()) {
         throw ERROR_CONDITIONAL_FAILED_COUNT.create(optionalint.getAsInt());
      } else {
         p_137194_.getSource().sendSuccess(new TranslatableComponent("commands.execute.conditional.pass"), false);
         return 1;
      }
   }

   private static OptionalInt checkRegions(CommandContext<CommandSourceStack> p_137221_, boolean p_137222_) throws CommandSyntaxException {
      return checkRegions(p_137221_.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(p_137221_, "start"), BlockPosArgument.getLoadedBlockPos(p_137221_, "end"), BlockPosArgument.getLoadedBlockPos(p_137221_, "destination"), p_137222_);
   }

   private static OptionalInt checkRegions(ServerLevel p_137037_, BlockPos p_137038_, BlockPos p_137039_, BlockPos p_137040_, boolean p_137041_) throws CommandSyntaxException {
      BoundingBox boundingbox = BoundingBox.fromCorners(p_137038_, p_137039_);
      BoundingBox boundingbox1 = BoundingBox.fromCorners(p_137040_, p_137040_.offset(boundingbox.getLength()));
      BlockPos blockpos = new BlockPos(boundingbox1.minX() - boundingbox.minX(), boundingbox1.minY() - boundingbox.minY(), boundingbox1.minZ() - boundingbox.minZ());
      int i = boundingbox.getXSpan() * boundingbox.getYSpan() * boundingbox.getZSpan();
      if (i > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, i);
      } else {
         int j = 0;

         for(int k = boundingbox.minZ(); k <= boundingbox.maxZ(); ++k) {
            for(int l = boundingbox.minY(); l <= boundingbox.maxY(); ++l) {
               for(int i1 = boundingbox.minX(); i1 <= boundingbox.maxX(); ++i1) {
                  BlockPos blockpos1 = new BlockPos(i1, l, k);
                  BlockPos blockpos2 = blockpos1.offset(blockpos);
                  BlockState blockstate = p_137037_.getBlockState(blockpos1);
                  if (!p_137041_ || !blockstate.is(Blocks.AIR)) {
                     if (blockstate != p_137037_.getBlockState(blockpos2)) {
                        return OptionalInt.empty();
                     }

                     BlockEntity blockentity = p_137037_.getBlockEntity(blockpos1);
                     BlockEntity blockentity1 = p_137037_.getBlockEntity(blockpos2);
                     if (blockentity != null) {
                        if (blockentity1 == null) {
                           return OptionalInt.empty();
                        }

                        if (blockentity1.getType() != blockentity.getType()) {
                           return OptionalInt.empty();
                        }

                        CompoundTag compoundtag = blockentity.saveWithoutMetadata();
                        CompoundTag compoundtag1 = blockentity1.saveWithoutMetadata();
                        if (!compoundtag.equals(compoundtag1)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++j;
                  }
               }
            }
         }

         return OptionalInt.of(j);
      }
   }

   @FunctionalInterface
   interface CommandNumericPredicate {
      int test(CommandContext<CommandSourceStack> p_137301_) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface CommandPredicate {
      boolean test(CommandContext<CommandSourceStack> p_137303_) throws CommandSyntaxException;
   }
}