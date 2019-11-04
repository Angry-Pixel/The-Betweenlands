package thebetweenlands.client.render.model.loader;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public interface IFastTESRBakedModels {
	public Collection<ModelResourceLocation> getModelLocations();

	public void onModelBaked(ModelResourceLocation location, IBakedModel model);
}
