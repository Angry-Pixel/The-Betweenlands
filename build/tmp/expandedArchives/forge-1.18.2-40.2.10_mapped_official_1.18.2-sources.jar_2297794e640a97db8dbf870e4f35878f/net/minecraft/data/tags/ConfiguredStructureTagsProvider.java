package net.minecraft.data.tags;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ConfiguredStructureTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class ConfiguredStructureTagsProvider extends TagsProvider<ConfiguredStructureFeature<?, ?>> {
   /** @deprecated Forge: Use the {@link #ConfiguredStructureTagsProvider(DataGenerator, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant} */
   @Deprecated
   public ConfiguredStructureTagsProvider(DataGenerator p_211098_) {
      super(p_211098_, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE);
   }
   public ConfiguredStructureTagsProvider(DataGenerator p_211098_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_211098_, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, modId, existingFileHelper);
   }

   protected void addTags() {
      this.tag(ConfiguredStructureTags.VILLAGE).add(BuiltinStructures.VILLAGE_PLAINS).add(BuiltinStructures.VILLAGE_DESERT).add(BuiltinStructures.VILLAGE_SAVANNA).add(BuiltinStructures.VILLAGE_SNOWY).add(BuiltinStructures.VILLAGE_TAIGA);
      this.tag(ConfiguredStructureTags.MINESHAFT).add(BuiltinStructures.MINESHAFT).add(BuiltinStructures.MINESHAFT_MESA);
      this.tag(ConfiguredStructureTags.OCEAN_RUIN).add(BuiltinStructures.OCEAN_RUIN_COLD).add(BuiltinStructures.OCEAN_RUIN_WARM);
      this.tag(ConfiguredStructureTags.SHIPWRECK).add(BuiltinStructures.SHIPWRECK).add(BuiltinStructures.SHIPWRECK_BEACHED);
      this.tag(ConfiguredStructureTags.RUINED_PORTAL).add(BuiltinStructures.RUINED_PORTAL_DESERT).add(BuiltinStructures.RUINED_PORTAL_JUNGLE).add(BuiltinStructures.RUINED_PORTAL_MOUNTAIN).add(BuiltinStructures.RUINED_PORTAL_NETHER).add(BuiltinStructures.RUINED_PORTAL_OCEAN).add(BuiltinStructures.RUINED_PORTAL_STANDARD).add(BuiltinStructures.RUINED_PORTAL_SWAMP);
      this.tag(ConfiguredStructureTags.EYE_OF_ENDER_LOCATED).add(BuiltinStructures.STRONGHOLD);
      this.tag(ConfiguredStructureTags.DOLPHIN_LOCATED).addTag(ConfiguredStructureTags.OCEAN_RUIN).addTag(ConfiguredStructureTags.SHIPWRECK);
      this.tag(ConfiguredStructureTags.ON_WOODLAND_EXPLORER_MAPS).add(BuiltinStructures.WOODLAND_MANSION);
      this.tag(ConfiguredStructureTags.ON_OCEAN_EXPLORER_MAPS).add(BuiltinStructures.OCEAN_MONUMENT);
      this.tag(ConfiguredStructureTags.ON_TREASURE_MAPS).add(BuiltinStructures.BURIED_TREASURE);
   }

   public String getName() {
      return "Configured Structure Feature Tags";
   }
}
