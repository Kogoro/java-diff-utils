package com.github.difflib.patch;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PatchTest {

    @Test
    public void testPatch_Insert() throws DiffException {
        final List<String> insertTest_from = Arrays.asList("hhh");
        final List<String> insertTest_to = Arrays.asList("hhh", "jjj", "kkk", "lll");

        final Patch<String> patch = DiffUtils.diff(insertTest_from, insertTest_to, 1);
        try {
            assertEquals(insertTest_to, DiffUtils.patch(insertTest_from, patch));
        } catch (PatchFailedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPatch_Delete() throws DiffException {
        final List<String> deleteTest_from = Arrays.asList("ddd", "fff", "ggg", "hhh");
        final List<String> deleteTest_to = Arrays.asList("ggg");

        final Patch<String> patch = DiffUtils.diff(deleteTest_from, deleteTest_to, 1);
        try {
            assertEquals(deleteTest_to, DiffUtils.patch(deleteTest_from, patch));
        } catch (PatchFailedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPatch_Change() throws DiffException {
        final List<String> changeTest_from = Arrays.asList("aaa", "bbb", "ccc", "ddd");
        final List<String> changeTest_to = Arrays.asList("aaa", "bxb", "cxc", "ddd");

        final Patch<String> patch = DiffUtils.diff(changeTest_from, changeTest_to, 1);
        try {
            assertEquals(changeTest_to, DiffUtils.patch(changeTest_from, patch));
        } catch (PatchFailedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPatch_ChunkVerify() throws DiffException {
        final List<String> changeTest_from = Arrays.asList("aaa", "ddd");
        final List<String> changeTest_to = Arrays.asList("aaa", "bbb", "ccc", "ddd");

        final Patch<String> patchOne = DiffUtils.diff(changeTest_from, changeTest_to, 1);
        assertTrue(patchOne.getDeltas().size() > 0);
        final Patch<String> patchTwo = DiffUtils.diff(changeTest_from, changeTest_to, 1);
        assertTrue(patchTwo.getDeltas().size() > 0);

        for (Delta<String> deltaOne : patchOne.getDeltas()) {
            for (Delta<String> deltaTwo : patchTwo.getDeltas()) {
                PatchFailedException exception = null;
                try {
                    deltaOne.verify(deltaTwo);
                } catch (PatchFailedException e) {
                    exception = e;
                }
                assertTrue(exception != null);
            }
        }
    }
}
