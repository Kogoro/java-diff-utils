/*
 * Copyright 2017 java-diff-utils.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.difflib.algorithm.myers;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author tw
 */
public class MyersDiffTest {

    @Test
    public void testDiffMyersExample1Forward() throws DiffException {
        List<String> original = Arrays.asList("A", "B", "C", "A", "B", "B", "A");
        List<String> revised = Arrays.asList("C", "B", "A", "B", "A", "C");
        final Patch<String> patch = Patch.generate(original, revised, new MyersDiff<String>().diff(original, revised), 1);
        assertNotNull(patch);
        assertEquals(4, patch.getDeltas().size());
        assertEquals("Patch{deltas=[[DeleteDelta, position: 0, lines: [A, B]], [InsertDelta, position: 3, lines: [B]], [DeleteDelta, position: 5, lines: [B]], [InsertDelta, position: 7, lines: [C]]]}", patch.toString());
    }

}
