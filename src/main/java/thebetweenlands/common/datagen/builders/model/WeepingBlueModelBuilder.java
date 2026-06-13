package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class WeepingBlueModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	protected WeepingBlueModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(TheBetweenlands.prefix("weeping_blue"), parent, existingFileHelper, false);
	}

	public static <T extends ModelBuilder<T>> WeepingBlueModelBuilder<T> begin(T parent, ExistingFileHelper helper) {
		return new WeepingBlueModelBuilder<>(parent, helper);
	}
}
