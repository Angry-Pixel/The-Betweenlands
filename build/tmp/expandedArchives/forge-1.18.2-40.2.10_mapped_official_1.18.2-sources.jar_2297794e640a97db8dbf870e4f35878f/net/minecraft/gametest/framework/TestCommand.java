package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;

public class TestCommand {
   private static final int DEFAULT_CLEAR_RADIUS = 200;
   private static final int MAX_CLEAR_RADIUS = 1024;
   private static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
   private static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
   private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
   private static final int SHOW_POS_DURATION_MS = 10000;
   private static final int DEFAULT_X_SIZE = 5;
   private static final int DEFAULT_Y_SIZE = 5;
   private static final int DEFAULT_Z_SIZE = 5;

   public static void register(CommandDispatcher<CommandSourceStack> p_127947_) {
      p_127947_.register(Commands.literal("test").then(Commands.literal("runthis").executes((p_128057_) -> {
         return runNearbyTest(p_128057_.getSource());
      })).then(Commands.literal("runthese").executes((p_128055_) -> {
         return runAllNearbyTests(p_128055_.getSource());
      })).then(Commands.literal("runfailed").executes((p_128053_) -> {
         return runLastFailedTests(p_128053_.getSource(), false, 0, 8);
      }).then(Commands.argument("onlyRequiredTests", BoolArgumentType.bool()).executes((p_128051_) -> {
         return runLastFailedTests(p_128051_.getSource(), BoolArgumentType.getBool(p_128051_, "onlyRequiredTests"), 0, 8);
      }).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128049_) -> {
         return runLastFailedTests(p_128049_.getSource(), BoolArgumentType.getBool(p_128049_, "onlyRequiredTests"), IntegerArgumentType.getInteger(p_128049_, "rotationSteps"), 8);
      }).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_128047_) -> {
         return runLastFailedTests(p_128047_.getSource(), BoolArgumentType.getBool(p_128047_, "onlyRequiredTests"), IntegerArgumentType.getInteger(p_128047_, "rotationSteps"), IntegerArgumentType.getInteger(p_128047_, "testsPerRow"));
      }))))).then(Commands.literal("run").then(Commands.argument("testName", TestFunctionArgument.testFunctionArgument()).executes((p_128045_) -> {
         return runTest(p_128045_.getSource(), TestFunctionArgument.getTestFunction(p_128045_, "testName"), 0);
      }).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128043_) -> {
         return runTest(p_128043_.getSource(), TestFunctionArgument.getTestFunction(p_128043_, "testName"), IntegerArgumentType.getInteger(p_128043_, "rotationSteps"));
      })))).then(Commands.literal("runall").executes((p_128041_) -> {
         return runAllTests(p_128041_.getSource(), 0, 8);
      }).then(Commands.argument("testClassName", TestClassNameArgument.testClassName()).executes((p_128039_) -> {
         return runAllTestsInClass(p_128039_.getSource(), TestClassNameArgument.getTestClassName(p_128039_, "testClassName"), 0, 8);
      }).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128037_) -> {
         return runAllTestsInClass(p_128037_.getSource(), TestClassNameArgument.getTestClassName(p_128037_, "testClassName"), IntegerArgumentType.getInteger(p_128037_, "rotationSteps"), 8);
      }).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_128035_) -> {
         return runAllTestsInClass(p_128035_.getSource(), TestClassNameArgument.getTestClassName(p_128035_, "testClassName"), IntegerArgumentType.getInteger(p_128035_, "rotationSteps"), IntegerArgumentType.getInteger(p_128035_, "testsPerRow"));
      })))).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128033_) -> {
         return runAllTests(p_128033_.getSource(), IntegerArgumentType.getInteger(p_128033_, "rotationSteps"), 8);
      }).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_128031_) -> {
         return runAllTests(p_128031_.getSource(), IntegerArgumentType.getInteger(p_128031_, "rotationSteps"), IntegerArgumentType.getInteger(p_128031_, "testsPerRow"));
      })))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes((p_128029_) -> {
         return exportTestStructure(p_128029_.getSource(), StringArgumentType.getString(p_128029_, "testName"));
      }))).then(Commands.literal("exportthis").executes((p_128027_) -> {
         return exportNearestTestStructure(p_128027_.getSource());
      })).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes((p_128025_) -> {
         return importTestStructure(p_128025_.getSource(), StringArgumentType.getString(p_128025_, "testName"));
      }))).then(Commands.literal("pos").executes((p_128023_) -> {
         return showPos(p_128023_.getSource(), "pos");
      }).then(Commands.argument("var", StringArgumentType.word()).executes((p_128021_) -> {
         return showPos(p_128021_.getSource(), StringArgumentType.getString(p_128021_, "var"));
      }))).then(Commands.literal("create").then(Commands.argument("testName", StringArgumentType.word()).executes((p_128019_) -> {
         return createNewStructure(p_128019_.getSource(), StringArgumentType.getString(p_128019_, "testName"), 5, 5, 5);
      }).then(Commands.argument("width", IntegerArgumentType.integer()).executes((p_128014_) -> {
         return createNewStructure(p_128014_.getSource(), StringArgumentType.getString(p_128014_, "testName"), IntegerArgumentType.getInteger(p_128014_, "width"), IntegerArgumentType.getInteger(p_128014_, "width"), IntegerArgumentType.getInteger(p_128014_, "width"));
      }).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes((p_128007_) -> {
         return createNewStructure(p_128007_.getSource(), StringArgumentType.getString(p_128007_, "testName"), IntegerArgumentType.getInteger(p_128007_, "width"), IntegerArgumentType.getInteger(p_128007_, "height"), IntegerArgumentType.getInteger(p_128007_, "depth"));
      })))))).then(Commands.literal("clearall").executes((p_128000_) -> {
         return clearAllTests(p_128000_.getSource(), 200);
      }).then(Commands.argument("radius", IntegerArgumentType.integer()).executes((p_127949_) -> {
         return clearAllTests(p_127949_.getSource(), IntegerArgumentType.getInteger(p_127949_, "radius"));
      }))));
   }

   private static int createNewStructure(CommandSourceStack p_127968_, String p_127969_, int p_127970_, int p_127971_, int p_127972_) {
      if (p_127970_ <= 48 && p_127971_ <= 48 && p_127972_ <= 48) {
         ServerLevel serverlevel = p_127968_.getLevel();
         BlockPos blockpos = new BlockPos(p_127968_.getPosition());
         BlockPos blockpos1 = new BlockPos(blockpos.getX(), p_127968_.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockpos).getY(), blockpos.getZ() + 3);
         StructureUtils.createNewEmptyStructureBlock(p_127969_.toLowerCase(), blockpos1, new Vec3i(p_127970_, p_127971_, p_127972_), Rotation.NONE, serverlevel);

         for(int i = 0; i < p_127970_; ++i) {
            for(int j = 0; j < p_127972_; ++j) {
               BlockPos blockpos2 = new BlockPos(blockpos1.getX() + i, blockpos1.getY() + 1, blockpos1.getZ() + j);
               Block block = Blocks.POLISHED_ANDESITE;
               BlockInput blockinput = new BlockInput(block.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
               blockinput.place(serverlevel, blockpos2, 2);
            }
         }

         StructureUtils.addCommandBlockAndButtonToStartTest(blockpos1, new BlockPos(1, 0, -1), Rotation.NONE, serverlevel);
         return 0;
      } else {
         throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
      }
   }

   private static int showPos(CommandSourceStack p_127960_, String p_127961_) throws CommandSyntaxException {
      BlockHitResult blockhitresult = (BlockHitResult)p_127960_.getPlayerOrException().pick(10.0D, 1.0F, false);
      BlockPos blockpos = blockhitresult.getBlockPos();
      ServerLevel serverlevel = p_127960_.getLevel();
      Optional<BlockPos> optional = StructureUtils.findStructureBlockContainingPos(blockpos, 15, serverlevel);
      if (!optional.isPresent()) {
         optional = StructureUtils.findStructureBlockContainingPos(blockpos, 200, serverlevel);
      }

      if (!optional.isPresent()) {
         p_127960_.sendFailure(new TextComponent("Can't find a structure block that contains the targeted pos " + blockpos));
         return 0;
      } else {
         StructureBlockEntity structureblockentity = (StructureBlockEntity)serverlevel.getBlockEntity(optional.get());
         BlockPos blockpos1 = blockpos.subtract(optional.get());
         String s = blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ();
         String s1 = structureblockentity.getStructurePath();
         Component component = (new TextComponent(s)).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy to clipboard"))).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + p_127961_ + " = new BlockPos(" + s + ");")));
         p_127960_.sendSuccess((new TextComponent("Position relative to " + s1 + ": ")).append(component), false);
         DebugPackets.sendGameTestAddMarker(serverlevel, new BlockPos(blockpos), s, -2147418368, 10000);
         return 1;
      }
   }

   private static int runNearbyTest(CommandSourceStack p_127951_) {
      BlockPos blockpos = new BlockPos(p_127951_.getPosition());
      ServerLevel serverlevel = p_127951_.getLevel();
      BlockPos blockpos1 = StructureUtils.findNearestStructureBlock(blockpos, 15, serverlevel);
      if (blockpos1 == null) {
         say(serverlevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         GameTestRunner.clearMarkers(serverlevel);
         runTest(serverlevel, blockpos1, (MultipleTestTracker)null);
         return 1;
      }
   }

   private static int runAllNearbyTests(CommandSourceStack p_128002_) {
      BlockPos blockpos = new BlockPos(p_128002_.getPosition());
      ServerLevel serverlevel = p_128002_.getLevel();
      Collection<BlockPos> collection = StructureUtils.findStructureBlocks(blockpos, 200, serverlevel);
      if (collection.isEmpty()) {
         say(serverlevel, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
         return 1;
      } else {
         GameTestRunner.clearMarkers(serverlevel);
         say(p_128002_, "Running " + collection.size() + " tests...");
         MultipleTestTracker multipletesttracker = new MultipleTestTracker();
         collection.forEach((p_127943_) -> {
            runTest(serverlevel, p_127943_, multipletesttracker);
         });
         return 1;
      }
   }

   private static void runTest(ServerLevel p_127930_, BlockPos p_127931_, @Nullable MultipleTestTracker p_127932_) {
      StructureBlockEntity structureblockentity = (StructureBlockEntity)p_127930_.getBlockEntity(p_127931_);
      String s = structureblockentity.getStructurePath();
      TestFunction testfunction = GameTestRegistry.getTestFunction(s);
      GameTestInfo gametestinfo = new GameTestInfo(testfunction, structureblockentity.getRotation(), p_127930_);
      if (p_127932_ != null) {
         p_127932_.addTestToTrack(gametestinfo);
         gametestinfo.addListener(new TestCommand.TestSummaryDisplayer(p_127930_, p_127932_));
      }

      runTestPreparation(testfunction, p_127930_);
      AABB aabb = StructureUtils.getStructureBounds(structureblockentity);
      BlockPos blockpos = new BlockPos(aabb.minX, aabb.minY, aabb.minZ);
      GameTestRunner.runTest(gametestinfo, blockpos, GameTestTicker.SINGLETON);
   }

   static void showTestSummaryIfAllDone(ServerLevel p_127997_, MultipleTestTracker p_127998_) {
      if (p_127998_.isDone()) {
         say(p_127997_, "GameTest done! " + p_127998_.getTotalCount() + " tests were run", ChatFormatting.WHITE);
         if (p_127998_.hasFailedRequired()) {
            say(p_127997_, p_127998_.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
         } else {
            say(p_127997_, "All required tests passed :)", ChatFormatting.GREEN);
         }

         if (p_127998_.hasFailedOptional()) {
            say(p_127997_, p_127998_.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
         }
      }

   }

   private static int clearAllTests(CommandSourceStack p_127953_, int p_127954_) {
      ServerLevel serverlevel = p_127953_.getLevel();
      GameTestRunner.clearMarkers(serverlevel);
      BlockPos blockpos = new BlockPos(p_127953_.getPosition().x, (double)p_127953_.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(p_127953_.getPosition())).getY(), p_127953_.getPosition().z);
      GameTestRunner.clearAllTests(serverlevel, blockpos, GameTestTicker.SINGLETON, Mth.clamp(p_127954_, 0, 1024));
      return 1;
   }

   private static int runTest(CommandSourceStack p_127979_, TestFunction p_127980_, int p_127981_) {
      ServerLevel serverlevel = p_127979_.getLevel();
      BlockPos blockpos = new BlockPos(p_127979_.getPosition());
      int i = p_127979_.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockpos).getY();
      BlockPos blockpos1 = new BlockPos(blockpos.getX(), i, blockpos.getZ() + 3);
      GameTestRunner.clearMarkers(serverlevel);
      runTestPreparation(p_127980_, serverlevel);
      Rotation rotation = StructureUtils.getRotationForRotationSteps(p_127981_);
      GameTestInfo gametestinfo = new GameTestInfo(p_127980_, rotation, serverlevel);
      GameTestRunner.runTest(gametestinfo, blockpos1, GameTestTicker.SINGLETON);
      return 1;
   }

   private static void runTestPreparation(TestFunction p_127994_, ServerLevel p_127995_) {
      Consumer<ServerLevel> consumer = GameTestRegistry.getBeforeBatchFunction(p_127994_.getBatchName());
      if (consumer != null) {
         consumer.accept(p_127995_);
      }

   }

   private static int runAllTests(CommandSourceStack p_127956_, int p_127957_, int p_127958_) {
      GameTestRunner.clearMarkers(p_127956_.getLevel());
      Collection<TestFunction> collection = GameTestRegistry.getAllTestFunctions();
      say(p_127956_, "Running all " + collection.size() + " tests...");
      GameTestRegistry.forgetFailedTests();
      runTests(p_127956_, collection, p_127957_, p_127958_);
      return 1;
   }

   private static int runAllTestsInClass(CommandSourceStack p_127963_, String p_127964_, int p_127965_, int p_127966_) {
      Collection<TestFunction> collection = GameTestRegistry.getTestFunctionsForClassName(p_127964_);
      GameTestRunner.clearMarkers(p_127963_.getLevel());
      say(p_127963_, "Running " + collection.size() + " tests from " + p_127964_ + "...");
      GameTestRegistry.forgetFailedTests();
      runTests(p_127963_, collection, p_127965_, p_127966_);
      return 1;
   }

   private static int runLastFailedTests(CommandSourceStack p_127983_, boolean p_127984_, int p_127985_, int p_127986_) {
      Collection<TestFunction> collection;
      if (p_127984_) {
         collection = GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
      } else {
         collection = GameTestRegistry.getLastFailedTests();
      }

      if (collection.isEmpty()) {
         say(p_127983_, "No failed tests to rerun");
         return 0;
      } else {
         GameTestRunner.clearMarkers(p_127983_.getLevel());
         say(p_127983_, "Rerunning " + collection.size() + " failed tests (" + (p_127984_ ? "only required tests" : "including optional tests") + ")");
         runTests(p_127983_, collection, p_127985_, p_127986_);
         return 1;
      }
   }

   private static void runTests(CommandSourceStack p_127974_, Collection<TestFunction> p_127975_, int p_127976_, int p_127977_) {
      BlockPos blockpos = new BlockPos(p_127974_.getPosition());
      BlockPos blockpos1 = new BlockPos(blockpos.getX(), p_127974_.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockpos).getY(), blockpos.getZ() + 3);
      ServerLevel serverlevel = p_127974_.getLevel();
      Rotation rotation = StructureUtils.getRotationForRotationSteps(p_127976_);
      Collection<GameTestInfo> collection = GameTestRunner.runTests(p_127975_, blockpos1, rotation, serverlevel, GameTestTicker.SINGLETON, p_127977_);
      MultipleTestTracker multipletesttracker = new MultipleTestTracker(collection);
      multipletesttracker.addListener(new TestCommand.TestSummaryDisplayer(serverlevel, multipletesttracker));
      multipletesttracker.addFailureListener((p_127992_) -> {
         GameTestRegistry.rememberFailedTest(p_127992_.getTestFunction());
      });
   }

   private static void say(CommandSourceStack p_128004_, String p_128005_) {
      p_128004_.sendSuccess(new TextComponent(p_128005_), false);
   }

   private static int exportNearestTestStructure(CommandSourceStack p_128009_) {
      BlockPos blockpos = new BlockPos(p_128009_.getPosition());
      ServerLevel serverlevel = p_128009_.getLevel();
      BlockPos blockpos1 = StructureUtils.findNearestStructureBlock(blockpos, 15, serverlevel);
      if (blockpos1 == null) {
         say(serverlevel, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
         return 0;
      } else {
         StructureBlockEntity structureblockentity = (StructureBlockEntity)serverlevel.getBlockEntity(blockpos1);
         String s = structureblockentity.getStructurePath();
         return exportTestStructure(p_128009_, s);
      }
   }

   private static int exportTestStructure(CommandSourceStack p_128011_, String p_128012_) {
      Path path = Paths.get(StructureUtils.testStructuresDir);
      ResourceLocation resourcelocation = new ResourceLocation("minecraft", p_128012_);
      Path path1 = p_128011_.getLevel().getStructureManager().createPathToStructure(resourcelocation, ".nbt");
      Path path2 = NbtToSnbt.convertStructure(path1, p_128012_, path);
      if (path2 == null) {
         say(p_128011_, "Failed to export " + path1);
         return 1;
      } else {
         try {
            Files.createDirectories(path2.getParent());
         } catch (IOException ioexception) {
            say(p_128011_, "Could not create folder " + path2.getParent());
            ioexception.printStackTrace();
            return 1;
         }

         say(p_128011_, "Exported " + p_128012_ + " to " + path2.toAbsolutePath());
         return 0;
      }
   }

   private static int importTestStructure(CommandSourceStack p_128016_, String p_128017_) {
      Path path = Paths.get(StructureUtils.testStructuresDir, p_128017_ + ".snbt");
      ResourceLocation resourcelocation = new ResourceLocation("minecraft", p_128017_);
      Path path1 = p_128016_.getLevel().getStructureManager().createPathToStructure(resourcelocation, ".nbt");

      try {
         BufferedReader bufferedreader = Files.newBufferedReader(path);
         String s = IOUtils.toString((Reader)bufferedreader);
         Files.createDirectories(path1.getParent());
         OutputStream outputstream = Files.newOutputStream(path1);

         try {
            NbtIo.writeCompressed(NbtUtils.snbtToStructure(s), outputstream);
         } catch (Throwable throwable1) {
            if (outputstream != null) {
               try {
                  outputstream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (outputstream != null) {
            outputstream.close();
         }

         say(p_128016_, "Imported to " + path1.toAbsolutePath());
         return 0;
      } catch (CommandSyntaxException | IOException ioexception) {
         System.err.println("Failed to load structure " + p_128017_);
         ioexception.printStackTrace();
         return 1;
      }
   }

   private static void say(ServerLevel p_127934_, String p_127935_, ChatFormatting p_127936_) {
      p_127934_.getPlayers((p_127945_) -> {
         return true;
      }).forEach((p_127990_) -> {
         p_127990_.sendMessage(new TextComponent(p_127936_ + p_127935_), Util.NIL_UUID);
      });
   }

   static class TestSummaryDisplayer implements GameTestListener {
      private final ServerLevel level;
      private final MultipleTestTracker tracker;

      public TestSummaryDisplayer(ServerLevel p_128061_, MultipleTestTracker p_128062_) {
         this.level = p_128061_;
         this.tracker = p_128062_;
      }

      public void testStructureLoaded(GameTestInfo p_128064_) {
      }

      public void testPassed(GameTestInfo p_177797_) {
         TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
      }

      public void testFailed(GameTestInfo p_128066_) {
         TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
      }
   }
}