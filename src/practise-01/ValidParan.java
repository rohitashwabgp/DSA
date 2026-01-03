import java.util.Stack;

public class ValidParan {

    public static boolean valid(String sub) {

        Stack<Character> stack = new Stack<>();

        for (char c : sub.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else {

                if (stack.isEmpty())
                    return false;

                char top = stack.pop();

                if ((c == ')' && top != '(') ||
                        (c == '}' && top != '{') ||
                        (c == ']' && top != '[')) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}
