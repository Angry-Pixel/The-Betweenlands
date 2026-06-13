package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class SwampPlantModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected SwampPlantModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("swamp_plant"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> SwampPlantModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new SwampPlantModelBuilder<>(parent, helper);
	}
}
