package thebetweenlands.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.blocks.BlockBLDoor;

public class ItemBLDoor extends Item {

	private final BlockBLDoor door;

	public ItemBLDoor(Block door) {
		this.door = (BlockBLDoor) door;
		setMaxStackSize(64);
		setCreativeTab(ModCreativeTabs.items);

		String name = ((BlockBLDoor) door).name;
		setTextureName("thebetweenlands:door_" + name);
		setUnlocalizedName("thebetweenlands.door_" + name);

		this.door.setItem(this);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return StatCollector.translateToLocal("tile.thebetweenlands.door" + door.name + ".name");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (side != 1)
			return false;
		else {
			y++;
			if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
				if (!door.canPlaceBlockAt(world, x, y, z))
					return false;
				else {
					ItemDoor.placeDoorBlock(world, x, y, z, MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3, door);
					stack.stackSize--;
					return true;
				}
			} else
				return false;
		}
	}
}