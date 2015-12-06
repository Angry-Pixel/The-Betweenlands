package thebetweenlands.blocks.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.blocks.plants.crops.BlockBLGenericCrop;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.block.ItemBlockGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.tools.ItemSpadeBL;
import thebetweenlands.proxy.ClientProxy;

public class BlockFarmedDirt extends Block implements ISubBlocksBlock {

	public static final String[] iconPaths = new String[] { "purifiedSwampDirt", "dugSwampDirt", "dugSwampGrass", "dugPurifiedSwampDirt", "fertDirt", "fertGrass", "fertPurifiedSwampDirt", "fertDirtDecayed", "fertGrassDecayed" };
	public static final int PURE_SWAMP_DIRT = 0, DUG_SWAMP_DIRT = 1, DUG_SWAMP_GRASS = 2, DUG_PURE_SWAMP_DIRT = 3, FERT_DIRT = 4, FERT_GRASS = 5, FERT_PURE_SWAMP_DIRT_MIN = 6, FERT_DIRT_DECAYED = 7, FERT_GRASS_DECAYED = 8, FERT_PURE_SWAMP_DIRT_MID = 9, FERT_PURE_SWAMP_DIRT_MAX = 10;
	public static final int COMPOSTING_MODIFIER = 3, DECAY_CURE = 3, DECAY_CAUSE = 3;
	public static final int MATURE_CROP = 7, DECAYED_CROP = 8;
	public static int DECAY_TIME = 150, INFECTION_CHANCE = 6, DUG_SOIL_REVERT_TIME = 12;

	@SideOnly(Side.CLIENT)
	private IIcon iconPureSwampDirt, iconDugSwampGrassMap, iconDugSwampDirtMap, iconDugPurifiedSwampDirtMap, iconCompostedSwampGrassMap, iconCompostedSwampDirtMap, iconCompostedPurifiedSwampDirtMap, iconDecayedSwampGrassMap, iconDecayedSwampDirtMap, iconDecayedPurifiedSwampDirtMap;

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
		if (stack !=null && stack.getItem() instanceof ItemSpadeBL) {
			if (!world.isRemote) {
				if(meta == PURE_SWAMP_DIRT) {
					world.setBlockMetadataWithNotify(x, y, z, DUG_PURE_SWAMP_DIRT, 3);
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
					world.playAuxSFXAtEntity(null, 2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
					stack.damageItem(1, player);
				}
			}
			return true;
		}	
		if (stack != null && stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.COMPOST.id) {
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
		if (stack != null && stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.PLANT_TONIC.id) {
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
		world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if(meta == PURE_SWAMP_DIRT || meta == DUG_PURE_SWAMP_DIRT || meta == FERT_PURE_SWAMP_DIRT_MIN || meta == FERT_PURE_SWAMP_DIRT_MID ||meta == FERT_PURE_SWAMP_DIRT_MAX)
			drops.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
		if(meta == DUG_SWAMP_DIRT || meta == FERT_DIRT || meta == FERT_DIRT_DECAYED || meta == DUG_SWAMP_GRASS || meta == FERT_GRASS || meta == FERT_GRASS_DECAYED)
			drops.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.swampDirt), 1, 0));

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

		//Spread decay to adjacent blocks
		if(isDecayed(meta)) {
			if(rand.nextInt(INFECTION_CHANCE) == 0) {
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						if((xo == 0 && zo == 0) || (zo != 0 && xo != 0) || rand.nextInt(3) != 0) continue;
						Block adjacentBlock = world.getBlock(x+xo, y, z+zo);
						int adjacentMeta = world.getBlockMetadata(x+xo, y, z+zo);
						if(adjacentBlock == this && (adjacentMeta == FERT_DIRT || adjacentMeta == FERT_GRASS) && !isDecayed(adjacentMeta)) {
							world.setBlockMetadataWithNotify(x+xo, y, z+zo, adjacentMeta + DECAY_CAUSE, 3);
							//Update decay to plants above
							if(getCropAboveBlock(world, x+xo, y, z+zo) instanceof BlockBLGenericCrop && getCropAboveBlockDamageValue(world, x+xo, y, z+zo) == MATURE_CROP)
								world.setBlockMetadataWithNotify(x+xo, y + 1, z+zo, DECAYED_CROP, 3);
						}
					}
				}
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
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.iconPureSwampDirt = reg.registerIcon("thebetweenlands:dugDirt/purifiedSwampDirt");

		this.iconDugSwampGrassMap = reg.registerIcon("thebetweenlands:dugDirt/dugSwampGrassMap");
		this.iconDugSwampDirtMap = reg.registerIcon("thebetweenlands:dugDirt/dugSwampDirtMap");
		this.iconDugPurifiedSwampDirtMap = reg.registerIcon("thebetweenlands:dugDirt/dugPurifiedSwampDirtMap");

		this.iconCompostedSwampGrassMap = reg.registerIcon("thebetweenlands:dugDirt/fertSwampGrassMap");
		this.iconCompostedSwampDirtMap = reg.registerIcon("thebetweenlands:dugDirt/fertSwampDirtMap");
		this.iconCompostedPurifiedSwampDirtMap = reg.registerIcon("thebetweenlands:dugDirt/fertPurifiedSwampDirtMap");

		this.iconDecayedSwampGrassMap = reg.registerIcon("thebetweenlands:dugDirt/decayedSwampGrassMap");
		this.iconDecayedSwampDirtMap = reg.registerIcon("thebetweenlands:dugDirt/decayedSwampDirtMap");
		this.iconDecayedPurifiedSwampDirtMap = reg.registerIcon("thebetweenlands:dugDirt/decayedPurifiedSwampDirtMap");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch(meta) {
		case PURE_SWAMP_DIRT:
			return this.iconPureSwampDirt;
		case DUG_PURE_SWAMP_DIRT:
			return side == 1 ? this.iconDugPurifiedSwampDirtMap : this.iconPureSwampDirt;
		case FERT_PURE_SWAMP_DIRT_MIN:
		case FERT_PURE_SWAMP_DIRT_MID:
		case FERT_PURE_SWAMP_DIRT_MAX:
			return side == 1 ? this.iconCompostedPurifiedSwampDirtMap : this.iconPureSwampDirt;
		case DUG_SWAMP_DIRT:
			return side == 1 ? this.iconDugSwampDirtMap : BLBlockRegistry.swampDirt.getIcon(side, 0);
		case FERT_DIRT:
			return side == 1 ? this.iconCompostedSwampDirtMap : BLBlockRegistry.swampDirt.getIcon(side, 0);
		case FERT_DIRT_DECAYED:
			return side == 1 ? this.iconDecayedSwampDirtMap : BLBlockRegistry.swampDirt.getIcon(side, 0);
		case DUG_SWAMP_GRASS:
			return side == 1 ? this.iconDugSwampGrassMap : BLBlockRegistry.swampGrass.getIcon(side, 0);
		case FERT_GRASS:
			return side == 1 ? this.iconCompostedSwampGrassMap : BLBlockRegistry.swampGrass.getIcon(side, 0);
		case FERT_GRASS_DECAYED:
			return side == 1 ? this.iconDecayedSwampGrassMap : BLBlockRegistry.swampGrass.getIcon(side, 0);
		}
		return BLBlockRegistry.swampDirt.getIcon(side, 0);
	}

	public static boolean isDecayed(int meta) {
		return meta == FERT_GRASS_DECAYED || meta == FERT_DIRT_DECAYED;
	}

	public static boolean isFertilized(int meta) {
		return meta == FERT_PURE_SWAMP_DIRT_MIN || meta == FERT_PURE_SWAMP_DIRT_MID || meta == FERT_PURE_SWAMP_DIRT_MAX ||
				meta == FERT_DIRT || meta == FERT_GRASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item id, CreativeTabs tab, List list) {
		for (int i = 0; i < 9; i++)
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
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double pixel = 0.0625D;
		int meta = getDamageValue(world, x, y, z);
		if (meta == FERT_DIRT_DECAYED || meta == FERT_GRASS_DECAYED) {
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
	public int getRenderType() {
		return ClientProxy.BlockRenderIDs.FARMED_DIRT.id(); 
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return side == 1 ? false : super.shouldSideBeRendered(world, x, y, z, side);
	}
}
