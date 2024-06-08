package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;

public class BlockEntityIdFix extends DataFix {
   private static final Map<String, String> ID_MAP = DataFixUtils.make(Maps.newHashMap(), (p_14839_) -> {
      p_14839_.put("Airportal", "minecraft:end_portal");
      p_14839_.put("Banner", "minecraft:banner");
      p_14839_.put("Beacon", "minecraft:beacon");
      p_14839_.put("Cauldron", "minecraft:brewing_stand");
      p_14839_.put("Chest", "minecraft:chest");
      p_14839_.put("Comparator", "minecraft:comparator");
      p_14839_.put("Control", "minecraft:command_block");
      p_14839_.put("DLDetector", "minecraft:daylight_detector");
      p_14839_.put("Dropper", "minecraft:dropper");
      p_14839_.put("EnchantTable", "minecraft:enchanting_table");
      p_14839_.put("EndGateway", "minecraft:end_gateway");
      p_14839_.put("EnderChest", "minecraft:ender_chest");
      p_14839_.put("FlowerPot", "minecraft:flower_pot");
      p_14839_.put("Furnace", "minecraft:furnace");
      p_14839_.put("Hopper", "minecraft:hopper");
      p_14839_.put("MobSpawner", "minecraft:mob_spawner");
      p_14839_.put("Music", "minecraft:noteblock");
      p_14839_.put("Piston", "minecraft:piston");
      p_14839_.put("RecordPlayer", "minecraft:jukebox");
      p_14839_.put("Sign", "minecraft:sign");
      p_14839_.put("Skull", "minecraft:skull");
      p_14839_.put("Structure", "minecraft:structure_block");
      p_14839_.put("Trap", "minecraft:dispenser");
   });

   public BlockEntityIdFix(Schema p_14830_, boolean p_14831_) {
      super(p_14830_, p_14831_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
      Type<?> type1 = this.getOutputSchema().getType(References.ITEM_STACK);
      TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      TaggedChoiceType<String> taggedchoicetype1 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
      return TypeRewriteRule.seq(this.convertUnchecked("item stack block entity name hook converter", type, type1), this.fixTypeEverywhere("BlockEntityIdFix", taggedchoicetype, taggedchoicetype1, (p_14835_) -> {
         return (p_145135_) -> {
            return p_145135_.mapFirst((p_145137_) -> {
               return ID_MAP.getOrDefault(p_145137_, p_145137_);
            });
         };
      }));
   }
}