package thebetweenlands.common.item.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemFumigant extends Item {

	public ItemFumigant() {
		this.setMaxStackSize(64);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.fumigant"), 0));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
			removeSmell(player, stack);
		if (world.isRemote)
			for (int count = 0; count <= 5; ++count)
				BLParticles.FANCY_BUBBLE.spawn(world, player.posX + (world.rand.nextDouble() - 0.5D), player.posY + 1D + world.rand.nextDouble(), player.posZ + (world.rand.nextDouble() - 0.5D));

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	public static void removeSmell(EntityPlayer player, ItemStack stack) {
		IRotSmellCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
		if (cap != null) {
			if (cap.isSmellingBad()) {
				cap.setNotSmellingBad();
				cap.setImmune(Math.max(cap.getRemainingImmunityTicks(), 600));
				if (!player.capabilities.isCreativeMode)
					stack.shrink(1);
				if(player instanceof EntityPlayerMP)
					AdvancementCriterionRegistry.USED_FUMIGANT.trigger((EntityPlayerMP) player);
			}
		}
	}
}
