import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author dawn
 */
public class ToImage {

    public static void main(String[] args) {
        List<NumberImage> td = Load.getData("training_data.txt");
        write(td, "training_data");
        td = Load.getData("test_data.txt");
        write(td, "test_data");
        td = Load.getData("validation_data.txt");
        write(td, "validation_data");
    }

    private static void write(List<NumberImage> list, String name) {
        int[] ccc = new int[1];
        int size = list.size();
        File file = new File("data/" + name);
        if (!file.exists()) {
            file.mkdir();
        }
        int[] count = new int[10];
        list.forEach(ni -> {
            String n = "data/" + name + "/" + ni.number + "-" + (count[ni.number]++) + ".png";
            BufferedImage newBufferedImage = new BufferedImage(
                    28, 28,
                    BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 28; x++) {
                for (int y = 0; y < 28; y++) {
                    double v = ni.data[y * 28 + x];
                    newBufferedImage.setRGB(x, y, getRGB(v));
                }
            }
            try {
                ImageIO.write(newBufferedImage, "png", new File(n));
                System.out.println((ccc[0]++) + "/" + size);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static int getRGB(double v) {
        int value = 255 - (int) (v * 256);
        String hex = Integer.toString(value, 16);
        return Integer.parseInt(hex + hex + hex, 16);
    }




}
