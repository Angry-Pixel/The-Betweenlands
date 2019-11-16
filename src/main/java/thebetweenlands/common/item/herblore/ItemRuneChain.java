package thebetweenlands.common.item.herblore;

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
import thebetweenlands.api.rune.impl.RuneChainComposition;
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

			if(player.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null)) {
				IRuneChainUserCapability userCap = player.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

				ItemStack stack = player.getHeldItem(hand);

				if(stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null)) {
					IRuneChainCapability chainCap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

					RuneChainComposition.Blueprint bp = chainCap.getBlueprint();

					if(bp != null) {
						/*RuneChainComposition.Blueprint bp = new RuneChainComposition.Blueprint();

						bp.addNodeBlueprint(new RuneMarkArea.Blueprint());
						//bp.addNodeBlueprint(new NodeDestroyBlocks.Blueprint());
						bp.addNodeBlueprint(new RuneSelectGrass.Blueprint());
						bp.addNodeBlueprint(new RuneFire.Blueprint());

						System.out.println("Link mark -> grass 1: " + bp.link(1, 0, 0, 0));
						System.out.println("Link mark -> grass 2: " + bp.link(1, 1, 0, 1));
						System.out.println("Link mark -> destroy 1: " + bp.link(2, 0, 0, 0));
						//System.out.println("Link mark -> destroy 2: " + bp.link(2, 1, 0, 1));*/

						final RuneChainComposition composition = bp.create();

						final AspectContainer aspects = new AspectContainer();

						aspects.add(AspectRegistry.ORDANIIS, 10000);
						aspects.add(AspectRegistry.FERGALAZ, 10000);

						final IAspectBuffer buffer = type -> aspects;

						composition.setAspectBuffer(buffer);

						composition.run(userCap.getUser());

						userCap.setUpdating(userCap.addRuneChain(composition), true, true);
					}
				}
			}
		}

		return super.onItemRightClick(world, player, hand);
	}
}
