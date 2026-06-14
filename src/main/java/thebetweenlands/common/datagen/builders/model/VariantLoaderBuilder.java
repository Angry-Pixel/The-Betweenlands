package thebetweenlands.common.datagen.builders.model;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class VariantLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> { //kind of a hacky solution, but it cuts down on the boilerplate
    public VariantLoaderBuilder(ResourceLocation loaderId, T parent, ExistingFileHelper existingFileHelper) {
        super(loaderId, parent, existingFileHelper, false);
    }
}