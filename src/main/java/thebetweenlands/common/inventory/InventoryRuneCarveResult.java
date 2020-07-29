package thebetweenlands.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import thebetweenlands.api.aspect.IAspectType;

public class InventoryRuneCarveResult extends InventoryCustomCraftResult {
	private IAspectType aspectType;
	private int aspectAmount;
	
	public InventoryRuneCarveResult(TileEntity tile, Container eventHandler) {
		super(tile, eventHandler);
	}

	public void setAspectUsed(@Nullable IAspectType type, int amount) {
		this.aspectType = type;
		this.aspectAmount = amount;
	}
	
	@Nullable
	public IAspectType getAspectTypeUsed() {
		return this.aspectType;
	}
	
	public int getAspectAmountUsed() {
		return this.aspectAmount;
	}
}
