package thebetweenlands.client.render.entity;

import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;

public interface IMultipartDummyRendererDelegate<T extends Entity> {
	public void setRenderFromMultipart(EntityMultipartDummy dummy);
}
