package thebetweenlands.api.runechain.chain;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.api.runechain.base.INodeCompositionBlueprint;
import thebetweenlands.api.runechain.container.IRuneChainContainerData;

/**
 * The rune chain data contains all data that comprises a rune chain, e.g. the rune items and the rune chain container data (links etc.).
 * A rune chain blueprint {@link INodeCompositionBlueprint} can be created from this data.
 */
public interface IRuneChainData {
	/**
	 * Returns a the rune items that comprise the rune chain
	 * @return a the rune items that comprise the rune chain
	 */
	public NonNullList<ItemStack> getRuneItems();

	/**
	 * Returns the rune chain container data containing e.g. links
	 * @return the rune chain container data
	 */
	public IRuneChainContainerData getContainerData();
}
