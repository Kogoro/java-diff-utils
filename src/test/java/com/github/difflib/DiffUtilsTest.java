package com.github.difflib;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipFile;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class DiffUtilsTest {

    public static List<String> readStringListFromInputStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {

            return reader.lines().collect(toList());
        }
    }

    @Test
    public void testDiff_Insert() throws DiffException {
        final Patch<String> patch = DiffUtils.diff(Arrays.asList("hhh"), Arrays.
                asList("hhh", "jjj", "kkk"), 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());
        final Delta<String> delta = patch.getDeltas().get(0);
        assertTrue(delta instanceof InsertDelta);

        Chunk<String> originalChunk = new Chunk<>(1, Collections.<String>emptyList());
        originalChunk.setBefore(Arrays.asList("hhh"));
        Chunk<String> revisedChunk = new Chunk<>(1, Arrays.asList("jjj", "kkk"));
        revisedChunk.setBefore(Arrays.asList("hhh"));

        assertEquals(originalChunk, delta.getOriginal());
        assertEquals(revisedChunk, delta.getRevised());
    }

    @Test
    public void testDiff_Insert_Before_After() throws DiffException {
        final List<String> test_from = Arrays.asList("hhh", "ttt", "xxx");
        final List<String> test_to = Arrays.asList("hhh", "jjj", "kkk", "ttt", "xxx");

        final Patch<String> patch = DiffUtils.diff(test_from, test_to, 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());

        final Delta<String> delta = patch.getDeltas().get(0);
        System.out.println("INSERT: Original Chunk: " + delta.getOriginal() + " Before: " + delta.getOriginal().getBefore() + " After: " + delta.getOriginal().getAfter());
        assertTrue(delta.getOriginal().getBefore().size() == 1);
        assertTrue(delta.getOriginal().getAfter().size() == 1);
        assertEquals(Arrays.asList("hhh"), delta.getOriginal().getBefore());
        assertEquals(Arrays.asList("ttt"), delta.getOriginal().getAfter());

        System.out.println("INSERT: Revised Chunk: " + delta.getRevised() + " Before: " + delta.getRevised().getBefore() + " After: " + delta.getRevised().getAfter());
        assertTrue(delta.getRevised().getBefore().size() == 1);
        assertTrue(delta.getRevised().getAfter().size() == 1);
        assertEquals(Arrays.asList("hhh"), delta.getRevised().getBefore());
        assertEquals(Arrays.asList("ttt"), delta.getRevised().getAfter());
    }

    @Test
    public void testDiff_Delete() throws DiffException {
        final Patch<String> patch = DiffUtils.diff(Arrays.asList("ddd", "fff", "ggg"), Arrays.
                asList("ggg"), 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());
        final Delta<String> delta = patch.getDeltas().get(0);
        assertTrue(delta instanceof DeleteDelta);

        Chunk<String> originalChunk = new Chunk<>(0, Arrays.asList("ddd", "fff"));
        originalChunk.setAfter(Arrays.asList("ggg"));
        Chunk<String> revisedChunk = new Chunk<>(0, Collections.<String>emptyList());
        revisedChunk.setAfter(Arrays.asList("ggg"));

        assertEquals(originalChunk, delta.getOriginal());
        assertEquals(revisedChunk, delta.getRevised());
    }

    @Test
    public void testDiff_Delete_Before_After() throws DiffException {
        final List<String> test_from = Arrays.asList("fff", "ggg");
        final List<String> test_to = Arrays.asList("ggg");

        final Patch<String> patch = DiffUtils.diff(test_from, test_to, 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());

        final Delta<String> delta = patch.getDeltas().get(0);
        System.out.println("DELETE: Original Chunk: " + delta.getOriginal() + " Before: " + delta.getOriginal().getBefore() + " After: " + delta.getOriginal().getAfter());
        assertTrue(delta.getOriginal().getBefore().size() == 0);
        assertTrue(delta.getOriginal().getAfter().size() == 1);
        assertEquals(Arrays.asList(), delta.getOriginal().getBefore());
        assertEquals(Arrays.asList("ggg"), delta.getOriginal().getAfter());

        System.out.println("DELETE: Revised Chunk: " + delta.getRevised() + " Before: " + delta.getRevised().getBefore() + " After: " + delta.getRevised().getAfter());
        assertTrue(delta.getRevised().getBefore().size() == 0);
        assertTrue(delta.getRevised().getAfter().size() == 1);
        assertEquals(Arrays.asList(), delta.getRevised().getBefore());
        assertEquals(Arrays.asList("ggg"), delta.getRevised().getAfter());
    }

    @Test
    public void testDiff_Change() throws DiffException {
        final List<String> changeTest_from = Arrays.asList("aaa", "bbb", "ccc");
        final List<String> changeTest_to = Arrays.asList("aaa", "zzz", "ccc");

        final Patch<String> patch = DiffUtils.diff(changeTest_from, changeTest_to, 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());
        final Delta<String> delta = patch.getDeltas().get(0);
        assertTrue(delta instanceof ChangeDelta);

        Chunk<String> originalChunk = new Chunk<>(1, Arrays.asList("bbb"));
        originalChunk.setBefore(Arrays.asList("aaa"));
        originalChunk.setAfter(Arrays.asList("ccc"));
        Chunk<String> revisedChunk = new Chunk<>(1, Arrays.asList("zzz"));
        revisedChunk.setBefore(Arrays.asList("aaa"));
        revisedChunk.setAfter(Arrays.asList("ccc"));
        assertEquals(originalChunk, delta.getOriginal());
        assertEquals(revisedChunk, delta.getRevised());
    }

    @Test
    public void testDiff_Change_Before_After() throws DiffException {
        final List<String> test_from = Arrays.asList("aaa", "bbb", "ccc");
        final List<String> test_to = Arrays.asList("aaa", "zzz", "ccc");

        final Patch<String> patch = DiffUtils.diff(test_from, test_to, 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());

        final Delta<String> delta = patch.getDeltas().get(0);
        System.out.println("CHANGE: Original Chunk: " + delta.getOriginal() + " Before: " + delta.getOriginal().getBefore() + " After: " + delta.getOriginal().getAfter());
        assertTrue(delta.getOriginal().getBefore().size() == 1);
        assertTrue(delta.getOriginal().getAfter().size() == 1);
        assertEquals(Arrays.asList("aaa"), delta.getOriginal().getBefore());
        assertEquals(Arrays.asList("ccc"), delta.getOriginal().getAfter());

        System.out.println("CHANGE: Revised Chunk: " + delta.getRevised() + " Before: " + delta.getRevised().getBefore() + " After: " + delta.getRevised().getAfter());
        assertTrue(delta.getRevised().getBefore().size() == 1);
        assertTrue(delta.getRevised().getAfter().size() == 1);
        assertEquals(Arrays.asList("aaa"), delta.getRevised().getBefore());
        assertEquals(Arrays.asList("ccc"), delta.getRevised().getAfter());
    }

    @Test
    public void testDiff_EmptyList() throws DiffException {
        final Patch<String> patch = DiffUtils.diff(new ArrayList<>(), new ArrayList<>(), 1);
        assertNotNull(patch);
        assertEquals(0, patch.getDeltas().size());
    }

    @Test
    public void testDiff_EmptyListWithNonEmpty() throws DiffException {
        final Patch<String> patch = DiffUtils.diff(new ArrayList<>(), Arrays.asList("aaa"), 1);
        assertNotNull(patch);
        assertEquals(1, patch.getDeltas().size());
        final Delta<String> delta = patch.getDeltas().get(0);
        assertTrue(delta instanceof InsertDelta);
    }

    @Test
    public void testDiffInline() throws DiffException {
        final Patch<String> patch = DiffUtils.diffInline("", "test", 1);
        assertEquals(1, patch.getDeltas().size());
        assertTrue(patch.getDeltas().get(0) instanceof InsertDelta);
        assertEquals(0, patch.getDeltas().get(0).getOriginal().getPosition());
        assertEquals(0, patch.getDeltas().get(0).getOriginal().getLines().size());
        assertEquals("test", patch.getDeltas().get(0).getRevised().getLines().get(0));
    }

    @Test
    public void testDiffInline2() throws DiffException {
        final Patch<String> patch = DiffUtils.diffInline("es", "fest", 1);
        assertEquals(2, patch.getDeltas().size());
        assertTrue(patch.getDeltas().get(0) instanceof InsertDelta);
        assertEquals(0, patch.getDeltas().get(0).getOriginal().getPosition());
        assertEquals(2, patch.getDeltas().get(1).getOriginal().getPosition());
        assertEquals(0, patch.getDeltas().get(0).getOriginal().getLines().size());
        assertEquals(0, patch.getDeltas().get(1).getOriginal().getLines().size());
        assertEquals("f", patch.getDeltas().get(0).getRevised().getLines().get(0));
        assertEquals("t", patch.getDeltas().get(1).getRevised().getLines().get(0));
    }

    @Test
    public void testDiffIntegerList() throws DiffException {
        List<Integer> original = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> revised = Arrays.asList(2, 3, 4, 6);

        final Patch<Integer> patch = DiffUtils.diff(original, revised, 1);

        for (Delta delta : patch.getDeltas()) {
            System.out.println(delta);
        }

        assertEquals(2, patch.getDeltas().size());
        assertEquals("[DeleteDelta, position: 0, lines: [1]]", patch.getDeltas().get(0).toString());
        assertEquals("[ChangeDelta, position: 4, lines: [5] to [6]]", patch.getDeltas().get(1).toString());
    }

    @Test
    public void testDiffMissesChangeForkDnaumenkoIssue31() throws DiffException {
        List<String> original = Arrays.asList("line1", "line2", "line3");
        List<String> revised = Arrays.asList("line1", "line2-2", "line4");

        Patch<String> patch = DiffUtils.diff(original, revised, 1);
        assertEquals(1, patch.getDeltas().size());
        assertEquals("[ChangeDelta, position: 1, lines: [line2, line3] to [line2-2, line4]]", patch.getDeltas().get(0).toString());
    }

    /**
     * To test this, the greedy meyer algorithm is not suitable.
     */
    @Test
    @Ignore
    public void testPossibleDiffHangOnLargeDatasetDnaumenkoIssue26() throws IOException, DiffException {
        ZipFile zip = new ZipFile(TestConstants.MOCK_FOLDER + "/large_dataset1.zip");

        Patch<String> patch = DiffUtils.diff(
                readStringListFromInputStream(zip.getInputStream(zip.getEntry("ta"))),
                readStringListFromInputStream(zip.getInputStream(zip.getEntry("tb"))), 1);

        assertEquals(1, patch.getDeltas().size());
    }

    @Test
    public void testDiffMyersExample1() throws DiffException {
        final Patch<String> patch = DiffUtils.diff(Arrays.asList("A", "B", "C", "A", "B", "B", "A"), Arrays.asList("C", "B", "A", "B", "A", "C"), 1);
        assertNotNull(patch);
        assertEquals(4, patch.getDeltas().size());
        assertEquals("Patch{deltas=[[DeleteDelta, position: 0, lines: [A, B]], [InsertDelta, position: 3, lines: [B]], [DeleteDelta, position: 5, lines: [B]], [InsertDelta, position: 7, lines: [C]]]}", patch.toString());
    }
}
