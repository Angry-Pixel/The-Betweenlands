package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import org.slf4j.Logger;

public class V99 extends Schema {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final Map<String, String> ITEM_TO_BLOCKENTITY = DataFixUtils.make(Maps.newHashMap(), (p_145919_) -> {
      p_145919_.put("minecraft:furnace", "Furnace");
      p_145919_.put("minecraft:lit_furnace", "Furnace");
      p_145919_.put("minecraft:chest", "Chest");
      p_145919_.put("minecraft:trapped_chest", "Chest");
      p_145919_.put("minecraft:ender_chest", "EnderChest");
      p_145919_.put("minecraft:jukebox", "RecordPlayer");
      p_145919_.put("minecraft:dispenser", "Trap");
      p_145919_.put("minecraft:dropper", "Dropper");
      p_145919_.put("minecraft:sign", "Sign");
      p_145919_.put("minecraft:mob_spawner", "MobSpawner");
      p_145919_.put("minecraft:noteblock", "Music");
      p_145919_.put("minecraft:brewing_stand", "Cauldron");
      p_145919_.put("minecraft:enhanting_table", "EnchantTable");
      p_145919_.put("minecraft:command_block", "CommandBlock");
      p_145919_.put("minecraft:beacon", "Beacon");
      p_145919_.put("minecraft:skull", "Skull");
      p_145919_.put("minecraft:daylight_detector", "DLDetector");
      p_145919_.put("minecraft:hopper", "Hopper");
      p_145919_.put("minecraft:banner", "Banner");
      p_145919_.put("minecraft:flower_pot", "FlowerPot");
      p_145919_.put("minecraft:repeating_command_block", "CommandBlock");
      p_145919_.put("minecraft:chain_command_block", "CommandBlock");
      p_145919_.put("minecraft:standing_sign", "Sign");
      p_145919_.put("minecraft:wall_sign", "Sign");
      p_145919_.put("minecraft:piston_head", "Piston");
      p_145919_.put("minecraft:daylight_detector_inverted", "DLDetector");
      p_145919_.put("minecraft:unpowered_comparator", "Comparator");
      p_145919_.put("minecraft:powered_comparator", "Comparator");
      p_145919_.put("minecraft:wall_banner", "Banner");
      p_145919_.put("minecraft:standing_banner", "Banner");
      p_145919_.put("minecraft:structure_block", "Structure");
      p_145919_.put("minecraft:end_portal", "Airportal");
      p_145919_.put("minecraft:end_gateway", "EndGateway");
      p_145919_.put("minecraft:shield", "Banner");
   });
   protected static final HookFunction ADD_NAMES = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_18312_, T p_18313_) {
         return V99.addNames(new Dynamic<>(p_18312_, p_18313_), V99.ITEM_TO_BLOCKENTITY, "ArmorStand");
      }
   };

   public V99(int p_18185_, Schema p_18186_) {
      super(p_18185_, p_18186_);
   }

   protected static TypeTemplate equipment(Schema p_18189_) {
      return DSL.optionalFields("Equipment", DSL.list(References.ITEM_STACK.in(p_18189_)));
   }

   protected static void registerMob(Schema p_18194_, Map<String, Supplier<TypeTemplate>> p_18195_, String p_18196_) {
      p_18194_.register(p_18195_, p_18196_, () -> {
         return equipment(p_18194_);
      });
   }

   protected static void registerThrowableProjectile(Schema p_18225_, Map<String, Supplier<TypeTemplate>> p_18226_, String p_18227_) {
      p_18225_.register(p_18226_, p_18227_, () -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18225_));
      });
   }

   protected static void registerMinecart(Schema p_18237_, Map<String, Supplier<TypeTemplate>> p_18238_, String p_18239_) {
      p_18237_.register(p_18238_, p_18239_, () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18237_));
      });
   }

   protected static void registerInventory(Schema p_18247_, Map<String, Supplier<TypeTemplate>> p_18248_, String p_18249_) {
      p_18247_.register(p_18248_, p_18249_, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_18247_)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_18305_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      p_18305_.register(map, "Item", (p_18301_) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(p_18305_));
      });
      p_18305_.registerSimple(map, "XPOrb");
      registerThrowableProjectile(p_18305_, map, "ThrownEgg");
      p_18305_.registerSimple(map, "LeashKnot");
      p_18305_.registerSimple(map, "Painting");
      p_18305_.register(map, "Arrow", (p_18298_) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18305_));
      });
      p_18305_.register(map, "TippedArrow", (p_18295_) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18305_));
      });
      p_18305_.register(map, "SpectralArrow", (p_18292_) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18305_));
      });
      registerThrowableProjectile(p_18305_, map, "Snowball");
      registerThrowableProjectile(p_18305_, map, "Fireball");
      registerThrowableProjectile(p_18305_, map, "SmallFireball");
      registerThrowableProjectile(p_18305_, map, "ThrownEnderpearl");
      p_18305_.registerSimple(map, "EyeOfEnderSignal");
      p_18305_.register(map, "ThrownPotion", (p_18289_) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18305_), "Potion", References.ITEM_STACK.in(p_18305_));
      });
      registerThrowableProjectile(p_18305_, map, "ThrownExpBottle");
      p_18305_.register(map, "ItemFrame", (p_18284_) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(p_18305_));
      });
      registerThrowableProjectile(p_18305_, map, "WitherSkull");
      p_18305_.registerSimple(map, "PrimedTnt");
      p_18305_.register(map, "FallingSand", (p_18279_) -> {
         return DSL.optionalFields("Block", References.BLOCK_NAME.in(p_18305_), "TileEntityData", References.BLOCK_ENTITY.in(p_18305_));
      });
      p_18305_.register(map, "FireworksRocketEntity", (p_18274_) -> {
         return DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(p_18305_));
      });
      p_18305_.registerSimple(map, "Boat");
      p_18305_.register(map, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18305_), "Items", DSL.list(References.ITEM_STACK.in(p_18305_)));
      });
      registerMinecart(p_18305_, map, "MinecartRideable");
      p_18305_.register(map, "MinecartChest", (p_18269_) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18305_), "Items", DSL.list(References.ITEM_STACK.in(p_18305_)));
      });
      registerMinecart(p_18305_, map, "MinecartFurnace");
      registerMinecart(p_18305_, map, "MinecartTNT");
      p_18305_.register(map, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18305_), References.UNTAGGED_SPAWNER.in(p_18305_));
      });
      p_18305_.register(map, "MinecartHopper", (p_18264_) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18305_), "Items", DSL.list(References.ITEM_STACK.in(p_18305_)));
      });
      registerMinecart(p_18305_, map, "MinecartCommandBlock");
      registerMob(p_18305_, map, "ArmorStand");
      registerMob(p_18305_, map, "Creeper");
      registerMob(p_18305_, map, "Skeleton");
      registerMob(p_18305_, map, "Spider");
      registerMob(p_18305_, map, "Giant");
      registerMob(p_18305_, map, "Zombie");
      registerMob(p_18305_, map, "Slime");
      registerMob(p_18305_, map, "Ghast");
      registerMob(p_18305_, map, "PigZombie");
      p_18305_.register(map, "Enderman", (p_18259_) -> {
         return DSL.optionalFields("carried", References.BLOCK_NAME.in(p_18305_), equipment(p_18305_));
      });
      registerMob(p_18305_, map, "CaveSpider");
      registerMob(p_18305_, map, "Silverfish");
      registerMob(p_18305_, map, "Blaze");
      registerMob(p_18305_, map, "LavaSlime");
      registerMob(p_18305_, map, "EnderDragon");
      registerMob(p_18305_, map, "WitherBoss");
      registerMob(p_18305_, map, "Bat");
      registerMob(p_18305_, map, "Witch");
      registerMob(p_18305_, map, "Endermite");
      registerMob(p_18305_, map, "Guardian");
      registerMob(p_18305_, map, "Pig");
      registerMob(p_18305_, map, "Sheep");
      registerMob(p_18305_, map, "Cow");
      registerMob(p_18305_, map, "Chicken");
      registerMob(p_18305_, map, "Squid");
      registerMob(p_18305_, map, "Wolf");
      registerMob(p_18305_, map, "MushroomCow");
      registerMob(p_18305_, map, "SnowMan");
      registerMob(p_18305_, map, "Ozelot");
      registerMob(p_18305_, map, "VillagerGolem");
      p_18305_.register(map, "EntityHorse", (p_18254_) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_18305_)), "ArmorItem", References.ITEM_STACK.in(p_18305_), "SaddleItem", References.ITEM_STACK.in(p_18305_), equipment(p_18305_));
      });
      registerMob(p_18305_, map, "Rabbit");
      p_18305_.register(map, "Villager", (p_18245_) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(p_18305_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", References.ITEM_STACK.in(p_18305_), "buyB", References.ITEM_STACK.in(p_18305_), "sell", References.ITEM_STACK.in(p_18305_)))), equipment(p_18305_));
      });
      p_18305_.registerSimple(map, "EnderCrystal");
      p_18305_.registerSimple(map, "AreaEffectCloud");
      p_18305_.registerSimple(map, "ShulkerBullet");
      registerMob(p_18305_, map, "Shulker");
      return map;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_18303_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      registerInventory(p_18303_, map, "Furnace");
      registerInventory(p_18303_, map, "Chest");
      p_18303_.registerSimple(map, "EnderChest");
      p_18303_.register(map, "RecordPlayer", (p_18235_) -> {
         return DSL.optionalFields("RecordItem", References.ITEM_STACK.in(p_18303_));
      });
      registerInventory(p_18303_, map, "Trap");
      registerInventory(p_18303_, map, "Dropper");
      p_18303_.registerSimple(map, "Sign");
      p_18303_.register(map, "MobSpawner", (p_18223_) -> {
         return References.UNTAGGED_SPAWNER.in(p_18303_);
      });
      p_18303_.registerSimple(map, "Music");
      p_18303_.registerSimple(map, "Piston");
      registerInventory(p_18303_, map, "Cauldron");
      p_18303_.registerSimple(map, "EnchantTable");
      p_18303_.registerSimple(map, "Airportal");
      p_18303_.registerSimple(map, "Control");
      p_18303_.registerSimple(map, "Beacon");
      p_18303_.registerSimple(map, "Skull");
      p_18303_.registerSimple(map, "DLDetector");
      registerInventory(p_18303_, map, "Hopper");
      p_18303_.registerSimple(map, "Comparator");
      p_18303_.register(map, "FlowerPot", (p_18192_) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(p_18303_)));
      });
      p_18303_.registerSimple(map, "Banner");
      p_18303_.registerSimple(map, "Structure");
      p_18303_.registerSimple(map, "EndGateway");
      return map;
   }

   public void registerTypes(Schema p_18307_, Map<String, Supplier<TypeTemplate>> p_18308_, Map<String, Supplier<TypeTemplate>> p_18309_) {
      p_18307_.registerType(false, References.LEVEL, DSL::remainder);
      p_18307_.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(p_18307_)), "EnderItems", DSL.list(References.ITEM_STACK.in(p_18307_)));
      });
      p_18307_.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(p_18307_)), "TileEntities", DSL.list(DSL.or(References.BLOCK_ENTITY.in(p_18307_), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(p_18307_)))));
      });
      p_18307_.registerType(true, References.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), p_18309_);
      });
      p_18307_.registerType(true, References.ENTITY_TREE, () -> {
         return DSL.optionalFields("Riding", References.ENTITY_TREE.in(p_18307_), References.ENTITY.in(p_18307_));
      });
      p_18307_.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      p_18307_.registerType(true, References.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), p_18308_);
      });
      p_18307_.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(p_18307_)), "tag", DSL.optionalFields("EntityTag", References.ENTITY_TREE.in(p_18307_), "BlockEntityTag", References.BLOCK_ENTITY.in(p_18307_), "CanDestroy", DSL.list(References.BLOCK_NAME.in(p_18307_)), "CanPlaceOn", DSL.list(References.BLOCK_NAME.in(p_18307_)), "Items", DSL.list(References.ITEM_STACK.in(p_18307_)))), ADD_NAMES, HookFunction.IDENTITY);
      });
      p_18307_.registerType(false, References.OPTIONS, DSL::remainder);
      p_18307_.registerType(false, References.BLOCK_NAME, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(NamespacedSchema.namespacedString()));
      });
      p_18307_.registerType(false, References.ITEM_NAME, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      p_18307_.registerType(false, References.STATS, DSL::remainder);
      p_18307_.registerType(false, References.SAVED_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(References.STRUCTURE_FEATURE.in(p_18307_)), "Objectives", DSL.list(References.OBJECTIVE.in(p_18307_)), "Teams", DSL.list(References.TEAM.in(p_18307_))));
      });
      p_18307_.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
      p_18307_.registerType(false, References.OBJECTIVE, DSL::remainder);
      p_18307_.registerType(false, References.TEAM, DSL::remainder);
      p_18307_.registerType(true, References.UNTAGGED_SPAWNER, DSL::remainder);
      p_18307_.registerType(false, References.POI_CHUNK, DSL::remainder);
      p_18307_.registerType(true, References.WORLD_GEN_SETTINGS, DSL::remainder);
      p_18307_.registerType(false, References.ENTITY_CHUNK, () -> {
         return DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(p_18307_)));
      });
   }

   protected static <T> T addNames(Dynamic<T> p_18206_, Map<String, String> p_18207_, String p_18208_) {
      return p_18206_.update("tag", (p_145917_) -> {
         return p_145917_.update("BlockEntityTag", (p_145912_) -> {
            String s = p_18206_.get("id").asString().result().map(NamespacedSchema::ensureNamespaced).orElse("minecraft:air");
            if (!"minecraft:air".equals(s)) {
               String s1 = p_18207_.get(s);
               if (s1 != null) {
                  return p_145912_.set("id", p_18206_.createString(s1));
               }

               LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", (Object)s);
            }

            return p_145912_;
         }).update("EntityTag", (p_145908_) -> {
            String s = p_18206_.get("id").asString("");
            return "minecraft:armor_stand".equals(NamespacedSchema.ensureNamespaced(s)) ? p_145908_.set("id", p_18206_.createString(p_18208_)) : p_145908_;
         });
      }).getValue();
   }
}