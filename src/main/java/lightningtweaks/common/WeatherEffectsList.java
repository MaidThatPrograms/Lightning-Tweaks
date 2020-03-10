package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.common.event.EntityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class WeatherEffectsList extends ArrayList<Entity> {
    private static final SplittableRandom random = new SplittableRandom();

    public static List<int[]> getCoords(Chunk chunk) {
	double score = 0;
	List<int[]> coords = new ArrayList<>(256);
	for (int x = 0; x < 16; x++)
	    for (int z = 0; z < 16; z++) {
		int y = chunk.getHeightValue(x, z);
		if (y >= score) {
		    if (y > score) {
			score = y;
			coords.clear();
		    }
		    coords.add(new int[] { x, y, z });
		}
	    }
	return coords;
    }

    private final World world;

    public WeatherEffectsList(World world) {
	this.world = world;
    }

    @Override
    public boolean add(Entity e) {
	Chunk chunk = world.getChunk(e.getPosition());
	List<int[]> coords = getCoords(chunk);
	int[] coord = coords.get(random.nextInt(coords.size()));
	e.setPositionAndUpdate(coord[0] + 16 * chunk.x, coord[1], coord[2] + 16 * chunk.z);

	setFireOptions(e);

	return super.add(e);
    }

    public void setFireOptions(Entity e) {
	if (EntityHandler.pollFireRule(world)) {
	    EntityHandler.setFireRule(world.getGameRules(), true);
	    if (!LTConfig.fire && e instanceof EntityLightningBolt)
		ObfuscationReflectionHelper.setPrivateValue(EntityLightningBolt.class, (EntityLightningBolt) e, true,
			"field_184529_d"); // effectOnly
	}
    }
}