# Acacia 🌱
Acacia strives to be a clean, elegant and modern OOP language with elements of functional programming.

Largely based off of Bob Nystrom's [Lox](https://github.com/munificent/craftinginterpreters), it is interpreted top down using a tree-walker written in Java. Most of the syntax and elegant scoping rules are parallel to that of Lox's, however it also shines in it's support for modern convenience utilities and features of the realm of python and ruby. An infinite thanks to Nystrom's [book](http://craftinginterpreters.com/) though, which guided me through laying the foundations for this project.

Programming in Acacia should instantly feel familiar, with a syntax much like javascript's.
```javascript
let x = 16;

while (x > 0) {
    //Print numbers divisible by 4.
    if (x % 4 == 0) println(x);
    x--;
}
```

Variables are dynamically typed and garbage collected. Loops and blocks use brackets. Statements end in semicolons. Comments start with double backslashes. All of that.

But...
### There's only 3 primitive types.
Strings, Numbers and Booleans. 

No need for chars. Or ints. Any single character string is still a string, and any number with no decimal is still a double (even if it ends with .0). Any internal conversions are automatically taken care of, and casting errors are no more.

### Built in lists.
Or sets should I say? Thats what they're called in Acacia, but same thing. Much like in python, no instantiations are necesary. let `set = [1,2,3]` and you're *set*. Sets also come with a whole *set* of built in methods that harness their inner array-ness. Use them as a stack, queue, tuple, etc.

### Closured functions, classes and variables.
Blocks can be declared anywhere, not just in functions, classes and loops. At any point, a complete separation from the current environment is possible. Define a variable outside a scope, create the most complex of algorithms inside, assign the result to the variable, and voila. All is stored under the hood. This allows for easy test blocks, iterating functions, singleton classes, and singular variable imports. Speaking of which...

### Open a box.
Boxes, or packages, can represent anything from a full fledged extension to another one of your scripts you also want to load. Much like JavaScript, your starting script is the entry point, which then calls other boxes to be opened (with the `open` keyword, in fact) allowing extendable scripts the freedom to run their own code, modify your's, implement an object or data structure, or completely emerse you in a new program environment. And any code you write can be opened by another script. Just put everything you want to keep private in its own block.

# Test it out
Once compiled (or downloaded), fire it up in REPL mode by simply running Acacia.
```
.\acacia
```

Or specify a `.aci` file to run.
```
.\acacia testScript.aci
```

## Write some programs
Acacia is a scripting language, meaning files are read and statements are evaluated top to bottom. No need for a main() method or other entry point. Just start scripting away. Any plaintext file can be read, but convention specifies that it should have the `.aci` extension. (but really do whatever you want, no one's stopping you).
Learn more about language specifics in the [docs](DOCS.md).
