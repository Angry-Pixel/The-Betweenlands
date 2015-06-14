package thebetweenlands.blocks.plants.crops;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBLGenericCrop extends BlockCrops {

	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	private String type;
	private Item plantDrops;
	private Item seed;

	/**
	 * @param blockName
	 *            the name of this block
	 * @param blockDrops
	 *            whatever item this crop drops that isn't seeds
	 * @param seedDrop
	 *            whatever item this crop drops as seeds
	 */

	public BlockBLGenericCrop(String blockName, Item blockDrops, Item seedDrop) {
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
		type = blockName;
		plantDrops = blockDrops;
		seed = seedDrop;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);

		if (metadata >= 7 && plantDrops != null) {
			for (int i = 0; i < 1 + fortune; ++i)
				if (world.rand.nextInt(15) <= metadata)
					ret.add(new ItemStack(func_149866_i()));
			ret.add(new ItemStack(func_149865_P()));
		}
		return ret;
	}

	@Override
	protected Item func_149866_i() {
		return seed;
	}

	@Override
	protected Item func_149865_P() { 
		return plantDrops;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		int meta = world.getBlockMetadata(x, y -1, z);
		return soil != null && soil instanceof BlockFarmedDirt && meta >= 4 && meta <= 8;
	}

	@Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == BLBlockRegistry.farmedDirt;
    }
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		System.out.println("Crop Meta is: " + meta + " Crop:" + plantDrops + " Seed: "+ seed);
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack != null && !(stack.getItem() == Items.dye)) {
			//TODO Temp Bonemeal will end up being plant tonic
			if (stack.getItem() == BLItemRegistry.materialsBL && stack.getItemDamage() == EnumMaterialsBL.COMPOST.ordinal()) {
				if (ItemDye.applyBonemeal(stack, world, x, y, z, player)) {
					if (!world.isRemote)
						world.playAuxSFX(2005, x, y, z, 0);
					return true;
				}
			}
		}
		return false;
	}

	@Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);

        if (world.getBlockLightValue(x, y + 1, z) >= 9) {
            int meta = world.getBlockMetadata(x, y, z);

            if (meta < 9) {
                if (rand.nextInt(25) == 0) {
                    ++meta;
                    world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                }
            }
        }
    }
	
	@Override
    public void func_149863_m(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
        if (meta > 7)
            meta = 7;
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= iconArray.length)
			return null;
		return iconArray[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconArray = new IIcon[8];

		for (int i = 0; i < iconArray.length; ++i)
			iconArray[i] = iconRegister.registerIcon("thebetweenlands:" + type + i);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 1;
	}
}