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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds the information about the part of text involved in the diff process <p> Text is represented as <code>Object[]</code> because the diff engine is capable
 * of handling more than plain ascci. In fact, arrays or lists of any type that implements {@link java.lang.Object#hashCode hashCode()} and
 * {@link java.lang.Object#equals equals()} correctly can be subject to differencing using this library. </p> T The type of the compared elements in the
 * 'lines'.
 *
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 * @author <a href="ch.sontag@gmail.com">Christopher Sontag</a>
 */
public final class Chunk<T> {

    private final int position;
    private List<T> lines;
    private List<T> before = new ArrayList<>();
    private List<T> after = new ArrayList<>();

    /**
     * Creates a chunk and saves a copy of affected lines
     *
     * @param position the start position
     * @param lines    the affected lines
     */
    public Chunk(int position, List<T> lines) {
        this.position = position;
        this.lines = lines;
    }

    /**
     * Creates a chunk and saves a copy of affected lines
     *
     * @param position the start position
     * @param lines    the affected lines
     */
    public Chunk(int position, T[] lines) {
        this.position = position;
        this.lines = Arrays.asList(lines);
    }

    /**
     * Verifies that this chunk's saved text matches the corresponding text in the given sequence.
     *
     * @param target the sequence to verify against.
     */
    public void verify(List<T> target) throws PatchFailedException {
        if (position > target.size() || last() > target.size()) {
            throw new PatchFailedException("Incorrect Chunk: the position of chunk > target size");
        }
        for (int i = 0; i < size(); i++) {
            if (!target.get(position + i).equals(lines.get(i))) {
                throw new PatchFailedException("Incorrect Chunk: the chunk content doesn't match the target");
            }
        }
        for (int i = before.size(); i > 0; i--) {
            if (!before.get(before.size() - i).equals(target.get(position - i))) {
                throw new PatchFailedException("Incorrect Chunk: the chunk's surroundings (before) doesn't match the target");
            }
        }
        for (int i = 0; i < after.size(); i++) {
            if (!after.get(i).equals(target.get(last() + i + 1))) {
                throw new PatchFailedException("Incorrect Chunk: the chunk's surroundings (after) doesn't match the target");
            }
        }
    }

    /**
     * Verifies that this chunk does not affect the same positions as the given chunk.
     *
     * @param other the chunk to be proof against this chunk.
     * @throws PatchFailedException if both deltas affect the same chunk.
     */
    public void verify(Chunk<T> other) throws PatchFailedException {
        if (Math.max(last(), other.last()) - Math.min(getPosition(), other.getPosition()) < (size() + other.size())) {
            throw new PatchFailedException("Multiple chunks would override the same target position");
        }
    }

    /**
     * @return the start position of chunk in the text
     */
    public int getPosition() {
        return position;
    }

    public void setLines(List<T> lines) {
        this.lines = lines;
    }

    /**
     * @return the affected lines
     */
    public List<T> getLines() {
        return lines;
    }

    public int size() {
        return lines.size();
    }

    /**
     * Returns the index of the last line of the chunk.
     */
    public int last() {
        return getPosition() + size() - 1;
    }

    /**
     * @return the lines before the affected lines
     */
    public List<T> getBefore() {
        return before;
    }

    public void setBefore(List<T> before) {
        this.before = before;
    }

    /**
     * @return the lines after the affected lines
     */
    public List<T> getAfter() {
        return after;
    }

    public void setAfter(List<T> after) {
        this.after = after;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lines == null) ? 0 : lines.hashCode());
        result = prime * result + position;
        result = prime * result + size();
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Chunk<T> other = (Chunk) obj;
        if (lines == null) {
            if (other.lines != null) {
                return false;
            }
        } else if (!lines.equals(other.lines)) {
            return false;
        }
        return before.equals(other.before) && after.equals(other.after) && position == other.position;
    }

    @Override
    public String toString() {
        return "[position: " + position + ", size: " + size() + ", lines: " + lines + ", before: " + before + ", after: " + after + "]";
    }

}
