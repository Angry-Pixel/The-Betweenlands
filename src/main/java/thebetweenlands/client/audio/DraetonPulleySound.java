package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.registries.SoundRegistry;

public class DraetonPulleySound extends EntitySound<EntityDraeton> {
	private float targetVolume;
	
	public DraetonPulleySound(EntityDraeton draeton) {
		super(SoundRegistry.DRAETON_PULLEY, SoundCategory.NEUTRAL, draeton, EntityDraeton::isReelingInAnchor);
		this.repeat = true;
		this.targetVolume = 1.0f;
		this.volume = 0.01f;
	}
	
	@Override
	public void update() {
		super.update();
		
		if(!this.fadeOut && this.volume < this.targetVolume) {
			this.volume = Math.min(this.targetVolume, this.volume + 0.2f);
		}
	}
}