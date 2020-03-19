package lightningtweaks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import lightningtweaks.common.event.EntityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

public class GlobalEntitiesList extends ArrayList<Entity> {
    private static final SplittableRandom random = new SplittableRandom();
    private final IWorld world;

    public GlobalEntitiesList(IWorld world) {
	this.world = world;
    }

    @Override
    public boolean add(Entity e) {
	EntityHandler.revertDifficulty(world);
	BlockPos pos = getMaxBlockPos(e);
	e.setPosition(pos.getX(), pos.getY(), pos.getZ());
	return super.add(e);
    }

    private BlockPos getMaxBlockPos(Entity e) {
	IChunk chunk = world.getChunk(e.getPosition());
	Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING);
	int max = 0;
	List<int[]> coords = new ArrayList<>();
	for (int x = 0; x < 16; x++)
	    for (int z = 0; z < 16; z++) {
		int y = heightmap.getHeight(x, z);
		if (y > max) {
		    max = y;
		    coords.clear();
		}
		if (y == max)
		    coords.add(new int[] { x, z });
	    }
	int[] coord = coords.get(random.nextInt(coords.size()));
	return chunk.getPos().getBlock(coord[0], max, coord[1]);
    }
}
