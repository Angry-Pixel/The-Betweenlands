package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.registries.SoundRegistry;

public class DraetonLeakSound extends EntitySound<EntityDraeton> {
	public DraetonLeakSound(EntityDraeton draeton) {
		super(SoundRegistry.DRAETON_LEAK_LOOP, SoundCategory.NEUTRAL, draeton, EntityDraeton::isLeaking);
		this.repeat = true;
		this.volume = 0.01f;
	}
	
	@Override
	public void update() {
		super.update();
		
		if(!this.fadeOut) {
			float targetVolume = Math.min(this.entity.getLeakages().size(), 8) / 8.0f * 0.8f + 0.1f;
			
			if(this.volume < targetVolume) {
				this.volume = Math.min(targetVolume, this.volume + 0.1f);
			} else if(this.volume > targetVolume) {
				this.volume = Math.max(0, this.volume - 0.1f);
			}
		}
		
		Vec3d burnerPos = this.entity.getBalloonPos(1);
		
		this.xPosF = (float) burnerPos.x;
		this.yPosF = (float) burnerPos.y;
		this.zPosF = (float) burnerPos.z;
	}
}