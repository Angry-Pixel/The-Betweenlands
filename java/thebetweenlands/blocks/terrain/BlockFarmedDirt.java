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
	public final int COMPOSTING_MODIFIER = 3, DECAY_CURE = 3, DECAY_CAUSE = 3;
	public final int MATURE_CROP = 7, DECAYED_CROP = 8;
	public int DECAY_TIME = 150, DUG_SOIL_REVERT_TIME = 10;

	@SideOnly(Side.CLIENT)
	public IIcon iconPureSwampDirt, iconHolePiece, iconConnectionPiece, iconCornerPiece, iconHalfCornerPiece, iconFertHolePiece, iconFertConnectionPiece, iconFertCornerPiece, iconFertHalfCornerPiece, iconDecayedHolePiece, iconDecayedConnectionPiece, iconDecayedCornerPiece, iconDecayedHalfCornerPiece;

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
		if (stack != null && stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.COMPOST.ordinal()) {
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
		if (stack != null && stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.PLANT_TONIC.ordinal()) {
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

		this.iconHolePiece = reg.registerIcon("thebetweenlands:dugDirt/hole");
		this.iconFertHolePiece = reg.registerIcon("thebetweenlands:dugDirt/fertHole");
		this.iconDecayedHolePiece = reg.registerIcon("thebetweenlands:dugDirt/decayedHole");

		this.iconConnectionPiece = reg.registerIcon("thebetweenlands:dugDirt/connection");
		this.iconFertConnectionPiece = reg.registerIcon("thebetweenlands:dugDirt/fertConnection");
		this.iconDecayedConnectionPiece = reg.registerIcon("thebetweenlands:dugDirt/decayedConnection");

		this.iconCornerPiece = reg.registerIcon("thebetweenlands:dugDirt/corner");
		this.iconFertCornerPiece = reg.registerIcon("thebetweenlands:dugDirt/fertCorner");
		this.iconDecayedCornerPiece = reg.registerIcon("thebetweenlands:dugDirt/decayedCorner");

		this.iconHalfCornerPiece = reg.registerIcon("thebetweenlands:dugDirt/halfCorner");
		this.iconFertHalfCornerPiece = reg.registerIcon("thebetweenlands:dugDirt/fertHalfCorner");
		this.iconDecayedHalfCornerPiece = reg.registerIcon("thebetweenlands:dugDirt/decayedHalfCorner");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch(meta) {
		case PURE_SWAMP_DIRT:
		case DUG_PURE_SWAMP_DIRT:
		case FERT_PURE_SWAMP_DIRT_MIN:
		case FERT_PURE_SWAMP_DIRT_MID:
		case FERT_PURE_SWAMP_DIRT_MAX:
			//pure
			return this.iconPureSwampDirt;
		case DUG_SWAMP_DIRT:
		case FERT_DIRT:
		case FERT_DIRT_DECAYED:
			//dirt
			return BLBlockRegistry.swampDirt.getIcon(side, 0);
		case DUG_SWAMP_GRASS:
		case FERT_GRASS:
		case FERT_GRASS_DECAYED:
			//grass
			return BLBlockRegistry.swampGrass.getIcon(side, 0);
		}
		return BLBlockRegistry.swampDirt.getIcon(side, 0);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getOverlayIcon(int piece, int meta) {
		switch(meta) {
		case PURE_SWAMP_DIRT:
		case DUG_PURE_SWAMP_DIRT:
		case DUG_SWAMP_DIRT:
		case DUG_SWAMP_GRASS:
			//normal
			switch(piece) {
			case 0:
				return this.iconHolePiece;
			case 1:
				return this.iconConnectionPiece;
			case 2:
				return this.iconCornerPiece;
			case 3:
				return this.iconHalfCornerPiece;
			}
			break;
		case FERT_PURE_SWAMP_DIRT_MIN:
		case FERT_PURE_SWAMP_DIRT_MID:
		case FERT_PURE_SWAMP_DIRT_MAX:
		case FERT_DIRT:
		case FERT_GRASS:
			//fert
			switch(piece) {
			case 0:
				return this.iconFertHolePiece;
			case 1:
				return this.iconFertConnectionPiece;
			case 2:
				return this.iconFertCornerPiece;
			case 3:
				return this.iconFertHalfCornerPiece;
			}
			break;
		case FERT_DIRT_DECAYED:
		case FERT_GRASS_DECAYED:
			//decayed
			switch(piece) {
			case 0:
				return this.iconDecayedHolePiece;
			case 1:
				return this.iconDecayedConnectionPiece;
			case 2:
				return this.iconDecayedCornerPiece;
			case 3:
				return this.iconDecayedHalfCornerPiece;
			}
			break;
		}
		return null;
	}

	public boolean isDecayed(int meta) {
		return meta == FERT_GRASS_DECAYED || meta == FERT_DIRT_DECAYED;
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
}
