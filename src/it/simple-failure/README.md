In this test, the main project `put` of type `jar` has two dependencies: `jar1` and `jar2` with `type=jar` and `scope=compile` and no classifier.

There are two different classes with the same name that appear in the classpath: `com.github.it.App`. The plugin should detect this.
