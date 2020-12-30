package thebetweenlands.common.item.herblore.rune;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.api.runechain.IAspectBuffer;
import thebetweenlands.api.runechain.initiation.InitiationPhase;
import thebetweenlands.api.runechain.initiation.InitiationPhases;
import thebetweenlands.api.runechain.initiation.InteractionInitiationPhase;
import thebetweenlands.api.runechain.initiation.UseInitiationPhase;
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
		if(!world.isRemote && player.isSneaking()) {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		if(this.updateRuneChainInitiation(player.getHeldItem(hand), player, new UseInitiationPhase(), !world.isRemote)) {
			player.swingArm(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(this.updateRuneChainInitiation(player.getHeldItem(hand), player, new UseInitiationPhase(pos, facing, new Vec3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ)), !worldIn.isRemote)) {
			player.swingArm(hand);
			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if(this.updateRuneChainInitiation(stack, playerIn, new InteractionInitiationPhase(target), !playerIn.world.isRemote)) {
			playerIn.swingArm(hand);
			return true;
		}
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		this.updateRuneChainInitiation(stack, entityIn, InitiationPhases.TICK, !worldIn.isRemote);
	}

	protected boolean updateRuneChainInitiation(ItemStack stack, Entity user, InitiationPhase state, boolean runOnInitiation) {
		IRuneChainUserCapability userCap = user.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER, null);

		if(userCap != null) {
			IRuneChainCapability chainCap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

			if(chainCap != null) {
				int id = chainCap.checkInitiation(userCap, state, composition -> {
					//TODO Temporary for debug
					final AspectContainer aspects = new AspectContainer();

					aspects.add(AspectRegistry.ORDANIIS, 1000000);
					aspects.add(AspectRegistry.FERGALAZ, 1000000);

					final IAspectBuffer buffer = type -> aspects;

					composition.setAspectBuffer(buffer);
				}, runOnInitiation);
				return id >= 0;
			}
		}

		return false;
	}
}
