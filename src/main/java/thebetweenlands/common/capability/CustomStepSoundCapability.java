package thebetweenlands.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.ICustomStepSoundCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class CustomStepSoundCapability extends EntityCapability<CustomStepSoundCapability, ICustomStepSoundCapability, EntityPlayer> implements ICustomStepSoundCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "custom_step_sound");
	}

	@Override
	protected Capability<ICustomStepSoundCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_CUSTOM_STEP_SOUND;
	}

	@Override
	protected Class<ICustomStepSoundCapability> getCapabilityClass() {
		return ICustomStepSoundCapability.class;
	}

	@Override
	protected CustomStepSoundCapability getDefaultCapabilityImplementation() {
		return new CustomStepSoundCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityLivingBase;
	}

	private float nextWeedwoodBushStep = 0;
	
	@Override
	public float getNextWeedwoodBushStep() {
		return this.nextWeedwoodBushStep;
	}

	@Override
	public float setNextWeeedwoodBushStep(float next) {
		return this.nextWeedwoodBushStep = next;
	}
}
