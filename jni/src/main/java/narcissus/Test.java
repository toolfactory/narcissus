package narcissus;

public class Test {

    public static void main(String[] args) {
        try {
            final Object fieldVal = Narcissus.getObjectFieldVal(Test.class.getClassLoader(), "ucp",
                    "jdk.internal.loader.URLClassPath");
            System.out.println(fieldVal);
        } catch (NoSuchFieldError e) {
            throw new RuntimeException("Could not find field", e);
        }
    }
}