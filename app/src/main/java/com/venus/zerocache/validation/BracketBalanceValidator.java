package com.venus.zerocache.validation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BracketBalanceValidator implements StructureValidator{

    @Override
    public boolean isWellFormed(String text) {
        Objects.requireNonNull(text, "text must not be null");

        // List<Character> targetSymb = List.of('(', ')', '[', ']', '{', '}');
        Map<Character, Character> mapping = Map.of(
                                                '(', ')',
                                                '[', ']',
                                                '{', '}'
                                            );   

        Deque<Character> stack = new ArrayDeque<>();

        for (int i=0; i<text.length(); i++) {
            char currChar = text.charAt(i); 

            if (mapping.containsKey(currChar)) {
                stack.push(currChar);
            } else if (mapping.containsValue(currChar)) {
                if (stack.isEmpty() || !mapping.get(stack.pop()).equals(currChar)){
                    return false;
                }
            }
        }
        return stack.isEmpty();

    }
    
}
