package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SplittableRandom;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class WorldHandler {
    public static SplittableRandom random = new SplittableRandom();

    @SubscribeEvent
    public static void onWorldLoad(Load event) {
	World world = event.getWorld();
	if (!world.isRemote)
	    ObfuscationReflectionHelper.setPrivateValue(World.class, world, new ArrayList<Entity>() {
		@Override
		public boolean add(Entity e) {
		    Chunk chunk = world.getChunk(e.getPosition());
		    Entry<Integer, List<Pair<Integer, Integer>>> entry = ChunkHandler.chunks.get(chunk).lastEntry();
		    List<Pair<Integer, Integer>> value = entry.getValue();
		    Pair<Integer, Integer> pair = value.get(random.nextInt(value.size()));
		    return super.add(new EntityLightningBolt(world, pair.getLeft() + chunk.x, entry.getKey(),
			    pair.getRight() + chunk.z, true));
		}
	    }, "weatherEffects"); // TODO mapping
    }
}