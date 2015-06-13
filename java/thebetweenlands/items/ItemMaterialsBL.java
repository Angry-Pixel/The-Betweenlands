package thebetweenlands.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMaterialsBL extends Item {
	public static ItemStack createStack(EnumMaterialsBL materialsBL) {
		return createStack(materialsBL, 1);
	}

	public static ItemStack createStack(EnumMaterialsBL materialsBL, int size) {
		return new ItemStack(BLItemRegistry.materialsBL, size, materialsBL.ordinal());
	}

	public static ItemStack createStack(Item item, int size, int meta) {
		return new ItemStack(item, size, meta);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemMaterialsBL() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[EnumMaterialsBL.VALUES.length];
		for (int i = 0; i < EnumMaterialsBL.VALUES.length; i++) {
			icons[i] = reg.registerIcon("thebetweenlands:" + EnumMaterialsBL.VALUES[i].iconName);
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
		for (int i = 0; i < EnumMaterialsBL.VALUES.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + EnumMaterialsBL.VALUES[stack.getItemDamage()].iconName;
		} catch (Exception e) {
			return "item.thebetweenlands.unknown";
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (is.getItemDamage() == this.createStack(EnumMaterialsBL.SWAMP_REED).getItemDamage() && side == 1) {
			Block block = world.getBlock(x, y + 1, z);
			if (block == Blocks.air) {
				if (BLBlockRegistry.swampReed.canPlaceBlockOn(world.getBlock(x, y, z))) {
					if(!world.isRemote) {
						world.setBlock(x, y + 1, z, BLBlockRegistry.swampReed);
						world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.swampReed.stepSound.func_150496_b(), (BLBlockRegistry.swampReed.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.swampReed.stepSound.getPitch() * 0.8F);
						--is.stackSize;
					}
					return true;
				}
			} else if (block == BLBlockRegistry.swampWater) {
				if (BLBlockRegistry.swampReedUW.canPlaceBlockAt(world, x, y + 1, z)) {
					if(!world.isRemote) {
						world.setBlock(x, y + 1, z, BLBlockRegistry.swampReedUW);
						world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), BLBlockRegistry.swampReed.stepSound.func_150496_b(), (BLBlockRegistry.swampReed.stepSound.getVolume() + 1.0F) / 2.0F, BLBlockRegistry.swampReed.stepSound.getPitch() * 0.8F);
						--is.stackSize;
					}
					return true;
				}
			}
		}
		return false;
	}

	public static enum EnumMaterialsBL {
		AQUA_MIDDLE_GEM("aquaMiddleGem"), CRIMSON_MIDDLE_GEM("crimsonMiddleGem"), GREEN_MIDDLE_GEM("greenMiddleGem"), 
		BLOOD_SNAIL_SHELL("bloodSnailShell"), MIRE_SNAIL_SHELL("mireSnailShell"), COMPOST("compost"), DRAGONFLY_WING("dragonflyWing"), 
		LURKER_SKIN("lurkerSkin"), SWAMP_REED("swampReed"), DRIED_SWAMP_REED("driedSwampReed"), SWAMP_REED_ROPE("swampReedRope"), 
		LIFE_CRYSTAL("lifeCrystal"), MIDDLE_FRUIT_SEEDS("middleFruitSeeds"), MIRE_CORAL("mireCoral"), DEEP_WATER_CORAL("deepWaterCoral"), 
		MOSS("moss"), MUD_BRICK("mudBrick"), OCTINE_INGOT("octineIngot"), ROTTEN_BARK("rottenBark"), SLIMY_BONE("slimyBone"), 
		SLUDGE_BALL("sludgeBall"), SNAPPER_ROOT("snapperRoot"), SPORES("spores"), STALKER_EYE("stalkerEye"), SULFUR("sulfur"), 
		VALONITE_SHARD("valoniteShard"), WEEDWOOD_STICK("weedWoodStick"), ANGLER_TOOTH("anglerTooth"), WEEDWOOD_BOWL("weedwoodBowl"), 
		GROUND_WEEDWOOD_BARK("groundWeedwoodBark"), RUBBER_BALL("rubber");

		public final String iconName;

		private EnumMaterialsBL(String unlocName) {
			iconName = unlocName;
		}

		public static final EnumMaterialsBL[] VALUES = values();
	}

	// Slimy Bonemeal

}
