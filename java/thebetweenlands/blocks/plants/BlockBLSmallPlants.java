package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ISyrmoriteShearable;
import thebetweenlands.items.ItemGenericPlantDrop;
import thebetweenlands.items.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLSmallPlants extends BlockTallGrass implements IShearable, ISyrmoriteShearable {
	protected final String name;

	@SideOnly(Side.CLIENT)
	private IIcon spoopyBlockIcon;

	public BlockBLSmallPlants(String type) {
		super();
		name = type;
		setHardness(0.0F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockName("thebetweenlands." + name);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if (rand.nextInt(25) == 0) {
			int xx;
			int yy;
			int zz;
			xx = x + rand.nextInt(3) - 1;
			yy = y + rand.nextInt(2) - rand.nextInt(2);
			zz = z + rand.nextInt(3) - 1;
			if (world.isAirBlock(xx, yy, zz) && canBlockStay(world, xx, yy, zz)) {
				if ("nettle".equals(name) && rand.nextInt(3) == 0)
					world.setBlock(x, y, z, BLBlockRegistry.nettleFlowered);
				if ("nettleFlowered".equals(name))
					world.setBlock(xx, yy, zz, BLBlockRegistry.nettle);
			}
			if (rand.nextInt(40) == 0) {
				if ("nettleFlowered".equals(name))
					world.setBlock(x, y, z, BLBlockRegistry.nettle);
			}
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		return soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this) || soil != null && canPlaceBlockOn(soil);
	}

	@Override	
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.deadGrass || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.sludgyDirt || block == BLBlockRegistry.mud;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if("nettle".equals(name) || "nettleFlowered".equals(name)) 
			entity.attackEntityFrom(DamageSource.cactus, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && (this.name.equals("swampTallGrass") || this.name.equals("softRush") || this.name.equals("cattail") || this.name.equals("blueEyedGrass"))) {
			return spoopyBlockIcon;
		}
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("thebetweenlands:" + name);
		if(this.name.equals("swampTallGrass") || this.name.equals("blueEyedGrass") || this.name.equals("cattail") || this.name.equals("softRush")) {
			spoopyBlockIcon = iconRegister.registerIcon("thebetweenlands:" + name + "Spoopy");
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		return drops;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return item.getItem() == BLItemRegistry.sickle;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
		switch(this.name) {
		case "arrowArum":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.ARROW_ARUM_LEAF));
			break;
		case "blueEyedGrass":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BLUE_EYED_GRASS_FLOWERS));
			break;
		case "blueIris":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BLUE_IRIS_PETAL));
			break;
		case "boneset":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BONESET_FLOWERS));
			break;
		case "bottleBrushGrass":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BOTTLE_BRUSH_GRASS_BLADES));
			break;
		case "buttonBush":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.BUTTON_BUSH_FLOWERS));
			break;
		case "cattail":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CATTAIL_HEAD));
			break;
		case "copperIris":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.COPPER_IRIS_PETALS));
			break;
		case "marshHibiscus":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_HIBISCUS_FLOWER));
			break;
		case "marshMallow":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.MARSH_MALLOW_FLOWER));
			break;
		case "nettle":
		case "nettleFlowered":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.NETTLE_LEAF));
			break;
		case "pickerelWeed":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.PICKEREL_WEED_FLOWER));
			break;
		case "shoots":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SHOOT_LEAVES));
			break;
		case "sludgecreep":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SLUDGECREEP_LEAVES));
			break;
		case "softRush":
			dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.SOFT_RUSH_LEAVES));
			break;
		}
		return dropList;
	}

	@Override
	public ItemStack getSpecialDrop(Block block, int x, int y, int z, int meta) {
		return null;
	}
}