If the test classpath has ordering problems, it means that we might test a different behavior that the one of the production application.

For example: your project has a dependency `jar1` that provides the class `com.example.DbEngine` and one of the test dependencies `jar2` also provides a different version of the same class. If in the test classpath, `jar2` happens to be before `jar1` it means that you actually test your app with the DB engine provided by `jar2`, while in production you will run your application with the `jar1` implementation.

Note that it may be that `jar2` is actually a mock implementation of the `com.example.DbEngine`. This kind of classloading time dependency overriding may be the only choice we can make a legacy code use a mock DB engine for tests.


