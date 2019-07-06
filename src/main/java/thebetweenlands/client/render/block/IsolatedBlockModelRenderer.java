package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

@SideOnly(Side.CLIENT)
public class IsolatedBlockModelRenderer {
	public static interface OcclusionCuller {
		/**
		 * Returns whether the specified face should be rendered
		 * @param blockState Block state
		 * @param facing Face of the quad
		 * @return
		 */
		public boolean shouldSideBeRendered(IBlockState blockState, EnumFacing facing);
	}

	public static interface AmbientOcclusionProvider {
		/**
		 * Returns the ambient occlusion 
		 * @param state Block state
		 * @param facing Face of the quad, may be null
		 * @return
		 */
		public AmbientOcclusionFace getAmbientOcclusion(IBlockState state, @Nullable EnumFacing facing, float[] faceShape, BitSet shapeState);
	}

	public static interface LightingProvider {
		/**
		 * Returns a light map coordinate depending on the face
		 * @param state Block state
		 * @param facing Face of the quad, may be null
		 * @return
		 */
		public int getPackedLightmapCoords(IBlockState state, @Nullable EnumFacing facing);
	}

	public static interface TintProvider {
		/**
		 * Returns the block tint depending on the tint index
		 * @param state BlockState
		 * @param tintIndex Tint index
		 * @return
		 */
		public int getBlockTint(IBlockState state, int tintIndex);
	}

	private OcclusionCuller culler;
	private AmbientOcclusionProvider ao;
	private LightingProvider lighting;
	private TintProvider tint;
	private boolean useRandomOffsets = true;

	/**
	 * Sets the occlusion culler
	 * @param culler
	 * @return
	 */
	public IsolatedBlockModelRenderer setOcclusionCuller(OcclusionCuller culler) {
		this.culler = culler;
		return this;
	}

	/**
	 * Sets the ambient occlusion provider
	 * @param ao
	 * @return
	 */
	public IsolatedBlockModelRenderer setAmbientOcclusion(AmbientOcclusionProvider ao) {
		this.ao = ao;
		return this;
	}

	/**
	 * Sets the lighting provider
	 * @param lighting
	 * @return
	 */
	public IsolatedBlockModelRenderer setLighting(LightingProvider lighting) {
		this.lighting = lighting;
		return this;
	}

	/**
	 * Sets the tint provider
	 * @param tint
	 * @return
	 */
	public IsolatedBlockModelRenderer setTint(TintProvider tint) {
		this.tint = tint;
		return this;
	}

	/**
	 * Sets whether random offsets should be used
	 * @param useRandomOffsets
	 * @return
	 */
	public IsolatedBlockModelRenderer setUseRandomOffsets(boolean useRandomOffsets) {
		this.useRandomOffsets = useRandomOffsets;
		return this;
	}

	/**
	 * Renders a block model
	 * @param pos Position used for random offsets
	 * @param model Model to render
	 * @param state Block state
	 * @param rand Random seed used to get a weighted random model
	 * @param buffer Vertex buffer
	 * @return
	 */
	public boolean renderModel(BlockPos pos, IBakedModel model, IBlockState state, long rand, BufferBuilder buffer) {
		@SuppressWarnings("deprecation")
		boolean useAO = Minecraft.isAmbientOcclusionEnabled() && state.getLightValue() == 0 && model.isAmbientOcclusion() && this.ao != null;

		try {
			return useAO ? this.renderModelSmooth(pos, model, state, buffer, rand) : this.renderModelFlat(pos, model, state, buffer, rand);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
			CrashReportCategory.addBlockInfo(crashreportcategory, pos, state);
			crashreportcategory.addCrashSection("Using AO", Boolean.valueOf(useAO));
			throw new ReportedException(crashreport);
		}
	}

	private boolean renderModelSmooth(BlockPos pos, IBakedModel model, IBlockState state, BufferBuilder buffer, long rand) {
		boolean flag = false;
		float[] blockBounds = new float[EnumFacing.VALUES.length * 2];
		BitSet bitset = new BitSet(3);

		List<BakedQuad> list = model.getQuads(state, null, rand);
		
		if (!list.isEmpty()) {
			this.renderQuadsSmooth(pos, state, buffer, list, blockBounds, bitset);
			flag = true;
		}
		
		for (EnumFacing facing : EnumFacing.VALUES) {
			list = model.getQuads(state, facing, rand);
			
			if (!list.isEmpty() && (culler == null || culler.shouldSideBeRendered(state, facing))) {
				this.renderQuadsSmooth(pos, state, buffer, list, blockBounds, bitset);
				flag = true;
			}
		}

		/*List<BakedQuad> list1 = model.getQuads(state, (EnumFacing)null, rand);

		if (!list1.isEmpty()) {
			this.renderQuadsSmooth(pos, state, buffer, list1, blockBounds, bitset);
			flag = true;
		}*/

		return flag;
	}

	private boolean renderModelFlat(BlockPos pos, IBakedModel model, IBlockState state, BufferBuilder buffer, long rand) {
		boolean flag = false;
		BitSet bitset = new BitSet(3);

		List<BakedQuad> list = model.getQuads(state, null, rand);
		
		if (!list.isEmpty()) {
			this.renderQuadsFlat(state, pos, -1, true, buffer, list, bitset, lighting);
			flag = true;
		}
		
		for (EnumFacing facing : EnumFacing.VALUES) {
			list = model.getQuads(state, facing, rand);
			
			if (!list.isEmpty() && (culler == null || culler.shouldSideBeRendered(state, facing))) {
				int i = lighting != null ? lighting.getPackedLightmapCoords(state, facing) : 0;
				this.renderQuadsFlat(state, pos, i, false, buffer, list, bitset, lighting);
				flag = true;
			}
		}

		/*List<BakedQuad> list1 = model.getQuads(state, (EnumFacing)null, rand);

		if (!list1.isEmpty()) {
			this.renderQuadsFlat(state, pos, -1, true, buffer, list1, bitset, lighting);
			flag = true;
		}*/

		return flag;
	}

	private void renderQuadsSmooth(BlockPos pos, IBlockState state, BufferBuilder vertexBuffer, List<BakedQuad> quads, float[] blockBounds, BitSet blockBoundsState) {
		double blockX = 0.0D;
		double blockY = 0.0D;
		double blockZ = 0.0D;
		Block block = state.getBlock();
		Block.EnumOffsetType offsetType = block.getOffsetType();

		if (this.useRandomOffsets && offsetType != Block.EnumOffsetType.NONE) {
			long posRand = MathHelper.getPositionRandom(pos);
			blockX += ((double)((float)(posRand >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
			blockZ += ((double)((float)(posRand >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

			if (offsetType == Block.EnumOffsetType.XYZ) {
				blockY += ((double)((float)(posRand >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
			}
		}

		int l = 0;

		for (int j = quads.size(); l < j; ++l) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(l);
			this.fillQuadBounds(state, bakedQuad.getVertexData(), bakedQuad.getFace(), blockBounds, blockBoundsState);
			AmbientOcclusionFace aoFace = null;
			if(ao != null)
				aoFace = ao.getAmbientOcclusion(state, bakedQuad.getFace(), blockBounds, blockBoundsState);
			if(aoFace == null)
				aoFace = new AmbientOcclusionFace();
			vertexBuffer.addVertexData(bakedQuad.getVertexData());
			vertexBuffer.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3]);
			if(bakedQuad.shouldApplyDiffuseLighting()) {
				float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedQuad.getFace());
				aoFace.vertexColorMultiplier[0] *= diffuse;
				aoFace.vertexColorMultiplier[1] *= diffuse;
				aoFace.vertexColorMultiplier[2] *= diffuse;
				aoFace.vertexColorMultiplier[3] *= diffuse;
			}
			if (bakedQuad.hasTintIndex()) {
				int tint = this.tint != null ? this.tint.getBlockTint(state, bakedQuad.getTintIndex()) : 0xFFFFFF;

				if (EntityRenderer.anaglyphEnable) {
					tint = TextureUtil.anaglyphColor(tint);
				}

				float tintRed = (float)(tint >> 16 & 255) / 255.0F;
				float tintGreen = (float)(tint >> 8 & 255) / 255.0F;
				float tintBlue = (float)(tint & 255) / 255.0F;
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[0] * tintRed, aoFace.vertexColorMultiplier[0] * tintGreen, aoFace.vertexColorMultiplier[0] * tintBlue, 4);
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[1] * tintRed, aoFace.vertexColorMultiplier[1] * tintGreen, aoFace.vertexColorMultiplier[1] * tintBlue, 3);
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[2] * tintRed, aoFace.vertexColorMultiplier[2] * tintGreen, aoFace.vertexColorMultiplier[2] * tintBlue, 2);
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[3] * tintRed, aoFace.vertexColorMultiplier[3] * tintGreen, aoFace.vertexColorMultiplier[3] * tintBlue, 1);
			} else {
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], 4);
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], 3);
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], 2);
				vertexBuffer.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], 1);
			}

			vertexBuffer.putPosition(blockX, blockY, blockZ);
		}
	}

	private void renderQuadsFlat(IBlockState state, BlockPos pos, int brightness, boolean updateBrightness, BufferBuilder vertexBuffer, List<BakedQuad> quads, BitSet blockBoundsState, @Nullable LightingProvider lighting) {
		double blockX = 0.0D;
		double blockY = 0.0D;
		double blockZ = 0.0D;
		Block block = state.getBlock();
		Block.EnumOffsetType offsetType = block.getOffsetType();

		if (this.useRandomOffsets && offsetType != Block.EnumOffsetType.NONE) {
			int randX = pos.getX();
			int randZ = pos.getZ();
			long posRand = (long)(randX * 3129871) ^ (long)randZ * 116129781L;
			posRand = posRand * posRand * 42317861L + posRand * 11L;
			blockX += ((double)((float)(posRand >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
			blockZ += ((double)((float)(posRand >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

			if (offsetType == Block.EnumOffsetType.XYZ)
			{
				blockY += ((double)((float)(posRand >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
			}
		}

		int l = 0;

		for (int i1 = quads.size(); l < i1; ++l) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(l);

			if (updateBrightness) {
				this.fillQuadBounds(state, bakedQuad.getVertexData(), bakedQuad.getFace(), (float[])null, blockBoundsState);
				brightness = lighting.getPackedLightmapCoords(state, blockBoundsState.get(0) ? bakedQuad.getFace() : null);
			}

			vertexBuffer.addVertexData(bakedQuad.getVertexData());
			vertexBuffer.putBrightness4(brightness, brightness, brightness, brightness);

			if (bakedQuad.hasTintIndex()) {
				int tint = this.tint != null ? this.tint.getBlockTint(state, bakedQuad.getTintIndex()) : 0xFFFFFF;

				if (EntityRenderer.anaglyphEnable) {
					tint = TextureUtil.anaglyphColor(tint);
				}

				float tintRed = (float)(tint >> 16 & 255) / 255.0F;
				float tintGreen = (float)(tint >> 8 & 255) / 255.0F;
				float tintBlue = (float)(tint & 255) / 255.0F;
				if(bakedQuad.shouldApplyDiffuseLighting()) {
					float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedQuad.getFace());
					tintRed *= diffuse;
					tintGreen *= diffuse;
					tintBlue *= diffuse;
				}
				vertexBuffer.putColorMultiplier(tintRed, tintGreen, tintBlue, 4);
				vertexBuffer.putColorMultiplier(tintRed, tintGreen, tintBlue, 3);
				vertexBuffer.putColorMultiplier(tintRed, tintGreen, tintBlue, 2);
				vertexBuffer.putColorMultiplier(tintRed, tintGreen, tintBlue, 1);
			} else if(bakedQuad.shouldApplyDiffuseLighting()) {
				float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedQuad.getFace());
				vertexBuffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
				vertexBuffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
				vertexBuffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
				vertexBuffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
			}

			vertexBuffer.putPosition(blockX, blockY, blockZ);
		}
	}

	private void fillQuadBounds(IBlockState state, int[] vertexData, EnumFacing facing, @Nullable float[] blockBounds, BitSet blockBoundsState) {
		float f = 32.0F;
		float f1 = 32.0F;
		float f2 = 32.0F;
		float f3 = -32.0F;
		float f4 = -32.0F;
		float f5 = -32.0F;

		for (int i = 0; i < 4; ++i) {
			float f6 = Float.intBitsToFloat(vertexData[i * 7]);
			float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
			float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
			f = Math.min(f, f6);
			f1 = Math.min(f1, f7);
			f2 = Math.min(f2, f8);
			f3 = Math.max(f3, f6);
			f4 = Math.max(f4, f7);
			f5 = Math.max(f5, f8);
		}

		if (blockBounds != null) {
			blockBounds[EnumFacing.WEST.getIndex()] = f;
			blockBounds[EnumFacing.EAST.getIndex()] = f3;
			blockBounds[EnumFacing.DOWN.getIndex()] = f1;
			blockBounds[EnumFacing.UP.getIndex()] = f4;
			blockBounds[EnumFacing.NORTH.getIndex()] = f2;
			blockBounds[EnumFacing.SOUTH.getIndex()] = f5;
			blockBounds[EnumFacing.WEST.getIndex() + EnumFacing.VALUES.length] = 1.0F - f;
			blockBounds[EnumFacing.EAST.getIndex() + EnumFacing.VALUES.length] = 1.0F - f3;
			blockBounds[EnumFacing.DOWN.getIndex() + EnumFacing.VALUES.length] = 1.0F - f1;
			blockBounds[EnumFacing.UP.getIndex() + EnumFacing.VALUES.length] = 1.0F - f4;
			blockBounds[EnumFacing.NORTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - f2;
			blockBounds[EnumFacing.SOUTH.getIndex() + EnumFacing.VALUES.length] = 1.0F - f5;
		}

		switch (facing) {
		case DOWN:
			blockBoundsState.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			blockBoundsState.set(0, (f1 < 1.0E-4F || state.isFullCube()) && f1 == f4);
			break;
		case UP:
			blockBoundsState.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			blockBoundsState.set(0, (f4 > 0.9999F || state.isFullCube()) && f1 == f4);
			break;
		case NORTH:
			blockBoundsState.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			blockBoundsState.set(0, (f2 < 1.0E-4F || state.isFullCube()) && f2 == f5);
			break;
		case SOUTH:
			blockBoundsState.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			blockBoundsState.set(0, (f5 > 0.9999F || state.isFullCube()) && f2 == f5);
			break;
		case WEST:
			blockBoundsState.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			blockBoundsState.set(0, (f < 1.0E-4F || state.isFullCube()) && f == f3);
			break;
		case EAST:
			blockBoundsState.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			blockBoundsState.set(0, (f3 > 0.9999F || state.isFullCube()) && f == f3);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class AmbientOcclusionFace {
		protected final float[] vertexColorMultiplier = new float[4];
		protected final int[] vertexBrightness = new int[4];

		public void updateVertexBrightness(IBlockAccess worldIn, IBlockState state, BlockPos centerPos, EnumFacing direction, float[] faceShape, BitSet shapeState) {
			BlockPos blockpos = shapeState.get(0) ? centerPos.offset(direction) : centerPos;
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
			IsolatedBlockModelRenderer.EnumNeighborInfo blockmodelrenderer$enumneighborinfo = IsolatedBlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(direction);
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos1 = BlockPos.PooledMutableBlockPos.retain(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]);
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos2 = BlockPos.PooledMutableBlockPos.retain(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]);
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos3 = BlockPos.PooledMutableBlockPos.retain(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[2]);
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos4 = BlockPos.PooledMutableBlockPos.retain(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[3]);
			int i = state.getPackedLightmapCoords(worldIn, blockpos$pooledmutableblockpos1);
			int j = state.getPackedLightmapCoords(worldIn, blockpos$pooledmutableblockpos2);
			int k = state.getPackedLightmapCoords(worldIn, blockpos$pooledmutableblockpos3);
			int l = state.getPackedLightmapCoords(worldIn, blockpos$pooledmutableblockpos4);
			float f = worldIn.getBlockState(blockpos$pooledmutableblockpos1).getAmbientOcclusionLightValue();
			float f1 = worldIn.getBlockState(blockpos$pooledmutableblockpos2).getAmbientOcclusionLightValue();
			float f2 = worldIn.getBlockState(blockpos$pooledmutableblockpos3).getAmbientOcclusionLightValue();
			float f3 = worldIn.getBlockState(blockpos$pooledmutableblockpos4).getAmbientOcclusionLightValue();
			boolean flag = worldIn.getBlockState(blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos1).move(direction)).isTranslucent();
			boolean flag1 = worldIn.getBlockState(blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos2).move(direction)).isTranslucent();
			boolean flag2 = worldIn.getBlockState(blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos3).move(direction)).isTranslucent();
			boolean flag3 = worldIn.getBlockState(blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos4).move(direction)).isTranslucent();
			float f4;
			int i1;

			if (!flag2 && !flag) {
				f4 = f;
				i1 = i;
			} else {
				BlockPos blockpos1 = blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos1).move(blockmodelrenderer$enumneighborinfo.corners[2]);
				f4 = worldIn.getBlockState(blockpos1).getAmbientOcclusionLightValue();
				i1 = state.getPackedLightmapCoords(worldIn, blockpos1);
			}

			float f5;
			int j1;

			if (!flag3 && !flag) {
				f5 = f;
				j1 = i;
			} else {
				BlockPos blockpos2 = blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos1).move(blockmodelrenderer$enumneighborinfo.corners[3]);
				f5 = worldIn.getBlockState(blockpos2).getAmbientOcclusionLightValue();
				j1 = state.getPackedLightmapCoords(worldIn, blockpos2);
			}

			float f6;
			int k1;

			if (!flag2 && !flag1) {
				f6 = f1;
				k1 = j;
			} else {
				BlockPos blockpos3 = blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos2).move(blockmodelrenderer$enumneighborinfo.corners[2]);
				f6 = worldIn.getBlockState(blockpos3).getAmbientOcclusionLightValue();
				k1 = state.getPackedLightmapCoords(worldIn, blockpos3);
			}

			float f7;
			int l1;

			if (!flag3 && !flag1) {
				f7 = f1;
				l1 = j;
			} else {
				BlockPos blockpos4 = blockpos$pooledmutableblockpos.setPos(blockpos$pooledmutableblockpos2).move(blockmodelrenderer$enumneighborinfo.corners[3]);
				f7 = worldIn.getBlockState(blockpos4).getAmbientOcclusionLightValue();
				l1 = state.getPackedLightmapCoords(worldIn, blockpos4);
			}

			int i3 = state.getPackedLightmapCoords(worldIn, centerPos);

			if (shapeState.get(0) || !worldIn.getBlockState(centerPos.offset(direction)).isOpaqueCube()) {
				i3 = state.getPackedLightmapCoords(worldIn, centerPos.offset(direction));
			}

			float f8 = shapeState.get(0) ? worldIn.getBlockState(blockpos).getAmbientOcclusionLightValue() : worldIn.getBlockState(centerPos).getAmbientOcclusionLightValue();
			IsolatedBlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = IsolatedBlockModelRenderer.VertexTranslations.getVertexTranslations(direction);
			blockpos$pooledmutableblockpos.release();
			blockpos$pooledmutableblockpos1.release();
			blockpos$pooledmutableblockpos2.release();
			blockpos$pooledmutableblockpos3.release();
			blockpos$pooledmutableblockpos4.release();

			if (shapeState.get(1) && blockmodelrenderer$enumneighborinfo.doNonCubicWeight) {
				float f29 = (f3 + f + f5 + f8) * 0.25F;
				float f30 = (f2 + f + f4 + f8) * 0.25F;
				float f31 = (f2 + f1 + f6 + f8) * 0.25F;
				float f32 = (f3 + f1 + f7 + f8) * 0.25F;
				float f13 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[1].shape];
				float f14 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[3].shape];
				float f15 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[5].shape];
				float f16 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[7].shape];
				float f17 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[1].shape];
				float f18 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[3].shape];
				float f19 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[5].shape];
				float f20 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[7].shape];
				float f21 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[1].shape];
				float f22 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[3].shape];
				float f23 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[5].shape];
				float f24 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[7].shape];
				float f25 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[1].shape];
				float f26 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[3].shape];
				float f27 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[5].shape];
				float f28 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[7].shape];
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
				int i2 = this.getAoBrightness(l, i, j1, i3);
				int j2 = this.getAoBrightness(k, i, i1, i3);
				int k2 = this.getAoBrightness(k, j, k1, i3);
				int l2 = this.getAoBrightness(l, j, l1, i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(i2, j2, k2, l2, f13, f14, f15, f16);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(i2, j2, k2, l2, f17, f18, f19, f20);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(i2, j2, k2, l2, f21, f22, f23, f24);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(i2, j2, k2, l2, f25, f26, f27, f28);
			} else {
				float f9 = (f3 + f + f5 + f8) * 0.25F;
				float f10 = (f2 + f + f4 + f8) * 0.25F;
				float f11 = (f2 + f1 + f6 + f8) * 0.25F;
				float f12 = (f3 + f1 + f7 + f8) * 0.25F;
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i3);
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
			}
		}

		/**
		 * Get ambient occlusion brightness
		 */
		protected int getAoBrightness(int br1, int br2, int br3, int br4) {
			if (br1 == 0) {
				br1 = br4;
			}

			if (br2 == 0) {
				br2 = br4;
			}

			if (br3 == 0) {
				br3 = br4;
			}

			return br1 + br2 + br3 + br4 >> 2 & 16711935;
		}

		protected int getVertexBrightness(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_) {
			int i = (int)((float)(p_178203_1_ >> 16 & 255) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 255) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 255) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
			int j = (int)((float)(p_178203_1_ & 255) * p_178203_5_ + (float)(p_178203_2_ & 255) * p_178203_6_ + (float)(p_178203_3_ & 255) * p_178203_7_ + (float)(p_178203_4_ & 255) * p_178203_8_) & 255;
			return i << 16 | j;
		}
	}

	@SideOnly(Side.CLIENT)
	public static enum EnumNeighborInfo {
		DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, true, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.SOUTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.SOUTH}),
		UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, true, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.SOUTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.SOUTH}),
		NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_WEST}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_EAST}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_EAST}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_WEST}),
		SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.WEST}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_WEST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.WEST, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.WEST}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.EAST}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_EAST, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.EAST, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.EAST}),
		WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.SOUTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.SOUTH}),
		EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.SOUTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.DOWN, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.NORTH, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_NORTH, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.NORTH}, new IsolatedBlockModelRenderer.Orientation[]{IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.SOUTH, IsolatedBlockModelRenderer.Orientation.FLIP_UP, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.FLIP_SOUTH, IsolatedBlockModelRenderer.Orientation.UP, IsolatedBlockModelRenderer.Orientation.SOUTH});

		private final EnumFacing[] corners;
		@SuppressWarnings("unused")
		private final float shadeWeight;
		private final boolean doNonCubicWeight;
		private final IsolatedBlockModelRenderer.Orientation[] vert0Weights;
		private final IsolatedBlockModelRenderer.Orientation[] vert1Weights;
		private final IsolatedBlockModelRenderer.Orientation[] vert2Weights;
		private final IsolatedBlockModelRenderer.Orientation[] vert3Weights;
		private static final IsolatedBlockModelRenderer.EnumNeighborInfo[] VALUES = new IsolatedBlockModelRenderer.EnumNeighborInfo[6];

		private EnumNeighborInfo(EnumFacing[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_, IsolatedBlockModelRenderer.Orientation[] p_i46236_6_, IsolatedBlockModelRenderer.Orientation[] p_i46236_7_, IsolatedBlockModelRenderer.Orientation[] p_i46236_8_, IsolatedBlockModelRenderer.Orientation[] p_i46236_9_) {
			this.corners = p_i46236_3_;
			this.shadeWeight = p_i46236_4_;
			this.doNonCubicWeight = p_i46236_5_;
			this.vert0Weights = p_i46236_6_;
			this.vert1Weights = p_i46236_7_;
			this.vert2Weights = p_i46236_8_;
			this.vert3Weights = p_i46236_9_;
		}

		public static IsolatedBlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing p_178273_0_) {
			return VALUES[p_178273_0_.getIndex()];
		}

		static {
			VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
			VALUES[EnumFacing.UP.getIndex()] = UP;
			VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
			VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
			VALUES[EnumFacing.WEST.getIndex()] = WEST;
			VALUES[EnumFacing.EAST.getIndex()] = EAST;
		}
	}

	@SideOnly(Side.CLIENT)
	public static enum Orientation {
		DOWN(EnumFacing.DOWN, false),
		UP(EnumFacing.UP, false),
		NORTH(EnumFacing.NORTH, false),
		SOUTH(EnumFacing.SOUTH, false),
		WEST(EnumFacing.WEST, false),
		EAST(EnumFacing.EAST, false),
		FLIP_DOWN(EnumFacing.DOWN, true),
		FLIP_UP(EnumFacing.UP, true),
		FLIP_NORTH(EnumFacing.NORTH, true),
		FLIP_SOUTH(EnumFacing.SOUTH, true),
		FLIP_WEST(EnumFacing.WEST, true),
		FLIP_EAST(EnumFacing.EAST, true);

		private final int shape;

		private Orientation(EnumFacing p_i46233_3_, boolean p_i46233_4_) {
			this.shape = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing.VALUES.length : 0);
		}
	}

	@SideOnly(Side.CLIENT)
	static enum VertexTranslations {
		DOWN(0, 1, 2, 3),
		UP(2, 3, 0, 1),
		NORTH(3, 0, 1, 2),
		SOUTH(0, 1, 2, 3),
		WEST(3, 0, 1, 2),
		EAST(1, 2, 3, 0);

		private final int vert0;
		private final int vert1;
		private final int vert2;
		private final int vert3;
		private static final IsolatedBlockModelRenderer.VertexTranslations[] VALUES = new IsolatedBlockModelRenderer.VertexTranslations[6];

		private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_) {
			this.vert0 = p_i46234_3_;
			this.vert1 = p_i46234_4_;
			this.vert2 = p_i46234_5_;
			this.vert3 = p_i46234_6_;
		}

		public static IsolatedBlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing p_178184_0_) {
			return VALUES[p_178184_0_.getIndex()];
		}

		static {
			VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
			VALUES[EnumFacing.UP.getIndex()] = UP;
			VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
			VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
			VALUES[EnumFacing.WEST.getIndex()] = WEST;
			VALUES[EnumFacing.EAST.getIndex()] = EAST;
		}
	}
}