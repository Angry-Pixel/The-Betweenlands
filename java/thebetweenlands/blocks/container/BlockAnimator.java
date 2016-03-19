package thebetweenlands.blocks.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
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
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.particles.EntityAnimatorFX;
import thebetweenlands.entities.particles.EntityAnimatorFX2;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.recipes.misc.AnimatorRecipe;
import thebetweenlands.tileentities.TileEntityAnimator;

public class BlockAnimator extends BlockContainer {

	private final Random rand = new Random();

	public BlockAnimator() {
		super(Material.rock);
		setStepSound(soundTypeStone);
		setCreativeTab(BLCreativeTabs.blocks);
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
		world.setBlockMetadataWithNotify(x, y, z, rot, 3);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		TileEntityAnimator te = (TileEntityAnimator) world.getTileEntity(x, y, z);
		if(te != null && te.isSlotInUse(0) && te.isCrystalInslot() && te.isSulfurInslot() && te.fuelConsumed < te.requiredFuelCount && te.isValidFocalItem()) {
			List<Vector3d> points = new ArrayList<Vector3d>();

			// Sulfur Particles
			if (te.getStackInSlot(2) != null) {
				rand = te.getWorldObj().rand;
				points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.6D - 0.3D, te.yCoord + 0.1D, te.zCoord + 0.5D + rand.nextFloat() * 0.6D - 0.3D));
				points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
				points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
				points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D, te.yCoord + 1.2, te.zCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D));
				points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1.5D, te.zCoord + 0.5D));
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.xCoord + 0.5D, te.yCoord, te.zCoord + 0.5D, 0, 0, 0, points, ItemGeneric.createStack(EnumItemGeneric.SULFUR).getIconIndex(), 0.01F));
			}

			// Life Crystal Particles
			if (te.getStackInSlot(1) != null) {
				points = new ArrayList<Vector3d>();
				points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.3D - 0.15D, te.yCoord + 0.5D, te.zCoord + 0.5D + rand.nextFloat() * 0.3D - 0.15D));
				points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
				points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
				points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D, te.yCoord + 1.2, te.zCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D));
				points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1.5D, te.zCoord + 0.5D));
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.xCoord + 0.5D, te.yCoord, te.zCoord + 0.5D, 0, 0, 0, points, new ItemStack(BLItemRegistry.lifeCrystal).getIconIndex(), 0.0003F));
			}

			int meta = te.getBlockMetadata();

			double xOff = 0;
			double zOff = 0;

			switch(meta) {
			case 0:
				xOff = -0.5F;
				zOff = 0.14F;
				break;
			case 1:
				xOff = -0.14F;
				zOff = -0.5F;
				break;
			case 2:
				xOff = 0.5F;
				zOff = -0.14F;
				break;
			case 3:
				xOff = 0.14F;
				zOff = 0.5F;
				break;
			}

			// Runes
			points = new ArrayList<Vector3d>();
			points.add(new Vector3d(te.xCoord + 0.5D + (rand.nextFloat()-0.5F) * 0.3D + xOff, te.yCoord + 0.9, te.zCoord + 0.5 + (rand.nextFloat()-0.5F) * 0.3D + zOff));
			points.add(new Vector3d(te.xCoord + 0.5D + (rand.nextFloat()-0.5F) * 0.3D + xOff, te.yCoord + 1.36, te.zCoord + 0.5 + (rand.nextFloat()-0.5F) * 0.3D + zOff));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1.5D, te.zCoord + 0.5D));
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX2(te.getWorldObj(), te.xCoord, te.yCoord + 0.9, te.zCoord + 0.65, 0, 0, 0, points));

			// Smoke
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySmokeFX(te.getWorldObj(), te.xCoord + 0.5 + rand.nextFloat() * 0.3D - 0.15D, te.yCoord + 0.3, te.zCoord + 0.5 + rand.nextFloat() * 0.3D - 0.15D, 0, 0, 0, (rand.nextFloat() / 2.0F) + 1F));
		}
	}
}
