package thebetweenlands.blocks.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityBLDualFurnace;

import java.util.Random;

public class BlockBLDualFurnace extends BlockContainer {
    private final Random rand = new Random();
    private final boolean active;
    private static boolean isBurning;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon frontIcon;

    public BlockBLDualFurnace(boolean isActive) {
        super(Material.rock);
        active = isActive;
        setHardness(3.5F);
        setStepSound(soundTypePiston);
        if(!isActive)
        	setCreativeTab(ModCreativeTabs.blocks);
    }

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(BLBlockRegistry.dualFurnaceBL);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (side == 1)
			return topIcon;
		switch(meta) {
		case 0:
			return side == 3 ? frontIcon : blockIcon;
		case 1:
			return side == 4 ? frontIcon : blockIcon;
		case 2:
			return side == 2 ? frontIcon : blockIcon;
		case 3:
			return side == 5 ? frontIcon : blockIcon;
		default :
			return blockIcon;
		}
	}

    @SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon("thebetweenlands:sulfurFurnaceSide");
        frontIcon = icon.registerIcon(active ? "thebetweenlands:sulfurFurnaceDualFrontOn" : "thebetweenlands:sulfurFurnaceDualFrontOff");
        topIcon = icon.registerIcon("thebetweenlands:sulfurFurnaceTop");
    }

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;

		else {
            TileEntityBLDualFurnace tileentityfurnace = (TileEntityBLDualFurnace)world.getTileEntity(x, y, z);
            if (tileentityfurnace != null)
            	player.openGui(TheBetweenlands.instance, CommonProxy.GUI_BL_DUAL_FURNACE, world, x, y, z);
            return true;
        }
    }

    public static void updateFurnaceBlockState(boolean inUse, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        isBurning = true;

        if (inUse)
            world.setBlock(x, y, z, BLBlockRegistry.dualFurnaceBLLit);
        else
            world.setBlock(x, y, z, BLBlockRegistry.dualFurnaceBL);

        isBurning = false;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);

        if (tileentity != null) {
            tileentity.validate();
            world.setTileEntity(x, y, z, tileentity);
        }
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBLDualFurnace();
    }

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack is) {
		int rot = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot, 3);
        if (is.hasDisplayName())
            ((TileEntityBLDualFurnace)world.getTileEntity(x, y, z)).getStackDisplayName(is.getDisplayName());
    }

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!isBurning) {
            TileEntityBLDualFurnace tileentityfurnace = (TileEntityBLDualFurnace)world.getTileEntity(x, y, z);

            if (tileentityfurnace != null) {
                for (int slot = 0; slot < tileentityfurnace.getSizeInventory(); ++slot) {
                    ItemStack itemstack = tileentityfurnace.getStackInSlot(slot);

                    if (itemstack != null) {
                        float xOffset = rand.nextFloat() * 0.8F + 0.1F;
                        float yOffset = rand.nextFloat() * 0.8F + 0.1F;
                        float zOffset = rand.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.stackSize > 0) {
                            int ammount = rand.nextInt(21) + 10;

                            if (ammount > itemstack.stackSize)
                                ammount = itemstack.stackSize;

                            itemstack.stackSize -= ammount;
                            EntityItem entityitem = new EntityItem(world, (double)((float)x + xOffset), (double)((float)y + yOffset), (double)((float)z + zOffset), new ItemStack(itemstack.getItem(), ammount, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());

                            float velocity = 0.05F;
                            entityitem.motionX = (double)((float)rand.nextGaussian() * velocity);
                            entityitem.motionY = (double)((float)rand.nextGaussian() * velocity + 0.2F);
                            entityitem.motionZ = (double)((float)rand.nextGaussian() * velocity);
                            world.spawnEntityInWorld(entityitem);
                        }
                    }
                }
                world.func_147453_f(x, y, z, block);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (active) {
            int meta = world.getBlockMetadata(x, y, z);
            float f = (float)x + 0.5F;
            float f1 = (float)y + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
            float f2 = (float)z + 0.5F;
            float f3 = 0.52F;
            float f4 = rand.nextFloat() * 0.6F - 0.3F;

            if (meta == 1) {
            	BLParticle.SULFUR_TORCH.spawn(world, (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D, 0);
                world.spawnParticle("flame", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
            else if (meta == 3) {
            	BLParticle.SULFUR_TORCH.spawn(world, (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D, 0);
                world.spawnParticle("flame", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
            else if (meta == 2) {
            	BLParticle.SULFUR_TORCH.spawn(world, (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D, 0);
                world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
            }
            else if (meta == 0) {
            	BLParticle.SULFUR_TORCH.spawn(world, (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D, 0);
                world.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
            }
        }
    }

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return Container.calcRedstoneFromInventory((IInventory)world.getTileEntity(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(BLBlockRegistry.dualFurnaceBL);
	}

}