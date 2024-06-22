package thebetweenlands.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGecko extends BetweenlandsEntity {

	public static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(EntityGecko.class, EntityDataSerializers.BOOLEAN);

	private static final int MIN_HIDE_TIME = 20 * 60 * 2;

	private static final float UNHIDE_CHANCE = 0.1F;

	private static final int PLAYER_MIN_DISTANCE = 7;

	private BlockPos hidingBush;

	private int timeHiding;

	public EntityGecko(EntityType<? extends Monster> p_33002_, Level p_33003_) {
		super(p_33002_, p_33003_);
		this.ambientSoundTime = 80;
	}

	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(EntityGecko.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 12.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.5D).build());
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GECKO_DEATH.get();
	}

	protected void registerGoals() {
	      this.goalSelector.addGoal(0, new FloatGoal(this));
	      this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
	      //this.goalSelector.addGoal(2, new TemptGoal(this, 0.5D, ItemRegistry.SAP_SPIT, true));
	      this.goalSelector.addGoal(3, new GeckoAvoidGoal<>(this, Player.class, PLAYER_MIN_DISTANCE, 0.65, 1));
	      // rain this.goalSelector.addGoal(4, new Goal);
	      this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.6));
	      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
	      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	protected void defineSynchedData() {
	      super.defineSynchedData();
	      this.entityData.define(HIDING, false);
	   }

	@Override
	protected SoundEvent getHurtSound(DamageSource p_33034_) {
		return SoundRegistry.GECKO_HURT.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GECKO_LIVING.get();
	}

	static class GeckoAvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
	      public GeckoAvoidGoal(EntityGecko gecko, Class<T> p_29276_, float p_29277_, double p_29278_, double p_29279_) {
	         super(gecko, p_29276_, p_29277_, p_29278_, p_29279_, EntitySelector.NO_SPECTATORS::test);
	      }
	   }
}
