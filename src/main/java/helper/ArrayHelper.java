package helper;

import org.nd4j.linalg.api.ndarray.INDArray;

public class ArrayHelper {

    private ArrayHelper() {
        //empty private constructor. This cannot be instantiated.
    }

    /**
     * Calculates the magnitude of the paased array
     * @param array
     * @return array length
     */
    public static double ArrayMagnitude(INDArray array) {
        double[] double_array = array.data().asDouble();
        double sum = 0;
        for (double d: double_array ) {
            sum += d*d;
        }
        //magnitude is the square root of the sum squares. 
        return Math.sqrt(sum);
    }
    
}
