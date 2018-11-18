package thebetweenlands.api.rune.gui;

public interface IRuneMenuFactory {
	public IRuneContainer createContainer();

	public IRuneGui createGui(RuneMenuType type);
}
