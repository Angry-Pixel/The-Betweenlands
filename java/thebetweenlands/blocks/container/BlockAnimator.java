package thebetweenlands.blocks.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityAnimator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAnimator extends BlockContainer {
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
			System.out.println(world.isRemote);
			TileEntityAnimator animator = (TileEntityAnimator) world.getTileEntity(x, y, z);
			if (animator.itemsConsumed < animator.stackSize) player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ANIMATOR, world, x, y, z);
			else{
				if(animator.getStackInSlot(0).getItem() instanceof ItemMonsterPlacer){
					Entity entity = EntityList.createEntityByID(animator.getStackInSlot(0).getItemDamage(), world);
					entity.posX = x;
					entity.posY = y + 0.5D;
					entity.posZ = z;
					world.spawnEntityInWorld(entity);
				}
				else{
			        EntityItem entityitem = new EntityItem(world, x, y + 1D, z, animator.getStackInSlot(0));
			        entityitem.motionX = 0;
			        entityitem.motionZ = 0;
			        entityitem.motionY = 0.11000000298023224D;
			        world.spawnEntityInWorld(entityitem);					
				}
				animator.decrStackSize(0, 1);
				animator.itemsConsumed = 0;
				animator.isDirty = true;
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.betweenstone.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}
}
