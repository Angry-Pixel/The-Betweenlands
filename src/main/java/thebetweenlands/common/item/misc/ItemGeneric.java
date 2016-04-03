package thebetweenlands.common.item.misc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.herblore.aspect.Aspect;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

import java.util.List;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemGeneric extends Item {
    public ItemGeneric() {
        setMaxDamage(0);
        setHasSubtypes(true);
        this.setUnlocalizedName("thebetweenlands.unknownGeneric");
    }

    public static ItemStack createStack(EnumItemGeneric enumGeneric) {
        return createStack(enumGeneric, 1);
    }

    public static ItemStack createStack(EnumItemGeneric enumGeneric, int size) {
        return new ItemStack(ItemRegistry.itemsGeneric, size, enumGeneric.id);
    }

    public static ItemStack createStack(Item item, int size, int meta) {
        return new ItemStack(item, size, meta);
    }

    public static EnumItemGeneric getEnumFromID(int id) {
        for (int i = 0; i < EnumItemGeneric.VALUES.length; i++) {
            EnumItemGeneric enumGeneric = EnumItemGeneric.VALUES[i];
            if (enumGeneric.id == id) return enumGeneric;
        }
        return EnumItemGeneric.INVALID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < EnumItemGeneric.VALUES.length; i++) {
            if (EnumItemGeneric.VALUES[i] != EnumItemGeneric.INVALID)
                list.add(new ItemStack(item, 1, EnumItemGeneric.VALUES[i].id));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            return "item.thebetweenlands." + getEnumFromID(stack.getItemDamage()).iconName;
        } catch (Exception e) {
            return "item.thebetweenlands.unknownGeneric";
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getItemDamage() == EnumItemGeneric.ASPECTRUS_FRUIT.id) {
            List<Aspect> itemAspects = AspectManager.getDynamicAspects(stack);
            if (!itemAspects.isEmpty()) {
                Aspect aspect = itemAspects.get(0);
                return super.getItemStackDisplayName(stack) + " - " + aspect.type.getName() + " (" + aspect.getAmount() + ")";
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    /*@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (stack.getItemDamage() == this.createStack(EnumItemGeneric.SWAMP_REED).getItemDamage() && side == 1) {
            Block block = world.getBlock(x, y + 1, z);
            if (block == Blocks.air) {
                if (BLBlockRegistry.swampReed.canPlaceBlockAt(world, x, y+1, z)) {
                    if(!world.isRemote) {
                        world.setBlock(x, y + 1, z, BLBlockRegistry.swampReed);
                        world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.swampReed.stepSound.func_150496_b(), (BLBlockRegistry.swampReed.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.swampReed.stepSound.getPitch() * 0.8F);
                        --stack.stackSize;
                    }
                    return true;
                }
            } else if (block == BLBlockRegistry.swampWater) {
                if (BLBlockRegistry.swampReedUW.canPlaceBlockAt(world, x, y + 1, z)) {
                    if(!world.isRemote) {
                        world.setBlock(x, y + 1, z, BLBlockRegistry.swampReedUW);
                        world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.swampReed.stepSound.func_150496_b(), (BLBlockRegistry.swampReed.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.swampReed.stepSound.getPitch() * 0.8F);
                        --stack.stackSize;
                    }
                    return true;
                }
            }
        }
        if (stack.getItemDamage() == this.createStack(EnumItemGeneric.SWAMP_KELP).getItemDamage() && side == 1) {
            Block block = world.getBlock(x, y + 1, z);
            if (block == BLBlockRegistry.swampWater) {
                if (BLBlockRegistry.swampKelp.canPlaceBlockAt(world, x, y + 1, z)) {
                    if(!world.isRemote) {
                        world.setBlock(x, y + 1, z, BLBlockRegistry.swampKelp);
                        world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.swampKelp.stepSound.func_150496_b(), (BLBlockRegistry.swampKelp.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.swampKelp.stepSound.getPitch() * 0.8F);
                        --stack.stackSize;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.id)
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        if(stack.getItemDamage() == EnumItemGeneric.OCTINE_INGOT.id) {
            Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY + (player.worldObj.isRemote ? 0 : player.getEyeHeight()), player.posZ);
            Vec3 rayDir = player.getLookVec();
            Vec3 rayTraceEnd = playerPos.addVector(rayDir.xCoord*5, rayDir.yCoord*5, rayDir.zCoord*5);
            MovingObjectPosition pos = player.worldObj.rayTraceBlocks(playerPos, rayTraceEnd);
            if(pos != null && pos.typeOfHit == MovingObjectType.BLOCK) {
                ForgeDirection dir = ForgeDirection.getOrientation(pos.sideHit);
                int bx = pos.blockX + dir.offsetX;
                int by = pos.blockY + dir.offsetY;
                int bz = pos.blockZ + dir.offsetZ;
                boolean hasTinder = false;
                boolean isBlockTinder = false;
                Block block = player.worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ);
                if(this.isTinder(block, null)) {
                    hasTinder = true;
                    isBlockTinder = true;
                } else {
                    List<EntityItem> items = player.worldObj.getEntitiesWithinAABB(EntityItem.class,
                            AxisAlignedBB.getBoundingBox(bx, by, bz, bx+1, by+1, bz+1));
                    for(EntityItem entityItem : items) {
                        ItemStack entityItemStack = entityItem.getEntityItem();
                        if(this.isTinder(null, entityItemStack)) {
                            hasTinder = true;
                            break;
                        }
                    }
                }
                if((hasTinder || isBlockTinder) && block != Blocks.fire) {
                    player.setItemInUse(stack, getMaxItemUseDuration(stack));
                }
            }
        }
        return stack;
    }*/

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if (stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.id)
            return EnumAction.EAT;
        if (stack.getItemDamage() == EnumItemGeneric.OCTINE_INGOT.id)
            return EnumAction.BOW;
        return null;
    }

    /*@Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if(stack.getItemDamage() == EnumItemGeneric.OCTINE_INGOT.id) {
            Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY + (player.worldObj.isRemote ? 0 : player.getEyeHeight()), player.posZ);
            Vec3 rayDir = player.getLookVec();
            Vec3 rayTraceEnd = playerPos.addVector(rayDir.xCoord*5, rayDir.yCoord*5, rayDir.zCoord*5);
            MovingObjectPosition pos = player.worldObj.rayTraceBlocks(playerPos, rayTraceEnd);
            if(pos != null && pos.typeOfHit == MovingObjectType.BLOCK) {
                ForgeDirection dir = ForgeDirection.getOrientation(pos.sideHit);
                int bx = pos.blockX + dir.offsetX;
                int by = pos.blockY + dir.offsetY;
                int bz = pos.blockZ + dir.offsetZ;
                boolean hasTinder = false;
                boolean isBlockTinder = false;
                Block block = player.worldObj.getBlock(pos.blockX, pos.blockY, pos.blockZ);
                if(this.isTinder(block, null)) {
                    hasTinder = true;
                    isBlockTinder = true;
                } else {
                    List<EntityItem> items = player.worldObj.getEntitiesWithinAABB(EntityItem.class,
                            AxisAlignedBB.getBoundingBox(bx, by, bz, bx+1, by+1, bz+1));
                    for(EntityItem entityItem : items) {
                        ItemStack entityItemStack = entityItem.getEntityItem();
                        if(this.isTinder(null, entityItemStack)) {
                            hasTinder = true;
                            break;
                        }
                    }
                }
                if(hasTinder) {
                    if(player.worldObj.rand.nextInt(count / 10 + 1) == 0) {
                        player.worldObj.spawnParticle("flame",
                                pos.hitVec.xCoord+player.worldObj.rand.nextFloat()*0.2-0.1,
                                pos.hitVec.yCoord+player.worldObj.rand.nextFloat()*0.2-0.1,
                                pos.hitVec.zCoord+player.worldObj.rand.nextFloat()*0.2-0.1, 0, 0.1, 0);
                        player.worldObj.spawnParticle("smoke",
                                pos.hitVec.xCoord+player.worldObj.rand.nextFloat()*0.2-0.1,
                                pos.hitVec.yCoord+player.worldObj.rand.nextFloat()*0.2-0.1,
                                pos.hitVec.zCoord+player.worldObj.rand.nextFloat()*0.2-0.1, 0, 0.1, 0);
                    }
                    if(!player.worldObj.isRemote) {
                        if(count == 1) {
                            if(isBlockTinder) {
                                player.worldObj.setBlock(pos.blockX, pos.blockY, pos.blockZ, Blocks.fire);
                            } else {
                                if(player.worldObj.getBlock(bx, by, bz).getMaterial().isReplaceable()) {
                                    player.worldObj.setBlock(bx, by, bz, Blocks.fire);
                                }
                            }
                        }
                    }
                }
            }
        }
        super.onUsingTick(stack, player, count);
    }*/


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debug) {
        if (stack.getItemDamage() == EnumItemGeneric.OCTINE_INGOT.id)
            info.add(TranslationHelper.translateToLocal("octine.fire"));
        super.addInformation(stack, player, info, debug);
    }

    /*private boolean isTinder(Block block, ItemStack stack) {
        if(block != null) {
            return block == BLBlockRegistry.caveMoss ||
                    block == BLBlockRegistry.wallPlants ||
                    block == BLBlockRegistry.thorns;
        }
        if(stack != null) {
            if(stack.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) stack.getItem();
                return this.isTinder(itemBlock.field_150939_a, null);
            }
            return (stack.getItem() == BLItemRegistry.itemsGenericPlantDrop &&
                    (stack.getItemDamage() == EnumItemPlantDrop.CAVE_MOSS.id ||
                            stack.getItemDamage() == EnumItemPlantDrop.MOSS.id ||
                            stack.getItemDamage() == EnumItemPlantDrop.LICHEN.id ||
                            stack.getItemDamage() == EnumItemPlantDrop.THORNS.id)) ||

                    (stack.getItem() == BLItemRegistry.itemsGenericCrushed &&
                            (stack.getItemDamage() == EnumItemGenericCrushed.GROUND_CAVE_MOSS.id ||
                                    stack.getItemDamage() == EnumItemGenericCrushed.GROUND_MOSS.id ||
                                    stack.getItemDamage() == EnumItemGenericCrushed.GROUND_LICHEN.id ||
                                    stack.getItemDamage() == EnumItemGenericCrushed.GROUND_THORNS.id));
        }
        return false;
    }*/
    /*
    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.id) {
            stack.stackSize--;
            world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            onFoodEaten(stack, world, player);
        }
        return stack;
    }*/

    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote)
            player.curePotionEffects(new ItemStack(Items.milk_bucket));
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }


    public enum EnumItemGeneric {
        INVALID("invalid", 1024),
        BLOOD_SNAIL_SHELL("bloodSnailShell", 3), MIRE_SNAIL_SHELL("mireSnailShell", 4), COMPOST("compost", 5), DRAGONFLY_WING("dragonflyWing", 6),
        LURKER_SKIN("lurkerSkin", 7), SWAMP_REED("swampReed", 8), DRIED_SWAMP_REED("driedSwampReed", 9), SWAMP_REED_ROPE("swampReedRope", 10),
        TANGLED_ROOT("tangledRoot", 11), PLANT_TONIC("plantTonic", 12),
        MUD_BRICK("mudBrick", 13), SYRMORITE_INGOT("syrmoriteIngot", 14), OCTINE_INGOT("octineIngot", 15), ROTTEN_BARK("rottenBark", 16), SLIMY_BONE("slimyBone", 17),
        SLUDGE_BALL("sludgeBall", 18), SNAPPER_ROOT("snapperRoot", 19), STALKER_EYE("stalkerEye", 20), SULFUR("sulfur", 21),
        VALONITE_SHARD("valoniteShard", 22), WEEDWOOD_STICK("weedwoodStick", 23), ANGLER_TOOTH("anglerTooth", 24), WEEDWOOD_BOWL("weedwoodBowl", 25),
        RUBBER_BALL("rubber", 26), TAR_BEAST_HEART("tarBeastHeart", 27), TAR_BEAST_HEART_ANIMATED("tarBeastHeartAnimated", 28), TAR_DRIP("tarDrip", 29), LIMESTONE_FLUX("limestoneFlux", 30),
        SWAMP_KELP("swampKelp", 31), INANIMATE_TARMINION("inanimateTarminion", 32), POISON_GLAND("poisonGland", 33), ASPECTRUS_FRUIT("aspectrusFruit", 34), PARCHMENT("parchment", 35),
        SHOCKWAVE_SWORD_1("shockwaveSword1", 36), SHOCKWAVE_SWORD_2("shockwaveSword2", 37), SHOCKWAVE_SWORD_3("shockwaveSword3", 38), SHOCKWAVE_SWORD_4("shockwaveSword4", 39),
        PYRAD_FLAME("pyradFlame", 40), AMULET_SOCKET("amuletSocket", 41), SCABYST("scabyst", 42);

        public static final EnumItemGeneric[] VALUES = values();
        public final String iconName;
        public final int id;

        EnumItemGeneric(String unlocName, int id) {
            iconName = unlocName;
            this.id = id;
        }
    }
}
