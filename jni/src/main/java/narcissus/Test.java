package narcissus;

public class Test {
    public static void main(String[] args) {
        System.out.println(
                Narcissus.getObjectField(Test.class.getClassLoader(), "ucp", "Ljdk/internal/loader/URLClassPath;"));
    }
}