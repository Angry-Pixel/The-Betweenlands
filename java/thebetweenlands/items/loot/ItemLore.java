package thebetweenlands.items.loot;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.CommonProxy;

/**
 * Created by Bart on 23/03/2016.
 */
public class ItemLore extends Item {
	private static final String[] pages = new String[]{"them", "mutants", "shadows", "ruins", "heads", "tar", "dungeon", "pitstone", "tower", "fort"};

	public ItemLore() {
		this.maxStackSize = 1;
		setTextureName("thebetweenlands:lore");
		setUnlocalizedName("thebetweenlands.lore");
	}

	public static ItemStack createPageStack(Random random) {
		ItemStack stack = new ItemStack(BLItemRegistry.lore);
		stack = setRandomPageName(stack, random);
		return stack;
	}

	private static ItemStack setRandomPageName(ItemStack stack, Random random) {
		setPageName(pages[random.nextInt(pages.length)], stack);
		return stack;
	}

	private static ItemStack setPageName(String name, ItemStack stack) {
		if (stack != null && name != null && stack.getItem() == BLItemRegistry.lore) {
			if (stack.stackTagCompound == null) {
				NBTTagCompound tagCompound = new NBTTagCompound();
				tagCompound.setString("name", name);
				stack.stackTagCompound = tagCompound;
			} else
				stack.stackTagCompound.setString("name", name);
		}
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List items) {
		for(String page : this.pages) {
			ItemStack stack = new ItemStack(item, 1, 0);
			setPageName(page, stack);
			items.add(stack);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String name = "";
		if (stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("name"))
			name = stack.getTagCompound().getString("name");
		return "item.thebetweenlands.lore" + (name.length() > 0 ? "." + name : "");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (itemStack != null && itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("name"))
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LORE, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return itemStack;
	}


}
