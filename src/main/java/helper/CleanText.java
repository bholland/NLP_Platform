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

/**
 * @author ben
 * Static class designed to standardize the way text gets cleaned. 
 * This class cannot be extended and instantiated.
 */

public final class CleanText {
    
    private CleanText() {
        //empty private constructor. This cannot be instantiated.
    }
    
    public static String Standardize(String input) {
        String output = input.toLowerCase();
        return output;
    }
}
