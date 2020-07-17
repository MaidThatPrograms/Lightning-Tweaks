package lightningtweaks.common;

import java.util.HashMap;
import java.util.List;

import lightningtweaks.LightningTweaks;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * TODO
 */
public class ResistivityMap extends HashMap<Item, ResistivityMap.Resistivity> {
	public enum Resistivity {
		CONDUCTOR, GROUND, INSULATOR;
	}

	private static final ResistivityMap instance = new ResistivityMap();

	/**
	 * TODO
	 */
	public static void commonSetup() {
		LightningTweaks.log("Setting up the ResistivityMap.");
		ForgeRegistries.ITEMS.forEach(item -> instance.put(item, getType(item.toString())));
		LightningTweaks.log("ResistivityMap: " + instance);
	}

	/**
	 * TODO
	 *
	 * @param string TODO
	 * @param list   TODO
	 * @return TODO
	 */
	private static boolean containsAny(String string, List<String> list) {
		for (String element : list)
			if (string.contains(element.toLowerCase()))
				return true;
		return false;
	}

	/**
	 * TODO
	 *
	 * @param item TODO
	 * @return TODO
	 */
	public static Resistivity getType(Item item) {
		return instance.get(item);
	}

	/**
	 * TODO
	 *
	 * @param item TODO
	 * @return TODO
	 */
	private static Resistivity getType(String item) {
		if (containsAny(item, LTConfig.getConductors()))
			return Resistivity.CONDUCTOR;
		if (containsAny(item, LTConfig.getInsulators()))
			return Resistivity.INSULATOR;
		return Resistivity.GROUND;
	}
}
