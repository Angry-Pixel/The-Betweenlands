package thebetweenlands.common.capability.corrosion;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.capability.corrosion.ICorrosionHandler;
import thebetweenlands.api.capability.corrosion.ICorrosionHandlerModifiable;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.component.item.CorrosionData;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.registries.DataComponentRegistry;

public class DefaultCorrosionHandler implements ICorrosionHandler, ICorrosionHandlerModifiable {

	protected final ItemStack stack;
	
	public DefaultCorrosionHandler(ItemStack stack) {
		this.stack = stack;
	}

	@Nullable
	public static DefaultCorrosionHandler createIfCorrodible(ItemStack stack, @Nullable Void context) {
		if(stack.is(BLItemTagProvider.CORRODIBLE)) {
			return new DefaultCorrosionHandler(stack);
		} else {
			return null;
		}
	}
	
	@Override
	public int getMaxCoating() {
		return stack.getOrDefault(DataComponentRegistry.MAX_COATING, CorrosionHelper.MAX_COATING);
	}

	@Override
	public int getMaxCorrosion() {
		return stack.getOrDefault(DataComponentRegistry.MAX_CORROSION, CorrosionHelper.MAX_CORROSION);
	}

	@Override
	public int getCoating() {
		return stack.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY).coating();
	}

	@Override
	public int getCorrosion() {
		return stack.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY).corrosion();
	}

	@Override
	public int addCoating(int amount, boolean simulate) {
		// Ignore negative amounts
		if(amount <= 0) {
			return 0;
		}
		
		// Get current coating and max coating
		final int currentCoating = this.getCoating();
		final int maxCoating = this.getMaxCoating();
		
		// If current coating >= max coating, we will not be able to add anything
		if(currentCoating >= maxCoating) {
			return 0;
		}

		// Get the amount of coating we can add
		final int toAdd = Math.min(amount, maxCoating - currentCoating);
		
		// Add all of the points we can
		if(!simulate) {
			this.setCoating(currentCoating + toAdd);
		}
		return toAdd;
	}

	@Override
	public int addCorrosion(int amount, boolean simulate) {
		// Ignore negative amounts
		if(amount <= 0) {
			return 0;
		}
		
		// Get current corrosion and max corrosion
		final int currentCorrosion = this.getCorrosion();
		final int maxCorrosion = this.getMaxCoating();

		// If current corrosion >= max corrosion, we will not be able to add anything
		if(currentCorrosion >= maxCorrosion) {
			return 0;
		}

		// Get the maximum amount of corrosion we can add
		final int toAdd = Math.min(amount, maxCorrosion - currentCorrosion);

		// Add all of the points we can
		if(!simulate) {
			this.setCoating(currentCorrosion + toAdd);
		}
		return toAdd;
	}

	@Override
	public int removeCoating(int amount, boolean simulate) {
		// Ignore negative amounts
		if(amount <= 0) {
			return 0;
		}

		// Get current coating
		final int currentCoating = this.getCoating();

		// If current coating <= 0, we can't remove anything
		if(currentCoating <= 0) {
			return 0;
		}
		
		// Get the maximum amount of coating we can remove
		final int toRemove = Math.min(amount, currentCoating);

		// Remove all of the points we can
		if(!simulate) {
			this.setCoating(currentCoating - toRemove);
		}
		return toRemove;
	}

	@Override
	public int removeCorrosion(int amount, boolean simulate) {
		// Ignore negative amounts
		if(amount <= 0) {
			return 0;
		}

		// Get current corrosion
		final int currentCorrosion = this.getCorrosion();

		// If current corrosion <= 0, we can't remove anything
		if(currentCorrosion <= 0) {
			return 0;
		}
		
		// Get the maximum amount of corrosion we can remove
		final int toRemove = Math.min(amount, currentCorrosion);

		// Remove all of the points we can
		if(!simulate) {
			this.setCoating(currentCorrosion - toRemove);
		}
		return toRemove;
	}

	@Override
	public void setCoating(int coating) {
		CorrosionData data = stack.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY);
		stack.set(DataComponentRegistry.CORROSION, data.withCoating(Math.clamp(coating, 0, this.getMaxCoating())));
	}

	@Override
	public void setCorrosion(int corrosion) {
		CorrosionData data = stack.getOrDefault(DataComponentRegistry.CORROSION, CorrosionData.EMPTY);
		stack.set(DataComponentRegistry.CORROSION, data.withCorrosion(Math.clamp(corrosion, 0, this.getMaxCorrosion())));
	}
}
