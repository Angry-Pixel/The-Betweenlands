package thebetweenlands.client.perspective;

public interface PerspectiveExtension {
	public boolean disallowCycle(Perspective perspective);

	public Perspective getPerspectiveIfPlayerInOpaqueBlock(Perspective currentPerspective);

	public void onCycleTo(Perspective perspective);
}
