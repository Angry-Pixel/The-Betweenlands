package thebetweenlands.common.registries;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.item.herblore.ItemCrushed;
import thebetweenlands.common.item.herblore.ItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.item.misc.ItemSwampTalisman.EnumTalisman;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;
import thebetweenlands.common.tile.TileEntityAnimator;

public class RecipeRegistry {
    private RecipeRegistry() {
    }

    public static void init() {
        registerDruidAltarRecipes();
        registerCompostRecipes();
        registerPestleAndMortarRecipes();
        registerAnimatorRecipes();
        registerPurifierRecipes();
    }

    private static void registerDruidAltarRecipes() {
        DruidAltarRecipe.addRecipe(EnumTalisman.SWAMP_TALISMAN_1.create(1), EnumTalisman.SWAMP_TALISMAN_2.create(1), EnumTalisman.SWAMP_TALISMAN_3.create(1), EnumTalisman.SWAMP_TALISMAN_4.create(1), EnumTalisman.SWAMP_TALISMAN_0.create(1));
    }

    private static void registerCompostRecipes() {
        CompostRecipe.addRecipe(30, 12000, ItemRegistry.ITEMS_MISC, EnumItemMisc.ROTTEN_BARK.getID());
        //        CompostRecipe.addRecipe(25, 12000, Item.getItemFromBlock(BlockRegistry.rottenWeedwoodBark));
        CompostRecipe.addRecipe(10, 8000, Item.getItemFromBlock(BlockRegistry.SUNDEW));
        CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BlockRegistry.SWAMP_DOUBLE_TALLGRASS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.PHRAGMITES));
        CompostRecipe.addRecipe(6, 10000, Item.getItemFromBlock(BlockRegistry.TALL_CATTAIL));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.CARDINAL_FLOWER));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BROOMSEDGE));
        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.WEEPING_BLUE));
        CompostRecipe.addRecipe(12, 11000, Item.getItemFromBlock(BlockRegistry.PITCHER_PLANT));
        CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.BOG_BEAN_FLOWER));
        CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.GOLDEN_CLUB_FLOWER));
        CompostRecipe.addRecipe(6, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_MARIGOLD_FLOWER));
        CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.SWAMP_KELP));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.WATER_WEEDS));
//                CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterFlower));
//                CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterFlowerStalk));
        CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.ROOT));
        CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.ROOT_UNDERWATER));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLACK_HAT_MUSHROOM));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.FLAT_HEAD_MUSHROOM));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BULB_CAPPED_MUSHROOM));
        CompostRecipe.addRecipe(4, 6000, Item.getItemFromBlock(BlockRegistry.SWAMP_PLANT));
        CompostRecipe.addRecipe(12, 10000, Item.getItemFromBlock(BlockRegistry.VENUS_FLY_TRAP));
        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.VOLARPAD));
        CompostRecipe.addRecipe(20, 12000, Item.getItemFromBlock(BlockRegistry.WEEDWOOD_BUSH));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.THORNS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.POISON_IVY));
//                CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.wallPlants));
//                CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.wallPlants), 1);
        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.CAVE_MOSS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.CAVE_GRASS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.CATTAIL));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SWAMP_TALLGRASS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SHOOTS));
        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.NETTLE_FLOWERED));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.NETTLE));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.ARROW_ARUM));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BUTTON_BUSH));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_HIBISCUS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.PICKEREL_WEED));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SOFT_RUSH));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.MARSH_MALLOW));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLUE_IRIS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.COPPER_IRIS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BLUE_EYED_GRASS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.MILKWEED));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BONESET));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.BOTTLE_BRUSH_GRASS));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.SLUDGECREEP));
        CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.DEAD_WEEDWOOD_BUSH));
//                CompostRecipe.addRecipe(3, 5000, Item.getItemFromBlock(BlockRegistry.hanger));
//                CompostRecipe.addRecipe(5, 8000, Item.getItemFromBlock(BlockRegistry.waterFlowerStalk));
        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.MIRE_CORAL));
        CompostRecipe.addRecipe(6, 9000, Item.getItemFromBlock(BlockRegistry.DEEP_WATER_CORAL));
        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_RUBBER));
        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_SAP));
        CompostRecipe.addRecipe(15, 11000, Item.getItemFromBlock(BlockRegistry.SAPLING_WEEDWOOD));
        CompostRecipe.addRecipe(3, 5000, ItemRegistry.SWAMP_REED_ITEM);
        CompostRecipe.addRecipe(3, 5000, ItemRegistry.ITEMS_MISC, EnumItemMisc.DRIED_SWAMP_REED.getID());
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.ITEMS_MISC, EnumItemMisc.SWAMP_REED_ROPE.getID());
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.ITEMS_MISC, EnumItemMisc.TANGLED_ROOT.getID());
        CompostRecipe.addRecipe(3, 5000, ItemRegistry.ITEMS_MISC, EnumItemMisc.SWAMP_KELP_ITEM.getID());
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM);
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.BLACK_HAT_MUSHROOM_ITEM);
        CompostRecipe.addRecipe(5, 8000, ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM);
        CompostRecipe.addRecipe(12, 10000, ItemRegistry.YELLOW_DOTTED_FUNGUS);

        for (ItemCrushed.EnumItemCrushed type : ItemCrushed.EnumItemCrushed.values()) {
            CompostRecipe.addRecipe(3, 4000, ItemRegistry.ITEMS_CRUSHED, type.getID());
        }

        for (ItemPlantDrop.EnumItemPlantDrop type : ItemPlantDrop.EnumItemPlantDrop.values()) {
            CompostRecipe.addRecipe(3, 4000, ItemRegistry.ITEMS_PLANT_DROP, type.getID());
        }
    }

    private static void registerPestleAndMortarRecipes() {
        PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(1)), new ItemStack(BlockRegistry.LIMESTONE));
        PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(1)), new ItemStack(BlockRegistry.LIMESTONE_CHISELED));
        PestleAndMortarRecipe.addRecipe((EnumItemMisc.LIMESTONE_FLUX.create(1)), new ItemStack(BlockRegistry.POLISHED_LIMESTONE));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ALGAE.create(1)), (ItemPlantDrop.EnumItemPlantDrop.ALGAE_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ANGLER_TOOTH.create(1)), (EnumItemMisc.ANGLER_TOOTH.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_AQUA_MIDDLE_GEM.create(1)), new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM, 1));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_ARROW_ARUM.create(1)), (ItemPlantDrop.EnumItemPlantDrop.ARROW_ARUM_LEAF.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLACKHAT_MUSHROOM.create(1)), new ItemStack(ItemRegistry.BLACK_HAT_MUSHROOM_ITEM));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLOOD_SNAIL_SHELL.create(1)), (EnumItemMisc.BLOOD_SNAIL_SHELL.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLUE_EYED_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BLUE_IRIS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BLUE_IRIS_PETAL.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BOG_BEAN.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BOG_BEAN_FLOWER_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BONESET.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BONESET_FLOWERS.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BOTTLE_BRUSH_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BROOM_SEDGE.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BROOM_SEDGE_LEAVES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BULB_CAPPED_MUSHROOM.create(1)), new ItemStack(ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_BUTTON_BUSH.create(1)), (ItemPlantDrop.EnumItemPlantDrop.BUTTON_BUSH_FLOWERS.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CARDINAL_FLOWER.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CARDINAL_FLOWER_PETALS.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CATTAIL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CATTAIL_HEAD.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CAVE_GRASS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CAVE_GRASS_BLADES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CAVE_MOSS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.CAVE_MOSS_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_COPPER_IRIS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.COPPER_IRIS_PETALS.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_CRIMSON_MIDDLE_GEM.create(1)), new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM, 1));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_DEEP_WATER_CORAL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.DEEP_WATER_CORAL_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_DRIED_SWAMP_REED.create(1)), (EnumItemMisc.DRIED_SWAMP_REED.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_FLATHEAD_MUSHROOM.create(1)), new ItemStack(ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_GOLDEN_CLUB.create(1)), (ItemPlantDrop.EnumItemPlantDrop.GOLDEN_CLUB_FLOWER_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_GREEN_MIDDLE_GEM.create(1)), new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM, 1));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_LICHEN.create(1)), (ItemPlantDrop.EnumItemPlantDrop.LICHEN_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MARSH_HIBISCUS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MARSH_HIBISCUS_FLOWER.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MARSH_MALLOW.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MARSH_MALLOW_FLOWER.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MARSH_MARIGOLD.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MARSH_MARIGOLD_FLOWER_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MILKWEED.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MILKWEED_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MIRE_CORAL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MIRE_CORAL_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MIRE_SNAIL_SHELL.create(1)), (EnumItemMisc.MIRE_SNAIL_SHELL.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_MOSS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.MOSS_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_NETTLE.create(1)), (ItemPlantDrop.EnumItemPlantDrop.NETTLE_LEAF.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PHRAGMITES.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PHRAGMITE_STEMS.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PICKEREL_WEED.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PICKEREL_WEED_FLOWER.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SHOOTS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SHOOT_LEAVES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SLUDGECREEP.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SLUDGECREEP_LEAVES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SOFT_RUSH.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SOFT_RUSH_LEAVES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SUNDEW.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SUNDEW_HEAD.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SWAMP_KELP.create(1)), (EnumItemMisc.SWAMP_KELP_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_SWAMP_GRASS_TALL.create(1)), (ItemPlantDrop.EnumItemPlantDrop.SWAMP_TALL_GRASS_BLADES.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_TANGLED_ROOTS.create(1)), (EnumItemMisc.TANGLED_ROOT.create(1)));
        //TODO add when bark is added PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_WEEDWOOD_BARK.create(1)), (BlockRegistry.BARK));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_HANGER.create(1)), (ItemPlantDrop.EnumItemPlantDrop.HANGER.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_WATER_WEEDS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.WATER_WEEDS_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_VENUS_FLY_TRAP.create(1)), (ItemPlantDrop.EnumItemPlantDrop.VENUS_FLY_TRAP_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_VOLARPAD.create(1)), (ItemPlantDrop.EnumItemPlantDrop.VOLARPAD_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_THORNS.create(1)), (ItemPlantDrop.EnumItemPlantDrop.THORNS_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_POISON_IVY.create(1)), (ItemPlantDrop.EnumItemPlantDrop.POISON_IVY_ITEM.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_PITCHER_PLANT.create(1)), (ItemPlantDrop.EnumItemPlantDrop.PITCHER_PLANT_TRAP.create(1)));
        PestleAndMortarRecipe.addRecipe((ItemCrushed.EnumItemCrushed.GROUND_GENERIC_LEAF.create(1)), (ItemPlantDrop.EnumItemPlantDrop.GENERIC_LEAF.create(1)));
//TODO add when water flower is added		PestleAndMortarRecipe.addRecipe(new ItemStack(ItemCrushed.EnumItemCrushed.GROUND_WATER_FLOWER.create(1)), new ItemStack(ItemPlantDrop.EnumItemPlantDrop.WATER_FLOWER));
//TODO add when water flower is added		PestleAndMortarRecipe.addRecipe(new ItemStack(ItemCrushed.EnumItemCrushed.GROUND_WATER_FLOWER_STALK.create(1)), new ItemStack(ItemPlantDrop.EnumItemPlantDrop.WATER_FLOWER_STALK));
    }

    private static void registerAnimatorRecipes() {
        AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.SCROLL.create(1), 16, 16) {
            @Override
            public ItemStack onAnimated(World world, BlockPos pos) {
                LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableRegistry.ANIMATOR_SCROLL);
                LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer) world));
                ItemStack result = loottable.generateLootForPools(world.rand, lootcontext$builder.build()).get(0);

                int minStackSize = MathHelper.ceiling_double_int(result.stackSize / 2.0D);
                int maxStackSize = result.stackSize;
                result = result.copy();
                if (maxStackSize > minStackSize) {
                    result.stackSize = minStackSize + (maxStackSize - minStackSize);
                } else {
                    result.stackSize = minStackSize;
                }
                return result;
            }
        });
        AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.TAR_BEAST_HEART.create(1), 32, 32, EnumItemMisc.TAR_BEAST_HEART_ANIMATED.create(1)).setRenderEntity("thebetweenlands.tarminion"));
        //TODO add tarminion
        //AnimatorRecipe.addRecipe(new AnimatorRecipe(EnumItemMisc.INANIMATE_TARMINION.create(1), 8, 8, new ItemStack(ItemRegistry.TARMINION)).setRenderEntity("thebetweenlands.tarminion"));
        AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.TEST_ITEM), 2, 1) {
            @Override
            public boolean onRetrieved(TileEntityAnimator tile, World world, int x, int y, int z) {
                EntityItem entityitem = new EntityItem(world, x, y + 1D, z, new ItemStack(ItemRegistry.TEST_ITEM));
                entityitem.motionX = 0;
                entityitem.motionZ = 0;
                entityitem.motionY = 0.11000000298023224D;
                world.spawnEntityInWorld(entityitem);
                tile.setInventorySlotContents(0, null);
                return false;
            }
        });
        //TODO add spores
        //AnimatorRecipe.addRecipe(new AnimatorRecipe(new ItemStack(ItemRegistry.SPORES), 8, 4, EntitySporeling.class).setRenderEntity("thebetweenlands.sporeling"));
    }

    private static void registerPurifierRecipes() {
        PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_1.getMetadata()));
        PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.DEFAULT.getMetadata()), new ItemStack(BlockRegistry.CRAGROCK, 1, BlockCragrock.EnumCragrockType.MOSSY_2.getMetadata()));
        PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.AQUA_MIDDLE_GEM_ORE));
        PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE));
        PurifierRecipe.addRecipe(new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM, 1), new ItemStack(BlockRegistry.GREEN_MIDDLE_GEM_ORE));
        //TODO add swamp dirt
        //PurifierRecipe.addRecipe(new ItemStack(BlockRegistry.FARMED_DIRT, 1, 0), new ItemStack(BlockRegistry.SWAMP_DIRT));
        //TODO add vials
        //PurifierRecipe.addRecipe(ItemRegistry.dentrothystVial.createStack(0), ItemRegistry.dentrothystVial.createStack(1));
    }
}
