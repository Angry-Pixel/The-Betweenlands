package thebetweenlands.blocks.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.blocks.plants.crops.BlockBLGenericCrop;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.block.ItemBlockGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFarmedDirt extends Block implements ISubBlocksBlock {

	public static final String[] iconPaths = new String[] { "purifiedSwampDirt", "dugSwampDirt", "dugSwampGrass", "dugPurifiedSwampDirt", "fertDirt", "fertGrass", "fertPurifiedSwampDirt", "fertDirtDecayed", "fertGrassDecayed" };
	public final int PURE_SWAMP_DIRT = 0, DUG_SWAMP_DIRT = 1, DUG_SWAMP_GRASS = 2, DUG_PURE_SWAMP_DIRT = 3, FERT_DIRT = 4, FERT_GRASS = 5, FERT_PURE_SWAMP_DIRT_MIN = 6, FERT_DIRT_DECAYED = 7, FERT_GRASS_DECAYED = 8, FERT_PURE_SWAMP_DIRT_MID = 9, FERT_PURE_SWAMP_DIRT_MAX = 10;
	public final int COMPOSTING_MODIFIER = 3, DECAY_CURE = 3, DECAY_CAUSE = 3;
	public final int MATURE_CROP = 7, DECAYED_CROP = 8;
	public int DECAY_TIME = 10, DUG_SOIL_REVERT_TIME = 10;
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	private IIcon sideIconDirt, sideIconGrass;

	public BlockFarmedDirt() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.farmedDirt");
		setTickRandomly(true);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
			int meta = getDamageValue(world, x, y, z);
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack !=null && stack.getItem() instanceof SpadeBL) {
				if (!world.isRemote) {
					if(meta == PURE_SWAMP_DIRT) {
						world.setBlockMetadataWithNotify(x, y, z, DUG_PURE_SWAMP_DIRT, 3);
						world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
						world.playAuxSFXAtEntity(null, 2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
						stack.damageItem(1, player);
					}
				}
				return true;
			}	
		if (stack != null && stack.getItem() == BLItemRegistry.materialsBL && stack.getItemDamage() == EnumMaterialsBL.COMPOST.ordinal()) {
			if (!world.isRemote) {
				if (meta == DUG_SWAMP_DIRT || meta == DUG_SWAMP_GRASS) {
					world.setBlockMetadataWithNotify(x, y, z, meta + COMPOSTING_MODIFIER, 3);
					playCompostEffects(world, x, y, z);
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
				}
				if (meta == DUG_PURE_SWAMP_DIRT) {
					world.setBlockMetadataWithNotify(x, y, z, FERT_PURE_SWAMP_DIRT_MAX, 3);
					playCompostEffects(world, x, y, z);
					if (!player.capabilities.isCreativeMode)
						stack.stackSize--;
				}
			}
			return true;
		}
		if (stack != null && stack.getItem() == BLItemRegistry.materialsBL && stack.getItemDamage() == EnumMaterialsBL.PLANT_TONIC.ordinal()) {
			if (!world.isRemote) {
				if (meta == FERT_DIRT_DECAYED || meta == FERT_GRASS_DECAYED)
					world.setBlockMetadataWithNotify(x, y, z, meta - DECAY_CURE, 3);
			}
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
				if (!player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.weedwoodBucket)))
					player.dropPlayerItemWithRandomChoice(new ItemStack(BLItemRegistry.weedwoodBucket), false);
			}
			return true;
		}
		return false;
	}

	public void playCompostEffects(World world, int x, int y, int z) {
		world.playAuxSFX(2005, x, y + 1, z, 0);
		world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if(meta == PURE_SWAMP_DIRT || meta == DUG_PURE_SWAMP_DIRT || meta == FERT_PURE_SWAMP_DIRT_MIN || meta == FERT_PURE_SWAMP_DIRT_MID ||meta == FERT_PURE_SWAMP_DIRT_MAX)
			drops.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
		if(meta == DUG_SWAMP_DIRT || meta == FERT_DIRT || meta == FERT_DIRT_DECAYED)
			drops.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.swampDirt), 1, 0));
		if(meta == DUG_SWAMP_GRASS || meta == FERT_GRASS || meta == FERT_GRASS_DECAYED)
			drops.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.swampGrass), 1, 0));
		
		return drops;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int meta = getDamageValue(world, x, y, z);
		//Decay rate of composted blocks
		if(world.rand.nextInt(DECAY_TIME) == 0) {
			if(meta == FERT_DIRT || meta == FERT_GRASS) {
				world.setBlockMetadataWithNotify(x, y, z, meta + DECAY_CAUSE, 3);
				//Update decay to plants above
				if(getCropAboveBlock(world, x, y, z) instanceof BlockBLGenericCrop && getCropAboveBlockDamageValue(world, x, y, z) == MATURE_CROP)
					world.setBlockMetadataWithNotify(x, y + 1, z, DECAYED_CROP, 3);
			}
		}

		//Dug dirt reverts to un-dug
		if(world.rand.nextInt(DUG_SOIL_REVERT_TIME) == 0) {
			if(meta == DUG_SWAMP_DIRT || meta == DUG_PURE_SWAMP_DIRT)
				world.setBlock(x, y, z, BLBlockRegistry.swampDirt, 0, 3);
			if(meta == DUG_SWAMP_GRASS)
				world.setBlock(x, y, z, BLBlockRegistry.swampGrass, 0, 3);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		sideIconDirt = reg.registerIcon("thebetweenlands:swampDirt");
		sideIconGrass = reg.registerIcon("thebetweenlands:swampGrassSide");
		icons = new IIcon[iconPaths.length];
		int i = 0;
		for (String path : iconPaths)
			icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < PURE_SWAMP_DIRT || meta >= icons.length + 2)
			return null;
		if(meta == DUG_PURE_SWAMP_DIRT || meta == FERT_PURE_SWAMP_DIRT_MIN)
			if (side == 1)
				return icons[meta];
			else
				return icons[PURE_SWAMP_DIRT];
		if(meta == FERT_PURE_SWAMP_DIRT_MID || meta == FERT_PURE_SWAMP_DIRT_MAX)
			if (side == 1)
				return icons[FERT_PURE_SWAMP_DIRT_MIN];
			else
				return icons[PURE_SWAMP_DIRT];
		if(meta == DUG_SWAMP_DIRT || meta == FERT_DIRT || meta == FERT_DIRT_DECAYED)
			if (side == 1)
				return icons[meta];
			else
				return sideIconDirt;
		if(meta == DUG_SWAMP_GRASS || meta == FERT_GRASS || meta == FERT_GRASS_DECAYED)
			if (side == 1)
				return icons[meta];
			else
				return sideIconGrass;
		return icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < icons.length; i++)
			list.add(new ItemStack(id, 1, i));
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	public Block getCropAboveBlock(World world, int x, int y, int z) {
		return world.getBlock(x, y + 1, z);
	}

	public int getCropAboveBlockDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y + 1, z);
	}

	@Override
	public Class<? extends ItemBlock> getItemBlockClass() {
		return ItemBlockGeneric.class;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double pixel = 0.0625D;
		int meta = getDamageValue(world, x, y, z);
		if (meta == FERT_DIRT_DECAYED || meta == FERT_GRASS_DECAYED) {
			if (rand.nextInt(10) == 0) {
				for (int l = 0; l < 4; l++) {
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
