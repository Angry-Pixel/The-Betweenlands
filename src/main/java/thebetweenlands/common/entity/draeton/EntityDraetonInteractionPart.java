package thebetweenlands.common.entity.draeton;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class EntityDraetonInteractionPart extends MultiPartEntityPart {
	private final EntityDraeton draeton;
	private final boolean isCarriage;

	private boolean enabled = true;
	
	EntityDraetonInteractionPart(EntityDraeton draeton, String name, float width, float height, boolean isCarriage) {
		super(draeton, name, width, height);
		this.draeton = draeton;
		this.isCarriage = isCarriage;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		return this.draeton.interactFromMultipart(this, player, hand);
	}

	@Override
	public boolean canBeCollidedWith() {
		if(!this.enabled) {
			return false;
		}
		if(this.isCarriage) {
			return this.draeton.canBeCollidedWith();
		}
		return true;
	}

	@Override
	public boolean canRiderInteract() {
		if(this.isCarriage) {
			return this.draeton.canRiderInteract();
		}
		return super.canRiderInteract();
	}

	@Override
	public Entity getLowestRidingEntity() {
		if(this.isCarriage) {
			return this.draeton;
		}
		return super.getLowestRidingEntity();
	}
}