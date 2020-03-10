package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WeatherEffectsList extends ArrayList<Entity> {
    private static final SplittableRandom random = new SplittableRandom();
    private final World world;

    public WeatherEffectsList(World world) {
	this.world = world;
    }

    @Override
    public boolean add(Entity e) {
	double score = 0;
	List<int[]> coords = new ArrayList<>(256);
	BlockPos pos = e.getPosition();
	Chunk chunk = world.getChunk(pos);
	for (int x = 0; x < 16; x++)
	    for (int z = 0; z < 16; z++) {
		int y = chunk.getHeightValue(x, z);
		if (y > score) {
		    score = y;
		    coords.clear();
		}
		if (y >= score)
		    coords.add(new int[] { x, y, z });
	    }
	int[] coord = coords.get(random.nextInt(coords.size()));
	e.setPositionAndUpdate(coord[0] + 16 * chunk.x, coord[1], coord[2] + 16 * chunk.z);
	return super.add(e);
    }
}
