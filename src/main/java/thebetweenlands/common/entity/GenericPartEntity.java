package thebetweenlands.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

public class GenericPartEntity<T extends Entity> extends PartEntity<T> {

	private final EntityDimensions size;

	public GenericPartEntity(T parent, float width, float height) {
		super(parent);
		this.size = EntityDimensions.scalable(width, height);
		this.refreshDimensions();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	public boolean isCurrentlyGlowing() {
		return this.getParent().isCurrentlyGlowing();
	}

	@Override
	public boolean isInvisible() {
		return this.getParent().isInvisible();
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return this.getParent().hurt(source, amount);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		return this.getParent().interact(player, hand);
	}

	@Override
	public boolean is(Entity entity) {
		return this == entity || this.getParent() == entity;
	}

	@Override
	public @Nullable ItemStack getPickResult() {
		return this.getParent().getPickResult();
	}

	@Override
	public @Nullable ItemStack getPickedResult(HitResult target) {
		return this.getParent().getPickedResult(target);
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return this.size;
	}

	@Override
	public boolean canUsePortal(boolean allowPassengers) {
		return false;
	}
}
