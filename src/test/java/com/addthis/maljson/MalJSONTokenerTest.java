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

public class MalJSONTokenerTest {

    @Test
    public void testUnquotedStrings() {
        try {
            new JSONObject("{ hello : world }");
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTrailingCommas() {
        try {
            new JSONObject("{ hello : world , }");
            new JSONArray("[ hello, world , ]");
        } catch (JSONException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testForbidDuplicates() {
        boolean fail = false;
        try {
            new JSONObject("{ a : 1, a: 2 }", false);
        } catch(JSONException e) {
            assertEquals(2, e.getLineNumberInfoSize());
            assertEquals(0, e.getLine(0));
            assertEquals(0, e.getLine(1));
            assertEquals(2, e.getColumn(0));
            assertEquals(9, e.getColumn(1));
            fail = true;
            System.out.println(e);
        }
        assert(fail);
    }

}
