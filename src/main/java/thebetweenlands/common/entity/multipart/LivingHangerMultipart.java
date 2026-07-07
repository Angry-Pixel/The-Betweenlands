package thebetweenlands.common.entity.multipart;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thebetweenlands.common.entity.monster.LivingHanger;

public class LivingHangerMultipart extends GenericPartEntity<LivingHanger> {

	public int index;

    public LivingHangerMultipart(LivingHanger parentMob, float width, float height, int index) {
        super(parentMob, width, height);
        this.index = index;
    }

	@Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return !isInvulnerableTo(source) && getParent().hurtSegment(this, source, amount);
	}

	public int getPartIndex() {
		return index;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
	    ItemStack itemStack = player.getItemInHand(hand);

	    if (itemStack.is(Items.SHEARS)) {
	        if (!level().isClientSide()) {
	            if (getParent() instanceof LivingHanger parentVine) {
	                parentVine.cutAtPart(index, player, hand);
	                itemStack.hurtAndBreak(1, (ServerPlayer) player, LivingEntity.getSlotForHand(hand));
	            }
	        }
	        playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
	        return InteractionResult.SUCCESS;
	    }

	    return super.interact(player, hand);
	}
}