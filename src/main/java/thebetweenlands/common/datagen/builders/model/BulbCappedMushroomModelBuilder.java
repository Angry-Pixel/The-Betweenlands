package thebetweenlands.common.datagen.builders.model;

import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class BulbCappedMushroomModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    protected BulbCappedMushroomModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(TheBetweenlands.prefix("bulb_capped_mushroom"), parent, existingFileHelper, false);
    }

    public static <T extends ModelBuilder<T>> BulbCappedMushroomModelBuilder<T> begin(T parent,
            ExistingFileHelper helper) {
        return new BulbCappedMushroomModelBuilder<>(parent, helper);
    }
}
