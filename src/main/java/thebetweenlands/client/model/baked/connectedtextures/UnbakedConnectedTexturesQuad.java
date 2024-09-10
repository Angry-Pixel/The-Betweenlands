package thebetweenlands.client.model.baked.connectedtextures;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import thebetweenlands.util.QuadBuilder;

// Would be a record if it didn't need to do maths
public class UnbakedConnectedTexturesQuad {
	public final ResourceLocation[] textures;
	public final String[] indexNames;
	public final int[] indices;
	public final ConnectedTexturesVertex[] verts;
	public final Direction cullface;
	public final String cullFaceName;
	public final int tintIndex;
	public final float minU, minV, maxU, maxV;
	
	protected boolean hasBaked = false;
	protected final BakedQuad[][] quads;

//	protected ModelProperty<?>[] indexProperties;
//	protected ModelProperty<?> cullFaceProperty;
	
	public UnbakedConnectedTexturesQuad(ResourceLocation[] textures, String[] indexNames, int[] indices, ConnectedTexturesVertex[] verts, @Nullable Direction cullFace, @Nullable String cullFaceName, int tintIndex, float minU, float minV, float maxU, float maxV) {
		this.textures = textures;
		this.indexNames = indexNames;
		this.indices = indices;
		this.verts = verts;
		this.cullface = cullFace;
		this.cullFaceName = cullFaceName;
		this.tintIndex = tintIndex;
		this.quads = new BakedQuad[4][this.textures.length];
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}

	public boolean getHasBaked() {
		return this.hasBaked;
	}
	
	public BakedQuad[][] getBakedQuads() {
		return this.quads;
	}
	
	public void bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		Optional<Transformation> rootTransform = context.getRootTransform().isIdentity() ? Optional.empty() : Optional.of(context.getRootTransform());
		this.bake(location -> spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, location)), rootTransform, context.getTransforms(), DefaultVertexFormat.BLOCK);
	}

    /**
     * @deprecated Use {@link #bake(IGeometryBakingContext, ModelBaker,
     *             java.util.function.Function, ModelState, ItemOverrides)} instead.
     */
	@Deprecated
	public void bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter,
			Optional<Transformation> rootTransform, ItemTransforms transforms,
			VertexFormat format) {
		ConnectedTexturesVertex uvOffset = new ConnectedTexturesVertex(0, 0, 0, this.minU, this.minV);
		ConnectedTexturesVertex v0 = this.verts[0].scaleUVs(this.maxU - this.minU, this.maxV - this.minV).add(uvOffset);
		ConnectedTexturesVertex v1 = this.verts[1].scaleUVs(this.maxU - this.minU, this.maxV - this.minV).add(uvOffset);
		ConnectedTexturesVertex v2 = this.verts[2].scaleUVs(this.maxU - this.minU, this.maxV - this.minV).add(uvOffset);
		ConnectedTexturesVertex v3 = this.verts[3].scaleUVs(this.maxU - this.minU, this.maxV - this.minV).add(uvOffset);

		ConnectedTexturesVertex d01 = v1.subtract(v0);
		ConnectedTexturesVertex d12 = v2.subtract(v1);
		ConnectedTexturesVertex d23 = v3.subtract(v2);
		ConnectedTexturesVertex d30 = v0.subtract(v3);

		float e01 = this.extrapolateClamp(v0.uv().y, v1.uv().y, (this.maxV + this.minV) / 2.0F);
		float e12 = this.extrapolateClamp(v1.uv().x, v2.uv().x, (this.maxU + this.minU) / 2.0F);
		float e23 = this.extrapolateClamp(v2.uv().y, v3.uv().y, (this.maxV + this.minV) / 2.0F);
		float e30 = this.extrapolateClamp(v3.uv().x, v0.uv().x, (this.maxU + this.minU) / 2.0F);

		ConnectedTexturesVertex p01 = v0.add(d01.scale(e01));
		ConnectedTexturesVertex p12 = v1.add(d12.scale(e12));
		ConnectedTexturesVertex p23 = v2.add(d23.scale(e23));
		ConnectedTexturesVertex p30 = v3.add(d30.scale(e30));

		ConnectedTexturesVertex d0123 = p23.subtract(p01);

		float ecp = this.extrapolateClamp(v0.uv().x + (v1.uv().x - v0.uv().x) * e01, v2.uv().x + (v3.uv().x - v2.uv().x) * e23, (this.maxU + this.minU) / 2.0F);

		ConnectedTexturesVertex cp = p01.add(d0123.scale(ecp));

		TextureAtlasSprite[] sprites = new TextureAtlasSprite[this.textures.length];
		for(int i = 0; i < sprites.length; i++) {
			sprites[i] = bakedTextureGetter.apply(this.textures[i]);
		}

		this.quads[0] = this.bakeTextureVariants(format, rootTransform, sprites, new ConnectedTexturesVertex[] {v0, p01, cp, p30});
		this.quads[1] = this.bakeTextureVariants(format, rootTransform, sprites, new ConnectedTexturesVertex[] {p01, v1, p12, cp});
		this.quads[2] = this.bakeTextureVariants(format, rootTransform, sprites, new ConnectedTexturesVertex[] {cp, p12, v2, p23});
		this.quads[3] = this.bakeTextureVariants(format, rootTransform, sprites, new ConnectedTexturesVertex[] {p30, cp, p23, v3});
		this.hasBaked = true;
	}

	protected float extrapolateClamp(float v1, float v2, float v) {
		float extrapolant = (v - v1) / (v2 - v1);
		return Mth.clamp(extrapolant, 0, 1);
	}

	protected BakedQuad[] bakeTextureVariants(VertexFormat format, Optional<Transformation> transformation,
			TextureAtlasSprite[] sprites, ConnectedTexturesVertex[] verts) {
		QuadBuilder builder = new QuadBuilder(4 * this.textures.length, format);
		if(transformation.isPresent()) {
			builder.setTransformation(transformation.get());
		}
		for(int i = 0; i < sprites.length; i++) {
			builder.setSprite(sprites[i]);
			builder.addVertex(verts[0].pos(), verts[0].uv().x * 16.0F, verts[0].uv().y * 16.0F);
			builder.addVertex(verts[1].pos(), verts[1].uv().x * 16.0F, verts[1].uv().y * 16.0F);
			builder.addVertex(verts[2].pos(), verts[2].uv().x * 16.0F, verts[2].uv().y * 16.0F);
			builder.addVertex(verts[3].pos(), verts[3].uv().x * 16.0F, verts[3].uv().y * 16.0F);
		}
		
		return builder.build(b -> b.setTintIndex(this.tintIndex)).nonCulledQuads.toArray(new BakedQuad[0]);
	}

}
