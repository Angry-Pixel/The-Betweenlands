package thebetweenlands.client.model.baked.connectedtextures;

import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import thebetweenlands.api.client.ConnectedTextureHelper;

public class BakedConnectedTexturesQuad {

	protected final BakedQuad[][] quads;

//	public boolean hasFullyResolvedProperties = false;

	public final int[] indices;
	public final Direction cullface;
	public final ModelProperty<?>[] indexProperties;
	public final ModelProperty<Boolean> cullfaceProperty;

	protected BakedConnectedTexturesQuad(BakedQuad[][] quads, ModelProperty<?>[] indexProperties, ModelProperty<Boolean> cullfaceProperty, UnbakedConnectedTexturesQuad unbaked) {
		this.quads = quads;
		this.indexProperties = indexProperties;
		this.cullfaceProperty = cullfaceProperty;
		
		this.cullface = unbaked.cullface;
		this.indices = unbaked.indices.clone();
	}
	
	public BakedQuad[][] getBakedQuads() {
		return this.quads;
	}

	public static BakedConnectedTexturesQuad bakeFrom(UnbakedConnectedTexturesQuad quad, IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		if(!quad.getHasBaked()) quad.bake(context, baker, spriteGetter, modelState, overrides);
		
		ModelProperty<?>[] indexProperties = new ModelProperty<?>[4];
		ModelProperty<Boolean> cullfaceProperty = null;
		if(quad.cullFaceName != null) {
			cullfaceProperty = ConnectedTextureHelper.getCullfacePropertyNullable(ResourceLocation.tryParse(quad.cullFaceName));
		}
		for(int i = 0; i < 4; ++i) {
			String name = quad.indexNames[i];
			if(name != null) {
				indexProperties[i] = ConnectedTextureHelper.getIndexPropertyNullable(ResourceLocation.tryParse(name));
			}
		}
		
		return new BakedConnectedTexturesQuad(quad.getBakedQuads(), indexProperties, cullfaceProperty, quad);
	}
	
//	// Returns whether it's resolved all index properties
//	protected boolean resolveIndexProperties(ModelData data) {
//		if(this.indexProperties == null) {
//			this.indexProperties = new ModelProperty<?>[4];
//			
//			for(int i = 0; i < 4; i++) {
//				String indexName = this.indexNames[i];
//				if(indexName != null) {
//					for(ModelProperty<?> entry : data.getProperties()) {
//						if(indexName.equals(entry.getName())) {
//							this.indexProperties[i] = entry.getKey();
//							break;
//						}
//					}
//				}
//			}
//		}
//		return false;
//	}
//	
//	// Returns whether it's resolved the cullface property
//	protected boolean resolveCullfaceProperty(ModelData data) {
//		
//		return false;
//	}
//	
//	public void resolveProperties(ModelData data) {
//		if(this.hasFullyResolvedProperties) return;
//		
//		this.hasFullyResolvedProperties = true;
//		this.hasFullyResolvedProperties &= this.resolveIndexProperties(data);
//		this.hasFullyResolvedProperties &= this.resolveCullfaceProperty(data);
//	}
}
