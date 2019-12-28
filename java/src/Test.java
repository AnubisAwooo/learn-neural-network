/**
 * @author dawn
 */
public class Test {

    public static void main(String[] args) {

        Network net = new Network(new int[]{784, 100, 10});
        net.sgd(Load.getData("training_data.txt"), 130, 10, 1, Load.getData("test_data.txt"));

        System.out.println(1);

    }

}
