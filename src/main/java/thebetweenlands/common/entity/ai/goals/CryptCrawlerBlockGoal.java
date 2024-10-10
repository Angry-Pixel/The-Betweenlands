package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import thebetweenlands.common.entity.monster.BipedCryptCrawler;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CryptCrawlerBlockGoal extends Goal {

	private final BipedCryptCrawler crawler;
	@Nullable
	private LivingEntity target;
	private int blockingCount;
	private int blockingCountMax;
	private int meleeBlockingCounter;
	private int meleeBlockingCounterMax;

	private int blockingCooldownCounter;
	private int blockingCooldownCounterMax = -1;

	public CryptCrawlerBlockGoal(BipedCryptCrawler crawler) {
		this.crawler = crawler;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
	}

	protected boolean isInMeleeRange() {
		return this.target != null && this.crawler.distanceToSqr(this.target) < 9.0D;
	}

	protected boolean isHoldingShield() {
		ItemStack heldItem = this.crawler.getOffhandItem();
		if (heldItem.isEmpty()) {
			return false;
		}
		return heldItem.canPerformAction(ItemAbilities.SHIELD_BLOCK);

	}

	@Override
	public boolean canUse() {
		if (!this.isHoldingShield()) {
			return false;
		}

		this.target = this.crawler.getTarget();

		if (this.target != null && !this.crawler.swinging && this.crawler.invulnerableTime <= Math.max(0, this.crawler.invulnerableDuration - 5)) {
			this.blockingCooldownCounter++;

			if (this.blockingCooldownCounterMax < 0) {
				this.blockingCooldownCounterMax = 4 + this.crawler.getRandom().nextInt(5);
			}

			return this.blockingCooldownCounter > this.blockingCooldownCounterMax;
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return this.isHoldingShield() && !this.crawler.recentlyBlockedAttack && this.blockingCount != -1 && this.crawler.invulnerableTime <= Math.max(0, this.crawler.invulnerableDuration - 5) && !this.crawler.swinging &&
			!(this.blockingCount > this.blockingCountMax || (this.meleeBlockingCounterMax >= 0 && this.meleeBlockingCounter > this.meleeBlockingCounterMax));
	}

	@Override
	public void start() {
		this.blockingCount = 0;
		this.blockingCountMax = 40 + this.crawler.getRandom().nextInt(40);
		this.meleeBlockingCounterMax = -1;
		this.meleeBlockingCounter = 0;

		//Reset the recently blocked state
		this.crawler.recentlyBlockedAttack = false;

		this.blockingCooldownCounter = 0;
		this.blockingCooldownCounterMax = -1;
	}

	@Override
	public void stop() {
		this.crawler.setIsBlocking(false);
		this.blockingCount = -1;
		this.meleeBlockingCounterMax = -1;
		this.meleeBlockingCounter = 0;
	}

	@Override
	public void tick() {
		if (!this.crawler.isBlocking()) {
			this.crawler.setIsBlocking(true);
		}

		this.blockingCount++;

		if (this.isInMeleeRange()) {
			if (this.meleeBlockingCounterMax < 0) {
				this.meleeBlockingCounterMax = 10 + this.crawler.getRandom().nextInt(20);
			}
			this.meleeBlockingCounter++;
		} else {
			this.meleeBlockingCounterMax = -1;
			this.meleeBlockingCounter = 0;
		}
	}
}
