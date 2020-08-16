package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityFishBait;
import thebetweenlands.util.TranslationHelper;

public class ItemFishBait extends Item {

	public ItemFishBait() {
		setMaxStackSize(64);
		setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("sink_speed")) {
		//	tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fish_bait.saturation", stack.getTagCompound().getInteger("saturation")));
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fish_bait.sink_speed", stack.getTagCompound().getInteger("sink_speed")));
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fish_bait.dissolve_time", stack.getTagCompound().getInteger("dissolve_time")));
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fish_bait.range", stack.getTagCompound().getInteger("range")));
			tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.fish_bait.glowing", stack.getTagCompound().getBoolean("glowing")));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeldItem) {
		if(!world.isRemote) {
			// basic default here before crafting changes it
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
//			if (!stack.getTagCompound().hasKey("saturation"))
//				stack.getTagCompound().setInteger("saturation", 200);
			if (!stack.getTagCompound().hasKey("sink_speed"))
				stack.getTagCompound().setInteger("sink_speed", 3);
			if (!stack.getTagCompound().hasKey("dissolve_time"))
				stack.getTagCompound().setInteger("dissolve_time", 200);
			if (!stack.getTagCompound().hasKey("range"))
				stack.getTagCompound().setInteger("range", 1);
			if (!stack.getTagCompound().hasKey("glowing"))
				stack.getTagCompound().setBoolean("glowing", false);
		}
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {
		EntityFishBait entity = new EntityFishBait(world, location.posX, location.posY + ((double) location.getEyeHeight()* 0.75D - 0.10000000149011612D), location.posZ, stack);
		entity.motionX = location.motionX;
		entity.motionY = location.motionY;
		entity.motionZ = location.motionZ;
		entity.setPickupDelay(10);
//		entity.setBaitSaturation(stack.getTagCompound().getInteger("saturation"));
		entity.setBaitSinkSpeed(stack.getTagCompound().getInteger("sink_speed"));
		entity.setBaitDissolveTime(stack.getTagCompound().getInteger("dissolve_time"));
		entity.setBaitRange(stack.getTagCompound().getInteger("range"));
		entity.setGlowing(stack.getTagCompound().getBoolean("glowing"));
		return entity;
	}

}
