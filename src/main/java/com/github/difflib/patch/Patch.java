/*-
 * #%L
 * java-diff-utils
 * %%
 * Copyright (C) 2009 - 2017 java-diff-utils
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
package com.github.difflib.patch;

import com.github.difflib.algorithm.Change;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static java.util.Comparator.comparing;

/**
 * Describes the patch holding all deltas between the original and revised texts.
 *
 * T The type of the compared elements in the 'lines'.
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 * @author <a href="ch.sontag@gmail.com">Christopher Sontag</a>
 */
public final class Patch<T> {

    private final List<Delta<T>> deltas;

    public Patch() {
        this(10);
    }

    public Patch(int estimatedPatchSize) {
        deltas = new ArrayList<>(estimatedPatchSize);
    }

    /**
     * Apply this patch to the given target
     *
     * @return the patched text
     * @throws PatchFailedException if can't apply patch
     */
    public List<T> applyTo(List<T> target) throws PatchFailedException {
        List<T> result = new ArrayList<>(target);
        ListIterator<Delta<T>> it = getDeltas().listIterator(deltas.size());
        while (it.hasPrevious()) {
            Delta<T> delta = it.previous();
            delta.applyTo(result);
        }
        return result;
    }

    /**
     * Restore the text to original. Opposite to applyTo() method.
     *
     * @param target the given target
     * @return the restored text
     */
    public List<T> restore(List<T> target) {
        List<T> result = new ArrayList<>(target);
        ListIterator<Delta<T>> it = getDeltas().listIterator(deltas.size());
        while (it.hasPrevious()) {
            Delta<T> delta = it.previous();
            delta.restore(result);
        }
        return result;
    }

    /**
     * Add the given delta to this patch
     *
     * @param delta the given delta
     */
    public void addDelta(Delta<T> delta) {
        deltas.add(delta);
    }

    /**
     * Get the list of computed deltas
     *
     * @return the deltas
     */
    public List<Delta<T>> getDeltas() {
        Collections.sort(deltas, comparing(d -> d.getOriginal().getPosition()));
        return deltas;
    }

    @Override
    public String toString() {
        return "Patch{" + "deltas=" + deltas + '}';
    }

    public static <T> Patch<T> generate(List<T> original, List<T> revised, List<Change> changes, int surroundingLines) {
        Patch<T> patch = new Patch<>(changes.size());
        for (Change change : changes) {

            Chunk<T> orgChunk = new Chunk<>(change.startOriginal, new ArrayList<>(original.subList(change.startOriginal, change.endOriginal)));
            if (change.startOriginal - surroundingLines >= 0) {
                orgChunk.setBefore(new ArrayList<>(original.subList(change.startOriginal - surroundingLines, change.startOriginal)));
            } else {
                orgChunk.setBefore(new ArrayList<>(original.subList(0, change.startOriginal)));
            }
            if (change.endOriginal + surroundingLines <= original.size()) {
                orgChunk.setAfter(new ArrayList<>(original.subList(change.endOriginal, change.endOriginal + surroundingLines)));
            } else {
                orgChunk.setAfter(new ArrayList<>(original.subList(change.endOriginal, original.size())));
            }

            Chunk<T> revChunk = new Chunk<>(change.startRevised, new ArrayList<>(revised.subList(change.startRevised, change.endRevised)));
            if (change.startRevised - surroundingLines >= 0) {
                revChunk.setBefore(new ArrayList<>(revised.subList(change.startRevised - surroundingLines, change.startRevised)));
            } else {
                revChunk.setBefore(new ArrayList<>(revised.subList(0, change.startRevised)));
            }
            if (change.endRevised + surroundingLines <= revised.size()) {
                revChunk.setAfter(new ArrayList<>(revised.subList(change.endRevised, change.endRevised + surroundingLines)));
            } else {
                revChunk.setAfter(new ArrayList<>(revised.subList(change.endRevised, revised.size())));
            }

            switch (change.deltaType) {
                case DELETE:
                    patch.addDelta(new DeleteDelta<>(orgChunk, revChunk));
                    break;
                case INSERT:
                    patch.addDelta(new InsertDelta<>(orgChunk, revChunk));
                    break;
                case CHANGE:
                    patch.addDelta(new ChangeDelta<>(orgChunk, revChunk));
                    break;
            }
        }
        return patch;
    }

}
