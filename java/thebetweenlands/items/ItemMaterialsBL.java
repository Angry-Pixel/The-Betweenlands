package thebetweenlands.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMaterialsBL extends Item
{
	public static ItemStack createStack(EnumMaterialsBL materialsBL, int size) {
		return new ItemStack(BLItemRegistry.materialsBL, size, materialsBL.ordinal());
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
		for( int i = 0; i < EnumMaterialsBL.VALUES.length; i++ ) {
            icons[i] = reg.registerIcon("thebetweenlands:" + EnumMaterialsBL.VALUES[i].iconName);
        }
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if( meta < 0 || meta >= icons.length ) {
            return null;
        }

		return icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for( int i = 0; i < EnumMaterialsBL.VALUES.length; i++ ) {
            list.add(new ItemStack(item, 1, i));
        }
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + stack.getItemDamage();
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int meta, float hitX, float hitY, float hitZ) {
	//Nothing to see here - yet
		return false;
	}

	public static enum EnumMaterialsBL {
		AQUA_MIDDLE_GEM("aquaMiddleGem"),
		CRIMSON_MIDDLE_GEM("crimsonMiddleGem"),
		GREEN_MIDDLE_GEM("greenMiddleGem"),
		BLOOD_SNAIL_SHELL("bloodSnailShell"),
		MIRE_SNAIL_SHELL("mireSnailShell"),
		BRONZE_IDOL("bronzeIdol"),
		SILVER_IDOL("silverIdol"),
		GOLD_IDOL("goldIdol"),
		COMPOST("compost"),
		DRAGONFLY_WING("dragonflyWing"),
		LURKER_SKIN("lurkerSkin"),
		SWAMP_REED("swampReed"),
		SWAMP_REED_ROPE("swampReedRope"),
		SWAMP_REED_POWDER("swampReedPowder"),
		LIFE_CRYSTAL("lifeCrystal"),
		MIDDLE_FRUIT_SEEDS("middleFruitSeeds"),
		MIRE_CORAL("mireCoral"),
		MOSS("moss"),
		MUD_BRICK("mudBrick"),
		OCTINE_INGOT("octineIngot"),
		ROTTEN_BARK("rottenBark"),
		SAP_BALL("sapBall"),
		SLIMY_BONE("slimyBone"),
		SLUDGE_BALL("sludgeBall"),
		SNAPPER_ROOT("snapperRoot"),
		SPORES("spores"),
		STALKER_EYE("stalkerEye"),
		SULFUR("sulfur"),
		VALONITE_SHARD("valoniteShard"),
		WEEDWOOD_SICK("weedWoodStick"),
		WICK("wick"),
		WIGHT_HEART("wightHeart");

		public final String iconName;

		private EnumMaterialsBL(String unlocName) {
			iconName = unlocName;
		}

		public static final EnumMaterialsBL[] VALUES = values();
	}

   // Slimy Bonemeal

}
