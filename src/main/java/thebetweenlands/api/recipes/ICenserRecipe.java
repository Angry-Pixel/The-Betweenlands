package thebetweenlands.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public interface ICenserRecipe<Context> {
	/**
	 * Returns an ID that uniquely identifies
	 * this recipe
	 * @return
	 */
	public ResourceLocation getId();

	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);

	/**
	 * Returns whether this recipes matches the fluid stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(FluidStack stack);

	/**
	 * Allows creating some context object that can store custom data
	 * @param stack
	 * @return
	 */
	@Nullable
	public Context createContext(ItemStack stack);

	/**
	 * Allows creating some context object that can store custom data
	 * @param stack
	 * @return
	 */
	@Nullable
	public Context createContext(FluidStack stack);

	/**
	 * Called when the recipe starts being used.
	 * Only called on logical server side.
	 * @param context
	 */
	public void onStart(@Nullable Context context);

	/**
	 * Called when the recipe stops being used.
	 * Only called on logical server side.
	 * @param context
	 */
	public void onStop(@Nullable Context context);

	/**
	 * Called when the censer saves its data and context is not null
	 * @param context
	 * @param nbt
	 */
	public void save(Context context, NBTTagCompound nbt);

	/**
	 * Called when the censer reads its data and context is not null
	 * @param context
	 * @param nbt
	 */
	public void read(Context context, NBTTagCompound nbt);

	/**
	 * Called every tick while the recipe is running.
	 * Create and apply effects here.
	 * @param context
	 * @param amountLeft
	 * @param censer
	 */
	public void update(@Nullable Context context, int amountLeft, TileEntity censer);
	
	public int getConsumptionDuration(@Nullable Context context, int amountLeft, TileEntity censer);
	
	public int getConsumptionAmount(@Nullable Context context, int amountLeft, TileEntity censer);
}
