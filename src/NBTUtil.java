import net.minecraft.block.BlockState;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class NBTUtil {

    public static boolean getSubData(NbtCompound regionTag){
        NbtElement nbtBase = regionTag.get("BlockStates");
        Vec3i regionPos = readBlockPos(regionTag.getCompound("Position"));
        Vec3i regionSize = readBlockPos(regionTag.getCompound("Size"));
        // There are no convenience methods in NBTTagCompound yet in 1.12, so we'll have to do it the ugly way...
        if (nbtBase != null && nbtBase.getType() == Constants.NBT.TAG_LONG_ARRAY)
        {
            NbtList palette = regionTag.getList("BlockStatePalette", Constants.NBT.TAG_COMPOUND);
            long[] blockStateArr = ((NbtLongArray) nbtBase).getLongArray();

            Vec3i posEndRel = getRelativeEndPositionFromAreaSize(regionSize).add(regionPos);
            Vec3i posMin = getMinCorner(regionPos, posEndRel);
            Vec3i posMax = getMaxCorner(regionPos, posEndRel);
            Vec3i size = posMax.subtract(posMin).add(1, 1, 1);
            System.out.println(palette);
            int bits = Math.max(2, Integer.SIZE - Integer.numberOfLeadingZeros(palette.size() - 1));
            LitematicaBlockStateContainer container = LitematicaBlockStateContainer.createFrom(palette, blockStateArr, size);
            System.out.println( container.get(0,0,0));

            return true;
        }
        return true;
    }

    public static Vec3i getRelativeEndPositionFromAreaSize(Vec3i size)
    {
        int x = size.getX();
        int y = size.getY();
        int z = size.getZ();

        x = x >= 0 ? x - 1 : x + 1;
        y = y >= 0 ? y - 1 : y + 1;
        z = z >= 0 ? z - 1 : z + 1;

        return new Vec3i(x, y, z);
    }
    public static Vec3i getMinCorner(Vec3i pos1, Vec3i pos2)
    {
        return new Vec3i(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }

    public static Vec3i getMaxCorner(Vec3i pos1, Vec3i pos2)
    {
        return new Vec3i(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

    @Nullable
    public static Vec3i readBlockPos(@Nullable NbtCompound tag)
    {
        if (tag != null &&
                tag.contains("x", Constants.NBT.TAG_INT) &&
                tag.contains("y", Constants.NBT.TAG_INT) &&
                tag.contains("z", Constants.NBT.TAG_INT))
        {
            return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromArrayTag(NbtCompound tag, String tagName)
    {
        if (tag.contains(tagName, Constants.NBT.TAG_INT_ARRAY))
        {
            int[] pos = tag.getIntArray("Pos");

            if (pos.length == 3)
            {
                return new BlockPos(pos[0], pos[1], pos[2]);
            }
        }

        return null;
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(@Nullable NbtCompound tag)
    {
        return readVec3dFromListTag(tag, "Pos");
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(@Nullable NbtCompound tag, String tagName)
    {
        if (tag != null && tag.contains(tagName, Constants.NBT.TAG_LIST))
        {
            NbtList tagList = tag.getList(tagName, Constants.NBT.TAG_DOUBLE);

            if (tagList.getHeldType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vec3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }
    public static NbtCompound readNbtFromFile(File file)
    {
        if (file.exists() == false || file.canRead() == false)
        {
            return null;
        }

        FileInputStream is;

        try
        {
            is = new FileInputStream(file);
        }
        catch (Exception e)
        {
            return null;
        }

        NbtCompound nbt = null;

        if (is != null)
        {
            try
            {
                nbt = NbtIo.readCompressed(is);
            }
            catch (Exception e)
            {
                try
                {
                    is.close();
                    is = new FileInputStream(file);
                    nbt = NbtIo.read(new DataInputStream(is));
                }
                catch (Exception ignore) {}
            }

            try
            {
                is.close();
            }
            catch (Exception ignore) {}
        }

        return nbt;
    }

    public class Constants
    {
        public static class NBT
        {
            public static final int TAG_END         = 0;
            public static final int TAG_BYTE        = 1;
            public static final int TAG_SHORT       = 2;
            public static final int TAG_INT         = 3;
            public static final int TAG_LONG        = 4;
            public static final int TAG_FLOAT       = 5;
            public static final int TAG_DOUBLE      = 6;
            public static final int TAG_BYTE_ARRAY  = 7;
            public static final int TAG_STRING      = 8;
            public static final int TAG_LIST        = 9;
            public static final int TAG_COMPOUND    = 10;
            public static final int TAG_INT_ARRAY   = 11;
            public static final int TAG_LONG_ARRAY  = 12;
            public static final int TAG_ANY_NUMERIC = 99;
        }
    }
}
