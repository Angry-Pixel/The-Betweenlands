package net.minecraft.data.structures;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;

public class StructureUpdater implements SnbtToNbt.Filter {
   private static final Logger LOGGER = LogUtils.getLogger();

   public CompoundTag apply(String p_126503_, CompoundTag p_126504_) {
      return p_126503_.startsWith("data/minecraft/structures/") ? update(p_126503_, p_126504_) : p_126504_;
   }

   public static CompoundTag update(String p_176823_, CompoundTag p_176824_) {
      return updateStructure(p_176823_, patchVersion(p_176824_));
   }

   private static CompoundTag patchVersion(CompoundTag p_126506_) {
      if (!p_126506_.contains("DataVersion", 99)) {
         p_126506_.putInt("DataVersion", 500);
      }

      return p_126506_;
   }

   private static CompoundTag updateStructure(String p_126508_, CompoundTag p_126509_) {
      StructureTemplate structuretemplate = new StructureTemplate();
      int i = p_126509_.getInt("DataVersion");
      int j = 2965;
      if (i < 2965) {
         LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 2965, p_126508_);
      }

      CompoundTag compoundtag = NbtUtils.update(DataFixers.getDataFixer(), DataFixTypes.STRUCTURE, p_126509_, i);
      structuretemplate.load(compoundtag);
      return structuretemplate.save(new CompoundTag());
   }
}