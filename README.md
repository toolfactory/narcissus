# Narcissus: thwart strong encapsulation in JDK 16+

Narcissus is a JNI native code library that provides a small subset of the Java reflection API, while bypassing all of Java's access/visibility checks, security manager restrictions, and module strong encapsulation enforcement, by calling methods and accessing fields through the JNI API. This allows code that relies on reflective access to non-public classes, fields, and methods to keep working even now that strong encapsulation is being enforced in JDK 16+.

Narcissus works on JDK 7+, however it is most useful for suppressing reflective access warnings in JDK 9-15, and for circumventing strong encapsulation for JDK 16+, in order to keep legacy software running (for example, when legacy software depends upon `setAccessible` to access a needed private field of a class in some library).

## Usage

The API is defined as static methods of [Narcissus.java](https://github.com/lukehutch/narcissus/blob/main/jni/src/main/java/narcissus/Narcissus.java).

Note: You should check the static Boolean value `Narcissus.libraryLoaded` to make sure the library has actually loaded before you try calling any methods. Otherwise you may get an `UnsatisfiedLinkError` when calling other static methods of `Narcissus` if the library wasn't able to be loaded.

* Finding classes
  * **`Class<?> Narcissus.findClass(String className)`**

    Equivalent to `Class.forName(String className)`. Finds array classes if the class name is of the form `"com.xyz.MyClass[][]"`.

* Finding fields
  * **`Field[] Narcissus.getDeclaredFields(Class<?> cls)`**

    Equivalent to `cls.getDeclaredFields()`.

  * **`List<Field> Narcissus.enumerateFields(Class<?> cls)`**

    Equivalent to `cls.getDeclaredFields()`, but bypasses all security and visibility checks, and also iterates up through superclasses to collect all fields of the class and its superclasses.

  * **`Field Narcissus.findField(Class<?> cls, String fieldName)`**

    Find a field of a class by name.

  * You may want to also try using [**`ReflectionCache`**](https://github.com/toolfactory/narcissus/blob/main/src/main/java/io/github/toolfactory/narcissus/ReflectionCache.java) if you need to quickly find a lot of fields in the same class.

* Finding methods
  * **`Method[] Narcissus.getDeclaredMethods(Class<?> cls)`**

    Equivalent to `cls.getDeclaredMethods()`.

  * **`List<Method> Narcissus.enumerateMethods(Class<?> cls)`**

    Equivalent to `cls.getDeclaredMethods()`, but bypasses all security and visibility checks, and also iterates up through superclasses to collect all methods of the class and its superclasses.

  * **`Method Narcissus.findMethod(Class<?> cls, String methodName, Class<?>... paramTypes)`**

    Find a method of a class by name and parameter types.

  * **`Constructor[] Narcissus.getDeclaredConstructors(Class<?> cls)`**

    Equivalent to `cls.getDeclaredConstructors()`.

  * **`List<Constructor> Narcissus.findConstructor(Class<?> cls, Class<?>... paramTypes)`**

    Find the constructor with the required parameter types.

  * You may want to also try using [**`ReflectionCache`**](https://github.com/toolfactory/narcissus/blob/main/src/main/java/io/github/toolfactory/narcissus/ReflectionCache.java) if you need to quickly find a lot of methods in the same class.

* Getting/Setting fields

  * **`<<T>> Narcissus.get<<T>>Field(Object object, Field field)`**, e.g. `int Narcissus.getIntField(Object object, Field field)`
  
    **`void Narcissus.set<<T>>Field(Object object, Field field, <<T>> value)`**, e.g. `void Narcissus.setIntField(Object object, Field field, int value)`
  
    Get/set a non-static field value, for a field of type `<<T>>`. For non-primitive-typed fields, `<<T>>` is `Object`.
  
  * **`Object Narcissus.getField(Object object, Field field)`**
  
    **`void Narcissus.setField(Object object, Field field, Object value)`**
  
    Get/set a non-static field value. Automatically boxes/unboxes values if the field is primitive-typed.
  
  * **`<<T>> Narcissus.getStatic<<T>>Field(Field field)`**, e.g. `int Narcissus.getStaticIntField(Field field)`
  
    **`void Narcissus.setStatic<<T>>Field(Field field, <<T>> value)`**, e.g. `void Narcissus.setStaticIntField(Field field, int value)`

    Get/set a static field value, for a field of type `<<T>>`. For non-primitive-typed fields, `<<T>>` is `Object`.
  
  * **`Object Narcissus.getStaticField(Field field)`**
  
    **`void Narcissus.setStaticField(Field field, Object value)`**
  
    Get/set a static field value. Automatically boxes/unboxes values if the field is primitive-typed.

* Invoking methods

  * **`<<T>> Narcissus.invoke<<T>>Method(Object object, Method method, Object... args)`**, e.g. `int Narcissus.invokeIntMethod(Object object, Method method, Object... args)`
  
    Invoke a non-static method which returns type `<<T>>`. For methods with non-primitive return type, `<<T>>` is `Object`. For methods that do not return a value, `<<T>>` is `void`.

  * **`Object Narcissus.invokeMethod(Object object, Method method, Object... args)`**
  
    Invoke a non-static method. Automatically boxes the return type, if the method returns a primitive type.

  * **`<<T>> Narcissus.invokeStatic<<T>>Method(Method method, Object... args)`**, e.g. `int Narcissus.invokeStaticIntMethod(Method method, Object... args)`
  
    Invoke a static method which returns type `<<T>>`. For methods with non-primitive return type, `<<T>>` is `Object`. For methods that do not return a value, `<<T>>` is `void`.

  * **`Object Narcissus.invokeStaticMethod(Method method, Object... args)`**
  
    Invoke a static method. Automatically boxes the return type, if the method returns a primitive type.

## Status

Narcissus is feature complete; however, work is needed to port the build configuration to every major operating system and architecture. Please get in touch if you can help with this.

## Why do this?

The JDK team has broken the Java ecosystem by enforcing strong encapsulation. They give two reasons in JEP 396 for doing this: security and maintainability.

### (1) Java security is a total illusion.

The JDK team's *security* justification for enforcing encapsulation was stated as follows:

> Some non-public classes, methods, and fields of `java.*` packages define privileged operations such as the ability to define a new class in a specific class loader, while others convey sensitive data such as cryptographic keys. These elements are internal to the JDK, despite being in `java.*` packages. The use of these internal elements by external code, via reflection, puts the security of the platform at risk.  *(--[JEP 396](https://openjdk.java.net/jeps/396))*

However the Java Native Interface is a huge gaping hole in Java's intricate security structure, so this argument is moot. Furthermore, this gaping security hole is by design, presumably because the JDK team preferred to code the security infrastructure in Java rather than in C++:

> The JNI does not enforce class, field, and method access control restrictions that can be expressed at the Java programming language level through the use of modifiers such as `private` and `final`. It is possible to write native code to access or modify fields of an object even though doing so at the Java programming language level would lead to an `IllegalAccessException`. JNI’s permissiveness was a conscious design decision, given that native code can access and modify any
memory location in the heap anyway. *(--[The Java Native Interface — Programmer’s Guide and Specification](https://github.com/iTimeTraveler/mybooks/blob/master/The%20Java%20Native%20Interface%20%E2%80%94%20Programmer%E2%80%99s%20Guide%20and%20Specification.pdf), section 10.9)*

### (2) Backwards compatibility should never have been a concern for APIs that are clearly marked as internal.

The JDK team's *maintainability* justification for enforcing encapsulation was stated as follows:

> All classes, methods, and fields of `sun.*` packages are internal APIs of the JDK. Some classes, methods, and fields of `com.sun.*`, `jdk.*`, and `org.*` packages are also internal APIs. These APIs were never standard, never supported, and never intended for external use. The use of these internal elements by external code is an ongoing maintenance burden. Time and effort spent preserving these APIs, so as not to break existing code, could be better spent moving the platform forward. *(--[JEP 396](https://openjdk.java.net/jeps/396))*

No. It is not the job of the JDK team to support APIs that are clearly marked as internal-only and not supported -- and in fact the JDK team never seems to lose much sleep over breaking internal APIs. This is just a straw man argument, and it constitutes passing blame to the community. It is the community's job to keep up with the JDK team's changes, and it is certainly not the JDK team's "job" to try to thwart the community's efforts to get access to useful internals, particularly when the JDK team has acknowledged that the real problem is that they haven't provided supported APIs yet for all the useful internal APIs that many projects depend upon for their core functionality:

> It is not a goal to remove, encapsulate, or modify any critical internal APIs of the JDK for which standard replacements do not yet exist. This means that `sun.misc.Unsafe` will remain available. *(--[JEP 396](https://openjdk.java.net/jeps/396))*

However this principle has not been adhered to, since there are many useful private classes, fields, and methods that aren't in `Unsafe` but rather scattered all over the Java standard libraries, for which no standard replacement exists or will probably ever exist. Many libraries depend upon unfettered access to these internals just to be able to function -- and JDK 16 has broken all of these libraries.

## How is it possible to circumvent Java strong encapsulation for JDK 16+?

The [JNI API](https://docs.oracle.com/en/java/javase/16/docs/specs/jni/functions.html) provides the ability to call any method or access any field without any access checks whatsoever -- and there isn't even a reflective access warning given on `System.err` as was the case for JDK 9-15 when using reflection to access non-public internals.

Of course if you make use of JNI to bypass Java's security, and things break, you get to keep all the pieces:

> Native code that bypasses source-language-level access checks may have undesirable effects on program execution. For example, an inconsistency may be created if a native method modifies a `final` field after a just-in-time (JIT) compiler has inlined accesses to the field. Similarly, native methods should not modify immutable objects such as fields in instances of `java.lang.String` or `java.lang.Integer`. Doing so may lead to breakage of invariants in the Java platform implementation. *(--[The Java Native Interface — Programmer’s Guide and Specification](https://github.com/iTimeTraveler/mybooks/blob/master/The%20Java%20Native%20Interface%20%E2%80%94%20Programmer%E2%80%99s%20Guide%20and%20Specification.pdf), section 10.9)*

## Why "Narcissus"?

> "Narcissus walked by a pool of water and decided to drink some. He saw his reflection, became entranced by it, and killed himself because he could not have his object of desire." *([--Wikipedia: Narcissus](https://en.wikipedia.org/wiki/Narcissus_(mythology)))*

(Note "reflection" and "object" 😀)
