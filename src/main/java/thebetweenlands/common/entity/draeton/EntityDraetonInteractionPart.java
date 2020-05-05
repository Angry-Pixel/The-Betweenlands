package thebetweenlands.common.entity.draeton;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;

public class EntityDraetonInteractionPart extends MultiPartEntityPart {
	private final EntityDraeton draeton;
	private final boolean isCarriage;
	private boolean enabled = true;

	private static final String translationBase = "entity.thebetweenlands.draeton";

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
	public String getName() {
		String name = "";
		if (draeton.upgradePart1 == this) {
			name = getUpgradeName(0);
		} else if (draeton.upgradePart2 == this) {
			name = getUpgradeName(1);
		} else if (draeton.upgradePart3 == this) {
			name = getUpgradeName(2);
		} else if (draeton.upgradePart4 == this) {
			name = getUpgradeName(3);
		} else if (draeton.upgradeAnchorPart == this) {
			name = translationBase + "_upgrade_anchor.name";
		} else if (draeton.burnerPart == this) {
			name = translationBase + "_burner.name";
		} else if (draeton.balloonFront == this || draeton.balloonMiddle == this || draeton.balloonBack == this) {
			name = translationBase + "_balloon.name";
		} else if (draeton.guiPart == this) {
			name = translationBase + ".name";
		}
		return I18n.translateToLocal(name);
	}

	private String getUpgradeName(int index) {
		ItemStack stack = draeton.getUpgradesInventory().getStackInSlot(index);
		if(draeton.isFurnaceUpgrade(stack)) {
			return translationBase + "_upgrade_furnace.name";
		} else if (draeton.isStorageUpgrade(stack)) {
			return stack.getDisplayName();
		} else if (draeton.isCraftingUpgrade(stack)) {
			return translationBase + "_upgrade_crafting.name";
		}
		return "";
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