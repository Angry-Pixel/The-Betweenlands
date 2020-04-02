package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public class ItemDraeton extends Item {
	public ItemDraeton() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(facing == EnumFacing.UP) {
			if(!world.isRemote) {
				EntityDraeton draeton = new EntityDraeton(world);
				draeton.setLocationAndAngles(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, player.rotationYaw, 0);

				if(world.getCollisionBoxes(draeton, draeton.getEntityBoundingBox().grow(1, 0, 1).expand(0, 3, 0)).isEmpty()) {
					world.spawnEntity(draeton);
					player.getHeldItem(hand).shrink(1);
					player.addStat(StatList.getObjectUseStats(this));
					return EnumActionResult.SUCCESS;
				}

				return EnumActionResult.FAIL;
			}

			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}
