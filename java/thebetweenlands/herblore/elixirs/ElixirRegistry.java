package thebetweenlands.herblore.elixirs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.herblore.elixirs.effects.ElixirDecay;
import thebetweenlands.herblore.elixirs.effects.ElixirDraining;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;
import thebetweenlands.herblore.elixirs.effects.ElixirFeasting;
import thebetweenlands.herblore.elixirs.effects.ElixirHealing;
import thebetweenlands.herblore.elixirs.effects.ElixirMasking;
import thebetweenlands.herblore.elixirs.effects.ElixirPetrify;
import thebetweenlands.herblore.elixirs.effects.ElixirRipening;
import thebetweenlands.herblore.elixirs.effects.ElixirStarvation;
import thebetweenlands.herblore.elixirs.effects.ElixirSwiftarm;

public class ElixirRegistry {
	private static final List<ElixirEffect> EFFECTS = new ArrayList<ElixirEffect>();

	//Test
	public static final ElixirEffect EFFECT_TEST = new ElixirEffect(69, "bl.elixir.test", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));

	//Elixirs
	public static final ElixirEffect EFFECT_STRENGTH = new ElixirEffect(0, "bl.elixir.strength", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png")).addAttributeModifier(SharedMonsterAttributes.attackDamage, "241751b1-7e4c-409a-bc66-70ce1330b6f6", 3.0D, 2);
	public static final ElixirEffect EFFECT_NIMBLEFEET = new ElixirEffect(1, "bl.elixir.nimblefeet", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png")).addAttributeModifier(SharedMonsterAttributes.movementSpeed, "645a98a2-7bfd-11e5-8bcf-feff819cdc9f", 0.2D, 2);
	public static final ElixirEffect EFFECT_HEALING = new ElixirHealing(2, "bl.elixir.healing", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_RIPENING = new ElixirRipening(3, "bl.elixir.ripening", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_TOUGHSKIN = new ElixirEffect(4, "bl.elixir.toughskin", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_FEASTING = new ElixirFeasting(5, "bl.elixir.feasting", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_HUNTERSSENSE = new ElixirEffect(6, "bl.elixir.huntersSense", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirMasking EFFECT_MASKING = new ElixirMasking(7, "bl.elixir.masking", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_SWIFTARM = new ElixirSwiftarm(8, "bl.elixir.swiftarm", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_CATSEYES = new ElixirEffect(9, "bl.elixir.catsEyes", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_SAGITTARIUS = new ElixirEffect(10, "bl.elixir.sagittarius", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_SPIDERBREED = new ElixirEffect(11, "bl.elixir.spiderbreed", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_LIGHTWEIGHT = new ElixirEffect(12, "bl.elixir.lightweight", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_UNCLOUDED = new ElixirEffect(13, "bl.elixir.unclouded", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_GILLSGROWTH = new ElixirEffect(14, "bl.elixir.gillsgrowth", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_WINGS = new ElixirEffect(15, "bl.elixir.wings", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_HUNTERSSENSEMASTER = new ElixirEffect(16, "bl.elixir.huntersSenseMaster", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));

	//Anti Elixirs
	public static final ElixirEffect EFFECT_WEAKNESS = new ElixirEffect(17, "bl.elixir.weakness", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png")).addAttributeModifier(SharedMonsterAttributes.attackDamage, "886d753c-fcf2-4370-99b3-eb8e4fbaabcf", -3.0D, 2);
	public static final ElixirEffect EFFECT_LUMBERING = new ElixirEffect(18, "bl.elixir.lumbering", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png")).addAttributeModifier(SharedMonsterAttributes.movementSpeed, "2c06470c-b549-47e9-893c-0d9950c77716", -0.2D, 2);
	public static final ElixirEffect EFFECT_DRAINING = new ElixirDraining(19, "bl.elixir.draining", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_DECAY = new ElixirDecay(20, "bl.elixir.decay", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_POISONSTING = new ElixirEffect(21, "bl.elixir.poisonsting", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_STARVATION = new ElixirStarvation(22, "bl.elixir.starvation", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_DRUNKARD = new ElixirEffect(23, "bl.elixir.drunkard", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_STENCHING = new ElixirEffect(24, "bl.elixir.stenching", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_SLUGARM = new ElixirEffect(25, "bl.elixir.slugarm", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_BLINDMAN = new ElixirEffect(26, "bl.elixir.blindman", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_WEAKBOW = new ElixirEffect(27, "bl.elixir.weakbow", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_BASILISK = new ElixirPetrify(28, "bl.elixir.basilisk", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_HEAVYWEIGHT = new ElixirEffect(29, "bl.elixir.heavyweight", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_FOGGEDMIND = new ElixirEffect(30, "bl.elixir.foggedMind", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_DEFORMED = new ElixirEffect(31, "bl.elixir.deformed", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_LIMBLESS = new ElixirEffect(32, "bl.elixir.limbless", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));
	public static final ElixirEffect EFFECT_ISOLATEDSENSES = new ElixirEffect(33, "bl.elixir.isolatedSenses", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));

	//Other Elixirs
	public static final ElixirEffect EFFECT_PETRIFY = new ElixirPetrify(34, "bl.elixir.petrify", new ResourceLocation("thebetweenlands:textures/items/strictlyHerblore/misc/vialGreen.png"));

	static {
		registerElixirs();
	}

	private static void registerElixirs() {
		try {
			for (Field f : ElixirRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof ElixirEffect) register((ElixirEffect) obj);
			}
			Collections.sort(EFFECTS, new Comparator<ElixirEffect>() {
				@Override
				public int compare(ElixirEffect e1, ElixirEffect e2) {
					return e2.getID() - e1.getID();
				}
			});
			for(ElixirEffect e : EFFECTS) {
				e.registerPotion();
			}
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void register(ElixirEffect effect) {
		EFFECTS.add(effect);
	}

	public static List<ElixirEffect> getEffects() {
		return EFFECTS;
	}
}
