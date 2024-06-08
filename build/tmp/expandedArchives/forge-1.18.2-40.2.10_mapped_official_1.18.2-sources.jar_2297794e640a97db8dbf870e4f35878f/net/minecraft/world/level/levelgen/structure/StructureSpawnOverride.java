package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType boundingBox, WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
   public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create((p_210051_) -> {
      return p_210051_.group(StructureSpawnOverride.BoundingBoxType.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply(p_210051_, StructureSpawnOverride::new);
   });

   public static enum BoundingBoxType implements StringRepresentable {
      PIECE("piece"),
      STRUCTURE("full");

      public static final StructureSpawnOverride.BoundingBoxType[] VALUES = values();
      public static final Codec<StructureSpawnOverride.BoundingBoxType> CODEC = StringRepresentable.fromEnum(() -> {
         return VALUES;
      }, StructureSpawnOverride.BoundingBoxType::byName);
      private final String id;

      private BoundingBoxType(String p_210067_) {
         this.id = p_210067_;
      }

      public String getSerializedName() {
         return this.id;
      }

      @Nullable
      public static StructureSpawnOverride.BoundingBoxType byName(@Nullable String p_210070_) {
         if (p_210070_ == null) {
            return null;
         } else {
            for(StructureSpawnOverride.BoundingBoxType structurespawnoverride$boundingboxtype : VALUES) {
               if (structurespawnoverride$boundingboxtype.id.equals(p_210070_)) {
                  return structurespawnoverride$boundingboxtype;
               }
            }

            return null;
         }
      }
   }
}