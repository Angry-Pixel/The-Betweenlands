package thebetweenlands.blocks.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.SpawnEggs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityAnimator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAnimator extends BlockContainer {

	private final Random rand = new Random();

	public BlockAnimator() {
		super(Material.rock);
		setStepSound(soundTypeStone);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.animator");
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		if (world.getTileEntity(x, y, z) instanceof TileEntityAnimator) {
			TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(x, y, z);
			if (animator.itemsConsumed < animator.itemCount)
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, x, y, z);
			else {
				if (animator.getStackInSlot(0) != null) {
					if (animator.getStackInSlot(0).getItem() instanceof ItemMonsterPlacer) {
						Entity entity = null;
						if (animator.getStackInSlot(0).getItem() instanceof SpawnEggs)
							entity = SpawnEggs.getEntity(world, x, y, z, animator.getStackInSlot(0));
						else
							entity = EntityList.createEntityByID(animator.getStackInSlot(0).getItemDamage(), world);
						EntityLiving entityliving = (EntityLiving) entity;
						entity.setLocationAndAngles(x, y + 0.5D, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
						entityliving.rotationYawHead = entityliving.rotationYaw;
						entityliving.renderYawOffset = entityliving.rotationYaw;
						entityliving.onSpawnWithEgg((IEntityLivingData) null);
						world.spawnEntityInWorld(entityliving);
					} else {
						EntityItem entityitem = new EntityItem(world, x, y + 1D, z, animator.getStackInSlot(0));
						entityitem.motionX = 0;
						entityitem.motionZ = 0;
						entityitem.motionY = 0.11000000298023224D;
						world.spawnEntityInWorld(entityitem);
					}
				}
				animator.decrStackSize(0, 1);
				animator.itemsConsumed = 0;
			}
			animator.lifeDepleted = false;	
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
