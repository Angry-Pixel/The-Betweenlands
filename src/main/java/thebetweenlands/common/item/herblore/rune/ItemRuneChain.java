package thebetweenlands.common.item.herblore.rune;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.api.rune.impl.AbstractRune.Blueprint.InitiationPhase;
import thebetweenlands.api.rune.impl.RuneChainComposition.IAspectBuffer;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemRuneChain extends Item implements IRenamableItem {
	public ItemRuneChain() {
		this.setMaxStackSize(1);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			if (player.isSneaking()) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
				return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
			}

			if(this.updateRuneChainInitiation(player.getHeldItem(hand), player, InitiationPhase.USE)) {
				return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
			}
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		this.updateRuneChainInitiation(stack, entityIn, InitiationPhase.TICK);
	}

	protected boolean updateRuneChainInitiation(ItemStack stack, Entity user, InitiationPhase state) {
		IRuneChainUserCapability userCap = user.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

		if(userCap != null) {
			IRuneChainCapability chainCap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

			if(chainCap != null) {
				int id = chainCap.checkInitiationAndRun(userCap, state, composition -> {
					//TODO Temporary for debug
					final AspectContainer aspects = new AspectContainer();

					aspects.add(AspectRegistry.ORDANIIS, 1000000);
					aspects.add(AspectRegistry.FERGALAZ, 1000000);

					final IAspectBuffer buffer = type -> aspects;

					composition.setAspectBuffer(buffer);
				});
				return id >= 0;
			}
		}

		return false;
	}
}
