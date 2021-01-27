# Acacia 🌱
At first glance, this language looks super similar to javascript. That's because it's designed to feel familiar to the C family, and because dynamic typing is the future.

It shines in its ability to be both object oriented and functional, with a nice class system as well as features like closures and declarative statements.

# Data Types
Acacia contains your standard primitive types.
* Booleans - basic true or false.
* Strings - text enclosed in double quotes. Access individual chars with `[]`
* Numbers - ints and doubles are treated the same.
* Sets - lists with any number of items of any type. Variants include `frozenSet` and `uniqueSet`
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

[1, 2, "3", false]; // index [1] yields 2 and [2] yields "3"

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
nil and true; // false
```

>Aside from false Booleans, the only other things that evaluate to false are `nil` values and numbers with value `0`

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

let z = "a string"; // no need to specify type
```

Increments and doubling are also built in.
```javascript
let a = 1;
a++; // 2
a++; // 3
a--; // 2

a+++; // 4
a+++; // 16
a+++; // 32
a---; // 16

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

`if`, `for` and `while` are the same as in c.
```javascript
let thing = "Hi";
if (thing) { // Thing is not nil, and therefore evaluates to true.
  print("thing exists.");
}
else {
  print("thing is nil.");
}

for (let i = 1; i < 10; i++) {
  print(i);
}

let j = 10;
while (j > 0) { // prints from 9 to 1, skipping 3
  j--;
  if (j == 3) {
    next;
  }
  
  print(j);
}
```
>`exit` and `next` are the equivalent of C's `break` and `continue`.

`foreach` is syntactic sugar for set or string iteration.
```javascript
let letters = "abcd";

// You can write a foreach like this:
foreach (let c; letters; let i) { // syntax: ( initialize iterator; provide iterable; initialize index (optional) )

  print(string(i) + " : " + c); // 'string(i)' is optional, implicit conversion would happen otherwise.
  
}

// Behind the scenes it really just converts it into this:
for (let i = 0; i < length(letters); i++) {
  let c = letters[i];
  
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

Functions are 1st class objects, and nestable, allowing for functions to have environments like classes. For example:
```javascript

def makeCounter() {
  let i = 0;
  def count() {
    i ++;
    print i;
  }

  return count;
}

let counter = makeCounter();
counter(); // "1".
counter(); // "2".
counter(); // "3".

```

# Classes
Classes hold methods accessible by any instance. Individual instances can hold any number of unique fields / variables.
```javascript
class Truck {
  drive() {
    println("vroom!");
  }
  
  backup() {
    println("beep beep beep!");
  }
  
  getType() {
  return "This " + this.make + " is a " + this.model;
  }
}

let semi = Truck();
semi.drive(); // prints "vroom!"

semi.make = "Volvo";
semi.model = "VNR";
semi.electric = true;

semi.getType(); // "This Volvo is a VNR"
```
>Methods inside classes are defined like functions except without the `def` keyword.
