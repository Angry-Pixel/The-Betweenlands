package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class DyeItemRenameFix {
   public static final Map<String, String> RENAMED_IDS = ImmutableMap.<String, String>builder().put("minecraft:cactus_green", "minecraft:green_dye").put("minecraft:rose_red", "minecraft:red_dye").put("minecraft:dandelion_yellow", "minecraft:yellow_dye").build();
}