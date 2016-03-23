package thebetweenlands.items.loot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.proxy.CommonProxy;

import java.util.List;
import java.util.Random;

/**
 * Created by Bart on 23/03/2016.
 */
public class ItemLore extends Item {
    private static final String[] pages = new String[]{"them", "1", "2", "3", "3"};

    public ItemLore() {
        this.maxStackSize = 1;
        setTextureName("thebetweenlands:lore");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String name = "";
        if (stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("name"))
            name = stack.getTagCompound().getString("name");
        return "item.thebetweenlands.lore" + (name.length() > 0 ? "." + name : "");
    }

    public void setRandomPageName(ItemStack stack, Random random) {
        setPageName(pages[random.nextInt(pages.length)], stack);
    }

    private void setPageName(String name, ItemStack stack) {
        if (stack != null && name != null && stack.getItem() == this) {
            if (stack.stackTagCompound == null) {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setString("name", name);
                stack.stackTagCompound = tagCompound;
            } else
                stack.stackTagCompound.setString("name", name);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean idk) {
        super.addInformation(stack, player, list, idk);
        String name = "";
        if (stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("name"))
            name = stack.getTagCompound().getString("name");
        list.add(name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        setRandomPageName(itemStack, new Random());
        if (itemStack != null && itemStack.getTagCompound().hasKey("name"))
            player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LORE, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        return itemStack;
    }


}
