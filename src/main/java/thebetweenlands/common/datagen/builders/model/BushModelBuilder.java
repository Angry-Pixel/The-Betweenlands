package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class BushModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected BushModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("bush"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> BushModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new BushModelBuilder<>(parent, helper);
	}
}
