package thebetweenlands.items.tools;

import java.math.BigDecimal;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemWeedwoodBucketInfusion extends Item {
	public ItemWeedwoodBucketInfusion() {
		this.setMaxStackSize(1);
		this.setUnlocalizedName("thebetweenlands.weedwoodBucketInfusion");
		this.setTextureName("thebetweenlands:weedwoodBucketInfusion");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		if (hasTag(stack)) {
			if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("infused") && stack.stackTagCompound.hasKey("ingredients") && stack.stackTagCompound.hasKey("infusionTime")) {
				int infusionTime = stack.stackTagCompound.getInteger("infusionTime");
				String infusionTimeSeconds = BigDecimal.valueOf(infusionTime / 20.0F).setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
				list.add(EnumChatFormatting.GREEN + "Infusion time: " + EnumChatFormatting.RESET + infusionTimeSeconds);
				list.add(EnumChatFormatting.GREEN + "Ingredients:");
				// The properties will be retrieved in the Alembic's TE logic
				NBTTagList nbtList = (NBTTagList)stack.stackTagCompound.getTag("ingredients");
				for(int i = 0; i < nbtList.tagCount(); i++) {
					list.add(ItemStack.loadItemStackFromNBT(nbtList.getCompoundTagAt(i)).getDisplayName());
				}
			} else {
				list.add("This Infusion Contains Nothing");
			}
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		stack.setTagCompound(new NBTTagCompound());
	}

	private boolean hasTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return false;
		}
		return true;
	}
}
