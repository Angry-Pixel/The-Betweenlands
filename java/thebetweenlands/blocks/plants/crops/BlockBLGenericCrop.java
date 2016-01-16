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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockFarmedDirt;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.client.render.block.crops.CropRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockBLGenericCrop extends BlockCrops {
	public static final int MATURE_CROP = 7, DECAYED_CROP = 8;

	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;

	@SideOnly(Side.CLIENT)
	private CropRenderer cropRenderer;

	private String type;

	public BlockBLGenericCrop(String blockName) {
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.plants);
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
		if (metadata == MATURE_CROP) {
			for (int i = 0; i < world.rand.nextInt(4) + 1 + fortune; ++i) {
				if(getSeedDrops() != null) {
					if (world.rand.nextInt(15) <= metadata)
						ret.add(getSeedDrops());
				}
				if(getCropDrops() != null) ret.add(getCropDrops());
			}
		}
		if(getSeedDrops() != null) {
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
		if (stack != null && stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.PLANT_TONIC.id) {
			if (!world.isRemote) {
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						int currentMeta = world.getBlockMetadata(x+xo, y, z+zo);
						if (currentMeta == DECAYED_CROP) {
							world.setBlockMetadataWithNotify(x+xo, y, z+zo, currentMeta - 1, 3);
						}
						int metaDirt = world.getBlockMetadata(x+xo, y - 1, z+zo);
						if(BlockFarmedDirt.isDecayed(metaDirt)) {
							world.setBlockMetadataWithNotify(x+xo, y - 1, z+zo, metaDirt - BlockFarmedDirt.DECAY_CURE, 3);
						}
					}
				}
				world.playAuxSFX(2005, x, y, z, 0);
			}
			if(!player.capabilities.isCreativeMode) {
				stack.stackSize--;
				if (!player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.weedwoodBucket))) {
					player.dropPlayerItemWithRandomChoice(new ItemStack(BLItemRegistry.weedwoodBucket), false);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Whether this crop can be grown with a fertilizer
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta < MATURE_CROP;
	}

	/**
	 * After using a fertilizer this determines whether the grow successfully grows or not
	 */
	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		int chance = this.isCropOrSoilDecayed(world, x, y, z) ? 6 : 3;
		return rand.nextInt(chance * ((int)(world.getBlockMetadata(x, y, z) / 1.6D) + 1)) == 0;
	}

	/**
	 * Determines how much the crop grows after successfully using a fertilizer
	 */
	@Override
	public void func_149863_m(World world, int x, int y, int z) {
		this.onGrow(world, x, y, z, world.getBlockMetadata(x, y, z));
		int l = world.getBlockMetadata(x, y, z) + MathHelper.getRandomIntegerInRange(world.rand, 2, 4);
		if (l > 7) {
			l = 7;
		}
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
		world.playAuxSFX(2005, x, y, z, 0);
		if(this.isCropOrSoilDecayed(world, x, y, z) && this.isFullyGrown(world, x, y, z)) {
			world.setBlockMetadataWithNotify(x, y, z, DECAYED_CROP, 2);
		}
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int id, EntityPlayer player) {
		if(world.isRemote) return;
		boolean grown = this.isFullyGrown(world, x, y, z);
		if(!player.capabilities.isCreativeMode) this.harvestBlock(world, player, x, y, z, id);
		world.setBlock(x, y, z, Blocks.air);
		if(world.getBlock(x, y - 1, z) instanceof BlockFarmedDirt) {
			int metaDirt = world.getBlockMetadata(x, y - 1, z);
			if (grown) {
				switch(metaDirt) {
				case BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MAX:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MID, 3);
					break;
				case BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MID:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MIN, 3);
					break;
				case BlockFarmedDirt.FERT_PURE_SWAMP_DIRT_MIN:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.DUG_SWAMP_DIRT, 3);
					break;
				case BlockFarmedDirt.FERT_GRASS_DECAYED:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.DUG_SWAMP_GRASS, 3);
					break;
				case BlockFarmedDirt.FERT_DIRT_DECAYED:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.DUG_SWAMP_DIRT, 3);
					break;
				case BlockFarmedDirt.FERT_GRASS:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.DUG_SWAMP_GRASS, 3);
					break;
				case BlockFarmedDirt.FERT_DIRT:
					world.setBlockMetadataWithNotify(x, y - 1, z, BlockFarmedDirt.DUG_SWAMP_DIRT, 3);
					break;
				}
			}
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		int metaDirt = world.getBlockMetadata(x, y - 1, z);
		int meta = world.getBlockMetadata(x, y, z);
		this.onGrow(world, x, y, z, meta);
		if (!this.isFullyGrown(world, x, y, z) && BlockFarmedDirt.isFertilized(metaDirt)) {
			if (rand.nextInt(25) == 0) {
				++meta;
				world.setBlockMetadataWithNotify(x, y, z, meta, 3);
			}
		}
	}

	/**
	 * Returns whether the soil of the crop is decayed
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isCropOrSoilDecayed(World world, int x, int y, int z) {
		int meta = this.getSoilMetadata(world, x, y, z);
		if(meta == -1) return false;
		return BlockFarmedDirt.isDecayed(meta) || world.getBlockMetadata(x, y, z) == DECAYED_CROP;
	}

	/**
	 * Returns the metadata of the soil. Return -1 if there's no soil
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int getSoilMetadata(World world, int x, int y, int z) {
		return world.getBlock(x, y - 1, z) instanceof BlockFarmedDirt == false ? -1 : world.getBlockMetadata(x, y - 1, z);
	}

	/**
	 * Returns whether the crop is fully grown (decay stage included)
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isFullyGrown(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == MATURE_CROP || world.getBlockMetadata(x, y, z) == DECAYED_CROP;
	}

	/**
	 * Decays or purifies the crop
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param decayed
	 */
	public void setDecayed(World world, int x, int y, int z, boolean decayed) {
		if(!decayed) {
			if(world.getBlock(x, y, z) instanceof BlockBLGenericCrop && world.getBlockMetadata(x, y, z) == BlockBLGenericCrop.DECAYED_CROP) {
				world.setBlockMetadataWithNotify(x, y, z, BlockBLGenericCrop.DECAYED_CROP - 1, 2);
				((BlockBLGenericCrop)world.getBlock(x, y, z)).onCure(world, x, y, z);
			}
		} else {
			if(world.getBlock(x, y, z) instanceof BlockBLGenericCrop && world.getBlockMetadata(x, y, z) == BlockBLGenericCrop.MATURE_CROP) {
				world.setBlockMetadataWithNotify(x, y, z, BlockBLGenericCrop.DECAYED_CROP, 3);
				((BlockBLGenericCrop)world.getBlock(x, y, z)).onDecayed(world, x, y, z);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (meta <= MATURE_CROP) {
			if(meta == MATURE_CROP - 1) meta = MATURE_CROP - 2;
			return iconArray[meta >> 1];
		} else
			return iconArray[iconArray.length - 1];
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
		if (meta >= DECAYED_CROP) {
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

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		if(this.isFullyGrown(world, x, y, z) && this.isCropOrSoilDecayed(world, x, y, z)) {
			world.setBlockMetadataWithNotify(x, y, z, DECAYED_CROP, 2);
		}
	}

	/**
	 * Called when the crop decays
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void onDecayed(World world, int x, int y, int z) { }

	/**
	 * Called when the crop is cured
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void onCure(World world, int x, int y, int z) { }

	/**
	 * Called before the crop grows
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param prevMeta
	 * @param meta
	 */
	public void onGrow(World world, int x, int y, int z, int meta) { }

	/**
	 * Returns whether the soil (and crop) should decay this tick
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean shouldDecay(World world, int x, int y, int z) {
		return world.rand.nextInt(BlockFarmedDirt.DECAY_CHANCE) == 0;
	}
}