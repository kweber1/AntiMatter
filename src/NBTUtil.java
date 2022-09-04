import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
public class NBTUtil {


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


}
