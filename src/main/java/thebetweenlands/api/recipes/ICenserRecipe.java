package thebetweenlands.api.recipes;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.IAspectType;

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
	 * Called when an input item stack is consumed.
	 * Returns the item stack that is left over.
	 * Usually decreases the stack size by one or returns a
	 * container item.
	 * @param stack
	 */
	public ItemStack consumeInput(ItemStack stack);
	
	/**
	 * Returns the input amount for the given item stack.
	 * The default value is 1000 for regular items.
	 * Fluids are handled automatically.
	 * @param stack
	 * @return
	 */
	public int getInputAmount(ItemStack stack);
	
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
	 * @param packet
	 */
	public void save(Context context, NBTTagCompound nbt, boolean packet);

	/**
	 * Called when the censer reads its data and context is not null
	 * @param context
	 * @param nbt
	 * @param packet
	 */
	public void read(Context context, NBTTagCompound nbt, boolean packet);

	/**
	 * Called every tick while the recipe is running.
	 * Create and apply effects here.
	 * Can return a value > 0 to consume from the input.
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @return
	 */
	public int update(@Nullable Context context, int amountLeft, TileEntity censer);

	/**
	 * Returns how long it takes in ticks until some of the input is consumed.
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @return
	 */
	public int getConsumptionDuration(@Nullable Context context, int amountLeft, TileEntity censer);

	/**
	 * Returns how many units of the input are consumed.
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @return
	 */
	public int getConsumptionAmount(@Nullable Context context, int amountLeft, TileEntity censer);

	/**
	 * Returns the localized text that describes the effect
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public void getLocalizedEffectText(@Nullable Context context, int amountLeft, TileEntity censer, List<String> tooltip);

	public static enum EffectColorType {
		FOG,
		GUI
	}
	
	/**
	 * Returns the effect color.
	 * Used e.g. to color the censer's progress bar or the censer fog strips.
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @param type
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public int getEffectColor(@Nullable Context context, int amountLeft, TileEntity censer, EffectColorType type);

	/**
	 * Called when the censer is rendered with this recipe active
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @param partialTicks
	 */
	@SideOnly(Side.CLIENT)
	public void render(@Nullable Context context, int amountLeft, TileEntity censer, boolean running, float effectStrength, double x, double y, double z, float partialTicks);

	/**
	 * Whether this recipe/effect creates dungeon fog that accelerates
	 * plant/crop growth and allows sludge worm dungeon plants to grow
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @return
	 */
	public boolean isCreatingDungeonFog(@Nullable Context context, int amountLeft, TileEntity censer);
	
	/**
	 * Returns which aspect fog type this recipe creates, or null if no aspect fog is created
	 * @param context
	 * @param amountLeft
	 * @param censer
	 * @return
	 */
	@Nullable
	public IAspectType getAspectFogType(@Nullable Context context, int amountLeft, TileEntity censer);
}
