A `jar` artifact is usually placed in the same classpath with its dependencies, so we should check whether its position in the classpath influences the runtime behavior.

In this test, the main project `put` of type `jar` has one dependency: `jar1` with `type=jar` and `scope=compile` and no classifier.

There are two different classes with the same name that appear in the classpath: `com.github.it.App`, one coming from the `put` artifact and the other one from the `jar1` artifact. The plugin should detect this.

