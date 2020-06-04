package lightningtweaks.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import lightningtweaks.LightningTweaks;
import net.minecraft.item.Item;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * TODO
 */
@EventBusSubscriber
public class LTConfig {
	public static class Common {
		public IntValue extraIgnitions;
		public Set<Item> metallicItems;
		public ConfigValue<List<String>> metallicKeywords;
		public BooleanValue realisticLightning, spawnFire, verbose;

		/**
		 * TODO
		 *
		 * @param builder TODO
		 */
		public Common(Builder builder) {
			extraIgnitions = builder
					.comment("How many extra fires should be spawned around where the lightning strikes.",
							"The vanilla value is 4.")
					.defineInRange("Extra Ignitions", 4, 0, Integer.MAX_VALUE);
			metallicItems = new HashSet<>();
			metallicKeywords = builder.comment(
					"Keywords used to determine what items and blocks are considered metallic. An item or block is considered metallic if any of the keywords are contained within its localized name.",
					"Case does not matter.").define("Metallic Keywords", ImmutableList.of("gold", "iron"));
			realisticLightning = builder.comment(
					"Should lightning strike high or metal blocks more often? This is the main behavior of this mod.")
					.define("Realistic Lightning", true);
			spawnFire = builder
					.comment("Should lightning spawn fire where it strikes?",
							"Only changes the behavior of lightning that would have otherwise spawned fire.",
							"This also prevents entities from getting set on fire or damaged from lightning.")
					.define("Spawn Fire", true);
			verbose = builder.comment("Should log messages for this mod be posted?").define("Verbose", true);
		}
	}

	public static Pair<Common, ForgeConfigSpec> pair = new Builder().configure(Common::new);

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean doRealisticLightning() {
		return pair.getLeft().realisticLightning.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean doSpawnFire() {
		return pair.getLeft().spawnFire.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static int getExtraIgnitions() {
		return pair.getLeft().extraIgnitions.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static Set<Item> getMetallicItems() {
		return pair.getLeft().metallicItems;
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean isVerbose() {
		return pair.getLeft().verbose.get();
	}

	/**
	 * TODO
	 */
	public static void register() {
		LightningTweaks.log("Registering configuration file.");
		ModLoadingContext.get().registerConfig(Type.COMMON, pair.getRight());
		Set<Item> metallicItems = pair.getLeft().metallicItems;
		metallicItems.clear();
		pair.getLeft().metallicKeywords.get().forEach(keyword -> {
			keyword = keyword.toLowerCase();
			LanguageMap languageMap = LanguageMap.func_74808_a();
			for (Item item : ForgeRegistries.ITEMS)
				if (languageMap.translateKey(item.getTranslationKey()).toLowerCase().contains(keyword))
					metallicItems.add(item);
		});
		LightningTweaks.log("Stored the following items as metallic: " + metallicItems + '.');
	}
}