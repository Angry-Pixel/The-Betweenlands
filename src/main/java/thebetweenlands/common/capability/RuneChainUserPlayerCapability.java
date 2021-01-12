package thebetweenlands.common.capability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.chain.IRuneChain;
import thebetweenlands.api.runechain.chain.IRuneChainData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.capability.item.RuneChainItemCapability;
import thebetweenlands.common.herblore.rune.RuneChainComposition;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.clientbound.MessagePlayerRuneChainAdd;
import thebetweenlands.common.network.clientbound.MessagePlayerRuneChainPacket;
import thebetweenlands.common.network.clientbound.MessagePlayerRuneChainRemove;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RuneChainUserPlayerCapability extends EntityCapability<RuneChainUserPlayerCapability, IRuneChainUserCapability, EntityPlayer> implements IRuneChainUserCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "rune_chain_player");
	}

	@Override
	protected Capability<IRuneChainUserCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_RUNE_CHAIN_USER;
	}

	@Override
	protected Class<IRuneChainUserCapability> getCapabilityClass() {
		return IRuneChainUserCapability.class;
	}

	@Override
	protected RuneChainUserPlayerCapability getDefaultCapabilityImplementation() {
		return new RuneChainUserPlayerCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}


	private static class RuneChainEntry {
		private final int id;
		private final IRuneChain runeChain;
		private boolean ticking;
		private boolean removeOnFinish;

		private RuneChainEntry(int id, IRuneChain runeChain) {
			this.id = id;
			this.runeChain = runeChain;
		}
	}

	private static int nextRuneChainID = 0;

	private IRuneChainUser user;
	private final Int2ObjectMap<RuneChainEntry> entryById = new Int2ObjectOpenHashMap<>();
	private final Object2ObjectMap<IRuneChain, RuneChainEntry> entryByObject = new Object2ObjectOpenHashMap<>();
	private final Int2ObjectMap<IRuneChain> tickingRuneChains = new Int2ObjectOpenHashMap<>();

	@Override
	protected void init() {
		final EntityPlayer player = this.getEntity();

		this.user = new IRuneChainUser() {
			@Override
			public World getWorld() {
				return player.world;
			}

			@Override
			public Vec3d getPosition() {
				return player.getPositionVector();
			}

			@Override
			public Vec3d getEyesPosition() {
				return player.getPositionEyes(1);
			}

			@Override
			public Vec3d getLook() {
				return player.getLookVec();
			}

			@Override
			public Entity getEntity() {
				return player;
			}
			
			@Override
			public boolean isActive() {
				return player.isEntityAlive();
			}

			@Override
			public IInventory getInventory() {
				return player.inventory;
			}

			@Override
			public boolean isActivatingRuneChain(IRuneChain runeChain) {
				//TODO Implement this
				return false;
			}

			@Override
			public void sendPacket(IRuneChain runeChain, Consumer<PacketBuffer> serializer, @Nullable TargetPoint target) {
				RuneChainEntry entry = RuneChainUserPlayerCapability.this.entryByObject.get(runeChain);
				if(entry != null) {
					if(target != null) {
						TheBetweenlands.networkWrapper.sendToAllTracking(new MessagePlayerRuneChainPacket(player, entry.id, serializer), target);
					} else {
						MessagePlayerRuneChainPacket message = new MessagePlayerRuneChainPacket(player, entry.id, serializer);
						TheBetweenlands.networkWrapper.sendToAllTracking(message, player);
						if(player instanceof EntityPlayerMP) {
							TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) player);
						}
					}
				}
			}
		};
	}

	@Override
	public IRuneChainUser getUser() {
		return this.user;
	}

	@Override
	public int addRuneChain(IRuneChainData data) {
		IRuneChain chain = RuneChainItemCapability.createBlueprint(data).create();

		int id = nextRuneChainID++;
		RuneChainEntry entry = new RuneChainEntry(id, chain);
		this.entryById.put(id, entry);
		this.entryByObject.put(chain, entry);

		if(!this.getEntity().world.isRemote) {
			//TODO Also in start tracking
			MessagePlayerRuneChainAdd message = new MessagePlayerRuneChainAdd(this.getEntity(), id, data);
			TheBetweenlands.networkWrapper.sendToAllTracking(message, this.getEntity());
			if(this.getEntity() instanceof EntityPlayerMP) {
				TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) this.getEntity());
			}
		}

		return id;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addRuneChain(IRuneChain chain, int id) {
		RuneChainEntry entry = new RuneChainEntry(id, chain);
		this.entryById.put(id, entry);
		this.entryByObject.put(chain, entry);
	}

	@Override
	public boolean setUpdating(int id, boolean ticking, boolean removeOnFinish) {
		RuneChainEntry entry = this.entryById.get(id);

		if(entry != null) {
			if(ticking) {
				this.tickingRuneChains.put(id, entry.runeChain);
				entry.ticking = true;
			} else {
				this.tickingRuneChains.remove(id);
				entry.ticking = false;
			}

			entry.removeOnFinish = entry.removeOnFinish;

			return true;
		}

		return false;
	}

	@Override
	public IRuneChain removeRuneChain(int id) {
		this.tickingRuneChains.remove(id);

		RuneChainEntry entry = this.entryById.remove(id);

		if(entry != null) {
			if(!this.getEntity().world.isRemote) {
				MessagePlayerRuneChainRemove message = new MessagePlayerRuneChainRemove(this.getEntity(), id);
				TheBetweenlands.networkWrapper.sendToAllTracking(message, this.getEntity());
				if(this.getEntity() instanceof EntityPlayerMP) {
					TheBetweenlands.networkWrapper.sendTo(message, (EntityPlayerMP) this.getEntity());
				}
			}

			this.entryByObject.remove(entry.runeChain);
			return entry.runeChain;
		}

		return null;
	}

	@Override
	public IRuneChain getRuneChain(int id) {
		RuneChainEntry entry = this.entryById.get(id);
		return entry != null ? entry.runeChain : null;
	}

	@Override
	public void update() {
		if(!this.getEntity().world.isRemote) {
			List<IRuneChain> finished = null;

			Iterator<IRuneChain> chainIT = this.tickingRuneChains.values().iterator();
			while(chainIT.hasNext()) {
				IRuneChain chain = chainIT.next();
				if(chain.isRunning()) {
					chain.update();
					chain.updateRuneEffectModifiers();
				} else {
					if(finished == null) {
						finished = new ArrayList<>();
					}
					finished.add(chain);
				}
			}

			if(finished != null) {
				for(IRuneChain chain : finished) {
					this.removeRuneChain(this.entryByObject.get(chain).id);
				}
			}
		} else {
			//Update rune effect modifiers
			for(IRuneChain chain : this.entryByObject.keySet()) {
				chain.updateRuneEffectModifiers();
			}
		}
	}
}
