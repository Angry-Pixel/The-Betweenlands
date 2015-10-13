package thebetweenlands.blocks.terrain;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityLurker;
import thebetweenlands.entities.mobs.EntitySiltCrab;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.items.ItemRubberBoots;

public class BlockMud
        extends Block
{
	public BlockMud() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.mud");
		setBlockTextureName("thebetweenlands:mud");
		setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	public boolean canEntityWalkOnMud(Entity entity) {
		boolean canWalk = entity instanceof EntityPlayer && ((EntityPlayer)entity).inventory.armorInventory[0] != null && ((EntityPlayer)entity).inventory.armorInventory[0].getItem() instanceof ItemRubberBoots;
		return entity instanceof IEntityBL || entity instanceof EntityItem || canWalk;
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List aabblist, Entity entity) {
		AxisAlignedBB blockAABB = this.getCollisionBoundingBoxFromPool(world, x, y, z);
        if (blockAABB != null && aabb.intersectsWith(blockAABB) && this.canEntityWalkOnMud(entity)) {
            if(entity instanceof IEntityBL || entity instanceof EntityItem) {
            	aabblist.add(blockAABB);
            } else {
            	if(world.isRemote)aabblist.add(AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1-0.125, z+1));
            }
        }
    }
	
	@Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (!this.canEntityWalkOnMud(entity)) {
			entity.motionX *= 0.2D;
			if(!entity.isInWater() && entity.motionY < 0) entity.motionY = -0.05D;
			entity.motionZ *= 0.2D;
			if(!entity.isInWater()) {
				entity.setInWeb();
			} else {
				entity.motionY *= 0.002D;
			}
			entity.onGround = true;
		}
    }
}
