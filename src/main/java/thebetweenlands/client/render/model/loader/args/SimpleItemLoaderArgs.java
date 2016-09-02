package thebetweenlands.client.render.model.loader.args;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import thebetweenlands.client.render.model.baked.BakedModelItemWrapper;
import thebetweenlands.client.render.model.loader.LoaderArgs;

public class SimpleItemLoaderArgs extends LoaderArgs {
	private IModel dummyModel = null;
	private final Map<ModelResourceLocation, ResourceLocation> dummyReplacementMap = new HashMap<>();

	@Override
	public String getName() {
		return "from_item";
	}

	@Override
	public IModel loadModel(IModel original, ResourceLocation location, String arg) {
		ResourceLocation childModel = new ResourceLocation(arg);
		this.dummyReplacementMap.put(new ModelResourceLocation(new ResourceLocation(childModel.getResourceDomain(), childModel.getResourcePath()), "inventory"), location);
		return this.getDummyModel();
	}

	@Override
	public IBakedModel getModelReplacement(ModelResourceLocation location, IBakedModel original) {
		ResourceLocation replacementModelLocation = this.dummyReplacementMap.get(location);
		if(replacementModelLocation != null) {
			//Retrieve replacement model
			IModel replacementModel;
			try {
				//Makes sure that the model is loaded through the model loader and that the textures are registered properly
				replacementModel = ModelLoaderRegistry.getModel(replacementModelLocation);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to load model " + replacementModelLocation + " for child model " + location, ex);
			}

			//Bake replacement model
			IBakedModel bakedModel = replacementModel.bake(replacementModel.getDefaultState(), DefaultVertexFormats.ITEM, 
					(loc) -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(loc.toString()));

			//Return wrapped model
			return new BakedModelItemWrapper(original, bakedModel);
		}
		//Nothing to replace
		return null;
	}

	private IModel getDummyModel() {
		if(this.dummyModel == null) {
			try {
				this.dummyModel = ModelLoaderRegistry.getModel(new ResourceLocation("thebetweenlands:item/dummy"));
			} catch (Exception ex) {
				throw new RuntimeException("Failed to load dummy item model!", ex);
			}
		}
		return this.dummyModel;
	}
}
