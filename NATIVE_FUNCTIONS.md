## Here is a list of every native function in Acacia.
> a '*' means not implemented yet

#### Standard
>These are standard library functions useable wherever.
* print() - prints to console. Takes in anything. 
* println() - prints to console with a newline. Takes in as many of anything. 
* input() - gets console/user input. Takes in a type (as a string) to convert input to. 
* sleep() - pauses thread. Takes in a number of milliseconds to sleep.
* clock() - gets current system time in seconds. Takes in no arguments.
* type() - gets something's type. Takes in anything. 
* convert() - returns something of a new type. Takes in anything to convert, and new type (as a string) for it.
* callable() - returns true if a given object is callable. Takes in anything. 
* inherits() - returns true if a given object inherits a given class. Takes in an object and a class.
* len() - gets the number of elements in something. Takes in a set or string.
* *read() - gets a string from a file. Takes in a string representing the file path.
* *write() - writes a string to a file. Takes in a string representing the file path, and a string to write to file.

#### String
>These functions are mounted on strings automatically, but do not modify them. They only return a value. For example, `" abc ".strip()` returns `"abc"` but does not modify the original string. Note that `String` below refers to any string in code, as opposed to the word "String".
* String.contains() - returns whether or not a string contains another. Takes in a string to find.
* String.replace() - replaces given string with another. Takes in a string to find, and string to replace with.
* String.split() - returns a set from a string. Takes in string to use as splitter. Default is one space.
* String.strip() - removes spaces from beginning and end. Takes in no arguments.

#### Set
>These functions are mounted on sets automatically, but do not modify them. They only return a value. For example, `[1,2,3].get(1)` returns `2` but does not modify the original set. Note that `Set` below refers to any set in code, as opposed to the word "Set".
* *Set.find() - returns an index of an element. Takes in an object to look for.
* *Set.get() - returns an object in a set. Takes in an index.
* *Set.copy() - returns a copy of a set. Takes in no arguments.
* Set.join() - returns a newly joined string. Takes in a delimiter to put between elements of set.
>These functions are also mounted on sets automatically, however they do modify the sets they are called on (and usually don't return anything). For example, `[4,5,2].sort()` returns null but converts `[4,5,2]` into `[2,4,5]`.
* Set.sort() - sorts a set. Takes in no arguments.
* *Set.reverse() - reverses a set. Takes in no arguments.
* *Set.push() - adds an element to the end of a list. Takes in a new object to add.
* *Set.pop() - removes the last element of a set, and returns it. Takes in no arguments.
* *Set.add() - adds an element to a set at the given index, shifting everything after it to the right by one. Takes in an object to add, and an index to put it at.
* *Set.clear() - empties a set. Takes in no arguments.

#### Time
>These functions are part of the [Time box]()
* *Time.now() - gets system time.
* *Time.diff() - returns the difference as a time object between two time objects. Takes in two time objects.

#### Math
>These functions are part of the [Math box]()
* Math.random(min, max) - gets random number between the given minimum and maximum numbers.
* Math.abs(x) - returns absolute value of x.
* Math.ceil(x) - rounds to the nearest whole number above x.
* Math.floor(x) - rounds to the nearest whole number below x.
* Math.round(x) - rounds the number x to the nearest whole number.
* Math.min(a, b) - Returns whichever number is smaller.
* Math.max(a, b) - Returns whichever number is smaller.
* Math.quadratic(a, b, c) - Runs the quadratic formula on the given 3 numbers, and returns a set with two elements: The positive result, and the negative result.
* Math.setSum(set) - Returns the summation of every element in the set. Assumes elements in set are all numbers.
