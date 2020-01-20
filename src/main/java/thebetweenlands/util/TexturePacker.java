package thebetweenlands.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class TexturePacker {
	public static interface ITexturePackable {
		public void onPacked();
	}

	public static class TextureQuadMap {
		private final List<TextureQuad> quads;
		private final ITexturePackable owner;
		private final ResourceLocation texture;
		private final int width, height;

		public TextureQuadMap(ResourceLocation texture, int width, int height, List<TextureQuad> quads, @Nullable ITexturePackable owner) {
			this.texture = texture;
			this.width = width;
			this.height = height;
			this.quads = quads;
			this.owner = owner;
		}

		public List<TextureQuad> getQuads() {
			return this.quads;
		}

		@Nullable
		public ITexturePackable getOwner() {
			return this.owner;
		}
	}

	public static class TextureQuad {
		private final int u, v, w, h;
		private int su, sv, sw, sh;
		private double packedU, packedV, packedMaxU, packedMaxV;
		private ResourceLocation packedLocation;

		public TextureQuad(int u, int v, int width, int height) {
			this.su = this.u = u;
			this.sv = this.v = v;
			this.sw = this.w = width;
			this.sh = this.h = height;
		}

		public double getPackedU() {
			return this.packedU;
		}

		public double getPackedV() {
			return this.packedV;
		}

		public double getPackedMaxU() {
			return this.packedMaxU;
		}

		public double getPackedMaxV() {
			return this.packedMaxV;
		}

		public ResourceLocation getPackedLocation() {
			return this.packedLocation;
		}

		public void rescale(float scaleU, float scaleV) {
			this.su = (int)Math.floor(this.u * scaleU);
			this.sv = (int)Math.floor(this.v * scaleV);
			this.sw = (int)Math.floor(this.w * scaleU);
			this.sh = (int)Math.floor(this.h * scaleV);
		}
	}

	private static class TextureBin {
		private static class Space {
			private int x, y, w, h;

			private Space(int x, int y, int w, int h) {
				this.x = x;
				this.y = y;
				this.w = w;
				this.h = h;
			}
		}

		private final ResourceLocation location;
		private final BufferedImage image;
		private final int width, height;
		private final List<Space> spaces = new ArrayList<>();

		protected TextureBin(ResourceLocation location, int width, int height) {
			this.location = location;
			this.width = width;
			this.height = height;
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			this.spaces.add(new Space(0, 0, width, height));
		}
	}

	private final ResourceLocation textureName;
	private final List<TextureQuadMap> textureMaps;

	private final Map<ResourceLocation, BufferedImage> cachedTextures = new HashMap<>();

	private final List<TextureBin> textureBins = new ArrayList<>();

	private int packedTextureID = 0;

	private final BufferedImage missingTexture;

	private int optimalFootprint;
	private int packedFootprint;

	public TexturePacker(ResourceLocation textureName) {
		this(textureName, new ArrayList<>());
	}

	public TexturePacker(ResourceLocation textureName, List<TextureQuadMap> textureMaps) {
		this.textureName = textureName;
		this.textureMaps = textureMaps;

		this.missingTexture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		System.arraycopy(TextureUtil.MISSING_TEXTURE.getTextureData(), 0, ((DataBufferInt) this.missingTexture.getRaster().getDataBuffer()).getData(), 0, 16*16);
	}

	public void addTextureMap(TextureQuadMap map) {
		this.textureMaps.add(map);
	}

	public List<TextureQuadMap> getTextureMaps() {
		return this.textureMaps;
	}

	protected ResourceLocation generateNewTextureLocation() {
		ResourceLocation rl = new ResourceLocation(this.textureName.getNamespace(), this.textureName.getPath() + "_" + this.packedTextureID);
		this.packedTextureID++;
		return rl;
	}

	protected BufferedImage getOrLoadTexture(ResourceLocation location, IResourceManager manager) {
		BufferedImage cached = this.cachedTextures.get(location);
		if(cached == null) {
			try(IResource resource = manager.getResource(new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png"))) {
				this.cachedTextures.put(location, cached = ImageIO.read(resource.getInputStream()));
			} catch (IOException e) {
				TheBetweenlands.logger.error("Failed loading model texture to pack. Location: " + location, e);
				this.cachedTextures.put(location, cached = this.missingTexture);
			}
		}
		return cached;
	}

	public Map<ResourceLocation, BufferedImage> pack(IResourceManager manager) {
		this.optimalFootprint = 0;
		this.packedFootprint = 0;

		List<TextureQuad> quads = new ArrayList<>();
		Map<TextureQuad, BufferedImage> textures = new HashMap<>();

		for(TextureQuadMap map : this.textureMaps) {
			BufferedImage sourceImage = this.getOrLoadTexture(map.texture, manager);

			for(TextureQuad quad : map.getQuads()) {
				//Rescale quad to texture size for texture pack support
				quad.rescale((float)sourceImage.getWidth() / (float)map.width, (float)sourceImage.getHeight() / (float)map.height);

				textures.put(quad, sourceImage);
			}

			quads.addAll(map.getQuads());
		}

		Collections.sort(quads, (q1, q2) -> q2.h - q1.h);

		for(TextureQuad quad : quads) {
			this.optimalFootprint += quad.sw * quad.sh;

			boolean packed = false;

			BufferedImage sourceImage = textures.get(quad);

			for(TextureBin bin : this.textureBins) {
				if(this.packIntoBin(quad, bin, sourceImage)) {
					packed = true;
					break;
				}
			}

			if(!packed) {
				int dimension = Math.max(16, Math.max(quad.sw, quad.sh));
				int binSize = 1 << (32 - Integer.numberOfLeadingZeros(dimension - 1)); //Round to next power of 2

				TextureBin bin = new TextureBin(this.generateNewTextureLocation(), binSize, binSize);

				this.textureBins.add(bin);

				this.packedFootprint += binSize * binSize;

				if(!this.packIntoBin(quad, bin, sourceImage)) {
					throw new RuntimeException("Was unable to pack texture quad into new bin. This should not happen!");
				}
			}
		}

		Map<ResourceLocation, BufferedImage> packedTextures = new HashMap<>();

		for(TextureBin bin : this.textureBins) {
			packedTextures.put(bin.location, bin.image);
		}

		this.packedTextureID = 0;
		this.cachedTextures.clear();
		this.textureBins.clear();

		return packedTextures;
	}

	protected static void copySubImage(BufferedImage source, int x, int y, int w, int h, BufferedImage dest, int x2, int y2) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		for(int py = 0; py < h; py++) {
			for(int px = 0; px < w; px++) {
				//Model's UVs can be out of range, assuming texture wrapping is used
				int sx = ((x + px) % sourceWidth + sourceWidth) % sourceWidth;
				int sy = ((y + py) % sourceHeight + sourceHeight) % sourceHeight;

				dest.setRGB(x2 + px, y2 + py, source.getRGB(sx, sy));
			}
		}
	}

	protected boolean packIntoBin(TextureQuad quad, TextureBin bin, BufferedImage sourceImage) {
		List<TextureBin.Space> spaces = bin.spaces;

		for(int i = spaces.size() - 1; i >= 0; i--) {
			TextureBin.Space space = spaces.get(i);

			if(quad.sw > space.w || quad.sh > space.h) {
				continue;
			}

			int packedU = space.x;
			int packedV = space.y;

			copySubImage(sourceImage, quad.su, quad.sv, quad.sw, quad.sh, bin.image, packedU, packedV);

			quad.packedU = packedU / (double) bin.width;
			quad.packedV = packedV / (double) bin.height;
			quad.packedMaxU = (packedU + quad.sw) / (double) bin.width;
			quad.packedMaxV = (packedV + quad.sh) / (double) bin.height;
			quad.packedLocation = bin.location;

			if(quad.sw == space.w && quad.sh == space.h) {
				spaces.remove(i);
			} else if(quad.sh == space.h) {
				space.x += quad.sw;
				space.w -= quad.sw;
			} else if(quad.sw == space.w) {
				space.y += quad.sh;
				space.h -= quad.sh;
			} else {
				spaces.add(new TextureBin.Space(space.x + quad.sw, space.y, space.w - quad.sw, quad.sh));
				space.y += quad.sh;
				space.h -= quad.sh;
			}

			return true;
		}

		return false;
	}

	public int getOptimalFootprint() {
		return this.optimalFootprint;
	}

	public int getPackedFootprint() {
		return this.packedFootprint;
	}
}
