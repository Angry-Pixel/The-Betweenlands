package thebetweenlands.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;

public class MobSpawnerConfigurationScreen extends Screen {

	private final BetweenlandsBaseSpawner spawner;

	public MobSpawnerConfigurationScreen(BetweenlandsBaseSpawner spawner) {
		super(Component.empty());
		this.spawner = spawner;
	}

	//entity selector (drop down list or command autofill thing)
	//text field for delay
	//2 text fields for delay range (__ - __)
	//text field for spawn range
	//text field for check range
	//text field for max entities
	//check box for particles
	//check box for air spawning
	//text field for spawn count
}
