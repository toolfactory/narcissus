# Narcissus: thwart Java's strong encapsulation

Narcissus is a JNI native code library that provides a small subset of the Java reflection API, while bypassing all of Java's reflection access checks via JNI. 

Narcissus completely circumvents all of Java's class, method, and field reflection visibility restrictions. This allows reflection code to keep working even now that strong encapsulation is being enforced in JDK 16+.

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

## Status

Narcissus works today; however, work is needed to port the build configuration to every major operating system and architecture. Please get in touch if you can help with this.

## Why "Narcissus"?

> "Narcissus walked by a pool of water and decided to drink some. He saw his reflection, became entranced by it, and killed himself because he could not have his object of desire." *([--Wikipedia: Narcissus](https://en.wikipedia.org/wiki/Narcissus_(mythology)))*

(Note "reflection" and "object" 😀)
