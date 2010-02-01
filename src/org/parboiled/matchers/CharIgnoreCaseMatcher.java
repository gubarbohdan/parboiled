/*
 * Copyright (C) 2009 Mathias Doenitz
 *
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

package org.parboiled.matchers;

import org.jetbrains.annotations.NotNull;
import org.parboiled.MatcherContext;
import org.parboiled.support.Characters;
import org.parboiled.support.Chars;

/**
 * A Matcher matching a single character case independently.
 *
 * @param <V>
 */
public class CharIgnoreCaseMatcher<V> extends AbstractMatcher<V> {

    public final char charLow;
    public final char charUp;

    public CharIgnoreCaseMatcher(int index, char character) {
        super(index);
        this.charLow = Character.toLowerCase(character);
        this.charUp = Character.toUpperCase(character);
    }

    @Override
    public String getLabel() {
        if (hasLabel()) return super.getLabel();
        if (charLow == Chars.EOI) return "EOI";
        if (charLow == charUp) return "\'" + charLow + '\'';
        return "\'" + charLow + '/' + charUp + '\'';
    }

    public boolean match(@NotNull MatcherContext<V> context) {
        char c = context.getCurrentLocation().currentChar;
        if (c != charLow && c != charUp) return false;
        context.advanceInputLocation();
        context.createNode();
        return true;
    }

    public Characters getStarterChars() {
        return Characters.of(charLow, charUp);
    }

}