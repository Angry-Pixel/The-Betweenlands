package thebetweenlands.api.rune;

public interface IRuneContainerFactory {
	public IRuneContainer createContainer();

	public IRuneGui createGui(RuneMenuType menu);
}
