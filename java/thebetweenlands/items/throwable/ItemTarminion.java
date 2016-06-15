package thebetweenlands.items.throwable;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.entities.projectiles.EntityThrownTarminion;
import thebetweenlands.manual.IManualEntryItem;

public class ItemTarminion extends Item implements IManualEntryItem {

	public ItemTarminion() {
		super();
		maxStackSize = 16;
		setTextureName("paper");
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean isComplex) {
		tooltip.add(StatCollector.translateToLocal("tarminion.usage"));
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

	@Override
	public String manualName(int meta) {
		return "tarMinion";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2, 8};
	}

	@Override
	public int metas() {
		return 0;
	}
}