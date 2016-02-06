package thebetweenlands.blocks.plants;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.BLItemRegistry;


public class BlockBlubCappedMushroomStalk extends Block {
	public IIcon sides;
	public IIcon bot;
	public IIcon sideGround;

	public BlockBlubCappedMushroomStalk() {
		super(Material.wood);
		setStepSound(Block.soundTypeCloth);
		setHardness(0.2F);
		setBlockName("thebetweenlands.hugeMushroomStalk");
		setCreativeTab(BLCreativeTabs.plants);
		setLightLevel(1.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.sides = iconRegister.registerIcon("thebetweenlands:bulbCappedShroomStalk1");
		this.bot = iconRegister.registerIcon("thebetweenlands:bulbCappedShroomStalk2");
		this.sideGround = iconRegister.registerIcon("thebetweenlands:bulbCappedShroomStalk1Bottomy");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
		int meta = access.getBlockMetadata(x, y, z);
		if(side == 0 || side == 1)
			return this.bot;
		else if (meta == 1)
			return sideGround;
		else
			return sides;
	}

	@Override
	public IIcon getIcon(int side, int meta){
		if(side == 0 || side == 1)
			return this.bot;
		else if (meta == 1)
			return sideGround;
		else
			return sides;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(BLBlockRegistry.bulbCappedMushroom);
	}

	@Override
	public void dropBlockAsItemWithChance (World world, int x, int y, int z, int meta, float chance, int fortune) {
		if (!world.isRemote) {
			int dropChance = 2;

			if (fortune > 0){
				dropChance -= 2*fortune;
			}
			if(world.rand.nextInt(dropChance) <= 0) {
				this.dropBlockAsItem(world, x, y, z, new ItemStack(BLItemRegistry.bulbCappedMushroomItem, 1));
			}
		}
	}
}
