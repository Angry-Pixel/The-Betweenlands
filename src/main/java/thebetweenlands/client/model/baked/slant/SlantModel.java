package thebetweenlands.client.model.baked.slant;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.misc.SlopeBlock;
import thebetweenlands.util.QuadBuilder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SlantModel implements IDynamicBakedModel {

	public static final ModelProperty<Boolean> NORTH_WEST = new ModelProperty<>();
	public static final ModelProperty<Boolean> NORTH_EAST = new ModelProperty<>();
	public static final ModelProperty<Boolean> SOUTH_WEST = new ModelProperty<>();
	public static final ModelProperty<Boolean> SOUTH_EAST = new ModelProperty<>();

	private final LoadingCache<Integer, SlantModel> modelCache = CacheBuilder.newBuilder().maximumSize(256).build(new CacheLoader<>() {
		@Override
		public SlantModel load(Integer key) {
			return new SlantModel(SlantModel.this.textureSlant, SlantModel.this.textureSide, SlantModel.this.textureBase, SlantModel.this.transforms, SlantModel.this.identity, key);
		}
	});

	private final TextureAtlasSprite textureSlant;
	private final TextureAtlasSprite textureSide;
	private final TextureAtlasSprite textureBase;
	private final ItemTransforms transforms;
	private final Transformation identity;
	private Map<Direction, ImmutableList<BakedQuad>> faceQuads;
	private List<BakedQuad> nonCulledQuads;

	public SlantModel(TextureAtlasSprite base, TextureAtlasSprite side, TextureAtlasSprite slant, ItemTransforms transforms, Transformation identity) {
		this.textureBase = base;
		this.textureSide = side;
		this.textureSlant = slant;
		this.transforms = transforms;
		this.identity = identity;
	}

	private SlantModel(TextureAtlasSprite base, TextureAtlasSprite side, TextureAtlasSprite slant, ItemTransforms transforms, Transformation identity, int key) {
		this(base, side, slant, transforms, identity);

		boolean cornerNW = (key & 0x1) != 0;
		boolean cornerNE = (key & 0x2) != 0;
		boolean cornerSE = (key & 0x4) != 0;
		boolean cornerSW = (key & 0x8) != 0;
		boolean upsidedown = (key & 0x10) != 0;
		Direction slantDir = Direction.from3DDataValue(key >> 5);

		float slopeEdge = 1.0F / 16.0F * 3.0F;
		float cornerHeightNW = cornerNW ? 1.0F : slopeEdge;
		float cornerHeightNE = cornerNE ? 1.0F : slopeEdge;
		float cornerHeightSE = cornerSE ? 1.0F : slopeEdge;
		float cornerHeightSW = cornerSW ? 1.0F : slopeEdge;

		QuadBuilder builder = new QuadBuilder(DefaultVertexFormat.BLOCK).setTransformation(identity);

		int[] slantTexU;
		int[] slantTexV = switch (slantDir) {
			case SOUTH -> {
				slantTexU = new int[]{16, 16, 0, 0};
				yield new int[]{16, 0, 0, 16};
			}
			case EAST -> {
				slantTexU = new int[]{0, 16, 16, 0};
				yield new int[]{16, 16, 0, 0};
			}
			case WEST -> {
				slantTexU = new int[]{16, 0, 0, 16};
				yield new int[]{0, 0, 16, 16};
			}
			default -> {
				slantTexU = new int[]{0, 0, 16, 16};
				yield new int[]{0, 16, 16, 0};
			}
		};

		builder.setSprite(this.textureSide);
		builder.setCullFace(Direction.NORTH);
		if (!upsidedown) {
			//z- face
			builder.addVertex(0, 0, 0, 16, 16);
			builder.addVertex(0, cornerHeightNW, 0, 16, 16 - cornerHeightNW * 16.0F);
			builder.addVertex(1, cornerHeightNE, 0, 0, 16 - cornerHeightNE * 16.0F);
			builder.addVertex(1, 0, 0, 0, 16);

			//z+ face
			builder.setCullFace(Direction.SOUTH);
			builder.addVertex(0, 0, 1, 0, 16);
			builder.addVertex(1, 0, 1, 16, 16);
			builder.addVertex(1, cornerHeightSE, 1, 16, 16 - cornerHeightSE * 16.0F);
			builder.addVertex(0, cornerHeightSW, 1, 0, 16 - cornerHeightSW * 16.0F);

			//x+ face
			builder.setCullFace(Direction.EAST);
			builder.addVertex(1, 0, 0, 16, 16);
			builder.addVertex(1, cornerHeightNE, 0, 16, 16 - cornerHeightNE * 16.0F);
			builder.addVertex(1, cornerHeightSE, 1, 0, 16 - cornerHeightSE * 16.0F);
			builder.addVertex(1, 0, 1, 0, 16);

			//x- face
			builder.setCullFace(Direction.WEST);
			builder.addVertex(0, 0, 0, 0, 16);
			builder.addVertex(0, 0, 1, 16, 16);
			builder.addVertex(0, cornerHeightSW, 1, 16, 16 - cornerHeightSW * 16.0F);
			builder.addVertex(0, cornerHeightNW, 0, 0, 16 - cornerHeightNW * 16.0F);

			//top face
			builder.setCullFace(null);
			builder.setSprite(this.textureSlant);
			if (cornerNW && cornerNE && cornerSE) {
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);

				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
			} else if (cornerNE && cornerSE && cornerSW) {
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);

				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
			} else if (cornerSE && cornerSW && cornerNW) {
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);

				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
			} else if (cornerSW && cornerNW && cornerNE) {
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);

				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
			} else if (cornerNW && !cornerNE && !cornerSE && !cornerSW) {
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);

				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
			} else if (!cornerNW && cornerNE && !cornerSE && !cornerSW) {
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);

				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
			} else if (!cornerNW && !cornerNE && cornerSE && !cornerSW) {
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);

				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
			} else if (!cornerNW && !cornerNE && !cornerSE && cornerSW) {
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);

				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
			} else if (cornerNW && cornerSE) {
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);

				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
			} else if (!cornerNW && !cornerSE && cornerNE) {
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);

				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
			} else {
				//straight
				builder.addVertex(0, cornerHeightNW, 0, slantTexU[0], slantTexV[0]);
				builder.addVertex(0, cornerHeightSW, 1, slantTexU[1], slantTexV[1]);
				builder.addVertex(1, cornerHeightSE, 1, slantTexU[2], slantTexV[2]);
				builder.addVertex(1, cornerHeightNE, 0, slantTexU[3], slantTexV[3]);
			}

			//bottom face
			builder.setCullFace(Direction.DOWN);
			builder.setSprite(this.textureBase);
			builder.addVertex(0, 0, 0, 0, 0);
			builder.addVertex(1, 0, 0, 0, 16);
			builder.addVertex(1, 0, 1, 16, 16);
			builder.addVertex(0, 0, 1, 16, 0);
		} else {

			//z- face
			builder.addVertex(0, 1, 0, 16, 0);
			builder.addVertex(1, 1, 0, 0, 0);
			builder.addVertex(1, 1 - cornerHeightNE, 0, 0, cornerHeightNE * 16.0F);
			builder.addVertex(0, 1 - cornerHeightNW, 0, 16, cornerHeightNW * 16.0F);

			//z+ face
			builder.setCullFace(Direction.SOUTH);
			builder.addVertex(0, 1, 1, 0, 0);
			builder.addVertex(0, 1 - cornerHeightSW, 1, 0, cornerHeightSW * 16.0F);
			builder.addVertex(1, 1 - cornerHeightSE, 1, 16, cornerHeightSE * 16.0F);
			builder.addVertex(1, 1, 1, 16, 0);

			//x+ face
			builder.setCullFace(Direction.EAST);
			builder.addVertex(1, 1, 0, 16, 0);
			builder.addVertex(1, 1, 1, 0, 0);
			builder.addVertex(1, 1 - cornerHeightSE, 1, 0, cornerHeightSE * 16.0F);
			builder.addVertex(1, 1 - cornerHeightNE, 0, 16, cornerHeightNE * 16.0F);

			//x- face
			builder.setCullFace(Direction.WEST);
			builder.addVertex(0, 1, 0, 0, 0);
			builder.addVertex(0, 1 - cornerHeightNW, 0, 0, cornerHeightNW * 16.0F);
			builder.addVertex(0, 1 - cornerHeightSW, 1, 16, cornerHeightSW * 16.0F);
			builder.addVertex(0, 1, 1, 16, 0);

			//bottom face
			builder.setCullFace(null);
			builder.setSprite(this.textureSlant);
			if (cornerNW && cornerNE && cornerSE) {
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);

				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
			} else if (cornerNE && cornerSE && cornerSW) {
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);

				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
			} else if (cornerSE && cornerSW && cornerNW) {
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);

				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
			} else if (cornerSW && cornerNW && cornerNE) {
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);

				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
			} else if (cornerNW && !cornerNE && !cornerSE && !cornerSW) {
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);

				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
			} else if (!cornerNW && cornerNE && !cornerSE && !cornerSW) {
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);

				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
			} else if (!cornerNW && !cornerNE && cornerSE && !cornerSW) {
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);

				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
			} else if (!cornerNW && !cornerNE && !cornerSE && cornerSW) {
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);

				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
			} else if (cornerNW && cornerSE) {
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);

				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
			} else if (!cornerNW && !cornerSE && cornerNE) {
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);

				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
			} else {
				//straight
				builder.addVertex(1, 1 - cornerHeightNE, 0, slantTexU[3], 16 - slantTexV[3]);
				builder.addVertex(1, 1 - cornerHeightSE, 1, slantTexU[2], 16 - slantTexV[2]);
				builder.addVertex(0, 1 - cornerHeightSW, 1, slantTexU[1], 16 - slantTexV[1]);
				builder.addVertex(0, 1 - cornerHeightNW, 0, slantTexU[0], 16 - slantTexV[0]);
			}

			//top face
			builder.setCullFace(Direction.UP);
			builder.setSprite(this.textureBase);
			builder.addVertex(0, 1, 0, 0, 0);
			builder.addVertex(0, 1, 1, 16, 0);
			builder.addVertex(1, 1, 1, 16, 16);
			builder.addVertex(1, 1, 0, 0, 16);
		}

		QuadBuilder.Quads result = builder.build();
		this.faceQuads = result.culledQuads;
		this.nonCulledQuads = result.nonCulledQuads;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		boolean upsidedown = state != null && state.getValue(StairBlock.HALF) == Half.TOP;
		boolean cornerNW = extraData.has(NORTH_WEST) ? extraData.get(NORTH_WEST) : true;
		boolean cornerNE = extraData.has(NORTH_EAST) ? extraData.get(NORTH_EAST) : false;
		boolean cornerSE = extraData.has(SOUTH_EAST) ? extraData.get(SOUTH_EAST) : false;
		boolean cornerSW = extraData.has(SOUTH_WEST) ? extraData.get(SOUTH_WEST) : true;
		Direction slantDir = state != null ? state.getValue(StairBlock.FACING) : Direction.WEST;

		int index = 0;
		if (cornerNW) index |= 0x1;
		if (cornerNE) index |= 0x2;
		if (cornerSE) index |= 0x4;
		if (cornerSW) index |= 0x8;
		if (upsidedown) index |= 0x10;
		index |= slantDir.get3DDataValue() << 5;

		SlantModel model = this.modelCache.getUnchecked(index);

		return side == null ? model.nonCulledQuads : model.faceQuads.get(side);
	}

	@Override
	public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
		modelData = IDynamicBakedModel.super.getModelData(level, pos, state, modelData);

		//x, z
		//0, 0
		boolean cornerNW = false;
		//1, 0
		boolean cornerNE = false;
		//1, 1
		boolean cornerSE = false;
		//0, 1
		boolean cornerSW = false;

		EnumMap<Direction, Half> halves = new EnumMap<>(Direction.class);
		EnumMap<Direction, Direction> facings = new EnumMap<>(Direction.class);
		for(Direction side : Direction.Plane.HORIZONTAL) {
			BlockState offsetState = level.getBlockState(pos.relative(side));
			if(offsetState.getBlock() instanceof SlopeBlock) {
				facings.put(side, offsetState.getValue(StairBlock.FACING));
				halves.put(side, offsetState.getValue(StairBlock.HALF));
			}
		}

		Half half = state.getValue(StairBlock.HALF);

		switch(state.getValue(StairBlock.FACING)) {
			case NORTH:
				cornerNW = true;
				cornerNE = true;
				if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.WEST && facings.get(Direction.EAST) != Direction.NORTH) {
					cornerNE = false;
				}
				if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.EAST && facings.get(Direction.WEST) != Direction.NORTH) {
					cornerNW = false;
				}
				if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.WEST && facings.get(Direction.WEST) != Direction.NORTH) {
					cornerSW = true;
				}
				if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.EAST && facings.get(Direction.EAST) != Direction.NORTH) {
					cornerSE = true;
				}
				break;
			case SOUTH:
				cornerSE = true;
				cornerSW = true;
				if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.WEST && facings.get(Direction.EAST) != Direction.SOUTH) {
					cornerSE = false;
				}
				if(halves.get(Direction.SOUTH) == half && facings.get(Direction.SOUTH) == Direction.EAST && facings.get(Direction.WEST) != Direction.SOUTH) {
					cornerSW = false;
				}
				if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.WEST && facings.get(Direction.WEST) != Direction.SOUTH) {
					cornerNW = true;
				}
				if(halves.get(Direction.NORTH) == half && facings.get(Direction.NORTH) == Direction.EAST && facings.get(Direction.EAST) != Direction.SOUTH) {
					cornerNE = true;
				}
				break;
			case EAST:
				cornerNE = true;
				cornerSE = true;
				if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.SOUTH && facings.get(Direction.NORTH) != Direction.EAST) {
					cornerNE = false;
				}
				if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.NORTH && facings.get(Direction.SOUTH) != Direction.EAST) {
					cornerSE = false;
				}
				if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.SOUTH && facings.get(Direction.SOUTH) != Direction.EAST) {
					cornerSW = true;
				}
				if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.NORTH && facings.get(Direction.NORTH) != Direction.EAST) {
					cornerNW = true;
				}
				break;
			case WEST:
				cornerSW = true;
				cornerNW = true;
				if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.SOUTH && facings.get(Direction.NORTH) != Direction.WEST) {
					cornerNW = false;
				}
				if(halves.get(Direction.WEST) == half && facings.get(Direction.WEST) == Direction.NORTH && facings.get(Direction.SOUTH) != Direction.WEST) {
					cornerSW = false;
				}
				if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.SOUTH && facings.get(Direction.SOUTH) != Direction.WEST) {
					cornerSE = true;
				}
				if(halves.get(Direction.EAST) == half && facings.get(Direction.EAST) == Direction.NORTH && facings.get(Direction.NORTH) != Direction.WEST) {
					cornerNE = true;
				}
				break;
		}
		return modelData.derive().with(NORTH_WEST, cornerNW).with(NORTH_EAST, cornerNE).with(SOUTH_WEST, cornerSW).with(SOUTH_EAST, cornerSE).build();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.textureSlant;
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	@Override
	public ItemTransforms getTransforms() {
		return this.transforms;
	}
}
