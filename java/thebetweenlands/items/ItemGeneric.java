package thebetweenlands.items;

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

public class ItemGeneric extends Item {
	public static ItemStack createStack(EnumItemGeneric enumGeneric) {
		return createStack(enumGeneric, 1);
	}

	public static ItemStack createStack(EnumItemGeneric enumGeneric, int size) {
		return new ItemStack(BLItemRegistry.itemsGeneric, size, enumGeneric.ordinal());
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
		if (stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.ordinal())
			return EnumAction.eat;
		return null;
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (stack.getItemDamage() == EnumItemGeneric.TANGLED_ROOT.ordinal()) {
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

	public static enum EnumItemGeneric {
		AQUA_MIDDLE_GEM("aquaMiddleGem"), CRIMSON_MIDDLE_GEM("crimsonMiddleGem"), GREEN_MIDDLE_GEM("greenMiddleGem"), 
		BLOOD_SNAIL_SHELL("bloodSnailShell"), MIRE_SNAIL_SHELL("mireSnailShell"), COMPOST("compost"), DRAGONFLY_WING("dragonflyWing"), 
		LURKER_SKIN("lurkerSkin"), SWAMP_REED("swampReed"), DRIED_SWAMP_REED("driedSwampReed"), SWAMP_REED_ROPE("swampReedRope"), 
		TANGLED_ROOT("tangledRoot"), PLANT_TONIC("plantTonic"), 
		MUD_BRICK("mudBrick"), SYRMORITE_INGOT("syrmoriteIngot"), OCTINE_INGOT("octineIngot"), ROTTEN_BARK("rottenBark"), SLIMY_BONE("slimyBone"), 
		SLUDGE_BALL("sludgeBall"), SNAPPER_ROOT("snapperRoot"), SPORES("spores"), STALKER_EYE("stalkerEye"), SULFUR("sulfur"), 
		VALONITE_SHARD("valoniteShard"), WEEDWOOD_STICK("weedWoodStick"), ANGLER_TOOTH("anglerTooth"), WEEDWOOD_BOWL("weedwoodBowl"),
		RUBBER_BALL("rubber"), TAR_BEAST_HEART("tarBeastHeart"),TAR_BEAST_HEART_ANIMATED("tarBeastHeartAnimated"), TAR_DRIP("tarDrip"), LIMESTONE_FLUX("limestoneFlux"),
		SWAMP_KELP("swampKelp");

		public final String iconName;

		private EnumItemGeneric(String unlocName) {
			iconName = unlocName;
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
				&& (stack.getItemDamage() == EnumItemGeneric.OCTINE_INGOT.ordinal()
				|| stack.getItemDamage() == EnumItemGeneric.SYRMORITE_INGOT.ordinal()
				|| stack.getItemDamage() == EnumItemGeneric.SULFUR.ordinal()
				|| stack.getItemDamage() == EnumItemGeneric.VALONITE_SHARD.ordinal()))
				|| stack.getItem() == BLItemRegistry.lifeCrystal;
	}

	// Slimy Bonemeal

}
