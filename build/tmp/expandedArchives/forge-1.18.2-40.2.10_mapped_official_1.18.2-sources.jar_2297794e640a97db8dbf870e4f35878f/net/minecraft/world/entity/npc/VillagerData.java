package net.minecraft.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;

public class VillagerData {
   public static final int MIN_VILLAGER_LEVEL = 1;
   public static final int MAX_VILLAGER_LEVEL = 5;
   private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
   public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create((p_35570_) -> {
      return p_35570_.group(Registry.VILLAGER_TYPE.byNameCodec().fieldOf("type").orElseGet(() -> {
         return VillagerType.PLAINS;
      }).forGetter((p_150024_) -> {
         return p_150024_.type;
      }), Registry.VILLAGER_PROFESSION.byNameCodec().fieldOf("profession").orElseGet(() -> {
         return VillagerProfession.NONE;
      }).forGetter((p_150022_) -> {
         return p_150022_.profession;
      }), Codec.INT.fieldOf("level").orElse(1).forGetter((p_150020_) -> {
         return p_150020_.level;
      })).apply(p_35570_, VillagerData::new);
   });
   private final VillagerType type;
   private final VillagerProfession profession;
   private final int level;

   public VillagerData(VillagerType p_35557_, VillagerProfession p_35558_, int p_35559_) {
      this.type = p_35557_;
      this.profession = p_35558_;
      this.level = Math.max(1, p_35559_);
   }

   public VillagerType getType() {
      return this.type;
   }

   public VillagerProfession getProfession() {
      return this.profession;
   }

   public int getLevel() {
      return this.level;
   }

   public VillagerData setType(VillagerType p_35568_) {
      return new VillagerData(p_35568_, this.profession, this.level);
   }

   public VillagerData setProfession(VillagerProfession p_35566_) {
      return new VillagerData(this.type, p_35566_, this.level);
   }

   public VillagerData setLevel(int p_35562_) {
      return new VillagerData(this.type, this.profession, p_35562_);
   }

   public static int getMinXpPerLevel(int p_35573_) {
      return canLevelUp(p_35573_) ? NEXT_LEVEL_XP_THRESHOLDS[p_35573_ - 1] : 0;
   }

   public static int getMaxXpPerLevel(int p_35578_) {
      return canLevelUp(p_35578_) ? NEXT_LEVEL_XP_THRESHOLDS[p_35578_] : 0;
   }

   public static boolean canLevelUp(int p_35583_) {
      return p_35583_ >= 1 && p_35583_ < 5;
   }
}