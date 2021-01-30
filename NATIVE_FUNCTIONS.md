## Here is a list of every native function in Acacia.

#### Standard
* print() - prints to console. Takes in anything. 
* println() - prints to console with a newline. Takes in as many of anything. 
* input() - gets console/user input. Takes in a type (as a string) to convert input to. 
* sleep() - pauses thread. Takes in a number of milliseconds to sleep.
* type() - gets something's type. Takes in anything. 
* convert() - returns something of a new type. Takes in anything to convert, and new type for it.
* callable() - returns true if a given object is callable. Takes in anything. 
* inherits() - returns true if a given object inherits another one. Takes in two objects.
* len() - gets the number of elements in something. Takes in a set or string.
* read() - gets a string from a file. Takes in a string representing the file path.
* write() - writes a string to a file. Takes in a string representing the file path, and a string to write.

#### String
* String.strip() - removes spaces from beginning and end. Takes in a string
* String.replace() - replaces given string with another. Takes in a string to edit, string to find, and string to replace with.
* String.split() - returns a set from a string. Takes in string to use as splitter. Default is one space.

#### Set
* Set.sort() - sorts a set. Takes in no arguments.
* Set.find() - returns an index of an element. Takes in an object to look for.
* Set.get() - returns an object in a set. Takes in an index.
* Set.push() - adds an element to the end of a list. Takes in a new object to add.
* Set.pop() - removes the last element of a set, and returns it. Takes in no arguments.
* Set.clear() - empties a set. Takes in no arguments.
* Set.copy() - returns a copy of a set. Takes in no arguments.
* Set.add() - adds an element to a set at the given index, shifting everything after it to the right by one. Takes in an object to add, and an index to put it at.
* Set.join() - returns a newly joined string. Takes in a delimiter to put between elements of set.

#### Time
* Time.now() - gets system time.
* Time.diff() - returns the difference as a time object between two time objects. Takes in two time objects.

#### Math
* Math.random() - gets random number between 0 and 1.
* Math.abs() - returns absolute value. Takes in a number.
