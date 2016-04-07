package thebetweenlands.items.equipment;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.proxy.CommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLurkerSkinPouch extends Item {

	public ItemLurkerSkinPouch() {
		super();
		setMaxStackSize(1);
		setUnlocalizedName("thebetweenlands.lurkerSkinPouch");
		setTextureName("thebetweenlands:lurkerSkinPouch");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean showAdvancedInfo) {
		int meta = stack.getItemDamage();
		list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("lurkerSkinPouch.size", 9 + (meta * 9)));
		list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("lurkerSkinPouch.info"));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				int meta = stack.getItemDamage();
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH, world, meta, 0, 0);
			}
		}
		return stack;
	}

}
