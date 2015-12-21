package thebetweenlands.blocks.container;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.recipes.CompostRecipe;
import thebetweenlands.tileentities.TileEntityCompostBin;

public class BlockCompostBin extends BlockContainer {
	public BlockCompostBin() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("thebetweenlands.compostBin");
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockTextureName("thebetweenlands:weedwood");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.weedwood.getIcon(0, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot == 0 ? 2 : rot == 1 ? 5 : rot == 2 ? 3 : 4, 3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;
		if (world.getTileEntity(x, y, z) instanceof TileEntityCompostBin) {
			TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(x, y, z);

			boolean open = tile.open;

			if(!player.isSneaking() && player.getCurrentEquippedItem() == null && tile.getTotalCompostedAmount() == 0) {
				if(open) {
					if(tile.hasCompostableItems()) {
						player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.close")));
					} else {
						player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.add")));
					}
				} else {
					if(tile.hasCompostableItems()) {
						player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.composting")));
					} else {
						player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.add")));
					}
				}
			} else if (player.isSneaking()) {
				tile.open = !tile.open;
				world.markBlockForUpdate(x, y, z);
				tile.markDirty();
			} else {
				if(player.getCurrentEquippedItem() != null) {
					if(open) {
						ItemStack stack = player.getCurrentEquippedItem();
						CompostRecipe compostRecipe = CompostRecipe.getCompostRecipe(stack);
						if (compostRecipe != null) {
							switch (tile.addItemToBin(stack, compostRecipe.compostAmount, compostRecipe.compostTime, true)) {
							case 1:
								tile.addItemToBin(stack, compostRecipe.compostAmount, compostRecipe.compostTime, false);
								if(!player.capabilities.isCreativeMode) player.inventory.decrStackSize(player.inventory.currentItem, 1);
								break;
							case -1:
							default:
								player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.full")));
								break;
							}
						} else {
							player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.not.compostable")));
						}
					} else {
						player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.open.add")));
					}
				} else if(!open && tile.getTotalCompostedAmount() != 0) {
					player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.open.get")));
				} else if(open && tile.getTotalCompostedAmount() != 0) {
					if(tile.removeCompost(TileEntityCompostBin.COMPOST_PER_ITEM)) {
						world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, ItemGeneric.createStack(EnumItemGeneric.COMPOST)));
					}
				}
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(x, y, z);
		if (tile != null) {
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null) {
					if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, stack);
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public int getRenderType() {
		return -1;
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
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCompostBin();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (world.getTileEntity(x, y, z) instanceof TileEntityCompostBin) {
			TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(x, y, z);
			if(!tile.open && tile.hasCompostableItems()) {
				if (rand.nextInt(5) == 0) {
					double pixel = 0.0625D;
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
}