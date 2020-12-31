package thebetweenlands.common.herblore.elixir;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.herblore.elixir.effects.ElixirDecay;
import thebetweenlands.common.herblore.elixir.effects.ElixirDraining;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.herblore.elixir.effects.ElixirFeasting;
import thebetweenlands.common.herblore.elixir.effects.ElixirHealing;
import thebetweenlands.common.herblore.elixir.effects.ElixirMasking;
import thebetweenlands.common.herblore.elixir.effects.ElixirPetrify;
import thebetweenlands.common.herblore.elixir.effects.ElixirRipening;
import thebetweenlands.common.herblore.elixir.effects.ElixirStarvation;
import thebetweenlands.common.herblore.elixir.effects.ElixirSwiftarm;

public class ElixirEffectRegistry {
	//Elixirs
	public static final ElixirEffect EFFECT_STRENGTH = new ElixirEffect(0, "bl.elixir.strength", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "241751b1-7e4c-409a-bc66-70ce1330b6f6", 3.5D, 0).setShowInBook();
	public static final ElixirEffect EFFECT_NIMBLEFEET = new ElixirEffect(1, "bl.elixir.nimblefeet", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).addAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "645a98a2-7bfd-11e5-8bcf-feff819cdc9f", 0.2D, 2).setShowInBook();
	public static final ElixirEffect EFFECT_HEALING = new ElixirHealing(2, "bl.elixir.healing", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_RIPENING = new ElixirRipening(3, "bl.elixir.ripening", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_TOUGHSKIN = new ElixirEffect(4, "bl.elixir.toughskin", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_FEASTING = new ElixirFeasting(5, "bl.elixir.feasting", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_HUNTERSSENSE = new ElixirEffect(6, "bl.elixir.huntersSense", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirMasking EFFECT_MASKING = (ElixirMasking) new ElixirMasking(7, "bl.elixir.masking", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_SWIFTARM = new ElixirSwiftarm(8, "bl.elixir.swiftarm", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_CATSEYES = new ElixirEffect(9, "bl.elixir.catsEyes", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_SAGITTARIUS = new ElixirEffect(10, "bl.elixir.sagittarius", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_SPIDERBREED = new ElixirEffect(11, "bl.elixir.spiderbreed", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_LIGHTWEIGHT = new ElixirEffect(12, "bl.elixir.lightweight", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_UNCLOUDED = new ElixirEffect(13, "bl.elixir.unclouded", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setShowInBook();
	public static final ElixirEffect EFFECT_GILLSGROWTH = new ElixirEffect(14, "bl.elixir.gillsgrowth", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png"));
	public static final ElixirEffect EFFECT_WINGS = new ElixirEffect(15, "bl.elixir.wings", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png"));
	public static final ElixirEffect EFFECT_HUNTERSSENSEMASTER = new ElixirEffect(16, "bl.elixir.huntersSenseMaster", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png"));
	//Anti Elixirs
	public static final ElixirEffect EFFECT_WEAKNESS = new ElixirEffect(17, "bl.elixir.weakness", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "886d753c-fcf2-4370-99b3-eb8e4fbaabcf", -2.0D, 0).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_LUMBERING = new ElixirEffect(18, "bl.elixir.lumbering", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).addAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "2c06470c-b549-47e9-893c-0d9950c77716", -0.2D, 2).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_DRAINING = new ElixirDraining(19, "bl.elixir.draining", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_DECAY = new ElixirDecay(20, "bl.elixir.decay", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_POISONSTING = new ElixirEffect(21, "bl.elixir.poisonsting", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_STARVATION = new ElixirStarvation(22, "bl.elixir.starvation", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_DRUNKARD = new ElixirEffect(23, "bl.elixir.drunkard", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_STENCHING = new ElixirEffect(24, "bl.elixir.stenching", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_SLUGARM = new ElixirEffect(25, "bl.elixir.slugarm", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_BLINDMAN = new ElixirEffect(26, "bl.elixir.blindman", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_WEAKBOW = new ElixirEffect(27, "bl.elixir.weakbow", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_BASILISK = new ElixirPetrify(28, "bl.elixir.basilisk", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_HEAVYWEIGHT = new ElixirEffect(29, "bl.elixir.heavyweight", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_FOGGEDMIND = new ElixirEffect(30, "bl.elixir.foggedMind", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion().setShowInBook();
	public static final ElixirEffect EFFECT_DEFORMED = new ElixirEffect(31, "bl.elixir.deformed", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion();
	public static final ElixirEffect EFFECT_LIMBLESS = new ElixirEffect(32, "bl.elixir.limbless", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion();
	public static final ElixirEffect EFFECT_ISOLATEDSENSES = new ElixirEffect(33, "bl.elixir.isolatedSenses", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png")).setAntiInfusion();
	//Other Elixirs
	public static final ElixirEffect EFFECT_PETRIFY = new ElixirPetrify(34, "bl.elixir.petrify", new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/vial_green.png"));
	public static final ElixirEffect EFFECT_BLESSED = new ElixirEffect(35, "bl.effect.blessed", new ResourceLocation("thebetweenlands:textures/gui/effect_blessed.png"));
	private static final List<ElixirEffect> EFFECTS = new ArrayList<ElixirEffect>();

	//Potions
	public static final Potion ROOT_BOUND = new PotionRootBound();
	public static final Potion ENLIGHTENED = new PotionEnlightened();
	
	static {
		//Add elixirs to list
		try {
			for (Field f : ElixirEffectRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof ElixirEffect) register((ElixirEffect) obj, f.getName());
			}
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public static void registerElixirs(final RegistryEvent.Register<Potion> event) {
		EFFECTS.sort((e1, e2) -> e2.getID() - e1.getID());
		for(ElixirEffect e : EFFECTS) {
			event.getRegistry().register(e.getPotionEffect());
		}
		
		event.getRegistry().register(ROOT_BOUND);
		event.getRegistry().register(ENLIGHTENED);
	}

	private static void register(ElixirEffect effect, String fieldName) {
		EFFECTS.add(effect);
		String name = fieldName.toLowerCase(Locale.ENGLISH);
		effect.registerPotion(name);
	}

	public static List<ElixirEffect> getEffects() {
		return EFFECTS;
	}

	public static ElixirEffect getByID(int id) {
		for(ElixirEffect effect : EFFECTS) {
			if(effect.getID() == id)
				return effect;
		}
		return null;
	}
}
