package thebetweenlands.common.capability.recruitment;

import java.util.List;

import net.minecraft.entity.Entity;

public interface IPuppeteerCapability {
	public List<Entity> getPuppets();

	public void setActivatingEntity(Entity entity);

	public Entity getActivatingEntity();

	public int getActivatingTicks();

	public void setActivatingTicks(int ticks);
}
