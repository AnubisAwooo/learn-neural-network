import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
class Load {
    static List<NumberImage> getData(String name) {
        List<String> list = read(name);
        return list.stream().map(NumberImage::new).collect(Collectors.toList());
    }

    private static List<String> read(String name) {
        File file = new File("data/" + name);
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
             BufferedReader reader = new BufferedReader(isr)) {
            List<String> list = new LinkedList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("can not read " + name);
    }


}
