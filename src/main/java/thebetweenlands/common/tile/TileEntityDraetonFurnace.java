package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.util.NonNullDelegateList;

public class TileEntityDraetonFurnace extends TileEntityBLFurnace {
	private TileEntityDraetonFurnace(NonNullList<ItemStack> inventory) {
		super("container.bl.draeton_furnace", inventory);
	}

	public static TileEntityDraetonFurnace create(NonNullList<ItemStack> inventory, int index) {
		NonNullList<ItemStack> sublist = new NonNullDelegateList<ItemStack>(inventory.subList(index * 4, index * 4 + 4), ItemStack.EMPTY);
		return new TileEntityDraetonFurnace(sublist);
	}

	public NBTTagCompound writeDreatonFurnaceData(NBTTagCompound nbt) {
		return this.writeFurnaceData(nbt);
	}

	public void readDreatonFurnaceData(NBTTagCompound nbt) {
		this.readFurnaceData(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		//no-op
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		//no-op
	}

	@Override
	protected void updateState(boolean active) {
		//TE is on draeton, don't set block
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		//Check if furnace is in draeton
		List<EntityDraeton> draetons = player.world.getEntitiesWithinAABB(EntityDraeton.class, player.getEntityBoundingBox().grow(6));
		for(EntityDraeton dreaton : draetons) {
			if(player.getDistanceSq(dreaton) <= 64.0D) {
				for(int i = 0; i < 4; i++) {
					if(this == dreaton.getFurnace(i)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
