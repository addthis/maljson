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
import static org.junit.Assert.fail;

public class JSONObjectLineInfoTest {

    @Test
    public void testLineNumberArray() {
        try {

            JSONObject json = new JSONObject("{a=[1, 2.0, 3, \"hello\"]}");

            LineNumberInfo line0, line1, line2, line3, line4;
            line0 = json.getJSONArray("a").getLineNumber(0);
            line1 = json.getJSONArray("a").getLineNumber(1);
            line2 = json.getJSONArray("a").getLineNumber(2);
            line3 = json.getJSONArray("a").getLineNumber(3);
            line4 = json.getJSONArray("a").getLineNumber(4);

            assertEquals(0, line0.line);
            assertEquals(4, line0.column);
            assertEquals(0, line1.line);
            assertEquals(7, line1.column);
            assertEquals(0, line2.line);
            assertEquals(12, line2.column);
            assertEquals(0, line3.line);
            assertEquals(15, line3.column);
            assertEquals(-1, line4.line);
            assertEquals(-1, line4.column);
        } catch (JSONException ex) {
            fail("Unexpected JSONException: " + ex.getMessage());
        }
    }

    @Test
    public void testLineNumbers() {
        try {
            JSONObject json = new JSONObject("{a=1,\nb=\"hello\",\nc= 1.2}");

            LineNumberInfo aKey, bKey, cKey, fooKey;
            LineNumberInfo aVal, bVal, cVal, fooVal;

            aKey = json.getKeyLineNumber("a");
            bKey = json.getKeyLineNumber("b");
            cKey = json.getKeyLineNumber("c");
            fooKey = json.getKeyLineNumber("foo");

            aVal = json.getValLineNumber("a");
            bVal = json.getValLineNumber("b");
            cVal = json.getValLineNumber("c");
            fooVal = json.getValLineNumber("foo");

            assertEquals(0, aKey.line);
            assertEquals(1, aKey.column);
            assertEquals(0, aVal.line);
            assertEquals(3, aVal.column);

            assertEquals(1, bKey.line);
            assertEquals(0, bKey.column);
            assertEquals(1, bVal.line);
            assertEquals(2, bVal.column);

            assertEquals(2, cKey.line);
            assertEquals(0, cKey.column);
            assertEquals(2, cVal.line);
            assertEquals(3, cVal.column);

            assertEquals(-1, fooKey.line);
            assertEquals(-1, fooKey.column);
            assertEquals(-1, fooVal.line);
            assertEquals(-1, fooVal.column);
        } catch (JSONException ex) {
            fail("Unexpected JSONException: " + ex.getMessage());
        }
    }

    @Test
    public void testSyntaxErrors() {
        try {
            new JSONObject("{");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(0, ex.getColumn());
        }
        try {
            new JSONObject("{hello");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(5, ex.getColumn());
        }

        try {
            new JSONObject("{hello : ");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(8, ex.getColumn());
        }

        try {
            new JSONObject("{hello:3");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(7, ex.getColumn());
        }

        try {
            new JSONObject("{hello=['a'\n}");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(1, ex.getLine());
            assertEquals(0, ex.getColumn());
        }

        try {
            new JSONObject("/*\n\n\n");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(0, ex.getColumn());
        }

        try {
            new JSONObject("/*\n/*\n\n");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(0, ex.getColumn());
        }

        try {
            new JSONObject("{\"hello}");
            fail();
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            assertEquals(0, ex.getLine());
            assertEquals(1, ex.getColumn());
        }
    }

}
