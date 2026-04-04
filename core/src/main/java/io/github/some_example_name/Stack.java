package io.github.some_example_name;

public class Stack {
    private String[] stack;
    private Integer pointer = 0;

    public Stack(Integer length) {
        stack = new String[length];
    }
}
