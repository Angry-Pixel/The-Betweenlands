package net.minecraft.world.level.storage;

public class DataVersion {
   private final int version;
   private final String series;
   public static String MAIN_SERIES = "main";

   public DataVersion(int p_192998_) {
      this(p_192998_, MAIN_SERIES);
   }

   public DataVersion(int p_193000_, String p_193001_) {
      this.version = p_193000_;
      this.series = p_193001_;
   }

   public boolean isSideSeries() {
      return !this.series.equals(MAIN_SERIES);
   }

   public String getSeries() {
      return this.series;
   }

   public int getVersion() {
      return this.version;
   }

   public boolean isCompatible(DataVersion p_193004_) {
      return this.getSeries().equals(p_193004_.getSeries());
   }
}