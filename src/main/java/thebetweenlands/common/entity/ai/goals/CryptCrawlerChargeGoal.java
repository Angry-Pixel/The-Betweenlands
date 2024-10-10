package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import thebetweenlands.common.entity.monster.BipedCryptCrawler;

import java.util.EnumSet;

public class CryptCrawlerChargeGoal extends Goal {

	private final BipedCryptCrawler crawler;
	private int chargeCooldown;
	private int chargeCooldownMax;

	private int chargeTimer = 0;

	public CryptCrawlerChargeGoal(BipedCryptCrawler crawler) {
		this.crawler = crawler;
		this.setFlags(EnumSet.allOf(Flag.class));
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

		if (this.crawler.getTarget() != null) {
			if (this.chargeCooldownMax < 0) {
				this.chargeCooldownMax = 80 + this.crawler.getRandom().nextInt(90);
			}

			this.chargeCooldown++;

			return this.chargeCooldown > this.chargeCooldownMax;
		}

		return false;
	}

	@Override
	public void start() {
		this.chargeCooldown = 0;
		this.chargeCooldownMax = -1;
	}

	@Override
	public boolean canContinueToUse() {
		return this.isHoldingShield() && this.chargeTimer < 60;
	}

	@Override
	public void stop() {
		this.crawler.setIsBlocking(false);
		this.chargeTimer = 0;
	}

	@Override
	public void tick() {
		this.chargeTimer++;

		if (!this.crawler.isBlocking()) {
			this.crawler.setIsBlocking(true);
		}

		this.crawler.setShiftKeyDown(this.chargeTimer < 20);

		LivingEntity target = this.crawler.getTarget();
		if (target != null) {
			this.crawler.lookAt(target, 15, 15);
		}
	}
}
