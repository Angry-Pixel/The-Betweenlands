package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class GameTestRunner {
   private static final int MAX_TESTS_PER_BATCH = 100;
   public static final int PADDING_AROUND_EACH_STRUCTURE = 2;
   public static final int SPACE_BETWEEN_COLUMNS = 5;
   public static final int SPACE_BETWEEN_ROWS = 6;
   public static final int DEFAULT_TESTS_PER_ROW = 8;

   public static void runTest(GameTestInfo p_127743_, BlockPos p_127744_, GameTestTicker p_127745_) {
      p_127743_.startExecution();
      p_127745_.add(p_127743_);
      p_127743_.addListener(new ReportGameListener(p_127743_, p_127745_, p_127744_));
      p_127743_.spawnStructure(p_127744_, 2);
   }

   public static Collection<GameTestInfo> runTestBatches(Collection<GameTestBatch> p_127727_, BlockPos p_127728_, Rotation p_127729_, ServerLevel p_127730_, GameTestTicker p_127731_, int p_127732_) {
      GameTestBatchRunner gametestbatchrunner = new GameTestBatchRunner(p_127727_, p_127728_, p_127729_, p_127730_, p_127731_, p_127732_);
      gametestbatchrunner.start();
      return gametestbatchrunner.getTestInfos();
   }

   public static Collection<GameTestInfo> runTests(Collection<TestFunction> p_127753_, BlockPos p_127754_, Rotation p_127755_, ServerLevel p_127756_, GameTestTicker p_127757_, int p_127758_) {
      return runTestBatches(groupTestsIntoBatches(p_127753_), p_127754_, p_127755_, p_127756_, p_127757_, p_127758_);
   }

   public static Collection<GameTestBatch> groupTestsIntoBatches(Collection<TestFunction> p_127725_) {
      Map<String, List<TestFunction>> map = p_127725_.stream().collect(Collectors.groupingBy(TestFunction::getBatchName));
      return map.entrySet().stream().flatMap((p_177537_) -> {
         String s = p_177537_.getKey();
         Consumer<ServerLevel> consumer = GameTestRegistry.getBeforeBatchFunction(s);
         Consumer<ServerLevel> consumer1 = GameTestRegistry.getAfterBatchFunction(s);
         MutableInt mutableint = new MutableInt();
         Collection<TestFunction> collection = p_177537_.getValue();
         return Streams.stream(Iterables.partition(collection, 100)).map((p_177535_) -> {
            return new GameTestBatch(s + ":" + mutableint.incrementAndGet(), ImmutableList.copyOf(p_177535_), consumer, consumer1);
         });
      }).collect(ImmutableList.toImmutableList());
   }

   public static void clearAllTests(ServerLevel p_127695_, BlockPos p_127696_, GameTestTicker p_127697_, int p_127698_) {
      p_127697_.clear();
      BlockPos blockpos = p_127696_.offset(-p_127698_, 0, -p_127698_);
      BlockPos blockpos1 = p_127696_.offset(p_127698_, 0, p_127698_);
      BlockPos.betweenClosedStream(blockpos, blockpos1).filter((p_177540_) -> {
         return p_127695_.getBlockState(p_177540_).is(Blocks.STRUCTURE_BLOCK);
      }).forEach((p_177529_) -> {
         StructureBlockEntity structureblockentity = (StructureBlockEntity)p_127695_.getBlockEntity(p_177529_);
         BlockPos blockpos2 = structureblockentity.getBlockPos();
         BoundingBox boundingbox = StructureUtils.getStructureBoundingBox(structureblockentity);
         StructureUtils.clearSpaceForStructure(boundingbox, blockpos2.getY(), p_127695_);
      });
   }

   public static void clearMarkers(ServerLevel p_127686_) {
      DebugPackets.sendGameTestClearPacket(p_127686_);
   }
}