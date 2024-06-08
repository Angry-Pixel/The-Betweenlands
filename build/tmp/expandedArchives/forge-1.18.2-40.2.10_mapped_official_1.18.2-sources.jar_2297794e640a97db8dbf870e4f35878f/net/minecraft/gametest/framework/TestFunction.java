package net.minecraft.gametest.framework;

import java.util.function.Consumer;
import net.minecraft.world.level.block.Rotation;

public class TestFunction {
   private final String batchName;
   private final String testName;
   private final String structureName;
   private final boolean required;
   private final int maxAttempts;
   private final int requiredSuccesses;
   private final Consumer<GameTestHelper> function;
   private final int maxTicks;
   private final long setupTicks;
   private final Rotation rotation;

   public TestFunction(String p_177801_, String p_177802_, String p_177803_, int p_177804_, long p_177805_, boolean p_177806_, Consumer<GameTestHelper> p_177807_) {
      this(p_177801_, p_177802_, p_177803_, Rotation.NONE, p_177804_, p_177805_, p_177806_, 1, 1, p_177807_);
   }

   public TestFunction(String p_177820_, String p_177821_, String p_177822_, Rotation p_177823_, int p_177824_, long p_177825_, boolean p_177826_, Consumer<GameTestHelper> p_177827_) {
      this(p_177820_, p_177821_, p_177822_, p_177823_, p_177824_, p_177825_, p_177826_, 1, 1, p_177827_);
   }

   public TestFunction(String p_177809_, String p_177810_, String p_177811_, Rotation p_177812_, int p_177813_, long p_177814_, boolean p_177815_, int p_177816_, int p_177817_, Consumer<GameTestHelper> p_177818_) {
      this.batchName = p_177809_;
      this.testName = p_177810_;
      this.structureName = p_177811_;
      this.rotation = p_177812_;
      this.maxTicks = p_177813_;
      this.required = p_177815_;
      this.requiredSuccesses = p_177816_;
      this.maxAttempts = p_177817_;
      this.function = p_177818_;
      this.setupTicks = p_177814_;
   }

   public void run(GameTestHelper p_128077_) {
      this.function.accept(p_128077_);
   }

   public String getTestName() {
      return this.testName;
   }

   public String getStructureName() {
      return this.structureName;
   }

   public String toString() {
      return this.testName;
   }

   public int getMaxTicks() {
      return this.maxTicks;
   }

   public boolean isRequired() {
      return this.required;
   }

   public String getBatchName() {
      return this.batchName;
   }

   public long getSetupTicks() {
      return this.setupTicks;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public boolean isFlaky() {
      return this.maxAttempts > 1;
   }

   public int getMaxAttempts() {
      return this.maxAttempts;
   }

   public int getRequiredSuccesses() {
      return this.requiredSuccesses;
   }
}