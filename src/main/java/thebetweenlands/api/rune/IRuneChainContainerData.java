package thebetweenlands.api.rune;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

/**
 * The rune chain container data contains all data of the runes' {@link IRuneContainer} and the links.
 */
public interface IRuneChainContainerData {
	public <L extends IRuneLink> Map<Integer, Map<Integer, L>> getLinks();
	
	public Map<Integer, Integer> getConfigurationIds();
	
	public Map<Integer, NBTTagCompound> getContainerNbt();
	
	public Collection<Integer> getLinkedInputs(int runeIndex);

	@Nullable
	public IRuneLink getLink(int runeIndex, int input);

	public boolean link(int runeIndex, int input, int outputRuneIndex, int output);

	@Nullable
	public IRuneLink unlink(int runeIndex, int input);

	public void unlinkAll(int runeIndex);

	public void unlinkAllIncoming(int runeIndex);

	public void moveRuneData(int fromRune, int toRune);

	public NBTTagCompound getContainerNbt(int runeIndex);

	public void setContainerNbt(int runeIndex, NBTTagCompound nbt);

	public int getConfigurationId(int runeIndex);

	public boolean hasConfigurationId(int runeIndex);

	public void setConfigurationId(int runeIndex, int configurationId);

	public void removeConfigurationId(int runeIndex);

	public void removeContainerNbt(int runeIndex);
}
