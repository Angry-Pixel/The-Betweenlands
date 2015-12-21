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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.recipes.AnimatorRecipe;
import thebetweenlands.tileentities.TileEntityAnimator;

public class BlockAnimator extends BlockContainer {

	private final Random rand = new Random();

	public BlockAnimator() {
		super(Material.rock);
		setStepSound(soundTypeStone);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.animator");
		setBlockTextureName("thebetweenlands:betweenstone");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAnimator();
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot == 0 ? 2 : rot == 1 ? 5 : rot == 2 ? 3 : 4, 3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		if (world.getTileEntity(x, y, z) instanceof TileEntityAnimator) {
			TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(x, y, z);
			if (animator.fuelConsumed < animator.requiredFuelCount) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, x, y, z);
			} else {
				AnimatorRecipe recipe = AnimatorRecipe.getRecipe(animator.itemToAnimate);
				if(recipe == null || recipe.onRetrieved(animator, world, x, y, z)) {
					player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, x, y, z);
				}
				animator.fuelConsumed = 0;
			}
			animator.itemToAnimate = null;
			animator.itemAnimated = false;	
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.betweenstone.getIcon(0, 0);
	}

	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(x, y, z);
		if (animator != null) {
			for (int i1 = 0; i1 < animator.getSizeInventory(); ++i1) {
				ItemStack itemstack = animator.getStackInSlot(i1);

				if (itemstack != null) {
					float f = this.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
					float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {
						int j1 = this.rand.nextInt(21) + 10;

						if (j1 > itemstack.stackSize) {
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						EntityItem entityitem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1), (double) ((float) z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}

						float f3 = 0.05F;
						entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
						entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
						world.spawnEntityInWorld(entityitem);
					}
				}
			}

			world.func_147453_f(x, y, z, block);
		}

		super.breakBlock(world, x, y, z, block, meta);
	}
}
