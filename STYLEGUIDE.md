# Style Guide

There are a few simple style guide options we hope you can pick up on. This is heavily inspired by the [Twitter Java Style Guide](https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/styleguide.md).

This isn't a "full" style guide and most things are pretty lenient!

This may seem long, but we need to ensure we're all on the same level here.  

## Reference
* Use line breaks wisely.
* Indent style
* Field, class, and method declarations
* Variable naming
* Space pad operators and equals.
* Be explicit about operator precedence
* Documentation
* Imports
* Optional, and null
* Best practices

## Use line breaks wisely.

There are generally two reasons for a line break:

1) Your statement exceeds the column limit.

2) You want to logically separate a thought.
Writing code is like telling a story. Written language constructs like chapters, paragraphs, and punctuation (e.g. semicolons, commas, periods, hyphens) convey thought hierarchy and separation. We have similar constructs in programming languages; you should use them to your advantage to effectively tell the story to those reading the code.

## Indent style

We use the "one true brace style" ([1TBS](http://en.wikipedia.org/wiki/Indent_style#Variant:_1TBS)).

Indent size is 1 tab, but you may want to change your IDE to change the tab size to be 4 columns:

```java
	// Like this.
	if (x < 0) {
		negative(x);
	} else {
		nonnegative(x);
	}

	// Not like this, with 4 spaces.
	if (x < 0) {
	    negative(x);
	}

	// Not like this.
	if (x < 0)
		negative(x);

	// Also not like this.
	if (x < 0) negative(x);

	// Unless its a quick return
	if (x < 0) return false;


```

Continuation indent is 1 tab.

```java
// Bad.
//   - Line breaks are arbitrary.
//   - Scanning the code makes it difficult to piece the message together.
throw new IllegalStateException("Failed to process request" + request.getId()
	+ " for user " + user.getId() + " query: '" + query.getText()
	+ "'");

// Good.
//   - Each component of the message is separate and self-contained.
//   - Adding or removing a component of the message requires minimal reformatting.
throw new IllegalStateException("Failed to process"
	+ " request " + request.getId()
	+ " for user " + user.getId()
	+ " query: '" + query.getText() + "'");
```

### Don't break up a statement unnecessarily.

```java
// Bad.
final String value =
	otherValue;

// Good.
final String value = otherValue;
```

### 100 column limit

Stick to 100 columns. This keeps things tidy!

### CamelCase for types, camelCase for variables, UPPER_SNAKE for constants

Looks nice, right?


## Field, class, and method declarations

### Modifier order

We follow the [Java Language Specification](http://docs.oracle.com/javase/specs/) for modifier
ordering (sections
[8.1.1](http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.1.1),
[8.3.1](http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.3.1) and
[8.4.3](http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.4.3)).

```java
// Bad.
final volatile private String value;

// Good.
private final volatile String value;
```

## Variable naming

### Extremely short variable names should be reserved for instances like loop indices.

```java
// Bad.
//   - Field names give little insight into what fields are used for.
class User {

	private final Integer a;
	private final String m;
	private final String yub;

		...

	}

// Good.
class User {

	private final Integer ageInYears;
	private final String maidenName;
	private final Integer yearUserBorn;

		...

}
```
### Include units in variable names

```java
// Bad.
Long pollInterval;
Integer fileSize;

// Good.
Long pollIntervalMs;
Integer fileSizeGb.
```

### Don't embed metadata in variable names

A variable name should describe the variable's purpose. Adding extra information like scope and type is generally a sign of a bad variable name.

Avoid embedding the field type in the field name.

```java
// Bad.
Map<Integer, User> idToUserMap;
String valueString;

// Good.
Map<Integer, User> usersById;
String value;
```

Also avoid embedding scope information in a variable. Hierarchy-based naming suggests that a class is too complex and should be broken apart.

```java
// Bad.
String _value;
String mValue;

// Good.
String value;
```

## Space pad operators and equals.

```java
// Bad.
//   - This offers poor visual separation of operations.
int foo=a+b+1;

// Good.
int foo = a + b + 1;
```

## Be explicit about operator precedence

Don't make your reader open the
[spec](http://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html) to confirm,
if you expect a specific operation ordering, make it obvious with parenthesis.

```java
// Bad.
return a << 8 * n + 1 | 0xFF;

// Good.
return (a << (8 * n) + 1) | 0xFF;
```

It's even good to be really obvious.

```java
if ((values != null) && (10 > values.size())) {
  ...
}
```

## Documentation

Documentation in RedstoneOre Projects is a little different, but inspired by Twitter, MassiveCraft, and personal preferences.

### Use the 50 line blocks to organise constructors, fields, and methods

The order of this should be fields -> construct -> methods -> extra objects (e.g. enum)

```java

// -------------------------------------------------- //
// FIELDS
// -------------------------------------------------- //

private Integer id = null;
private String username = null;

// -------------------------------------------------- //
// CONSTRUCT
// -------------------------------------------------- //

public User(int id) {
	this.id = id;
	this.username = UserUtil.fetchUsername(id);
}

// -------------------------------------------------- //
// METHODS
// -------------------------------------------------- //

public String getUsername() {
	return this.username;
}

```

### "I'm writing a report about..."

Your elementary school teacher was right - you should never start a statement this way. Likewise, you shouldn't write documentation this way.

```java
// Bad.
/**
 * This is a class that implements a cache.  It does caching for you.
 */
class Cache {
	...
}

// Good.
/**
 * A volatile storage for objects based on a key, which may be invalidated and discarded.
 */
class Cache {
	...
}
```

### Documenting a class

Every class should be documented. Documentation for a class may range from a single sentence to paragraphs with code examples. Documentation should serve to disambiguate any conceptual blanks in the API, and make it easier to quickly and correctly use your API. A thorough class doc usually has a one sentence summary and, if necessary, a more detailed explanation.

```java
/**
 * An RPC equivalent of a unix pipe tee.  Any RPC sent to the tee input is guaranteed to have
 * been sent to both tee outputs before the call returns.
 *
 * @param <T> The type of the tee'd service.
 */
public class RpcTee<T> {
	...
}
```
### Documenting a method

A method doc should tell what the method does. Depending on the argument types, it may also be important to document input format.

```java
// Bad.
//   - The doc tells nothing that the method declaration didn't.
//   - This is the 'filler doc'.  It would pass style checks, but doesn't help anybody.
/**
 * Splits a string.
 *
 * @param s A string.
 * @return A list of strings.
 */
List<String> split(String s);

// Better.
//   - We know what the method splits on.
//   - Still some undefined behavior.
/**
 * Splits a string on whitespace.
 *
 * @param s The string to split.  An {@code null} string is treated as an empty string.
 * @return A list of the whitespace-delimited parts of the input.
 */
List<String> split(String s);

// Great.
//   - Covers yet another edge case.
/**
 * Splits a string on whitespace.  Repeated whitespace characters are collapsed.
 *
 * @param s The string to split.  An {@code null} string is treated as an empty string.
 * @return A list of the whitespace-delimited parts of the input.
 */
List<String> split(String s);
```

### Be professional

We've all encountered frustration when dealing with other libraries, but ranting about it doesn't do you any favors. Suppress the expletives and get to the point.

```java
// Bad.
// I hate xml/soap so much, why can't it do this for me!?
try {
	userId = Integer.parseInt(xml.getField("id"));
} catch (NumberFormatException e) {
	...
}

// Good.
// TODO(Jim): Tuck field validation away in a library.
try {
	userId = Integer.parseInt(xml.getField("id"));
} catch (NumberFormatException e) {
	...
}

```

### Use javadoc features

#### No author tags.

Code can change hands numerous times in its lifetime, and quite often the original author of a
source file is irrelevant after several iterations.  We find it's better to trust commit
history and `OWNERS` files to determine ownership of a body of code.

## Imports

### Import ordering

Imports are grouped by top-level package, with blank lines separating groups. Static imports are grouped in the same way, in a section below traditional imports.

```java
import java.*
import javax.*

import scala.*

import com.*

import net.redstoneore.*

import org.*

import com.*

import static *
```

### No wildcard imports

```java
// Bad.
//   - Where did Foo come from?
import com.twitter.baz.foo.*;
import com.twitter.*;

interface Bar extends Foo {
	...
}

// Good.
import com.twitter.baz.foo.BazFoo;
import com.twitter.Foo;

interface Bar extends Foo {
	...
}
```

## Optional, and null

### Don't use "get" in a method thats returns Optional.

```java
// Bad.
//   - object.getName().get(), this is messy.
public Optional<String> getName();

// Good.
//   - object.name().get(), this looks better.
public Optional<String> name();
```

### Never return null from a method

A method should ever return `null`, if there is a situation where you think you should use null you probably want to use Optional.

```java
// Bad.
//   - this will return null
public Location getArenaSpawn() {
	if (this.spawn == null) return null;

  return this.location;
}

// Good.
//   - we don't return null here
public Optional<Location> getArenaSpawn() {
	if (this.spawn == null) return Optional.empty();;

  return Optional.of(this.location);
}

// Better.
//   - don't use "get" in a method thats returns Optional.
public Optional<Location> arenaSpawn() {
	if (this.spawn == null) return Optional.empty();;

  return Optional.of(this.location);
}
```

## Best practices

### Avoid assert

We avoid the assert statement since it can be disabled at execution time, and prefer to enforce these types of invariants at all times.

### Preconditions

Preconditions checks are a good practice, since they serve as a well-defined barrier against bad input from callers. As a convention, object parameters to public constructors and methods should always be checked against null, unless null is explicitly allowed.

```java
// Bad.
//   - If the file or callback are null, the problem isn't noticed until much later.
class AsyncFileReader {
  void readLater(File file, Closure<String> callback) {
	scheduledExecutor.schedule(new Runnable() {
	  @Override public void run() {
		callback.execute(readSync(file));
	  }
	}, 1L, TimeUnit.HOURS);
  }
}

// Good.
class AsyncFileReader {
  void readLater(File file, Closure<String> callback) {
	checkNotNull(file);
	checkArgument(file.exists() && file.canRead(), "File must exist and be readable.");
	checkNotNull(callback);

	scheduledExecutor.schedule(new Runnable() {
	  @Override public void run() {
		callback.execute(readSync(file));
	  }
	}, 1L, TimeUnit.HOURS);
  }
}

```

### Minimize visibility

In a class API, you should support access to any methods and fields that you make accessible. Therefore, only expose what you intend the caller to use. This can be imperative when writing thread-safe code.

```java
public class Parser {
	// Bad.
	//	 - Callers can directly access and mutate, possibly breaking internal assumptions.
	public Map<String, String> rawFields;

	// Bad.
	//	 - This is probably intended to be an internal utility function.
	public String readConfigLine() {
	..
	}
}

// Good.
//	 - rawFields and the utility function are hidden
//	 - The class is package-private, indicating that it should only be accessed indirectly.
class Parser {
	private final Map<String, String> rawFields;

	private String readConfigLine() {
	..
	}
}
```

### Clean code

#### Favor readability
If there's an ambiguous and unambiguous route, always favor unambiguous.

```java
// Bad.
//   - Depending on the font, it may be difficult to discern 1001 from 100l.
long count = 100l + n;

// Good.
long count = 100L + n;
```

#### Remove dead code

Delete unused code (imports, fields, parameters, methods, classes). They will only rot.

#### Use general types
```java
// Bad.
//   - Implementations of Database must match the ArrayList return type.
//   - Changing return type to Set<User> or List<User> could break implementations and users.
interface Database {
	ArrayList<User> fetchUsers(String query);
}

// Good.
//   - Iterable defines the minimal functionality required of the return.
interface Database {
	Iterable<User> fetchUsers(String query);
}
```

#### Use final fields

Final fields are useful because they declare that a field may not be reassigned. When it comes to checking for thread-safety, a final field is one less thing that needs to be checked.

### Use newer/better libraries

#### StringBuilder over StringBuffer

StringBuffer is thread-safe, which is rarely needed.

### equals() and hashCode()

If you override one, you must implement both.
*See the equals/hashCode
[contract](http://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#hashCode())*

Objects.equal(): http://docs.guava-libraries.googlecode.com/git-history/v11.0.2/javadoc/com/google/common/base/Objects.html#equal(java.lang.Object, java.lang.Object)
and
Objects.hashCode(): http://docs.guava-libraries.googlecode.com/git-history/v11.0.2/javadoc/com/google/common/base/Objects.html#hashCode(java.lang.Object...)
