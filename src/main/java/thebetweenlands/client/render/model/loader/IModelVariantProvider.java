package thebetweenlands.client.render.model.loader;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;

public interface IModelVariantProvider {
	/**
	 * Can be implemented in an {@link IModel} to provide a model variant selector
	 */
	IModel getModelVariant(ResourceLocation location);
}
