/**
 * @author dawn
 */
class NumberImage {

    double[] data;

    int number = -1;

    double[] numbers = null;

    NumberImage(String d) {
        String[] s = d.split(",");
        data = new double[784];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(s[i]);
        }
        if (s.length == data.length + 10) {
            numbers = new double[10];
            for (int i = 0; i < 10; i++) {
                numbers[i] = Double.parseDouble(s[784 + i]);
                if (0.001 < numbers[i]) {
                    number = i;
                }
            }
        } else {
            number = Integer.parseInt(s[784]);
            numbers = new double[10];
            numbers[number] = 1.0;
        }
    }

}
