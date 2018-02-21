package thebetweenlands.common.item.armor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.util.NBTHelper;

public class ItemMarshRunnerBoots extends ItemRubberBoots {
	private static final int MAX_WALK_TICKS = 30;

	public ItemMarshRunnerBoots() {
		super();
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return EnumItemMisc.RUBBER_BALL.isItemOf(material);
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		if(itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(itemStack);
		int walkTicksLeft = nbt.getInteger("walkTicksLeft");
		IBlockState blockBelowPlayer = world.getBlockState(player.getPosition().down());

		if(player.onGround && blockBelowPlayer.getBlock() instanceof BlockSwampWater) {
			player.motionX *= 1.0D / MAX_WALK_TICKS * walkTicksLeft;
			player.motionZ *= 1.0D / MAX_WALK_TICKS * walkTicksLeft;
		}

		if(!player.world.isRemote) {
			boolean playerOnGround = player.onGround && !player.isInWater() && blockBelowPlayer.getBlock() instanceof BlockSwampWater == false;
			if(walkTicksLeft == 0 || playerOnGround) {
				nbt.setInteger("walkTicksLeft", MAX_WALK_TICKS);
			} else {
				if(walkTicksLeft > 1) {
					nbt.setInteger("walkTicksLeft", --walkTicksLeft);
				}
			}
		}
	}

	public static boolean checkPlayerWalkOnWater(EntityPlayer player) {
		if(player.isSneaking() || ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.isActive(player)) return false;
		ItemStack boots = player.inventory.armorInventory.get(0);
		if(!boots.isEmpty() && boots.getItem() instanceof ItemMarshRunnerBoots) {
			if(boots.getTagCompound() != null && boots.getTagCompound().getInteger("walkTicksLeft") > 1) {
				return true;
			}
		}
		return false;
	}

	public static double getWalkPercentage(EntityPlayer player) {
		ItemStack boots = player.inventory.armorInventory.get(0);
		if(!boots.isEmpty() && boots.getItem() instanceof ItemMarshRunnerBoots) {
			if(boots.getTagCompound() != null) {
				return (double)boots.getTagCompound().getInteger("walkTicksLeft") / (double)MAX_WALK_TICKS;
			}
		}
		return 0.0D;
	}
}