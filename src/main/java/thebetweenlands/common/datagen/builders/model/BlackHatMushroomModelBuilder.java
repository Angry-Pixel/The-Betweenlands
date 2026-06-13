package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class BlackHatMushroomModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected BlackHatMushroomModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("black_hat_mushroom1"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> BlackHatMushroomModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new BlackHatMushroomModelBuilder<>(parent, helper);
	}
}
