package thebetweenlands.common.capability.infection;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.entity.infection.DeathInfectionBehavior;
import thebetweenlands.common.entity.infection.PlantingInfectionBehavior;
import thebetweenlands.common.entity.infection.RandomLookingBehavior;
import thebetweenlands.common.entity.infection.SporeInfectionBehavior;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.clientbound.MessageInfectionIncrease;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class InfectionEntityCapability extends EntityCapability<InfectionEntityCapability, IInfectionCapability, EntityLivingBase> implements IInfectionCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "infection");
	}

	@Override
	protected Capability<IInfectionCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_INFECTION;
	}

	@Override
	protected Class<IInfectionCapability> getCapabilityClass() {
		return IInfectionCapability.class;
	}

	@Override
	protected InfectionEntityCapability getDefaultCapabilityImplementation() {
		return new InfectionEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		// TODO Swamp hags?
		return entity instanceof EntityPlayer;
	}

	private static final Random RNG = new Random();

	private float percentage;
	private int seed;

	private float infectionIncrease;
	private float increaseThreshold = 0.025f;
	private int infectionIncreaseTimer;

	private IInfectionBehavior currentInfectionBehavior;

	public InfectionEntityCapability() {
		this.seed = RNG.nextInt();
	}

	@Override
	public float getInfectionPercent() {
		return this.percentage;
	}

	@Override
	public void setInfectionPercent(float percent) {
		percent = MathHelper.clamp(percent, 0.0f, 1.0f);

		if(this.percentage != percent) {
			float diff = percent - this.percentage;

			this.percentage += diff;

			this.infectionIncrease = MathHelper.clamp(this.infectionIncrease + diff, 0.0f, 1.0f);

			this.markDirty();
		}
	}

	@Override
	public float getInfectionIncrease() {
		return this.infectionIncrease;
	}

	@Override
	public void resetInfectionIncrease() {
		this.infectionIncrease = 0.0f;
	}

	@Override
	public int getInfectionIncreaseTimer() {
		return this.infectionIncreaseTimer;
	}

	@Override
	public void incrementInfectionIncreaseTimer() {
		this.infectionIncreaseTimer = Math.min(this.infectionIncreaseTimer + 1, Integer.MAX_VALUE);
	}

	@Override
	public void resetInfectionIncreaseTimer() {
		this.infectionIncreaseTimer = 0;
	}

	@Override
	public boolean isInfectable() {
		Entity entity = this.getEntity();
		if(entity instanceof EntityPlayer) {
			ItemStack stack = ((EntityPlayer) entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			return stack.isEmpty() || stack.getItem() != ItemRegistry.SILK_MASK;
		}
		// TODO Probably swamp hags too
		return false;
	}

	@Override
	public float getInfectionHealingRate() {
		// TODO Testing
		return !this.isInfectable() ? 0.01f : 0;
	}

	@Override
	public int getInfectionRenderSeed() {
		return this.seed;
	}

	@Override
	public boolean triggerInfectionBehavior(IInfectionBehavior behavior) {
		if(this.currentInfectionBehavior != null) {
			return false;
		}

		this.currentInfectionBehavior = behavior;
		this.currentInfectionBehavior.start();

		return true;
	}

	@Override
	public boolean stopInfectionBehavior() {
		if(this.currentInfectionBehavior != null) {
			if(this.currentInfectionBehavior.stop()) {
				this.removeInfectionBehavior();
				return true;
			}
		}
		return false;
	}

	@Override
	public void removeInfectionBehavior() {
		if(this.currentInfectionBehavior != null) {
			this.currentInfectionBehavior.onRemove();
		}
		this.currentInfectionBehavior = null;		
	}

	@Override
	public IInfectionBehavior getCurrentInfectionBehavior() {
		return this.currentInfectionBehavior;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		this.writeTrackingDataToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.readTrackingDataFromNBT(nbt);
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		nbt.setFloat("percentage", this.percentage);
		nbt.setInteger("seed", this.seed);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.percentage = nbt.getFloat("percentage");
		this.seed = nbt.getInteger("seed");
	}

	@Override
	public int getTrackingTime() {
		return 10;
	}


	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			IInfectionCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);

			if(cap != null) {
				IInfectionBehavior behavior = cap.getCurrentInfectionBehavior();

				if(!event.player.world.isRemote) {
					if(behavior != null) {
						behavior.update();

						if(behavior.isDone()) {
							cap.stopInfectionBehavior();
						}
					}

					float healingRate = cap.getInfectionHealingRate();
					float infection = cap.getInfectionPercent();

					if(healingRate > 0 && infection > 0) {
						cap.setInfectionPercent(Math.max(0, infection - healingRate));
					}

					// TODO Debug
					//if(event.player.ticksExisted % 20 == 0)
					//	System.out.println("P: " + cap.getInfectionPercent());

					cap.incrementInfectionIncreaseTimer();

					float increase = cap.getInfectionIncrease();

					if(event.player instanceof EntityPlayerMP && increase > 0.015f && cap.getInfectionIncreaseTimer() > 30 + 70 * MathHelper.clamp(0.2f - increase, 0.0f, 1.0f)) {
						TheBetweenlands.networkWrapper.sendTo(new MessageInfectionIncrease(increase), (EntityPlayerMP) event.player);

						cap.resetInfectionIncrease();
						cap.resetInfectionIncreaseTimer();
					}


					// TODO Testing
					if(event.player.ticksExisted % 20 == 0 && cap.getCurrentInfectionBehavior() == null && cap.getInfectionPercent() > 0.5f && !event.player.isCreative()) {
						switch(event.player.world.rand.nextInt(4)) {
						case 0:
							cap.triggerInfectionBehavior(new PlantingInfectionBehavior(event.player));
							break;
						case 1:
							cap.triggerInfectionBehavior(new SporeInfectionBehavior(event.player));
							break;
						case 2:
							cap.triggerInfectionBehavior(new RandomLookingBehavior(event.player));
							break;
						case 3:
							cap.triggerInfectionBehavior(new DeathInfectionBehavior(event.player));
							break;
						}
					}
				} else if(behavior != null) {
					behavior.update();
				}
			}
		}
	}
}
