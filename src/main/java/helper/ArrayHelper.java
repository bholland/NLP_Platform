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
