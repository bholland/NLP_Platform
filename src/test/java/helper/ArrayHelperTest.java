package helper;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.assertEquals;

public class ArrayHelperTest {
    
    @Test
    public void zeroTest() {
        INDArray zero_array = Nd4j.zeros(10);
        assertEquals("The array magnitude should be zero.", 0, ArrayHelper.ArrayMagnitude(zero_array), 0.0000001);;
    }
    
    @Test
    public void oneTest() {
        INDArray one_array = Nd4j.ones(10);
        assertEquals("The array magnitude should be sqrt(10)",
                Math.sqrt(10.0), ArrayHelper.ArrayMagnitude(one_array), 0.0000001);
    }
    
    @Test
    public void randomTest() {
        double[] d_array = new double[] {.1, .2, .3, .4, .5};
        INDArray array = Nd4j.create(d_array);
        assertEquals("The magnutude should be close to .74161984",
                0.7416198487095663, ArrayHelper.ArrayMagnitude(array), 0.0000001);
    }
}

