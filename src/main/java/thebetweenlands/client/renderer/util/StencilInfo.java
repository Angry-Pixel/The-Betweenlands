package thebetweenlands.client.renderer.util;

public record StencilInfo(Stencil stencil, StencilState before, StencilState after) {
	
	public static final StencilInfo INVALID = new StencilInfo(Stencil.INVALID, null, null);
	
	public boolean isInvalid() {
		return this == INVALID || this.stencil() == null || !this.stencil().isValid();
	}

	public StencilInfo withStencil(Stencil stencil) {
		return StencilInfo.of(stencil, this.before, this.after);
	}

	public StencilInfo withBefore(StencilState before) {
		return StencilInfo.of(this.stencil, before, this.after);
	}

	public StencilInfo withAfter(StencilState after) {
		return StencilInfo.of(this.stencil, this.before, after);
	}

	public static StencilInfo of(Stencil stencil, StencilState before, StencilState after) {
		if(stencil == null) {
			stencil = Stencil.INVALID;
		}
		if(!stencil.isValid() && before == null && after == null) {
			return INVALID;
		}
		return new StencilInfo(stencil, before, after);
	}
	
	
}