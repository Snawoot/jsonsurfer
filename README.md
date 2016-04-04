# jsonsurfer
Memory-efficient JSON parser for extracting and grouping keys using regular expressions. Jsonsurfer uses SAX-like streaming parser for reading JSON file.

During read SAX-parser tracks JSON path to current value. Values is getting filtered by applying regexp filter to its paths. Also, values is getting grouped by key extracted from path by separate grouping regular expression.

This tool is intended for extracting data from huge JSON files that cannot fit into memory.

## Building
This application uses sbt as build tool.

To build standalone jar-file suitable for common Java VM you may issue `sbt assembly` command

## Usage
### Syntax
```
java -jar jsonsurfer-assembly-0.1.jar <filename> [ <filter regexp> [group key regexp] ]
```

### Examples

Let\`s assume we have JSON file names `example.json` with following content:
```json
{"employees":[
    {"firstName":"John", "lastName":"Doe", "age":32},
    {"firstName":"Anna", "lastName":"Smith", "age":21},
    {"firstName":"Peter", "lastName":"Jones", "age":43}
]}
```

#### Dump all keys and paths

```
$ java -jar jsonsurfer/target/scala-2.10/jsonsurfer-assembly-0.1.jar example.json
0,employees.0.firstName,"John"
1,employees.0.lastName,"Doe"
2,employees.0.age,32
3,employees.1.firstName,"Anna"
4,employees.1.lastName,"Smith"
5,employees.1.age,21
6,employees.2.firstName,"Peter"
7,employees.2.lastName,"Jones"
8,employees.2.age,43
```

Output is in following CSV format:
* First column is group ID (see below).
* Second column is path.
* Third column is value.

#### Filter fields

Command below extracts only name and age fields.
```
$ java -jar jsonsurfer/target/scala-2.10/jsonsurfer-assembly-0.1.jar example.json "employees\.\d+\.(firstName|age)"
0,employees.0.firstName,"John"
1,employees.0.age,32
2,employees.1.firstName,"Anna"
3,employees.1.age,21
4,employees.2.firstName,"Peter"
5,employees.2.age,43
```

#### Group by common part of path
```
MacBook-Pro-user:~ user$ java -jar jsonsurfer/target/scala-2.10/jsonsurfer-assembly-0.1.jar example.json ".*" "(employees\.\d+).*"
0,employees.0.firstName,"John"
0,employees.0.lastName,"Doe"
0,employees.0.age,32
1,employees.1.firstName,"Anna"
1,employees.1.lastName,"Smith"
1,employees.1.age,21
2,employees.2.firstName,"Peter"
2,employees.2.lastName,"Jones"
2,employees.2.age,43
```
