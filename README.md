# csv-orm-kotlin
CSV parser with ORM-like fields mapping.

### 1. Parse CSV

Define a class that describes the structure of the record
```kotlin
data class Person(
    val id: Int,
    @CsvColumn("first_name") val firstName: String,
    @CsvColumn("last_name") val lastName: String
)
```

Build a CSV parser based on the header of the input.
```kotlin
val inputData = "id;first_name;last_name\r1;John;Gola\r2;Peter;Jascriptson\r3;Linda;Javac"
val lines = inputData.split("\r")

val csv = CsvHeader.from<Person>(header = lines.first())
```

And then get a list of objects from the remaining lines using the `csv.makeInstance` method.
```kotlin
val items: List<Person> = lines.drop(1).map { csv.makeInstance(it) }
items.forEach { println(it) }
```

Or in one call
```kotlin
val inputBytes = inputData.toByteArray(Charsets.ISO_8859_1)

val items: List<Person> = CsvHeader.parse<Person>(inputBytes)
items.forEach { println(it) }
```

Both `items.forEach { println(it) }` will prints:
```
Person(id=1, firstName=John, lastName=Gola)
Person(id=2, firstName=Peter, lastName=Jascriptson)
Person(id=3, firstName=Linda, lastName=Javac)
```

### 2. Make CSV

Make CSV in one call
```kotlin
val items = listOf(
    Person(1, "Peter", "Jascriptson"),
    Person(2, "John", "Gola"),
    Person(3, "James", "Javac")
)

val outputData = CsvMaker().make(items)
println(outputData.replace("\r", "\n"))
```
Prints
```
first_name;id;last_name
Peter;1;Jascriptson
John;2;Gola
James;3;Javac
```

## Installation

Download the latest release files. Place .jar file(s) in the `<project>/app/libs` folder (in case of Android project).
Add dependencies to the `build.gradle` script of your app module.
```gradle
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'], excludes: ['*-sources.jar'])
    implementation "org.jetbrains.kotlin:kotlin-reflect:1.5.31"
    ...
}
```
Run gradle sync. Done.
