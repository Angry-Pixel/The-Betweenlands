package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class VolarpadModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected VolarpadModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("volarpad"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> VolarpadModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new VolarpadModelBuilder<>(parent, helper);
	}
}
