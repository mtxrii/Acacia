# Acacia
At first glance, this language looks super similar to javascript. That's because it's designed to feel familiar to the C family, and because dynamic typing is the future.

It shines in its ability to be both object oriented and functional, with a nice class system as well as closures and imperative declarations.

## Data Types
Acacia contains your standard primitive types.
* Booleans - basic true or false.
* Strings - text enclosed in double quotes.
* Numbers - all numbers are floats, the decimal is just hidden in ints.
* Nil - same as null or none.

```javascript
// examples
true;
false;

"A String";
""; // an empty string

12;
92.5;
```

## Expressions
These are sets of variables or literals combined with operators to produce a value.
### Arithmetic
* addition - `+`
* subtraction - `-`
* multiplication - `*`
* division - `/`
* modulo - `%`

```javascript
-3 + 4 / i - 9;

44 % 2;
```

### Comparison
* less than / greater than - `<` & `>`
* less than / greater than or equal to - `<=` & `>=`
* equal / not equal - `==` & `!=`

```javascript
3 < 2; // false
4 >= 4; // true
"apple" == "banana" // false
```
