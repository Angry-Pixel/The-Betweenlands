package thebetweenlands.common.capability;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public interface ICompostHandler extends IItemHandler {

	/**
	 * <p>Called every tick that the compost bin is composting</p>
	 * @param simulate The composting is simulated
	 * @return how much compost was composted this tick
	 */
	public int tickComposting(boolean simulate);

	/**
	 * <p>Called every tick for handling outside of composting items</p>
	 * 
	 * <p>Should NOT affect compost progress or call {@linkplain #tickComposting()}</p>
	 */
	public void tickNonComposting();
	
	/**
	 * @param stack
	 * @return if the stack is a valid compost item
	 */
	public boolean isValidCompost(ItemStack stack);
	
	/**
	 * @param slot
	 * @return if there is a composting item in this slot
	 */
	public boolean hasCompostItem(int slot);

	/**
	 * <p>The composting progress of the item in the slot.</p>
	 * 
	 * <p>If there is no item composting in this slot (i.e. {@linkplain ICompostHandler#hasCompostItem(int)} returns {@code false}), should return {@code -1}</p>
	 * @param slot
	 * @return
	 */
	public int getCompostProgress(int slot);
	
	/**
	 * <p>The total composting progress necessary for this slot to turn into compost.</p>
	 * 
	 * <p>If there is no item composting in this slot (i.e. {@linkplain ICompostHandler#hasCompostItem(int)} returns {@code false}), should return {@code -1}</p>
	 * @param slot
	 * @return
	 */
	public int getTotalCompostProgress(int slot);
	
	/**
	 * <p>How much compost this slot will turn into once {@link #getCompostProgress(int) compost progress} == {@link #getTotalCompostProgress(int) total compost progress}.</p>
	 * 
	 * <p>If there is no item composting in this slot (i.e. {@linkplain ICompostHandler#hasCompostItem(int)} returns {@code false}), should return {@code -1}</p>
	 * @param slot
	 * @return
	 */
	public int getCompostAmount(int slot);
	
	@Override
	public default boolean isItemValid(int slot, ItemStack stack) {
		return this.isValidCompost(stack);
	}

	public static int calculateTotalCompost(ICompostHandler compostHandler) {
		final int size = compostHandler.getSlots();
		
		int totalCompost = 0;
		for(int i = 0; i < size; ++i) {
			if(compostHandler.hasCompostItem(i)) {
				totalCompost += compostHandler.getCompostAmount(i);
			}
		}
		
		return totalCompost;
	}
}
