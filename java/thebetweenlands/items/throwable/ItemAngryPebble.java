package thebetweenlands.items.throwable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.entities.projectiles.EntityAngryPebble;
import thebetweenlands.manual.gui.entries.IManualEntryItem;

public class ItemAngryPebble extends Item implements IManualEntryItem {

	public ItemAngryPebble() {
		super();
		setUnlocalizedName("thebetweenlands.angryPebble");
		setTextureName("thebetweenlands:angryPebble");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode)
			--itemstack.stackSize;
		world.playSoundAtEntity(player, "thebetweenlands:sorry", 0.7F, 0.8F);

		if (!world.isRemote)
			world.spawnEntityInWorld(new EntityAngryPebble(world, player));

		return itemstack;
	}

	@Override
	public String manualName(int meta) {
		return "angryPebble";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[0];
	}

	@Override
	public int[] metas() {
		return new int[0];
	}
}