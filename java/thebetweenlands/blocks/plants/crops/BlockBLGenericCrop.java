package thebetweenlands.blocks.plants.crops;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.ItemMaterialsCrushed.EnumMaterialsCrushed;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBLGenericCrop extends BlockCrops {

	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	@SideOnly(Side.CLIENT)
	private IIcon decayedTexture;
	private String type;

	public BlockBLGenericCrop(String blockName) {
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);
		if (metadata == 7) {
			for (int i = 0; i < 3 + fortune; ++i) {
				if (world.rand.nextInt(15) <= metadata)
					ret.add(getSeedDrops());
				ret.add(getCropDrops());
			}
		}
		if (metadata < 7)
			ret.add(getSeedDrops());
		if (metadata > 7)
			ret.add(getSeedDrops());
		return ret;
	}
	
	public ItemStack getSeedDrops() {
		if(type.equals("middleFruitBush"))
			return new ItemStack(BLItemRegistry.middleFruitSeeds, 1, 0);
		return null;	
	}
		
	public ItemStack getCropDrops() {
		if(type.equals("middleFruitBush"))
			return new ItemStack(BLItemRegistry.middleFruit, 1, 0);
		return null;	
	}
	
	@Override
	protected Item func_149866_i() { //disabled for custom BL bits
		return null;
	}

	@Override
	protected Item func_149865_P() { //disabled for custom BL bits
		return null;
	}

	@Override
    public Item getItemDropped(int meta, Random rand, int amount) { //disabled for custom BL bits
        return null;
    }

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		int meta = world.getBlockMetadata(x, y -1, z);
		return soil != null && soil instanceof BlockFarmedDirt && meta >= 4 && meta <= 10;
	}

	@Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == BLBlockRegistry.farmedDirt;
    }
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack != null && !(meta >= 8)) {
			if (stack.getItem() == BLItemRegistry.materialCrushed && stack.getItemDamage() == EnumMaterialsCrushed.GROUND_DRIED_SWAMP_REED.ordinal()) {
				if (ItemDye.applyBonemeal(stack, world, x, y, z, player))
					if (!world.isRemote)
						world.playAuxSFX(2005, x, y, z, 0);
				return true;
			}
		}
		if (stack != null && stack.getItem() == BLItemRegistry.materialsBL && stack.getItemDamage() == EnumMaterialsBL.PLANT_TONIC.ordinal()) {
			int metaDirt = world.getBlockMetadata(x, y - 1 , z);
			if (!world.isRemote) {
				if (meta >= 8)
					world.setBlockMetadataWithNotify(x, y, z, meta - 1 , 3);
				world.playAuxSFX(2005, x, y, z, 0);
				if(metaDirt == 7 || metaDirt == 8)
					world.setBlockMetadataWithNotify(x, y - 1, z, metaDirt - 3, 3);
			}
			if(!player.capabilities.isCreativeMode) {
				stack.stackSize--;
				if (!player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.weedwoodBucket)))
					player.dropPlayerItemWithRandomChoice(new ItemStack(BLItemRegistry.weedwoodBucket), false);
			}
			return true;
		}
		return true;
	}
	
	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int id, EntityPlayer player) {
		int meta = world.getBlockMetadata(x, y, z);
		int metaDirt = world.getBlockMetadata(x, y - 1, z);
		if (meta >= 7) {
			if (metaDirt == 10)
				world.setBlockMetadataWithNotify(x, y - 1, z, 9, 3);
			if (metaDirt == 9)
				world.setBlockMetadataWithNotify(x, y - 1, z, 6, 3);
			if (metaDirt == 8)
				world.setBlockMetadataWithNotify(x, y - 1, z, 2, 3);
			if (metaDirt == 7)
				world.setBlockMetadataWithNotify(x, y - 1, z, 1, 3);
			if (metaDirt == 6)
				world.setBlockMetadataWithNotify(x, y - 1, z, 1, 3);
			if (metaDirt == 5)
				world.setBlockMetadataWithNotify(x, y - 1, z, 2, 3);
			if (metaDirt == 4)
				world.setBlockMetadataWithNotify(x, y - 1, z, 1, 3);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		int metaDirt = world.getBlockMetadata(x, y - 1, z);
		if (world.getBlockLightValue(x, y + 1, z) >= 9) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta < 7 && metaDirt <= 6) {
				if (rand.nextInt(25) == 0) {
					++meta;
					world.setBlockMetadataWithNotify(x, y, z, meta, 3);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (meta < 7) {
			if (meta == 6)
				meta = 5;
			return iconArray[meta >> 1];
		} else if (meta == 8)
			return decayedTexture;
		else
			return iconArray[3];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconArray = new IIcon[4];
		decayedTexture = iconRegister.registerIcon("thebetweenlands:" + type + "Decay");
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
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double pixel = 0.0625D;
		int meta = world.getBlockMetadata(x, y, z);
		if (meta >= 8) {
			if (rand.nextInt(10) == 0) {
				for (int l = 0; l <= 5; l++) {
					double particleX = x + rand.nextFloat();
					double particleY = y + rand.nextFloat();
					double particleZ = z + rand.nextFloat();

					if (l == 0 && !world.getBlock(x, y + 2, z).isOpaqueCube())
						particleY = y + 1 + pixel;

					if (l == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube())
						particleY = y - pixel;

					if (l == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube())
						particleZ = z + 1 + pixel;

					if (l == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube())
						particleZ = z - pixel;

					if (l == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube())
						particleX = x + 1 + pixel;

					if (l == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube())
						particleX = x - pixel;

					if (particleX < x || particleX > x + 1 || particleY < y || particleY > y + 1 || particleZ < z || particleZ > z + 1) {
						TheBetweenlands.proxy.spawnCustomParticle("dirtDecay", world, particleX, particleY, particleZ, 0, 0, 0, 0);
					}
				}
			}
		}
	}
}