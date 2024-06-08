package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;

public class GameTestTicker {
   public static final GameTestTicker SINGLETON = new GameTestTicker();
   private final Collection<GameTestInfo> testInfos = Lists.newCopyOnWriteArrayList();

   public void add(GameTestInfo p_127789_) {
      this.testInfos.add(p_127789_);
   }

   public void clear() {
      this.testInfos.clear();
   }

   public void tick() {
      this.testInfos.forEach(GameTestInfo::tick);
      this.testInfos.removeIf(GameTestInfo::isDone);
   }
}