package thebetweenlands.client.renderer.util.rendertype;

import java.util.Optional;

import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderType;

/**
 * Base class for other proxy render types, so they don't
 * have to re-implement all of these methods and can just
 * override what they need.
 */
public class ProxyRenderType extends RenderType {

	protected final RenderType delegate;
	
	public ProxyRenderType(String name, RenderType delegate) {
		// we override all the getters
		super(name, delegate.format, delegate.mode, delegate.bufferSize, delegate.affectsCrumbling, delegate.sortOnUpload, delegate.setupState, delegate.clearState);
		this.delegate = delegate;
	}

	public RenderType getDelegate() {
		return this.delegate;
	}
	
	@Override
	public VertexFormat format() {
		return this.delegate.format();
	}
	
	@Override
	public Mode mode() {
		return this.delegate.mode();
	}
	
	@Override
	public int bufferSize() {
		return this.delegate.bufferSize();
	}
	
	@Override
	public boolean affectsCrumbling() {
		return this.delegate.affectsCrumbling();
	}
	
	@Override
	public boolean sortOnUpload() {
		return this.delegate.sortOnUpload();
	}
	
	@Override
	public void setupRenderState() {
		this.delegate.setupRenderState();
	}
	
	@Override
	public void clearRenderState() {
		this.delegate.clearRenderState();
	}

	@Override
	public void draw(MeshData meshData) {
		this.delegate.draw(meshData);
	}
	
	@Override
	public boolean canConsolidateConsecutiveGeometry() {
		return this.delegate.canConsolidateConsecutiveGeometry();
	}
	
	@Override
	public boolean isOutline() {
		return this.delegate.isOutline();
	}
	
	@Override
	public Optional<RenderType> outline() {
		// Tough decision, but figured it can be overridden by subclasses
		return this.delegate.outline();
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s]", this.name, this.delegate.toString());
	}
}
