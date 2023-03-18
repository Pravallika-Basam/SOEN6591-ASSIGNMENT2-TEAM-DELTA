# Assignment 2 - Anti-Pattern Detection

This is a group project for the course X, where we have implemented anti-pattern detection tools for different code smells. The following are the anti-patterns and their corresponding contributors:

1. **Log and Throw Detector** - contributed by Ankur
2. **Throws Kitchen Sink** - contributed by Urvish
3. **Nested Try Detector** - contributed by Aniket
4. **Destructive Wrapping Detector** - contributed by Pravallika

## Anti-Pattern Descriptions

### Log and Throw Detector

This anti-pattern occurs when a method catches an exception and only logs it, then immediately rethrows the same exception. This can lead to unnecessary noise in logs and make it harder to pinpoint the root cause of an issue.

### Throws Kitchen Sink

This anti-pattern occurs when a method throws multiple exceptions, either by declaring all possible exceptions in the method signature or by catching exceptions and throwing a different exception instead. This can make code harder to read and maintain.

### Nested Try Detector

This anti-pattern occurs when a method contains nested try blocks, making the code harder to read and understand. It can also lead to unexpected behavior if exceptions are caught in the wrong place.

### Destructive Wrapping Detector

This anti-pattern occurs when an exception is caught and then wrapped in another exception before being rethrown, without providing any additional context. This can make it harder to diagnose the root cause of an issue.

## How to Use

Each anti-pattern detector is implemented as a Java program that takes a Java file as input and prints out any instances of the anti-pattern that it detects. To use the detectors, simply run the corresponding Java program and provide the path to the Java file to be analyzed.

## Contributors

- Ankur
- Urvish
- Aniket
- Pravallika
