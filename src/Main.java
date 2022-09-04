import net.minecraft.nbt.NbtCompound;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        List<String> items = new ArrayList<String>();
        File testfile = new File("C:\\Users\\kman0\\IdeaProjects\\CannonDiff\\src\\Flower_District_v2.litematic");
        NbtCompound dataNBT = NBTUtil.readNbtFromFile(testfile);
        Pattern pattern = Pattern.compile("id:\"minecraft:[a-z,A-Z]*\",x:[0-9]*,y:[0-9]*,z:[0-9]*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dataNBT.get("Regions").toString());
        while (matcher.find()) {
            items.add(matcher.group());
        }
        System.out.println(items);

    }
}