package thebetweenlands.api.rune.gui;

public interface IRuneContainerFactory {
	public IRuneContainer createContainer();

	public IRuneGui createGui(RuneMenuType menu);
}
