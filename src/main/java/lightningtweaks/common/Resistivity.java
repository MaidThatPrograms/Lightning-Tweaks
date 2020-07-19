package lightningtweaks.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Resistivity {
	public static final Map<Block, Double> BLOCKS = new HashMap<>();

	private static double getResistivity(Block block) {
		String name = block.getRegistryName().getPath();
		return 1;
	}

	public static void setup() {
		ForgeRegistries.BLOCKS.forEach(block -> BLOCKS.put(block, getResistivity(block)));
	}
}