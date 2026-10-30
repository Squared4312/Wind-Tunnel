package io.github.wind_tunnel;

public class MenuStack {
    private Menu[] stack;
    private int pointer = 0;

    public MenuStack(int length) {
        stack = new Menu[length];
    }

    public void push(Menu menuName) {
        if (pointer >= stack.length) {return;}
        stack[pointer] = menuName;
        pointer += 1;
    }

    public Menu pop() {
        if (pointer <= 0) {return null;}
        pointer -= 1;
        return stack[pointer];
    }

    public Menu peek() {
        if (pointer <= 0) {return null;}
        return stack[pointer-1];
    }

    public boolean isEmpty() {return (pointer == 0);}
}
