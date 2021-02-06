# Acacia 🌱
Acacia strives to be a clean, elegant and modern OOP language with elements of functional programming.

Largely based off of Bob Nystrom's [Lox](http://craftinginterpreters.com/), it is interpreted top down using a tree-walker written in Java. Most of the syntax and paradigms are parallel to that of Lox's, however it shines in it's support for many modern utilities and features of which Lox lacks.

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
### There are only 3 primitive types.
Strings, Numbers and Booleans. 

No need for chars. Or ints. Any single character string is still a string, and any number with no decimal is still a double (even if it ends with .0). Any internal conversions are automatically taken care of, and casting errors are no more.

### Add more here.

## Test it out
Once compiled (or downloaded), to fire it up in REPL mode simply run Acacia.
```
.\acacia
```

Or specify a `.aci` file (preferably with path) to run
```
.\acacia testScript.aci
```

## Write some programs
Acacia is a scripting language, meaning files are read and statements are evaluated top to bottom. No need for a main() method or other entry point. Just start scripting away. Any plaintext file can be read, but convention specifies that it should have the `.aci` extension.

Learn more about language specifics in the [docs](DOCS.md).
