package thebetweenlands.common.recipe.animator;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.item.misc.ItemRingOfGathering;
import thebetweenlands.common.item.misc.ItemRingOfGathering.RingEntityEntry;
import thebetweenlands.common.registries.ItemRegistry;

public class RingOfGatheringRespawnAnimatorRecipe implements IAnimatorRecipe {
	@Override
	public boolean matchesInput(ItemStack stack) {
		if(stack.getItem() == ItemRegistry.RING_OF_GATHERING) {
			ItemRingOfGathering ring = (ItemRingOfGathering) stack.getItem();
			UUID lastUserUuid = ring.getLastUserUuid(stack);
			if(lastUserUuid != null) {
				return ring.getEntry(lastUserUuid, true, e -> true, false) != null;
			}
		}
		return false;
	}

	@Override
	public int getRequiredFuel(ItemStack stack) {
		if(stack.getItem() == ItemRegistry.RING_OF_GATHERING) {
			ItemRingOfGathering ring = (ItemRingOfGathering) stack.getItem();
			UUID lastUserUuid = ring.getLastUserUuid(stack);
			if(lastUserUuid != null) {
				RingEntityEntry entry = ring.getEntry(lastUserUuid, true, e -> true, false);
				return entry != null ? entry.animatorSulfurCost : 0;
			}
		}
		return 0;
	}

	@Override
	public int getRequiredLife(ItemStack stack) {
		if(stack.getItem() == ItemRegistry.RING_OF_GATHERING) {
			ItemRingOfGathering ring = (ItemRingOfGathering) stack.getItem();
			UUID lastUserUuid = ring.getLastUserUuid(stack);
			if(lastUserUuid != null) {
				RingEntityEntry entry = ring.getEntry(lastUserUuid, true, e -> true, false);
				return entry != null ? entry.animatorLifeCrystalCost : 0;
			}
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Entity getRenderEntity(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack getResult(ItemStack stack) {
		return stack.copy();
	}

	@Override
	public Class<? extends Entity> getSpawnEntityClass(ItemStack stack) {
		return null;
	}

	@Override
	public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean onRetrieved(World world, BlockPos pos, ItemStack stack) {
		//Deprecated
		return true;
	}

	@Override
	public boolean onRetrieved(EntityPlayer player, BlockPos pos, ItemStack stack) {
		if(stack.getItem() == ItemRegistry.RING_OF_GATHERING) {
			ItemRingOfGathering ring = (ItemRingOfGathering) stack.getItem();
			if(ring.returnEntityFromRing(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, player, player.getUniqueID(), true) != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean getCloseOnFinish(ItemStack stack) {
		return true;
	}
}
