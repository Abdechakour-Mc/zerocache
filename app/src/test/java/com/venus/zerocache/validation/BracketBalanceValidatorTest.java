package com.venus.zerocache.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BracketBalanceValidatorTest {

    private final StructureValidator validator = new BracketBalanceValidator();

    // ---- balanced ----

    @Test
    void acceptsNestedBrackets() {
        assertTrue(validator.isWellFormed("{[()]}"));
    }

    @Test
    void acceptsSideBySideBrackets() {
        assertTrue(validator.isWellFormed("()[]{}"));
    }

    @Test
    void acceptsEmptyString() {
        assertTrue(validator.isWellFormed(""));
    }

    @Test
    void acceptsTextWithNoBrackets() {
        assertTrue(validator.isWellFormed("hello world"));
    }

    @Test
    void ignoresCharactersBetweenBrackets() {
        assertTrue(validator.isWellFormed("a(b)c"));
    }

    @Test
    void acceptsJsonLikeText() {
        assertTrue(validator.isWellFormed("{\"key\": [1, 2]}"));
    }

    // ---- not balanced ----

    @Test
    void rejectsWrongClosingOrder() {
        assertFalse(validator.isWellFormed("([)]"));
    }

    @Test
    void rejectsWrongClosingType() {
        assertFalse(validator.isWellFormed("foo(bar]"));
    }

    @Test
    void rejectsUnclosedOpeners() {
        assertFalse(validator.isWellFormed("(("));
        assertFalse(validator.isWellFormed("[ a { b }"));
    }

    @Test
    void rejectsCloserWithNothingOpen() {
        assertFalse(validator.isWellFormed("())"));
        assertFalse(validator.isWellFormed("))"));
    }

    @Test
    void rejectsCloserBeforeOpener() {
        assertFalse(validator.isWellFormed(") a ("));
    }

    // ---- invalid input ----

    @Test
    void nullTextThrows() {
        assertThrows(NullPointerException.class, () -> validator.isWellFormed(null));
    }
}