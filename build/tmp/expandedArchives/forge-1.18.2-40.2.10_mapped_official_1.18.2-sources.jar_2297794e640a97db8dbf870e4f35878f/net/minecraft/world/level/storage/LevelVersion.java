package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SharedConstants;

public class LevelVersion {
   private final int levelDataVersion;
   private final long lastPlayed;
   private final String minecraftVersionName;
   private final DataVersion minecraftVersion;
   private final boolean snapshot;

   private LevelVersion(int p_193023_, long p_193024_, String p_193025_, int p_193026_, String p_193027_, boolean p_193028_) {
      this.levelDataVersion = p_193023_;
      this.lastPlayed = p_193024_;
      this.minecraftVersionName = p_193025_;
      this.minecraftVersion = new DataVersion(p_193026_, p_193027_);
      this.snapshot = p_193028_;
   }

   public static LevelVersion parse(Dynamic<?> p_78391_) {
      int i = p_78391_.get("version").asInt(0);
      long j = p_78391_.get("LastPlayed").asLong(0L);
      OptionalDynamic<?> optionaldynamic = p_78391_.get("Version");
      return optionaldynamic.result().isPresent() ? new LevelVersion(i, j, optionaldynamic.get("Name").asString(SharedConstants.getCurrentVersion().getName()), optionaldynamic.get("Id").asInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()), optionaldynamic.get("Series").asString(DataVersion.MAIN_SERIES), optionaldynamic.get("Snapshot").asBoolean(!SharedConstants.getCurrentVersion().isStable())) : new LevelVersion(i, j, "", 0, DataVersion.MAIN_SERIES, false);
   }

   public int levelDataVersion() {
      return this.levelDataVersion;
   }

   public long lastPlayed() {
      return this.lastPlayed;
   }

   public String minecraftVersionName() {
      return this.minecraftVersionName;
   }

   public DataVersion minecraftVersion() {
      return this.minecraftVersion;
   }

   public boolean snapshot() {
      return this.snapshot;
   }
}