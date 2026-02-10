package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.ItemRegistry;

public class WatcherEyes extends BasicProximitySpawnerExtended {

	private static final EntityDataAccessor<Boolean> IS_OPENING = SynchedEntityData.defineId(WatcherEyes.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> OPEN_COUNT = SynchedEntityData.defineId(WatcherEyes.class, EntityDataSerializers.INT);
	public int MAX_OPEN = 10;
	public int MIN_OPEN = 0;
	public boolean triggerBlink1 = true;
	public boolean blink1 = true;
	public int blinkTick1 = 0;
	public boolean triggerBlink2 = true;
	public boolean blink2 = true;
	public int blinkTick2 = 0;
	public boolean triggerBlink3 = true;
	public boolean blink3 = true;
	public int blinkTick3 = 0;
	public int eyeGroupPick1 = 0;
	public int eyeGroupPick2 = 0;
	public int eyeGroupPick3 = 0;
	

	public WatcherEyes(EntityType<? extends BasicProximitySpawner> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_OPENING, false);
		builder.define(OPEN_COUNT, 0);
	}

	@Override
	public void tick() {
		//super.tick();
		if (!level().isClientSide() && level().getGameTime() % 5 == 0) {
				checkArea();
		}
		if (!level().isClientSide()) {
			if (!getOpening() && getOpenCount() > MIN_OPEN)
				setOpenCount(getOpenCount() - 1);

			if (getOpening() && getOpenCount() < MAX_OPEN)
				setOpenCount(getOpenCount() + 1);
		}
		
		if (level().isClientSide()) {
			if (getOpenCount() >= MAX_OPEN) {
				// one
				if (!triggerBlink1 && level().getGameTime() % 5 + random.nextInt(10) == 0) {
					triggerBlink1 = true;
					eyeGroupPick1 = random.nextInt(3);
				}

				if (!triggerBlink2 && level().getGameTime() % 5 + random.nextInt(10) == 0) {
					triggerBlink2 = true;
					eyeGroupPick2 = random.nextInt(3);
				}

				if (!triggerBlink3 && level().getGameTime() % 5 + random.nextInt(10) == 0) {
					triggerBlink3 = true;
					eyeGroupPick3 = random.nextInt(3);
				}

				if (triggerBlink1) {
					if (blink1) {
						blinkTick1 += 2;
						if (blinkTick1 > MAX_OPEN)
							blink1 = false;
					}
					if (!blink1) {
						blinkTick1--;
						if (blinkTick1 <= MIN_OPEN) {
							blink1 = true;
							triggerBlink1 = false;
						}
					}
				}
				// two
				if (triggerBlink2) {
					if (blink2) {
						blinkTick2 += 2;
						if (blinkTick2 > MAX_OPEN)
							blink2 = false;
					}
					if (!blink2) {
						blinkTick2--;
						if (blinkTick2 <= MIN_OPEN) {
							blink2 = true;
							triggerBlink2 = false;
						}
					}
				}

				// three
				if (triggerBlink3) {
					if (blink3) {
						blinkTick3 += 2;
						if (blinkTick3 > MAX_OPEN)
							blink3 = false;
					}
					if (!blink3) {
						blinkTick3--;
						if (blinkTick3 <= MIN_OPEN) {
							blink3 = true;
							triggerBlink3 = false;
						}
					}
				}
			}
		}

	}

	@Nullable
	public void checkArea() {
		Entity entity = null;
		if (!level().isClientSide() && level().getDifficulty() != Difficulty.PEACEFUL) {
			List<Player> list = level().getEntitiesOfClass(Player.class, proximityBox(this));
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);

				if (entity != null) {
					if (entity instanceof Player && !((Player) entity).isSpectator() && !((Player) entity).isCreative()) {
						if (canSneakPast() && entity.isCrouching())
							return;
						//else if (checkSight() && !hasLineOfSight(entity))
							//return;
						else {
							if(!getOpening())
								setOpening(true);
						}
					}
				}
			}
			if (entity == null) {
				if (getOpening())
					setOpening(false);
			}
		}
	}


    public boolean isWearingSilkMask(LivingEntity entity) {
    	if(entity instanceof Player) {
        	ItemStack helmet = ((Player)entity).getItemBySlot(EquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.is(ItemRegistry.SILK_MASK.get())) {
        		return true;
        	}
        }
    	return false;
    }

	private void setOpening(boolean open) {
		getEntityData().set(IS_OPENING, open);
	}

    public boolean getOpening() {
        return getEntityData().get(IS_OPENING);
    }

	private void setOpenCount(int openCountIn) {
		getEntityData().set(OPEN_COUNT, openCountIn);
	}

	public int getOpenCount() {
		return getEntityData().get(OPEN_COUNT);
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
    public boolean canBeCollidedWith() {
        return false;
    }

	@Override
	public void kill() {
		this.discard();
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
			return true;
		}
		if (source.is(DamageTypes.PLAYER_ATTACK)) {
			super.hurt(source, amount);
			return true;
		}
		return false;
	}

	@Override
	public float getProximityHorizontal() {
		return 6F;
	}

	@Override
	public float getProximityVertical() {
		return 3F;
	}

	@Override
	public boolean canSneakPast() {
		return true;
	}

	@Override
	public boolean checkSight() {
		return true;
	}

	@Override
	public Entity getEntitySpawned() {
		return null;
	}

	@Override
	public int getEntitySpawnCount() {
		return 1;
	}

	@Override
	public boolean isSingleUse() {
		return true;
	}

	@Override
	public int maxUseCount() {
		return 0;
	}

	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
		// TODO Auto-generated method stub
		
	}
}