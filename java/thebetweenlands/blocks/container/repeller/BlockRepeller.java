package thebetweenlands.blocks.container.repeller;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.particles.EntityAnimatorFX2;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityRepeller;

public class BlockRepeller extends BlockContainer {
	public BlockRepeller() {
		super(Material.wood);
		this.setHardness(1.5F);
		this.setCreativeTab(BLCreativeTabs.blocks);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if(meta >= 4) {
			this.setBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 0.7F, 0.85F);
		} else {
			this.setBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 1.0F, 0.85F);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && world.isSideSolid(x, y - 1, z, ForgeDirection.UP) && world.isAirBlock(x, y + 1, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if(world.getBlockMetadata(x, y, z) >= 4 && world.getBlock(x, y - 1, z) == this) {
			return this.onBlockActivated(world, x, y - 1, z, player, metadata, hitX, hitY, hitZ);
		} else if(world.getBlockMetadata(x, y, z) < 4) {
			TileEntityRepeller tile = (TileEntityRepeller) world.getTileEntity(x, y, z);
			if(!player.isSneaking() && player.getHeldItem() != null) {
				if(player.getHeldItem().getItem() == BLItemRegistry.shimmerStone) {
					if(!tile.hasShimmerstone()) {
						tile.addShimmerstone();
						if(!player.capabilities.isCreativeMode) --player.getHeldItem().stackSize;
					}
					return true;
				} else if(player.getHeldItem().getItem() == BLItemRegistry.aspectVial) {
					if(tile.hasShimmerstone()) {
						List<Aspect> aspects = AspectManager.getDynamicAspects(player.getHeldItem());
						if(!aspects.isEmpty() && aspects.get(0).type == AspectRegistry.BYARIIS) {
							Aspect aspect = aspects.get(0);
							if(!world.isRemote && tile.addFuel(aspect.amount)) {
								if(!player.capabilities.isCreativeMode) player.setCurrentItemOrArmor(0, player.getHeldItem().getItem().getContainerItem(player.getHeldItem()));
							}
							return true;
						}
					} else if(world.isRemote) {
						player.addChatMessage(new ChatComponentTranslation("chat.repeller.shimmerstone.missing"));
					}
				} else if(player.getHeldItem().getItem() == BLItemRegistry.dentrothystVial) {
					if(player.getHeldItem().getItemDamage() == 0 || player.getHeldItem().getItemDamage() == 2) {
						ItemStack newStack = new ItemStack(BLItemRegistry.aspectVial, 1, player.getHeldItem().getItemDamage() == 0 ? 0 : 1);
						if(!world.isRemote) {
							AspectManager.addDynamicAspects(newStack, new Aspect(AspectRegistry.BYARIIS, tile.getFuel()));
							tile.emptyFuel();
						}
						--player.getHeldItem().stackSize;
						if(player.getHeldItem().stackSize <= 0)
							player.setCurrentItemOrArmor(0, null);
						if(!player.inventory.addItemStackToInventory(newStack))
							player.dropPlayerItemWithRandomChoice(newStack, false);
						return true;
					}
				}
			} else if(player.isSneaking() && player.getHeldItem() == null && tile.hasShimmerstone()) {
				tile.removeShimmerstone();
				ItemStack stack = new ItemStack(BLItemRegistry.shimmerStone, 1);
				if(!player.inventory.addItemStackToInventory(stack))
					player.dropPlayerItemWithRandomChoice(stack, false);
				return true;
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.isRemote)
			return;
		TileEntityRepeller tile = (TileEntityRepeller) world.getTileEntity(x, y, z);
		if(tile != null) {
			if(tile.hasShimmerstone()) {
				float f = 0.7F;
				double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, new ItemStack(BLItemRegistry.shimmerStone, 1));
				entityitem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityitem);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack is) {
		int rotation = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rotation, 3);
		world.setBlock(x, y + 1, z, this, rotation + 4, 3);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return (world.getBlock(x, y - 1, z) != this && world.isSideSolid(x, y - 1, z, ForgeDirection.UP) && world.getBlock(x, y + 1, z) == this) || 
				(world.getBlock(x, y - 1, z) == this && world.getBlockMetadata(x, y, z) >= 4);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		this.checkAndBreakBlock(world, x, y, z);
	}

	protected void checkAndBreakBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			world.setBlock(x, y, z, getBlockById(0), 0, 3);
		}
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

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(rand.nextInt(6) == 0) {
			int meta = world.getBlockMetadata(x, y, z);
			if(meta < 4) {
				TileEntityRepeller tile = (TileEntityRepeller) world.getTileEntity(x, y, z);
				if(tile != null && tile.isRunning())  {
					for(int i = 0; i < 20; i++) {
						double radius = Math.max(tile.getRadius(0.0F), 1.0D);
						float rndRot = (float) (rand.nextFloat() * Math.PI * 2.0F);
						double rotX = Math.sin(rndRot) * radius;
						double rotZ = Math.cos(rndRot) * radius;
						double xOff = Math.sin(meta / 2.0F * Math.PI) * 0.23F;
						double zOff = Math.cos(meta / 2.0F * Math.PI) * 0.23F;
						double centerX = x + 0.5F + xOff;
						double centerY = y + 1.3F;
						double centerZ = z + 0.5F - zOff;
						List<Vector3d> points = new ArrayList<Vector3d>();
						points.add(new Vector3d(centerX, centerY, centerZ));
						points.add(new Vector3d(centerX, centerY + radius, centerZ));
						points.add(new Vector3d(centerX + rotX, centerY + radius, centerZ + rotZ));
						points.add(new Vector3d(centerX + rotX, y + 0.1D, centerZ + rotZ));
						Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX2(world, centerX, centerY, centerZ, 0, 0, 0, points));
					}
				}
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return meta < 4 ? new TileEntityRepeller() : null;
	}
}
