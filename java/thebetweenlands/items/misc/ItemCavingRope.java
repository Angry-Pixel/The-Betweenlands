package thebetweenlands.items.misc;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.entities.EntityRopeNode;

public class ItemCavingRope extends Item {
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			EntityRopeNode connectedRopeNode = null;
			for(Entity e : (List<Entity>) world.loadedEntityList) {
				if(e instanceof EntityRopeNode) {
					EntityRopeNode ropeNode = (EntityRopeNode) e;
					if(ropeNode.getNextNode() == player) {
						connectedRopeNode = ropeNode;
						break;
					}
				}
			}
			if(connectedRopeNode == null) {
				EntityRopeNode ropeNode = new EntityRopeNode(world);
				ropeNode.setLocationAndAngles(x + hitX, y + hitY, z + hitZ, 0, 0);
				ropeNode.setNextNode(player);
				world.spawnEntityInWorld(ropeNode);
				--stack.stackSize;
			} else {
				if(connectedRopeNode.getDistance(x + hitX, y + hitY, z + hitZ) > EntityRopeNode.ROPE_LENGTH) {
					player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("chat.rope.too_far")));
					return false;
				} else {
					connectedRopeNode.extendRope(player, x + hitX, y + hitY, z + hitZ);
					--stack.stackSize;
				}
			}
		}
		return true;
	}
}
