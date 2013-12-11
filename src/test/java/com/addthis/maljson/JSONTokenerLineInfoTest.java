/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addthis.maljson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JSONTokenerLineInfoTest {

    @Test
    public void testNext() {
        String input = new String("{a = \"world\",\nb = 1}");
        JSONTokener tokener = new JSONTokener(input);
        int expectedColumn = 0, expectedLine = 0;
        while (tokener.more()) {
            assertEquals(expectedColumn, tokener.getColumn());
            assertEquals(expectedLine, tokener.getLine());
            char next = tokener.next();
            if (next == '\n') {
                expectedColumn = 0;
                expectedLine++;
            } else {
                expectedColumn++;
            }
        }
        int finalColumn = tokener.getColumn();
        int finalLine = tokener.getLine();
        tokener.next();
        assertEquals(finalColumn, tokener.getColumn());
        assertEquals(finalLine, tokener.getLine());
    }

    @Test
    public void testNextExpected() {
        String input = new String("{a = \"world\",\nb = 1}");
        JSONTokener tokener = new JSONTokener(input);
        boolean success = false;
        try {
            tokener.next('a');
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            success = true;
        }
        assertTrue(success);
        assertEquals(0, tokener.getLine());
        assertEquals(1, tokener.getColumn());

        success = true;
        try {
            tokener.next('a');
        } catch (JSONException ex) {
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testNextSequence() {
        String input = new String("{a = \"world\",\nb = 1}");
        JSONTokener tokener = new JSONTokener(input);
        String sequence = null;
        try {
            sequence = tokener.next(14);
        } catch (JSONException ex) {
            fail();
        }
        assertEquals(input.substring(0, 14), sequence);
        assertEquals(1, tokener.getLine());
        assertEquals(0, tokener.getColumn());
    }

    @Test
    public void testSkipToPosition() {
        String input = "Lorem ipsum dolor sit amet";
        JSONTokener tokener = new JSONTokener(input);
        assertEquals(0, tokener.getColumn());
        assertEquals(0, tokener.getLine());
        assertEquals('L', tokener.next());
        assertEquals(1, tokener.getColumn());
        assertEquals(0, tokener.getLine());
        tokener.skipToPosition(5);
        assertEquals(' ', tokener.current());
        assertEquals(5, tokener.getColumn());
        assertEquals(0, tokener.getLine());
        input = "Lorem\nipsum\ndolor\nsit\namet";
        tokener = new JSONTokener(input);
        tokener.skipToPosition(5);
        assertEquals('\n', tokener.current());
        assertEquals(5, tokener.getColumn());
        assertEquals(0, tokener.getLine());
        tokener.skipToPosition(6);
        assertEquals('i', tokener.current());
        assertEquals(0, tokener.getColumn());
        assertEquals(1, tokener.getLine());
        tokener.skipToPosition(7);
        assertEquals('p', tokener.current());
        assertEquals(1, tokener.getColumn());
        assertEquals(1, tokener.getLine());
        tokener.skipToPosition(17);
        assertEquals('\n', tokener.current());
        assertEquals(5, tokener.getColumn());
        assertEquals(2, tokener.getLine());
    }

    @Test
    public void testBack() {
        String input = new String("{a = \"world\",\nb = 1,\nc = 2.0}");
        int firstNewline = input.indexOf('\n');
        int secondNewline = input.indexOf('\n', firstNewline + 1);
        JSONTokener tokener = new JSONTokener(input);
        while (tokener.more()) {
            tokener.next();
        }

        int expectedColumn = 8;
        int expectedLine = 2;

        for (int i = 0; i < input.length(); i++) {
            tokener.back();
            assertTrue(tokener.more());
            char next = tokener.next();
            assertEquals(next, input.charAt(input.length() - i - 1));
            if (next == '\n') {
                if (expectedLine == 2) {
                    expectedColumn = secondNewline - firstNewline;
                } else {
                    expectedColumn = firstNewline;
                }
                expectedLine--;
            } else {
                expectedColumn--;
            }
            tokener.back();
            assertEquals(expectedColumn, tokener.getColumn());
            assertEquals(expectedLine, tokener.getLine());
        }
        assertEquals(0, tokener.getColumn());
        assertEquals(0, tokener.getLine());
        tokener.back();
        assertEquals(0, tokener.getColumn());
        assertEquals(0, tokener.getLine());
    }

}
