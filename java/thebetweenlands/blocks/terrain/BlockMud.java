package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityLurker;
import thebetweenlands.entities.mobs.EntitySiltCrab;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.items.ItemRubberBoots;

import java.util.List;

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
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List aabblist, Entity entity) {
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(world, x, y, z);
        boolean canWalk = entity instanceof EntityPlayer && ItemRubberBoots.checkPlayerEffect((EntityPlayer)entity);
        if (axisalignedbb1 != null && aabb.intersectsWith(axisalignedbb1) && (entity instanceof IEntityBL || canWalk)) {
            aabblist.add(axisalignedbb1);
        }
    }
	
	@Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (!(entity instanceof EntityAngler) || !(entity instanceof EntitySiltCrab) || !(entity instanceof EntityLurker)) {
			entity.motionX *= 0.2D;
			entity.motionY *= 0.2D;
			entity.motionZ *= 0.2D;
		}
    }
}
