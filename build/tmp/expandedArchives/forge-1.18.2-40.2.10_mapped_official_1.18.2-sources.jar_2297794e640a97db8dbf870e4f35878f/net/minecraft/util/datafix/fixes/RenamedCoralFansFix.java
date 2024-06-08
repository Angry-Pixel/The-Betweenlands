package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class RenamedCoralFansFix {
   public static final Map<String, String> RENAMED_IDS = ImmutableMap.<String, String>builder().put("minecraft:tube_coral_fan", "minecraft:tube_coral_wall_fan").put("minecraft:brain_coral_fan", "minecraft:brain_coral_wall_fan").put("minecraft:bubble_coral_fan", "minecraft:bubble_coral_wall_fan").put("minecraft:fire_coral_fan", "minecraft:fire_coral_wall_fan").put("minecraft:horn_coral_fan", "minecraft:horn_coral_wall_fan").build();
}