package thebetweenlands.blocks.plants.crops;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.client.render.block.crops.CropRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.herblore.ItemGenericCrushed.EnumItemGenericCrushed;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockBLGenericCrop extends BlockCrops {

	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;

	@SideOnly(Side.CLIENT)
	private CropRenderer cropRenderer;

	private String type;

	public BlockBLGenericCrop(String blockName) {
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	@SideOnly(Side.CLIENT)
	public CropRenderer getCropRenderer() {
		return this.cropRenderer;
	}

	/**
	 * Sets the crop models. Needs 5 stages, last stage is decayed.
	 * @param models Crop models
	 * @param textureDimensions Texture dimensions
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public BlockBLGenericCrop setCropModels(ModelBase[] models, int[] textureDimensions) {
		this.cropRenderer = new CropRenderer();
		this.cropRenderer.setCropModels(models, textureDimensions);
		return this;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (metadata == 7) {
			for (int i = 0; i < 3 + fortune; ++i) {
				if(getSeedDrops() != null) {
					if (world.rand.nextInt(15) <= metadata)
						ret.add(getSeedDrops());
				}
				if(getCropDrops() != null) ret.add(getCropDrops());
			}
		}
		if(getSeedDrops() != null) {
			if (metadata < 7)
				ret.add(getSeedDrops());
			if (metadata > 7)
				ret.add(getSeedDrops());
		}
		return ret;
	}

	public ItemStack getSeedDrops() {
		return null;	
	}

	public ItemStack getCropDrops() {
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
			if (stack.getItem() == BLItemRegistry.itemsGenericCrushed && stack.getItemDamage() == EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED.ordinal()) {
				if (ItemDye.applyBonemeal(stack, world, x, y, z, player))
					if (!world.isRemote)
						world.playAuxSFX(2005, x, y, z, 0);
				return true;
			}
		}
		if (stack != null && stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.PLANT_TONIC.ordinal()) {
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
		if (meta <= 7) {
			return iconArray[meta >> 1];
		} else
			return iconArray[4];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconArray = new IIcon[5];
		for (int i = 0; i < iconArray.length; ++i) {
			iconArray[i] = iconRegister.registerIcon("thebetweenlands:crops/" + type + i);
		}
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
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return this.cropRenderer != null ? BlockRenderIDs.CROP.id() : 1;
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
						BLParticle.DIRT_DECAY.spawn(world, particleX, particleY, particleZ, 0, 0, 0, 0);
					}
				}
			}
		}
	}
}