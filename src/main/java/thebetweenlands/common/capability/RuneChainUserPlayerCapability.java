package thebetweenlands.common.capability;

import java.util.Iterator;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IRuneChainUserCapability;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.RuneChainComposition;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
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
		private final RuneChainComposition runeChain;
		private boolean ticking;
		private boolean removeOnFinish;

		private RuneChainEntry(int id, RuneChainComposition runeChain) {
			this.id = id;
			this.runeChain = runeChain;
		}
	}

	private static int nextRuneChainID = 0;

	private IRuneChainUser user;
	private final Int2ObjectMap<RuneChainEntry> runeChains = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<RuneChainComposition> tickingRuneChains = new Int2ObjectOpenHashMap<>();

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
			public IInventory getInventory() {
				return player.inventory;
			}

			@Override
			public boolean isUsingRuneChain() {
				//TODO Implement this
				return false;
			}
		};
	}

	@Override
	public IRuneChainUser getUser() {
		return this.user;
	}

	@Override
	public int addRuneChain(RuneChainComposition chain) {
		int id = nextRuneChainID++;
		this.runeChains.put(id, new RuneChainEntry(id, chain));
		return id;
	}

	@Override
	public boolean setUpdating(int id, boolean ticking, boolean removeOnFinish) {
		RuneChainEntry entry = this.runeChains.get(id);
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
	public RuneChainComposition removeRuneChain(int id) {
		this.tickingRuneChains.remove(id);
		RuneChainEntry entry = this.runeChains.remove(id);
		return entry != null ? entry.runeChain : null;
	}

	@Override
	public RuneChainComposition getRuneChain(int id) {
		RuneChainEntry entry = this.runeChains.remove(id);
		return entry != null ? entry.runeChain : null;
	}

	@Override
	public void update() {
		Iterator<RuneChainComposition> chainIT = this.tickingRuneChains.values().iterator();
		while(chainIT.hasNext()) {
			RuneChainComposition chain = chainIT.next();
			if(chain.isRunning()) {
				chain.update();
			} else {
				chainIT.remove();
			}
		}
	}
}
