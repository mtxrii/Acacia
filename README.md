# Acacia 🌱
Acacia tries to be a clean, elegant and modern OOP scripting language with elements of functional programming.

This project is largely based off of Bob Nystrom's [Lox](https://github.com/munificent/craftinginterpreters), it is interpreted top down using a tree-walker written in Java. Most of the syntax and elegant scoping rules are inspired by that of Lox's, however it also has support for modern convenience utilities and features of the realms of python and ruby. An infinite thanks to Nystrom's [Crafting Interpreters](http://craftinginterpreters.com/) though, which guided me through laying the foundations for this project.

## Features
Programming in Acacia should feel familiar, with a syntax much like javascript's.
```javascript
let x = 16;

while (x > 0) {
    // Print numbers divisible by 4.
    if (x % 4 == 0) println(x);
    x--;
}
```

Variables are dynamically typed and garbage collected. Loops and blocks use brackets. Statements end in semicolons. Comments start with double backslashes. All of that.

But...
* ### There's only 3 primitive types.
Strings, Numbers and Booleans.
```javascript
let x = "string";
let y = 3.5 + 2;
let z = (x and y) or false //true
```

* ### Closured functions, classes and variables.
Open new blocks anywhere to create new environments, and save them for later use.
```javascript
```

* ### Package, ship & open boxes.
Load other scripts from your main program with the `open` keyword. Everything not in a block is automatically public and ready to be imported. Split up code into various files or import some libraries.
```javascript
```

# Test it out
Once compiled (or downloaded), fire it up in REPL mode.
```
.\acacia
```

Or specify a `.aci` file to run.
```
.\acacia foo.aci
```

## Write some programs
Acacia is a scripting language, meaning files are read and statements are evaluated top to bottom. No main() method or other entry point. Just start scripting away. Any plaintext file can be read, but for the sake of uniformity, code lives in `.aci` files.
Learn more about language specifics in the [docs](DOCS.md).
