package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestListener {
   private final GameTestInfo originalTestInfo;
   private final GameTestTicker testTicker;
   private final BlockPos structurePos;
   int attempts;
   int successes;

   public ReportGameListener(GameTestInfo p_177692_, GameTestTicker p_177693_, BlockPos p_177694_) {
      this.originalTestInfo = p_177692_;
      this.testTicker = p_177693_;
      this.structurePos = p_177694_;
      this.attempts = 0;
      this.successes = 0;
   }

   public void testStructureLoaded(GameTestInfo p_177718_) {
      spawnBeacon(this.originalTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
      ++this.attempts;
   }

   public void testPassed(GameTestInfo p_177729_) {
      ++this.successes;
      if (!p_177729_.isFlaky()) {
         reportPassed(p_177729_, p_177729_.getTestName() + " passed!");
      } else {
         if (this.successes >= p_177729_.requiredSuccesses()) {
            reportPassed(p_177729_, p_177729_ + " passed " + this.successes + " times of " + this.attempts + " attempts.");
         } else {
            say(this.originalTestInfo.getLevel(), ChatFormatting.GREEN, "Flaky test " + this.originalTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
            this.rerunTest();
         }

      }
   }

   public void testFailed(GameTestInfo p_177737_) {
      if (!p_177737_.isFlaky()) {
         reportFailure(p_177737_, p_177737_.getError());
      } else {
         TestFunction testfunction = this.originalTestInfo.getTestFunction();
         String s = "Flaky test " + this.originalTestInfo + " failed, attempt: " + this.attempts + "/" + testfunction.getMaxAttempts();
         if (testfunction.getRequiredSuccesses() > 1) {
            s = s + ", successes: " + this.successes + " (" + testfunction.getRequiredSuccesses() + " required)";
         }

         say(this.originalTestInfo.getLevel(), ChatFormatting.YELLOW, s);
         if (p_177737_.maxAttempts() - this.attempts + this.successes >= p_177737_.requiredSuccesses()) {
            this.rerunTest();
         } else {
            reportFailure(p_177737_, new ExhaustedAttemptsException(this.attempts, this.successes, p_177737_));
         }

      }
   }

   public static void reportPassed(GameTestInfo p_177723_, String p_177724_) {
      spawnBeacon(p_177723_, Blocks.LIME_STAINED_GLASS);
      visualizePassedTest(p_177723_, p_177724_);
   }

   private static void visualizePassedTest(GameTestInfo p_177731_, String p_177732_) {
      say(p_177731_.getLevel(), ChatFormatting.GREEN, p_177732_);
      GlobalTestReporter.onTestSuccess(p_177731_);
   }

   protected static void reportFailure(GameTestInfo p_177726_, Throwable p_177727_) {
      spawnBeacon(p_177726_, p_177726_.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
      spawnLectern(p_177726_, Util.describeError(p_177727_));
      visualizeFailedTest(p_177726_, p_177727_);
   }

   protected static void visualizeFailedTest(GameTestInfo p_177734_, Throwable p_177735_) {
      String s = p_177735_.getMessage() + (p_177735_.getCause() == null ? "" : " cause: " + Util.describeError(p_177735_.getCause()));
      String s1 = (p_177734_.isRequired() ? "" : "(optional) ") + p_177734_.getTestName() + " failed! " + s;
      say(p_177734_.getLevel(), p_177734_.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, s1);
      Throwable throwable = MoreObjects.firstNonNull(ExceptionUtils.getRootCause(p_177735_), p_177735_);
      if (throwable instanceof GameTestAssertPosException) {
         GameTestAssertPosException gametestassertposexception = (GameTestAssertPosException)throwable;
         showRedBox(p_177734_.getLevel(), gametestassertposexception.getAbsolutePos(), gametestassertposexception.getMessageToShowAtBlock());
      }

      GlobalTestReporter.onTestFailed(p_177734_);
   }

   private void rerunTest() {
      this.originalTestInfo.clearStructure();
      GameTestInfo gametestinfo = new GameTestInfo(this.originalTestInfo.getTestFunction(), this.originalTestInfo.getRotation(), this.originalTestInfo.getLevel());
      gametestinfo.startExecution();
      this.testTicker.add(gametestinfo);
      gametestinfo.addListener(this);
      gametestinfo.spawnStructure(this.structurePos, 2);
   }

   protected static void spawnBeacon(GameTestInfo p_177720_, Block p_177721_) {
      ServerLevel serverlevel = p_177720_.getLevel();
      BlockPos blockpos = p_177720_.getStructureBlockPos();
      BlockPos blockpos1 = new BlockPos(-1, -1, -1);
      BlockPos blockpos2 = StructureTemplate.transform(blockpos.offset(blockpos1), Mirror.NONE, p_177720_.getRotation(), blockpos);
      serverlevel.setBlockAndUpdate(blockpos2, Blocks.BEACON.defaultBlockState().rotate(p_177720_.getRotation()));
      BlockPos blockpos3 = blockpos2.offset(0, 1, 0);
      serverlevel.setBlockAndUpdate(blockpos3, p_177721_.defaultBlockState());

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos4 = blockpos2.offset(i, -1, j);
            serverlevel.setBlockAndUpdate(blockpos4, Blocks.IRON_BLOCK.defaultBlockState());
         }
      }

   }

   private static void spawnLectern(GameTestInfo p_177739_, String p_177740_) {
      ServerLevel serverlevel = p_177739_.getLevel();
      BlockPos blockpos = p_177739_.getStructureBlockPos();
      BlockPos blockpos1 = new BlockPos(-1, 1, -1);
      BlockPos blockpos2 = StructureTemplate.transform(blockpos.offset(blockpos1), Mirror.NONE, p_177739_.getRotation(), blockpos);
      serverlevel.setBlockAndUpdate(blockpos2, Blocks.LECTERN.defaultBlockState().rotate(p_177739_.getRotation()));
      BlockState blockstate = serverlevel.getBlockState(blockpos2);
      ItemStack itemstack = createBook(p_177739_.getTestName(), p_177739_.isRequired(), p_177740_);
      LecternBlock.tryPlaceBook((Player)null, serverlevel, blockpos2, blockstate, itemstack);
   }

   private static ItemStack createBook(String p_177711_, boolean p_177712_, String p_177713_) {
      ItemStack itemstack = new ItemStack(Items.WRITABLE_BOOK);
      ListTag listtag = new ListTag();
      StringBuffer stringbuffer = new StringBuffer();
      Arrays.stream(p_177711_.split("\\.")).forEach((p_177716_) -> {
         stringbuffer.append(p_177716_).append('\n');
      });
      if (!p_177712_) {
         stringbuffer.append("(optional)\n");
      }

      stringbuffer.append("-------------------\n");
      listtag.add(StringTag.valueOf(stringbuffer + p_177713_));
      itemstack.addTagElement("pages", listtag);
      return itemstack;
   }

   protected static void say(ServerLevel p_177701_, ChatFormatting p_177702_, String p_177703_) {
      p_177701_.getPlayers((p_177705_) -> {
         return true;
      }).forEach((p_177709_) -> {
         p_177709_.sendMessage((new TextComponent(p_177703_)).withStyle(p_177702_), Util.NIL_UUID);
      });
   }

   private static void showRedBox(ServerLevel p_177697_, BlockPos p_177698_, String p_177699_) {
      DebugPackets.sendGameTestAddMarker(p_177697_, p_177698_, p_177699_, -2130771968, Integer.MAX_VALUE);
   }
}