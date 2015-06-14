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
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.block.ItemBlockGeneric;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFarmedDirt extends Block implements ISubBlocksBlock {

	public static final String[] iconPaths = new String[] { "purifiedSwampDirt", "dugSwampDirt", "dugSwampGrass", "dugPurifiedSwampDirt", "fertDirt", "fertGrass", "fertPurifiedSwampDirt", "fertDirtDecayed", "fertGrassDecayed" };
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	private IIcon sideIcon;
	
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
		if (world.isRemote)
			return true;

			int meta = world.getBlockMetadata(x, y, z);
			ItemStack stack = player.getCurrentEquippedItem();

			if (stack.getItem() instanceof SpadeBL) {
				if(meta == 0) {
					world.setBlockMetadataWithNotify(x, y, z, 3, 3);
					world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
					player.getCurrentEquippedItem().damageItem(1, player);
				}
				return true;
			}
			
			if (stack.getItem() == BLItemRegistry.materialsBL && stack.getItemDamage() == EnumMaterialsBL.COMPOST.ordinal()) {
				if(meta >= 1 && meta <= 3) {
					world.setBlockMetadataWithNotify(x, y, z, meta + 3, 3);
					if (!world.isRemote)
						world.playAuxSFX(2005, x, y + 1, z, 0);
					world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
					player.getCurrentEquippedItem().stackSize--;
				}
				return true;
			}
		return true;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		
		if(meta == 0 || meta == 3 ||meta == 6)
			drops.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
		if(meta == 1 || meta == 4 || meta == 7)
			drops.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.swampDirt), 1, 0));
		if(meta == 2 || meta == 5 || meta == 8)
			drops.add(new ItemStack(Item.getItemFromBlock(BLBlockRegistry.swampGrass), 1, 0));
		
		return drops;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int meta = world.getBlockMetadata(x, y, z);
		if(world.rand.nextInt(100) == 0)
			if(meta == 5 || meta == 4) 
				world.setBlockMetadataWithNotify(x, y, z, meta + 3, 3);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		sideIcon = reg.registerIcon("thebetweenlands:swampDirt");
		icons = new IIcon[iconPaths.length];

		int i = 0;
		for (String path : iconPaths)
			icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= icons.length)
			return null;
		
		return meta == 0 ? icons[meta] : side == 1 && meta != 0 ? icons[meta] : sideIcon;
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

	@Override
	public Class<? extends ItemBlock> getItemBlockClass() {
		return ItemBlockGeneric.class;
	}

}
