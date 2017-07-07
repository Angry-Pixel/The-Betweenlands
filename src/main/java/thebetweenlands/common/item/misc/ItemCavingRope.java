package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityRopeNode;

public class ItemCavingRope extends Item {
	public ItemCavingRope() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
			EntityRopeNode connectedRopeNode = null;
			for(Entity e : (List<Entity>) world.loadedEntityList) {
				if(e instanceof EntityRopeNode) {
					EntityRopeNode ropeNode = (EntityRopeNode) e;
					if(ropeNode.getNextNodeByUUID() == player) {
						connectedRopeNode = ropeNode;
						break;
					}
				}
			}
			if(connectedRopeNode == null) {
				EntityRopeNode ropeNode = new EntityRopeNode(world);
				ropeNode.setLocationAndAngles(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, 0, 0);
				ropeNode.setNextNode(player);
				world.spawnEntity(ropeNode);
				world.playSound((EntityPlayer)null, ropeNode.posX, ropeNode.posY, ropeNode.posZ, SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
				stack.shrink(1);
			} else {
				if(connectedRopeNode.getDistance(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ) > EntityRopeNode.ROPE_LENGTH) {
					player.sendMessage(new TextComponentTranslation("chat.rope.tooFar"));
					
					return EnumActionResult.FAIL;
				} else {
					EntityRopeNode ropeNode = connectedRopeNode.extendRope(player, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ);
					world.playSound((EntityPlayer)null, ropeNode.posX, ropeNode.posY, ropeNode.posZ, SoundEvents.BLOCK_METAL_STEP, SoundCategory.PLAYERS, 1, 1.5F);
					stack.shrink(1);
				}
			}
		}
		
		return EnumActionResult.SUCCESS;
	}
}
