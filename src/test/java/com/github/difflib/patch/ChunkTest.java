package com.github.difflib.patch;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ChunkTest {

    @Test
    public void testChunk_verify() throws DiffException {
        final List<String> insertTest_from = Arrays.asList("hhh", "lll");
        final List<String> insertTest_to = Arrays.asList("hhh", "jjj", "kkk", "lll");

        final Patch<String> patch = DiffUtils.diff(insertTest_from, insertTest_to, 1);
        assertTrue(patch.getDeltas().size() > 0);
        Delta<String> delta = patch.getDeltas().get(0);

        PatchFailedException exception = null;
        try {
            delta.verify(insertTest_from);
        } catch (PatchFailedException e) {
            exception = e;
        }
        assertTrue(exception == null);

        exception = null;
        try {
            delta.verify(insertTest_to);
        } catch (PatchFailedException e) {
            exception = e;
        }
        assertTrue(exception != null);
    }

}
