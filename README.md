cporder-maven-plugin
====================

This maven plugin checks whether two different maven dependencies with the artifact scope being either *compile* or *runtime* and artifact type being *jar* contain classes with the same (fully qualified) name.

Such a class contained in two jars may cause unexpected runtime behaviour to the application. For example, in the case of an web application, depending on the Servlet container, the classpath may be alphabetically sorted or not, leading to a different version of the class being loaded. Another example of bad behavior is in the case of a Java Applet, where classes from different jars are loaded using different classloader, so both of the classes are loaded at runtime.

Configuration
-------------

This plugin executes by default in the *verify* phase of the Maven lifecycle. In order to configure it in the *pom.xml* file, you should include the following snippet:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.github.ctalau</groupId>
      <artifactId>cporder-maven-plugin</artifactId>
      <version>1.0</version>
    </plugin>
  </plugins>
</build>
```
License
-------

This project is licensed under [ASL 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

