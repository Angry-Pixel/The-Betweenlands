package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;

public class GameTestBatch {
   public static final String DEFAULT_BATCH_NAME = "defaultBatch";
   private final String name;
   private final Collection<TestFunction> testFunctions;
   @Nullable
   private final Consumer<ServerLevel> beforeBatchFunction;
   @Nullable
   private final Consumer<ServerLevel> afterBatchFunction;

   public GameTestBatch(String p_177059_, Collection<TestFunction> p_177060_, @Nullable Consumer<ServerLevel> p_177061_, @Nullable Consumer<ServerLevel> p_177062_) {
      if (p_177060_.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
      } else {
         this.name = p_177059_;
         this.testFunctions = p_177060_;
         this.beforeBatchFunction = p_177061_;
         this.afterBatchFunction = p_177062_;
      }
   }

   public String getName() {
      return this.name;
   }

   public Collection<TestFunction> getTestFunctions() {
      return this.testFunctions;
   }

   public void runBeforeBatchFunction(ServerLevel p_127548_) {
      if (this.beforeBatchFunction != null) {
         this.beforeBatchFunction.accept(p_127548_);
      }

   }

   public void runAfterBatchFunction(ServerLevel p_177064_) {
      if (this.afterBatchFunction != null) {
         this.afterBatchFunction.accept(p_177064_);
      }

   }
}