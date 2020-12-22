# Acacia
At first glance, this language looks super similar to javascript. That's because it's designed to feel familiar to the C family, and because dynamic typing is the future.

It shines in its ability to be both object oriented and functional, with a nice class system as well as features like closures and declarative statements.

# Data Types
Acacia contains your standard primitive types.
* Booleans - basic true or false.
* Strings - text enclosed in double quotes. Access individual chars with `[]`
* Numbers - all numbers are floats, the decimal is just hidden in ints.
* Nil - same as null or none.

```javascript
// examples
true;
false;

"A String";
""; // an empty string
"Hello"[1]; // "e"

12;
92.5;

nil;
```

# Expressions
These are sets of variables or literals combined with operators to produce a value.
### Arithmetic
Operators that take in one or two numbers and produce another number. Addition operator can also concatenate strings.
* addition - `+`
* subtraction / negation - `-`
* multiplication - `*`
* division - `/`
* modulo - `%`

```javascript
-3 + 4 / i - 9;

44 % 2;
```

### Comparison
Operators that take in two numbers and produce another boolean. If given a string, its length is used. Equality comparisons can also compare any two of the same data types, including objects.
* less than / greater than - `<` & `>`
* less than / greater than or equal to - `<=` & `>=`
* equal / not equal - `==` & `!=`

```javascript
3 < 2; // false
4 >= 4; // true
"apple" == "banana" // false
```

### Logical
Operators that take in one or two booleans and produce another boolean. If given a non bool value, it will be replaced with whether or not that value is nil. For `and` and `or`, if the first value determines the result, the second won't be evaluated.
* negation - `!`
* both true - `and`
* either true - `or`

```javascript
!true; // false
!false; // true

true and false; // false
true or false; // true
nill and true; // false
```

### Grouping
Parenthesis work as separators for more flexibility on what parts of a statement should be evaluated first.
```javascript
(val[0] + val[1] + val[2]) / len(val)
```

# Statements
Unlike expressions, statements produce an effect rather than a value. All statements end with a `;`.

### Variables
Must be declared before usage, but can be assigned in the same line. They're declared with `let`. Variables are presumed nil until a value is assigned.

```javascript
let x; // currently nil
let y = 30; // holds number 30
x = 10.5; // replaced nil with number 10.5

var z = "a string"; // no need to specify type
```

variables are fully dynamic and can change the type they hold at all times.

```javascript
x = x / y; // now holds 0.35
y = "hi";

x = x / y; // error
```

### Blocks & Scope
There are many instances to group together statements, and they're grouped by brackets. Variables defined inside a scope are only accessible inside that scope or inner scopes.
```javascript
let a = "Hello, ";
{
  let b = "world!";
  print(a + b); // Hello, world!
}

{
  print(a + b); // error
}
```

### Control Flow
Statements that allow skipping or repeating code.

Acacia includes your standard C style control flow statements...
* if / else
* for
* while

...as well as some others:
* foreach
* match / with
* when (not implemented yet)
* hold (not implemented yet)

`if`, `for` and `while` are the same as in c.
```javascript
let thing = "Hi";
if (thing) { // Thing is not nil, and therefore evaluates to true.
  print("thing exists.");
}
else {
  print("thing is nil.");
}

for (let i = 1; i < 10; i = i + 1) {
  print(i);
}

let j = 10;
while (j > 0) {
  print(j);
  j = j - 1;
}
```

`foreach` is syntactic sugar for array or string iteration.
```javascript
let letters = "abcd";

// You can write a foreach like this:
foreach (let c; letters; let i) { // syntax: ( initialize iterator; provide iterable; initialize index (optional) )

  print(string(i) + " : " + c);
  
}

// Behind the scenes it really just converts it into this:
for (let i = 0; i < length(letters); i = i + 1) {
  let c = letters[i];
  
  print(string(i) + " : " + c);
  
}
```

`match with` is a take on the switch statement.
```ocaml
let x = 12;

match x with {
  (10) {
    print("too small");
    break;
  }
  
  (12) {
    print("just right");
    break;
  }
  
  (14) {
    print("too big");
    break;
  }
  
  print("default");
}

```

# Functions
* todo