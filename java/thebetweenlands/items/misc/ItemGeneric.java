package thebetweenlands.items.misc;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.IManualEntryItem;

public class ItemGeneric extends Item implements IManualEntryItem {
	public static ItemStack createStack(EnumItemGeneric enumGeneric) {
		return createStack(enumGeneric, 1);
	}

	public static ItemStack createStack(EnumItemGeneric enumGeneric, int size) {
		return new ItemStack(BLItemRegistry.itemsGeneric, size, enumGeneric.id);
	}

	public static ItemStack createStack(Item item, int size, int meta) {
		return new ItemStack(item, size, meta);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemGeneric() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[EnumItemGeneric.VALUES.length];
		for (int i = 0; i < EnumItemGeneric.VALUES.length; i++) {
			icons[i] = reg.registerIcon("thebetweenlands:" + EnumItemGeneric.VALUES[i].iconName);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if (meta < 0 || meta >= icons.length) {
			return null;
		}

		return icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < EnumItemGeneric.VALUES.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + EnumItemGeneric.VALUES[stack.getItemDamage()].iconName;
		} catch (Exception e) {
			return "item.thebetweenlands.unknownGeneric";
		}
	}

	@Override
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

	/*@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.setItemInUse(stack, getMaxItemUseDuration(stack));
		return stack;
	}*/

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		if (stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.id)
			return EnumAction.eat;
		return null;
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.id) {
			stack.stackSize--;
			world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			onFoodEaten(stack, world, player);
		}
		return stack;
	}

	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote)
			player.curePotionEffects(new ItemStack(Items.milk_bucket));
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public String manualName(int meta) {
		return "genericItems";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{1, 2, 3, 5, 4, 6, 8};
	}

	@Override
	public int metas() {
		return 32;
	}

	public static enum EnumItemGeneric {
		AQUA_MIDDLE_GEM("aquaMiddleGem", 0), CRIMSON_MIDDLE_GEM("crimsonMiddleGem", 1), GREEN_MIDDLE_GEM("greenMiddleGem", 2), 
		BLOOD_SNAIL_SHELL("bloodSnailShell", 3), MIRE_SNAIL_SHELL("mireSnailShell", 4), COMPOST("compost", 5), DRAGONFLY_WING("dragonflyWing", 6), 
		LURKER_SKIN("lurkerSkin", 7), SWAMP_REED("swampReed", 8), DRIED_SWAMP_REED("driedSwampReed", 9), SWAMP_REED_ROPE("swampReedRope", 10), 
		TANGLED_ROOT("tangledRoot", 11), PLANT_TONIC("plantTonic", 12), 
		MUD_BRICK("mudBrick", 13), SYRMORITE_INGOT("syrmoriteIngot", 14), OCTINE_INGOT("octineIngot", 15), ROTTEN_BARK("rottenBark", 16), SLIMY_BONE("slimyBone", 17), 
		SLUDGE_BALL("sludgeBall", 18), SNAPPER_ROOT("snapperRoot", 19), STALKER_EYE("stalkerEye", 20), SULFUR("sulfur", 21), 
		VALONITE_SHARD("valoniteShard", 22), WEEDWOOD_STICK("weedWoodStick", 23), ANGLER_TOOTH("anglerTooth", 24), WEEDWOOD_BOWL("weedwoodBowl", 25),
		RUBBER_BALL("rubber", 26), TAR_BEAST_HEART("tarBeastHeart", 27),TAR_BEAST_HEART_ANIMATED("tarBeastHeartAnimated", 28), TAR_DRIP("tarDrip", 29), LIMESTONE_FLUX("limestoneFlux", 30),
		SWAMP_KELP("swampKelp", 31), INANIMATE_TARMINION("inanimateTarminion", 32);

		public final String iconName;
		public final int id;

		private EnumItemGeneric(String unlocName, int id) {
			iconName = unlocName;
			this.id = id;
		}

		public static final EnumItemGeneric[] VALUES = values();
	}

	public static boolean isIngotFromOre(ItemStack input, ItemStack output) {
		if(input == null || output == null) return false;
		return isOre(input) && isIngot(output);
	}

	public static boolean isOre(ItemStack stack) {
		if(stack == null) return false;
		return stack.getItem() == Item.getItemFromBlock(BLBlockRegistry.octineOre)
				|| stack.getItem() == Item.getItemFromBlock(BLBlockRegistry.syrmoriteOre)
				|| stack.getItem() == Item.getItemFromBlock(BLBlockRegistry.sulfurOre)
				|| stack.getItem() == Item.getItemFromBlock(BLBlockRegistry.valoniteOre)
				|| stack.getItem() == Item.getItemFromBlock(BLBlockRegistry.lifeCrystalOre);
	}

	public static boolean isIngot(ItemStack stack) {
		if(stack == null) return false;
		return (stack.getItem() instanceof ItemGeneric 
				&& (stack.getItemDamage() == EnumItemGeneric.OCTINE_INGOT.id
				|| stack.getItemDamage() == EnumItemGeneric.SYRMORITE_INGOT.id
				|| stack.getItemDamage() == EnumItemGeneric.SULFUR.id
				|| stack.getItemDamage() == EnumItemGeneric.VALONITE_SHARD.id))
				|| stack.getItem() == BLItemRegistry.lifeCrystal;
	}

	// Slimy Bonemeal

}
