package lightningtweaks.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ChunkHandler {
    public static Map<Chunk, TreeMap<Integer, List<Pair<Integer, Integer>>>> chunks = new HashMap<>();

    @SubscribeEvent
    public static void onLoad(Load event) {
	if (!event.getWorld().isRemote) {
	    Chunk chunk = event.getChunk();
	    TreeMap<Integer, List<Pair<Integer, Integer>>> heights = new TreeMap<>();
	    chunks.put(chunk, heights);
	    for (int x = 0; x < 16; x++)
		for (int z = 0; z < 16; z++)
		    heights.computeIfAbsent(chunk.getHeightValue(x, z), ArrayList::new).add(Pair.of(x, z));
	}
    }

    @SubscribeEvent
    public static void onUnload(Unload event) {
	if (!event.getWorld().isRemote)
	    chunks.remove(event.getChunk());
    }
}
