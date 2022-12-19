package thebetweenlands.client.render.model.loader;

import java.util.Collection;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IFastTESRBakedModels {
	public Collection<ModelResourceLocation> getModelLocations();

	public void onModelBaked(ModelResourceLocation location, IBakedModel model);
}
