package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V100 extends Schema {
   public V100(int p_17328_, Schema p_17329_) {
      super(p_17328_, p_17329_);
   }

   protected static TypeTemplate equipment(Schema p_17331_) {
      return DSL.optionalFields("ArmorItems", DSL.list(References.ITEM_STACK.in(p_17331_)), "HandItems", DSL.list(References.ITEM_STACK.in(p_17331_)));
   }

   protected static void registerMob(Schema p_17336_, Map<String, Supplier<TypeTemplate>> p_17337_, String p_17338_) {
      p_17336_.register(p_17337_, p_17338_, () -> {
         return equipment(p_17336_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_17350_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_17350_);
      registerMob(p_17350_, map, "ArmorStand");
      registerMob(p_17350_, map, "Creeper");
      registerMob(p_17350_, map, "Skeleton");
      registerMob(p_17350_, map, "Spider");
      registerMob(p_17350_, map, "Giant");
      registerMob(p_17350_, map, "Zombie");
      registerMob(p_17350_, map, "Slime");
      registerMob(p_17350_, map, "Ghast");
      registerMob(p_17350_, map, "PigZombie");
      p_17350_.register(map, "Enderman", (p_17348_) -> {
         return DSL.optionalFields("carried", References.BLOCK_NAME.in(p_17350_), equipment(p_17350_));
      });
      registerMob(p_17350_, map, "CaveSpider");
      registerMob(p_17350_, map, "Silverfish");
      registerMob(p_17350_, map, "Blaze");
      registerMob(p_17350_, map, "LavaSlime");
      registerMob(p_17350_, map, "EnderDragon");
      registerMob(p_17350_, map, "WitherBoss");
      registerMob(p_17350_, map, "Bat");
      registerMob(p_17350_, map, "Witch");
      registerMob(p_17350_, map, "Endermite");
      registerMob(p_17350_, map, "Guardian");
      registerMob(p_17350_, map, "Pig");
      registerMob(p_17350_, map, "Sheep");
      registerMob(p_17350_, map, "Cow");
      registerMob(p_17350_, map, "Chicken");
      registerMob(p_17350_, map, "Squid");
      registerMob(p_17350_, map, "Wolf");
      registerMob(p_17350_, map, "MushroomCow");
      registerMob(p_17350_, map, "SnowMan");
      registerMob(p_17350_, map, "Ozelot");
      registerMob(p_17350_, map, "VillagerGolem");
      p_17350_.register(map, "EntityHorse", (p_17343_) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_17350_)), "ArmorItem", References.ITEM_STACK.in(p_17350_), "SaddleItem", References.ITEM_STACK.in(p_17350_), equipment(p_17350_));
      });
      registerMob(p_17350_, map, "Rabbit");
      p_17350_.register(map, "Villager", (p_17334_) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(p_17350_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", References.ITEM_STACK.in(p_17350_), "buyB", References.ITEM_STACK.in(p_17350_), "sell", References.ITEM_STACK.in(p_17350_)))), equipment(p_17350_));
      });
      registerMob(p_17350_, map, "Shulker");
      p_17350_.registerSimple(map, "AreaEffectCloud");
      p_17350_.registerSimple(map, "ShulkerBullet");
      return map;
   }

   public void registerTypes(Schema p_17352_, Map<String, Supplier<TypeTemplate>> p_17353_, Map<String, Supplier<TypeTemplate>> p_17354_) {
      super.registerTypes(p_17352_, p_17353_, p_17354_);
      p_17352_.registerType(false, References.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", References.ENTITY_TREE.in(p_17352_))), "blocks", DSL.list(DSL.optionalFields("nbt", References.BLOCK_ENTITY.in(p_17352_))), "palette", DSL.list(References.BLOCK_STATE.in(p_17352_)));
      });
      p_17352_.registerType(false, References.BLOCK_STATE, DSL::remainder);
   }
}