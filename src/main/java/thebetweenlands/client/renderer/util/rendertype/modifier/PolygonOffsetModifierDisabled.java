package thebetweenlands.client.renderer.util.rendertype.modifier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

// TODO see if this can be salvaged
public class PolygonOffsetModifierDisabled implements RenderTypeModifier<PolygonOffsetModifierDisabled.PolygonOffsetContext> {

	public static final PolygonOffsetModifierDisabled INSTANCE = new PolygonOffsetModifierDisabled(TheBetweenlands.prefix("polygon_offset"));

	protected final ResourceLocation id;

	public PolygonOffsetModifierDisabled(ResourceLocation id) {
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

		// TODO glIsEnabled and glGetXXX etc are BAD and SLOW, switch to using the stuff in GlStateManager
		int polygonMode = GlStateManager._getInteger(GL11.GL_POLYGON_MODE);

		PolygonOffsetMode mode = context.getMode();
		if(mode.state == polygonMode) {
			RenderSystem.enablePolygonOffset();
			PolygonOffsetType type = context.getType();
			final float newFactor;
			final float newUnits;
			if(type == PolygonOffsetType.ADD) {
				newFactor = offset.factor() + context.getFactor();
				newUnits = offset.units(); //bad
			} else {
				newFactor = context.getFactor();
				newUnits = context.getUnits();
			}
			RenderSystem.polygonOffset(newFactor, newUnits);
		}

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

	public record PolygonOffset(boolean fillEnabled, boolean lineEnabled, boolean pointEnabled, float factor, float units) {
		public PolygonOffset() {
			this(false, false, false, 0.0f, 0.0f);
		}

		public PolygonOffset(boolean fillEnabled, boolean lineEnabled, boolean pointEnabled, float factor, float units) {
			this.fillEnabled = fillEnabled;
			this.lineEnabled = lineEnabled;
			this.pointEnabled = pointEnabled;
			if(fillEnabled || lineEnabled || pointEnabled) {
				if(!Float.isFinite(factor)) {
					throw new IllegalArgumentException("'factor' must be finite, got " + factor);
				}
				if(!Float.isFinite(units)) {
					throw new IllegalArgumentException("'units' must be finite, got " + units);
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
			if(this.pointEnabled()) {
				GL11.glEnable(GL11.GL_POLYGON_OFFSET_POINT);
			} else {
				GL11.glDisable(GL11.GL_POLYGON_OFFSET_POINT);
			}
			RenderSystem.polygonOffset(this.factor(), this.units());
		}

		public static PolygonOffset get() {
			// TODO glIsEnabled and glGetXXX etc are BAD and SLOW, switch to using the stuff in GlStateManager
	        RenderSystem.assertOnRenderThread();
			boolean fillEnabled = GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_FILL);
			boolean lineEnabled = GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_LINE);
			boolean pointEnabled = GL11.glIsEnabled(GL11.GL_POLYGON_OFFSET_POINT);
			float factor = GL11.glGetFloat(GL11.GL_POLYGON_OFFSET_FACTOR);
			float units = GL11.glGetFloat(GL11.GL_POLYGON_OFFSET_FACTOR);
			return new PolygonOffset(fillEnabled, lineEnabled, pointEnabled, factor, units);
		}
	}

	public static class PolygonOffsetContext {
		protected Deque<PolygonOffset> stack;
		protected PolygonOffsetType type = PolygonOffsetType.SET;
		protected PolygonOffsetMode mode = PolygonOffsetMode.FILL;
		protected float factor = 0.0f;
		protected float units = 0.0f;

		public Deque<PolygonOffset> getStack() {
			if(this.stack == null) {
				this.stack = new ArrayDeque<>();
			}
			return this.stack;
		}

		public PolygonOffsetType getType() {
			return this.type;
		}

		public PolygonOffsetContext setType(PolygonOffsetType type) {
			this.type = Objects.requireNonNull(type);
			return this;
		}

		public PolygonOffsetMode getMode() {
			return this.mode;
		}

		public PolygonOffsetContext setMode(PolygonOffsetMode mode) {
			this.mode = Objects.requireNonNull(mode);
			return this;
		}

		public float getFactor() {
			return this.factor;
		}

		public PolygonOffsetContext setFactor(float factor) {
			if(!Float.isFinite(factor)) {
				throw new IllegalArgumentException("'factor' must be finite, got " + factor);
			}
			this.factor = factor;
			return this;
		}

		public float getUnits() {
			return this.units;
		}

		public PolygonOffsetContext setUnits(float units) {
			if(!Float.isFinite(units)) {
				throw new IllegalArgumentException("'units' must be finite, got " + units);
			}
			this.units = units;
			return this;
		}

		public PolygonOffsetContext set(PolygonOffsetType type, PolygonOffsetMode mode, float factor, float units) {
			this.setType(type);
			this.setMode(mode);
			this.setFactor(factor);
			this.setUnits(units);
			return this;
		}
	}

	public enum PolygonOffsetType {
		SET,
		ADD
    }

	public enum PolygonOffsetMode {
		FILL(GL11.GL_FILL), // this is the default
		LINE(GL11.GL_LINE),
		POINT(GL11.GL_POINT);

		public final int state;

		PolygonOffsetMode(int state) {
			this.state = state;
		}
	}
}
