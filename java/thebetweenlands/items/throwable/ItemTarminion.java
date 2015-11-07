package thebetweenlands.items.throwable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import thebetweenlands.entities.projectiles.EntityThrownTarminion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTarminion extends Item {

	public ItemTarminion() {
		super();
		maxStackSize = 16;
		setTextureName("paper");
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isComplex) {
		tooltip.add(EnumChatFormatting.WHITE + "One use.");
		tooltip.add(EnumChatFormatting.WHITE + "Can be thrown");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode)
			is.stackSize--;
		world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if (!world.isRemote) {
			EntityThrownTarminion tarminion = new EntityThrownTarminion(world, player);
			world.spawnEntityInWorld(tarminion);
		}
		return is;
	}
}