package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class PitcherPlantModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected PitcherPlantModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("pitcher_plant"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> PitcherPlantModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new PitcherPlantModelBuilder<>(parent, helper);
	}
}
