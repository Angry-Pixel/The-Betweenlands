package thebetweenlands.items;

import thebetweenlands.entities.EntityAngryPebble;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAngryPebble extends Item{
	
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player){
		if(!player.capabilities.isCreativeMode){
			--itemstack.stackSize;
		}
		world.playSoundAtEntity(player, "thebetweenlands:sorry", 0.7F, 0.8F);
		
		if(!world.isRemote){
			world.spawnEntityInWorld(new EntityAngryPebble(world, player));
		}
		
		return itemstack;
	}
}