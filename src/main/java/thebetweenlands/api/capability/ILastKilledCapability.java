package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

public interface ILastKilledCapability {
	public void setLastKilled(@Nullable ResourceLocation key);
	
	@Nullable
	public ResourceLocation getLastKilled();
}
