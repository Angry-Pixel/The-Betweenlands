package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class SlantModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected SlantModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("slant"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> SlantModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new SlantModelBuilder<>(parent, helper);
	}
}
