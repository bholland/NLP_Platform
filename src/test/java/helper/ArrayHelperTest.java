/*******************************************************************************
 * Copyright (C) 2018 by Benedict M. Holland <benedict.m.holland@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

