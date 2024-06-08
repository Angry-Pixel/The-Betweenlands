package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

@Name("minecraft.ChunkGeneration")
@Label("Chunk Generation")
@Category({"Minecraft", "World Generation"})
@StackTrace(false)
@Enabled(false)
@DontObfuscate
public class ChunkGenerationEvent extends Event {
   public static final String EVENT_NAME = "minecraft.ChunkGeneration";
   public static final EventType TYPE = EventType.getEventType(ChunkGenerationEvent.class);
   @Name("worldPosX")
   @Label("First Block X World Position")
   public final int worldPosX;
   @Name("worldPosZ")
   @Label("First Block Z World Position")
   public final int worldPosZ;
   @Name("chunkPosX")
   @Label("Chunk X Position")
   public final int chunkPosX;
   @Name("chunkPosZ")
   @Label("Chunk Z Position")
   public final int chunkPosZ;
   @Name("status")
   @Label("Status")
   public final String targetStatus;
   @Name("level")
   @Label("Level")
   public final String level;

   public ChunkGenerationEvent(ChunkPos p_195543_, ResourceKey<Level> p_195544_, String p_195545_) {
      this.targetStatus = p_195545_;
      this.level = p_195544_.toString();
      this.chunkPosX = p_195543_.x;
      this.chunkPosZ = p_195543_.z;
      this.worldPosX = p_195543_.getMinBlockX();
      this.worldPosZ = p_195543_.getMinBlockZ();
   }

   public static class Fields {
      public static final String WORLD_POS_X = "worldPosX";
      public static final String WORLD_POS_Z = "worldPosZ";
      public static final String CHUNK_POS_X = "chunkPosX";
      public static final String CHUNK_POS_Z = "chunkPosZ";
      public static final String STATUS = "status";
      public static final String LEVEL = "level";

      private Fields() {
      }
   }
}