package thebetweenlands.client.renderer.util.rendertype.modifier;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.GlStateBackup;
import thebetweenlands.common.TheBetweenlands;

public class PolygonOffsetModifier implements RenderTypeModifier<PolygonOffsetModifier.PolygonOffsetContext> {

	public static final PolygonOffsetModifier INSTANCE = new PolygonOffsetModifier(TheBetweenlands.prefix("polygon_offset"));

	protected final ResourceLocation id;
	
	public PolygonOffsetModifier(ResourceLocation id) {
		this.id = id;
	}
	
	@Override
	public ResourceLocation getId() {
		return id;
	}
	
	@Override
	public void beforeSetupRenderState(RenderType originalRenderType, PolygonOffsetContext context) {
		// NO-OP
	}

	@Override
	public void setupRenderState(RenderType originalRenderType, PolygonOffsetContext context) {
		PolygonOffset offset = PolygonOffset.get();
		context.getStack().addLast(offset);

		RenderSystem.enablePolygonOffset();
		RenderSystem.polygonOffset(context.getFactor(), context.getUnits());
	}

	@Override
	public void clearRenderState(RenderType originalRenderType, PolygonOffsetContext context) {
		PolygonOffset offset = context.getStack().pollLast();
		offset.apply();
	}

	@Override
	public void afterClearRenderState(RenderType originalRenderType, PolygonOffsetContext context) {
		// NO-OP
	}

	@Override
	public PolygonOffsetContext createDefaultData(RenderType originalRenderType) {
		return new PolygonOffsetContext();
	}
	
	public static record PolygonOffset(boolean fillEnabled, boolean lineEnabled, float factor, float units) {
		public PolygonOffset() {
			this(false, false, 0.0f, 0.0f);
		}
		
		public PolygonOffset(boolean fillEnabled, boolean lineEnabled, float factor, float units) { 
			this.fillEnabled = fillEnabled;
			this.lineEnabled = lineEnabled;
			if(fillEnabled || lineEnabled) {
				if(!Float.isFinite(factor)) {
					throw new IllegalArgumentException("'factor' must be finite, got " + Float.toString(factor));
				}
				if(!Float.isFinite(units)) {
					throw new IllegalArgumentException("'units' must be finite, got " + Float.toString(units));
				}
			}
			this.factor = factor;
			this.units = units;
		}
		
		public void apply() {
			if(this.fillEnabled()) {
				RenderSystem.enablePolygonOffset();
			} else {
				RenderSystem.disablePolygonOffset();
			}
			// TODO GlStateManager tracks GL_POLYGON_OFFSET_LINE, but doesn't expose any methods to change or get it
			if(this.lineEnabled()) {
				GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
			} else {
				GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
			}
			RenderSystem.polygonOffset(this.factor(), this.units());
		}
		
		public static PolygonOffset get() {
	        RenderSystem.assertOnRenderThread();
	        GlStateBackup backup = new GlStateBackup();
	        RenderSystem.backupGlState(backup);
			boolean fillEnabled = backup.polyOffsetFillEnabled;
			boolean lineEnabled = backup.polyOffsetLineEnabled;
			float factor = backup.polyOffsetFactor;
			float units = backup.polyOffsetUnits;
			return new PolygonOffset(fillEnabled, lineEnabled, factor, units);
		}
	}
	
	public static class PolygonOffsetContext {
		protected Deque<PolygonOffset> stack;
		protected float factor = 0.0f;
		protected float units = 0.0f;
		
		public Deque<PolygonOffset> getStack() {
			if(this.stack == null) {
				this.stack = new ArrayDeque<>();
			}
			return this.stack;
		}

		public float getFactor() {
			return this.factor;
		}
		
		public PolygonOffsetContext setFactor(float factor) {
			if(!Float.isFinite(factor)) {
				throw new IllegalArgumentException("'factor' must be finite, got " + Float.toString(factor));
			}
			this.factor = factor;
			return this;
		}

		public float getUnits() {
			return this.units;
		}
		
		public PolygonOffsetContext setUnits(float units) {
			if(!Float.isFinite(units)) {
				throw new IllegalArgumentException("'units' must be finite, got " + Float.toString(units));
			}
			this.units = units;
			return this;
		}
		
		public PolygonOffsetContext set(float factor, float units) {
			this.setFactor(factor);
			this.setUnits(units);
			return this;
		}
	}
}
