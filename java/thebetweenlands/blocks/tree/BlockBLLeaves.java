package thebetweenlands.blocks.tree;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLLeaves extends BlockLeaves {

	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon fastIcon;
	@SideOnly(Side.CLIENT)
	private IIcon spoopyIcon;
	@SideOnly(Side.CLIENT)
	private IIcon spoopyFastIcon;
	private boolean placedByPlayer;

	private boolean hasSpoopyTexture = false;

	public BlockBLLeaves(String blockName) {
		setHardness(0.2F);
		setLightOpacity(1);
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	public BlockBLLeaves setHasSpoopyTexture(boolean spoopyTexture) {
		this.hasSpoopyTexture = spoopyTexture;
		return this;
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && this.hasSpoopyTexture) {
			return Minecraft.getMinecraft().gameSettings.fancyGraphics ? spoopyIcon : spoopyFastIcon;
		}
		return Minecraft.getMinecraft().gameSettings.fancyGraphics ? blockIcon : fastIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return !Minecraft.getMinecraft().gameSettings.fancyGraphics && world.getBlock(x, y, z) == this ? false : true;
	}

	@Override
	public boolean isOpaqueCube() {
		return Blocks.leaves.isOpaqueCube();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName() + "Fancy");
		fastIcon = reg.registerIcon(getTextureName() + "Fast");
		if(this.hasSpoopyTexture) {
			spoopyIcon = reg.registerIcon(getTextureName() + "FancySpoopy");
			spoopyFastIcon = reg.registerIcon(getTextureName() + "FastSpoopy");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	public String[] func_150125_e() {
		return null;
	}


	@Override
	public void dropBlockAsItemWithChance (World world, int x, int y, int z, int meta, float chance, int fortune) {
		if (!world.isRemote) {
			int dropChance = 35;

			if (fortune > 0){
				dropChance -= 2*fortune;
			}
			if(world.rand.nextInt(dropChance) == 0) {
				this.dropBlockAsItem(world, x, y, z, new ItemStack(this.getItemDropped(meta, world.rand, fortune), 1));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		world.setBlockMetadataWithNotify(x, y, z, 1, 2);
	}

	@Override
	public void beginLeavesDecay(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) != 1) {
			int i2 = world.getBlockMetadata(x, y, z);

			if ((i2 & 8) == 0) {
				world.setBlockMetadataWithNotify(x, y, z, i2 | 8, 4);
			}
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) | 8, 4);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(200) == 0) {
			if(world.isAirBlock(x, y-1, z)) {
				BLParticle.LEAF.spawn(world, x + rand.nextFloat(), y, z + rand.nextFloat());
			}
		}
	}
}