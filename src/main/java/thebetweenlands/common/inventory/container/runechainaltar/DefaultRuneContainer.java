package thebetweenlands.common.inventory.container.runechainaltar;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
import thebetweenlands.api.rune.gui.IRuneLink;

public class DefaultRuneContainer implements IRuneContainer {
	protected final ResourceLocation id;
	protected IRuneContainerContext context;

	public DefaultRuneContainer(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public void init(IRuneContainerContext context) {
		this.context = context;
	}

	@Override
	public IRuneContainerContext getContext() {
		return this.context;
	}

	@Override
	public void onMarkLinked(int input, IRuneLink link) {

	}

	@Override
	public void onMarkUnlinked(int input, IRuneLink link) {

	}

	@Override
	public void onLinksMoved(int fromRuneIndex, int toRuneIndex) {

	}

	@Override
	public void onRuneShifted(int fromRuneIndex, int toRuneIndex) {

	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}
}
