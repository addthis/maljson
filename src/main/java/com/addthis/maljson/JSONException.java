/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.addthis.maljson;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

// Note: this class was written without inspecting the non-free org.json sourcecode.

/**
 * Thrown to indicate a problem with the JSON API. Such problems include:
 * <ul>
 *   <li>Attempts to parse or construct malformed documents
 *   <li>Use of null as a name
 *   <li>Use of numeric types not available to JSON, such as {@link
 *       Double#isNaN() NaNs} or {@link Double#isInfinite() infinities}.
 *   <li>Lookups using an out of range index or nonexistent name
 *   <li>Type mismatches on lookups
 * </ul>
 *
 * <p>Although this is a checked exception, it is rarely recoverable. Most
 * callers should simply wrap this exception in an unchecked exception and
 * rethrow:
 * <pre>  public JSONArray toJSONObject() {
 *     try {
 *         JSONObject result = new JSONObject();
 *         ...
 *     } catch (JSONException e) {
 *         throw new RuntimeException(e);
 *     }
 * }</pre>
 */
public class JSONException extends Exception {

    @Nonnull
    final private LineNumberInfo[] lineNumberInfo;

    private static LineNumberInfo[] filterMissingInfo(LineNumberInfo[] input) {
        List<LineNumberInfo> info = new ArrayList<LineNumberInfo>();
        for(LineNumberInfo element : input) {
            if (!LineNumberInfo.MissingInfo.equals(element)) {
                info.add(element);
            }
        }
        return info.toArray(new LineNumberInfo[info.size()]);
    }

    private static String generateLineInfoString(LineNumberInfo... info) {
        info = filterMissingInfo(info);
        StringBuilder builder = new StringBuilder();
        builder.append(" at ");
        for(int i = 0; i < info.length; i++) {
            LineNumberInfo current = info[i];
            if (i > 0) {
                if (i == info.length - 1) {
                    builder.append(" and ");
                } else {
                    builder.append(", ");
                }
            }
            builder.append("line " + (current.getLine() + 1));
            builder.append(" and column " + (current.getColumn() + 1));
        }
        return builder.toString();
    }

    public JSONException(String s) {
        super(s);
        this.lineNumberInfo = new LineNumberInfo[1];
        this.lineNumberInfo[0] = LineNumberInfo.MissingInfo;
    }

    public JSONException(String s, LineNumberInfo lineInfo) {
        super((lineInfo == LineNumberInfo.MissingInfo) ? s :
              s + generateLineInfoString(lineInfo));
        this.lineNumberInfo = new LineNumberInfo[1];
        this.lineNumberInfo[0] = lineInfo;
    }

    public JSONException(String s, LineNumberInfo... lineInfo) {
        super(s + generateLineInfoString(lineInfo));
        this.lineNumberInfo = lineInfo;
    }

    public int getLine() {
        return getLine(0);
    }

    public int getColumn() {
        return getColumn(0);
    }

    public int getLine(int pos) {
        return lineNumberInfo[pos].line;
    }

    public int getColumn(int pos) {
        return lineNumberInfo[pos].column;
    }

    public int getLineNumberInfoSize() {
        return lineNumberInfo.length;
    }

}
