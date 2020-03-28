package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.registries.SoundRegistry;

public class DraetonBurnerSound extends EntitySound<EntityDraeton> {
	private float targetVolume;
	
	public DraetonBurnerSound(EntityDraeton draeton) {
		super(SoundRegistry.DRAETON_BURNER, SoundCategory.NEUTRAL, draeton, EntityDraeton::isBurnerRunning);
		this.repeat = true;
		this.targetVolume = 0.8f;
		this.volume = 0.01f;
	}
	
	@Override
	public void update() {
		super.update();
		
		if(!this.fadeOut && this.volume < this.targetVolume) {
			this.volume = Math.min(this.targetVolume, this.volume + 0.1f);
		}
		
		Vec3d burnerPos = this.entity.getBalloonPos(1);
		
		this.xPosF = (float) burnerPos.x;
		this.yPosF = (float) burnerPos.y;
		this.zPosF = (float) burnerPos.z;
	}
}