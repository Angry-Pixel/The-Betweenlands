package thebetweenlands.api.event;

public class PreRenderShadersEvent extends Event {
	private final float partialTicks;

	public PreRenderShadersEvent(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public float getPartialTicks() {
		return this.partialTicks;
	}
}
