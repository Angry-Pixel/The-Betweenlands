package net.minecraft.world.level.storage;

public class LevelResource {
   public static final LevelResource PLAYER_ADVANCEMENTS_DIR = new LevelResource("advancements");
   public static final LevelResource PLAYER_STATS_DIR = new LevelResource("stats");
   public static final LevelResource PLAYER_DATA_DIR = new LevelResource("playerdata");
   public static final LevelResource PLAYER_OLD_DATA_DIR = new LevelResource("players");
   public static final LevelResource LEVEL_DATA_FILE = new LevelResource("level.dat");
   public static final LevelResource GENERATED_DIR = new LevelResource("generated");
   public static final LevelResource DATAPACK_DIR = new LevelResource("datapacks");
   public static final LevelResource MAP_RESOURCE_FILE = new LevelResource("resources.zip");
   public static final LevelResource ROOT = new LevelResource(".");
   private final String id;

   public LevelResource(String p_78186_) {
      this.id = p_78186_;
   }

   public String getId() {
      return this.id;
   }

   public String toString() {
      return "/" + this.id;
   }
}