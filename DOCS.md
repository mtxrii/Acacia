# Acacia 🌱
At a glance, Acacia feels a lot like JavaScript. That's intentional, as it's designed to not only feel familiar, but be even more intuitive than your average dynamically typed c-style scripting language.

# Data Types
Acacia contains our typical primitive types.
* Booleans - true or false.
* Strings - text enclosed in double quotes. Access individual chars with `[]`.
* Numbers - ints and doubles are treated the same.
* Sets - lists with any number of items of any type. Access elements with `[]`.
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
>In addition to false Booleans, `nil` values and numbers with value `0` are also evaluated to false when tested for truthiness.

### Comparison
Operators that take in two numbers and produce another boolean. If strings are given, their positions alphabetically are used. Equality comparisons can also compare any two of the same data types, including objects.
* less than / greater than - `<` & `>`
* less than / greater than or equal to - `<=` & `>=`
* equal / not equal - `==` & `!=`

```javascript
3 < 2; // false
4 >= 4; // true
"apple" == "banana" // false
```

### Logical
Operators that take in one or two values, determine their truthiness, and returns a value of equivalent truthiness. In the case of `or`, if the first value is evaluated to true, it is returned and the second is never evaluated. Likewise, if the first value is evaluated to false in an `and`, it is returned and the second isn't evaluated. The value returned need not be a boolean, as any type can evaulate to true or false.
* negation - `!`
* both true - `and`
* either true - `or`

```javascript
!true; // false
!false; // true

true and false; // false
true or false; // true
nil and true; // nil (equates to false)
1 or true; // 1 (equates to true)
false or "Yes" // "Yes" (equates to true)

(x + 4) / (i or 3) // Would probably evaluate to ( (x + 4) / i ),
                   // but if i is nil (or zero), then 3 is used instead. ( (x + 4) / 3 )
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
There are many reasons to group statements together, and they're grouped by brackets. Variables defined inside a scope are only accessible inside that scope or inner scopes.
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

`foreach` allows for set and string iteration.
```javascript
let letters = "abcd";

// You can write a foreach like this:
foreach (let c; letters; let i) { // syntax: ( initialize iterator; provide iterable; initialize index (optional) )

  print(i + " : " + c); 
}
// Prints:
// 0 : a
// 1 : b
// 2 : c
// 3 : d
```

`match with` is a take on the switch statement loosely inspired by OCaml. It can compare equality as well as types. When comparing equality, a literal or variable is provided in the parenthesis. When comparing type, a data type or object/class name (as a string) is given. For objects, anything inheriting it will also be considered a match.
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
  
  ("number") {
    print("yes, however a match has already been found (12) and so this wont be printed");
    break;
  }
  
  print("default");
}


let y = true;

match y with {
  ("string") {
    print("nope, not a string");
    break;
  }
  
  ("number") {
    print("also not a number");
    break;
  }
  
  ("bool") {
    print("yup, its a boolean");
    break;
  }
}

```

# Functions
Functions are declared with the keyword `def`.
```javascript
def sendTranscript(idNumber, date) {
  // ...something
}
```

They're called the same way as in most C style languages.
```javascript
sendTranscript(100234, Time.now());
```

The parenthesis are required, because they are needed to call a function. Without them, it instead references the function.
```javascript
let send = sendTranscript;
send(100234, Time.now()); // Same thing as above
```

Functions are first class objects with full support for nested functions and closures.
```javascript
def makeCounter() {
  let i = 0;
  def count() {
    i ++;
    println(i);
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

### Init & This
You can instantly define variables for a class & access them (as well as any later defined ones) with `this`.
```javascript
class Yerba {
  init (flavor, size) {
    this.flavor = flavor;
    println("You've purchased a " + size + "oz yerba.");
  }

  getFlavor() {
    return this.flavor;
  }
}

let drink = Yerba("Mango", 12); // prints "You've purchased a 12 oz yerba."

let flav = drink.getFlavor();
println(flav); // prints "Mango"
```

### Inheritance
Classes can inherit properties from other classes.
```javascript
class Guayaki < Yerba {
  init (flavor, size, color) {
    super.init(flavor, size);
    println("Also it's " + color);
  }
}

let fancyYerba = Guayaki("Peach", 9, "orange"); // prints "You've purchased a 9 oz yerba. Also it's orange"
println(fancyYerba.getFlavor()); // prints "Peach"
```

# Multiple Files & Packages
Acacia's import system slightly takes inspiration from [Npm](https://docs.npmjs.com/downloading-and-installing-packages-locally), in the sense that external packages and multiple user files are treated the same. Like how `index.js` loads up all other JavaScript.

### Open another file
By default, only one Acacia script is run at a time. But if you'd like to split up your code along multiple files, use the `open` keyword to load up another Acacia file.
```javascript
open "LinkedList.aci"; // This would be where LinkedList is defined.

let list = LinkedList();

list.push(10);
list.push(20);
list.push(30);
```

`open` isnt limited to just the start of a script. And any string variable may be passed.
```javascript
print("What file would you like to load? >");
let file = input();

open file;
println(file + " has been loaded.");
```
