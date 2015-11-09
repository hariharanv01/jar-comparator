# JAR Comparator

### About
JAR Comparator compares two different versions of the same jar files(compiled) and views/persists the difference between the two jars. The difference can be viewed in the command line or stored in an XML/HTML file.
 
### Basic Usage
1. Compile and the build the jar-comparator.
    1. Go to the home directory of the project, say. <proj_home_dir>
    2. mvn clean install
2. Once build, you can find the Uber jar at <proj_home_dir>/target/jar-comparator-<version>-executable.jar
3. Run the uber jar

    ```sh
    java -jar <path of uber jar> <path of jar 1 to be compared> <path of jar 2 to be compared>
    ```

### Additional Usages
The above command executes and prints the output in the console. If you prefer to vizualize it better, you can instead save the output in XML/HTML formats by running the following command.


```sh
java -jar <uber jar> <jar 1> <jar2> <path to XML/HTML file>
```

Also, if you intend to use the API to get the result in a Java object, you can use

```java
com.h2v.java.comparator.reflect.JarComparator.compare(jar1path, jar2path)
```

### Filtering Packages
If you intend to compare Classes only in a specific set of packages, pass the VM argument "config.pkgs"

```sh
java -jar -Dconfig.pkgs=<Comma separated list of package names> <uber jar> <jar 1> <jar2> [path to XML/HTML file]
```

Alternatively, if you are using the API, you can achieve the same effect by setting the System property "config.pkgs".

### Full Command
```bash
java -jar [-Dconfig.pkgs=<Comma separated package names>] <uber jar> <jar 1> <jar2> [path to XML/HTML file]
```

### What are all compared?
1. Classes (Top level, Local, Member, Anonymous) - Added/Removed.

2. Methods - Added/Removed. Methods whose parameters types are modified/new parameters added/existing parameters removed are showed as deleted and added. This limitation is due to Java's support for method overloading.

3. Annotations - Added/Removed/Modified. Annotations that are defined for Types(Class), methods and method arguments are considered. Note that, only top level annotation parameters are compared. Nested annotations are ignored.
 
### Sample output
1. Console Output

    ```text
    Element:com.tester.App$InnSta;Element Type:CLASS;Target:com.tester.App;Target Type:CLASS;Status:ADDED;
    Element:java.lang.Deprecated;Element Type:ANNOTATION;Target:public void com.tester.App.name();Target Type:METHOD;Status:ADDED;
    Element:com.tester.ann.Tin;Element Type:ANNOTATION;Target:com.tester.App;Target Type:CLASS;Left Value:[@com.tester.ann.Tin$TinTin(an=b), @com.tester.ann.Tin$TinTin(an=bb), @com.tester.ann.Tin$TinTin(an=bbb)];Right Value:[@com.tester.ann.Tin$TinTin(an=a), @com.tester.ann.Tin$TinTin(an=aa)];Status:MODIFIED;
    Element:java.lang.Deprecated;Element Type:ANNOTATION;Target:com.tester.App;Target Type:CLASS;Status:DELETED;
    ```

2. XML Output

    ```xml
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <comparisonResults>
    	<resultGroups>
    		<className>com.tester.App$InnSta</className>
    		<results>
    			<elementName>com.tester.App$InnSta</elementName>
    			<elementType>CLASS</elementType>
    			<status>ADDED</status>
    			<targetName>com.tester.App</targetName>
    			<targetType>CLASS</targetType>
    		</results>
    	</resultGroups>
    	<resultGroups>
    		<className>com.tester.App</className>
    		<results>
    			<elementName>java.lang.Deprecated</elementName>
    			<elementType>ANNOTATION</elementType>
    			<status>DELETED</status>
    			<targetName>com.tester.App</targetName>
    			<targetType>CLASS</targetType>
    		</results>
    		<results>
    			<elementName>java.lang.Deprecated</elementName>
    			<elementType>ANNOTATION</elementType>
    			<status>ADDED</status>
    			<targetName>public void com.tester.App.name()</targetName>
    			<targetType>METHOD</targetType>
    		</results>
    		<results>
    			<elementName>com.tester.ann.Tin</elementName>
    			<elementType>ANNOTATION</elementType>
    			<leftVal>[@com.tester.ann.Tin$TinTin(an=b),
    				@com.tester.ann.Tin$TinTin(an=bb),
    				@com.tester.ann.Tin$TinTin(an=bbb)]</leftVal>
    			<rightVal>[@com.tester.ann.Tin$TinTin(an=a),
    				@com.tester.ann.Tin$TinTin(an=aa)]</rightVal>
    			<status>MODIFIED</status>
    			<targetName>com.tester.App</targetName>
    			<targetType>CLASS</targetType>
    		</results>
    	</resultGroups>
    	<resultGroups>
    		<className>com.tester.App$Inn</className>
    		<results>
    			<elementName>com.tester.App$Inn</elementName>
    			<elementType>CLASS</elementType>
    			<status>ADDED</status>
    			<targetName>com.tester.App</targetName>
    			<targetType>CLASS</targetType>
    		</results>
    	</resultGroups>
    </comparisonResults>    
    ```

3. HTML Output

    ```html
    <html><head><title>Jar Comparison Result</title></head><body><h2>com.tester.App</h2><table border=1><tr><th>Target</th><th>Target type</th><th>Element</th><th>Element type</th><th>Left Value</th><th>Right Value</th><th>Status</th></tr><tr><td>public void com.tester.App.name()</td><td>METHOD</td><td>java.lang.Deprecated</td><td>ANNOTATION</td><td>'NA'</td><td>'NA'</td><td>ADDED</td></tr><tr><td>com.tester.App</td><td>CLASS</td><td>java.lang.Deprecated</td><td>ANNOTATION</td><td>'NA'</td><td>'NA'</td><td>DELETED</td></tr><tr><td>com.tester.App</td><td>CLASS</td><td>com.tester.ann.Tin</td><td>ANNOTATION</td><td>[@com.tester.ann.Tin$TinTin(an=b), @com.tester.ann.Tin$TinTin(an=bb), @com.tester.ann.Tin$TinTin(an=bbb)]</td><td>[@com.tester.ann.Tin$TinTin(an=a), @com.tester.ann.Tin$TinTin(an=aa)]</td><td>MODIFIED</td></tr></table><h2>com.tester.App$Inn</h2><table border=1><tr><th>Target</th><th>Target type</th><th>Element</th><th>Element type</th><th>Left Value</th><th>Right Value</th><th>Status</th></tr><tr><td>com.tester.App</td><td>CLASS</td><td>com.tester.App$Inn</td><td>CLASS</td><td>'NA'</td><td>'NA'</td><td>ADDED</td></tr></table><h2>com.tester.App$1</h2><table border=1><tr><th>Target</th><th>Target type</th><th>Element</th><th>Element type</th><th>Left Value</th><th>Right Value</th><th>Status</th></tr><tr><td>public void com.tester.App.name()</td><td>METHOD</td><td>com.tester.App$1</td><td>CLASS</td><td>'NA'</td><td>'NA'</td><td>ADDED</td></tr></table><h2>com.tester.App$InnSta</h2><table border=1><tr><th>Target</th><th>Target type</th><th>Element</th><th>Element type</th><th>Left Value</th><th>Right Value</th><th>Status</th></tr><tr><td>com.tester.App</td><td>CLASS</td><td>com.tester.App$InnSta</td><td>CLASS</td><td>'NA'</td><td>'NA'</td><td>ADDED</td></tr></table></body></html>
    ```


### Comparing Jars with dependencies

If your Jars have dependency libraries, you can still compare the Jars as below

1. Create Uber jars of your comparabale Jars, and then optionally give the package filter to not compare the dependency Jar files. 

2. Give the JVM classpath option passing the paths of all the dependency library along with the Comparator Jar and invoke the Main class.

    ```sh
    java -cp  "<path to dependency libray:path to Comparator Uber  jar>" [-Dconfig.pkgs=<Comma separated package names>] com.h2v.java.comparator.reflect.JarComparator <path to Jar 1> <path to Jar 2> [path to XML/HTML file]
    ```

