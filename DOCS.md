# Acacia 🌱
At first glance, this language looks super similar to javascript. That's because it's designed to feel familiar to the C family, and because dynamic typing is the future.

It shines in its ability to be both object oriented and functional, with a nice class system as well as features like closures and declarative statements.

# Data Types
Acacia contains your standard primitive types.
* Booleans - basic true or false.
* Strings - text enclosed in double quotes. Access individual chars with `<>`
* Numbers - integers and numbers with decimals are treated the same.
* Nil - same as null or none.

```javascript
// examples
true;
false;

"A String";
""; // an empty string
"Hello"<1>; // "e"

12;
92.5;

nil;
```

### Sets & Collections
Acacia ~~steals~~ borrows some collections from Python.
* Lists - built in linked lists. Extend, append, pop & push at will. Use `[]`
* Arrays - fixed length, contain same type (or null for empty). Use `<>`
* Dictionaries - built in hashmaps, keys and values can be of any type. Use `[:]`
```python
let sampleList = [1, 2, 3];
sampleList.append(4);
print(sampleList[1]); // prints "2"

let sampleArray = <9, 8, 7>;
print(sampleArray<2>); // prints "7"

let sampleDict = [24 : "spongebob", 25: "patrick"];
print(sampleDict[24]); // prints "spongebob"
```

> Because of the `match with` statement and the function `isinstance()`, type names are reserved keywords and cannot have variables named the same as them. The full list is:
> `boolean`, `string`, `number`, `list`, `array`, `dictionary`

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

>Aside from false Booleans, the only other things that evaluate to false are `nil` values and numbers with value `0`

### Grouping
Parenthesis work as separators for more flexibility on what parts of a statement should be evaluated first.
```javascript
(val<0> + val<1> + val<2>) / len(val)
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

  print(string(i) + " : " + c); // 'string(i)' is optional, implicit conversion would happen otherwise.
  
}

// Behind the scenes it really just converts it into this:
for (let i = 0; i < length(letters); i = i + 1) {
  let c = letters<i>;
  
  print(string(i) + " : " + c);
  
}
```

`match with` is a take on the switch statement loosely inspired by OCaml. It can compare equality as well as types. When comparing equality, a literal or variable is provided in the parenthesis. When comparing type, a data type or object/class name is given. For objects, anything inheriting it will also be considered a match.
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
  
  (number) {
    print("yes, however a match has already been found (12) and so this wont be printed");
    break;
  }
  
  print("default");
}


let y = true;

match x with {
  (string) {
    print("nope, not a string");
    break;
  }
  
  (number) {
    print("also not a number");
    break;
  }
  
  (bool) {
    print("yup, its a boolean");
    break;
  }
}

```

# Functions
Functions are called the same way as in most C style languages.
```javascript
// arguments
sendTranscript(idNumber, date);

// or no arguments
sendOranscript(); // it's all dynamicly typed so there's no difference with runtime.
```

The parenthesis are required, because they are needed to call a function. Without them, it instead references the function. Arguments already defined can stay however.

You can also define some arguments of a function to partially ready a procedure or program.

```javascript
let someDate = Date("2020-12-22");
def sendTranscript(idNumber) someDate {
  print("It's been " + (Time.now() - someDate);
}

sendTranscript(100234); // (for example)
```

### Defining New Functons
To create a new subroutine / procedure / function, use the keyword `def`.
```javascript
def getAvg(total, sum) {
  return sum / total;
}
