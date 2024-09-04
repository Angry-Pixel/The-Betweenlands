package thebetweenlands.api.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.block.Censer;

import java.util.List;

import javax.annotation.Nullable;

public interface CenserRecipe<Context> {

	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	boolean matchesInput(ItemStack stack);

	/**
	 * Returns whether this recipe matches the secondary item stack
	 * @param stack
	 * @return
	 */
	boolean matchesSecondaryInput(ItemStack stack);

	/**
	 * Called when an input item stack is consumed.
	 * Returns the item stack that is left over.
	 * Usually decreases the stack size by one or returns a
	 * container item.
	 * @param stack
	 */
	ItemStack consumeInput(ItemStack stack);

	/**
	 * Returns the input amount for the given item stack.
	 * The default value is 1000 for regular items.
	 * Maximum amount that the censer can hold is 1000.
	 * Fluids are handled automatically.
	 * @param stack
	 * @return
	 */
	int getInputAmount(ItemStack stack);

	/**
	 * Returns whether this recipes matches the fluid stack
	 * @param stack
	 * @return
	 */
	boolean matchesInput(FluidStack stack);

	/**
	 * Allows creating some context object that can store custom data
	 * @param stack
	 * @return
	 */
	@Nullable
	Context createContext(ItemStack stack);

	/**
	 * Allows creating some context object that can store custom data
	 * @param stack
	 * @return
	 */
	@Nullable
	Context createContext(FluidStack stack);

	/**
	 * Called when the recipe starts being used.
	 * Only called on logical server side.
	 * @param context
	 */
	void onStart(@Nullable Context context);

	/**
	 * Called when the recipe stops being used.
	 * Only called on logical server side.
	 * @param context
	 */
	void onStop(@Nullable Context context);

	/**
	 * Called when the censer saves its data and context is not null
	 * @param context
	 * @param tag
	 * @param packet
	 */
	void save(Context context, CompoundTag tag, boolean packet);

	/**
	 * Called when the censer reads its data and context is not null
	 * @param context
	 * @param nbt
	 * @param packet
	 */
	void read(Context context, CompoundTag nbt, boolean packet);

	/**
	 * Called every tick while the recipe is running.
	 * Create and apply effects here.
	 * Can return a value > 0 to consume from the input.
	 * @param context
	 * @param censer
	 * @return
	 */
	int update(@Nullable Context context, Censer censer);

	/**
	 * Returns how long it takes in ticks until some of the input is consumed.
	 * @param context
	 * @param censer
	 * @return
	 */
	int getConsumptionDuration(@Nullable Context context, Censer censer);

	/**
	 * Returns how many units of the input are consumed.
	 * @param context
	 * @param censer
	 * @return
	 */
	int getConsumptionAmount(@Nullable Context context, Censer censer);

	/**
	 * Returns the localized text that describes the effect
	 * @param context
	 * @param censer
	 * @param tooltip
	 * @return
	 */
	void getLocalizedEffectText(@Nullable Context context, Censer censer, List<Component> tooltip);

	enum EffectColorType {
		FOG,
		GUI
	}

	/**
	 * Returns the effect color.
	 * Used e.g. to color the censer's progress bar or the censer fog strips.
	 * @param context
	 * @param censer
	 * @param type
	 * @return
	 */
	int getEffectColor(@Nullable Context context, Censer censer, EffectColorType type);

	/**
	 * Called when the censer is rendered with this recipe active
	 * @param context
	 * @param censer
	 * @param pos
	 * @param partialTicks
	 */
	void render(@Nullable Context context, Censer censer, BlockPos pos, float partialTicks);

	/**
	 * Whether this recipe/effect creates dungeon fog that accelerates
	 * plant/crop growth and allows sludge worm dungeon plants to grow
	 * @param context
	 * @param censer
	 * @return
	 */
	boolean isCreatingDungeonFog(@Nullable Context context, Censer censer);

	/**
	 * Returns which aspect fog type this recipe creates, or null if no aspect fog is created
	 * @param context
	 * @param censer
	 * @return
	 */
	@Nullable
	Holder<AspectType> getAspectFogType(@Nullable Context context, Censer censer);
}
