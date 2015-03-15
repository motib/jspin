# The jSpin development environment for Spin #
jSpin is a graphical user interface for the Spin model checker that is used for verifying concurrent and distributed programs. The user interface of jSpin is simple and consists of a single window with menus, a toolbar and three adjustable text areas. Spin option strings are automatically supplied and the Spin output is filtered and presented in a tabular form. All aspects of jSpin are configurable: some at compile time, some at initialization through a configuration file and some at runtime.

# Required software #
jSpin needs Spin which is included in the Windows installer, but you might want to download the most up-to-date version from the [Spin](http://spinroot.com) website. A C compiler is needed for running Spin. I suggest [MinGW](http://www.mingw.org/) for Windows. **Warning** The version of MinGW `mingw-get-inst-20111118.exe` causes problems (a missing dll) when invoking the compiler from within jSpin. The workaround is to install only the C compiler. (Thanks to Bernhard Drabant.)

# The EUI development environment for Erigone #
EUI is a version of jSpin for the [Erigone Model Checker](http://code.google.com/p/erigone/).  A binary of Erigone is included in the Windows installer of EUI, or you can download or build Erigone from its project page.

# Sokoban #
This project includes a simple Sokoban solver written in Promela. Since Sokoban puzzles are extremely difficult, this program can be used for experimenting with hash functions, multicore systems, and so on.

# Spin Spider - a graphical representation of the state space #
Please consider this facility as deprecated and not supported. Instead, use the VMC (Visualization of Model Checking) postprocessor for the [Erigone Model Checker](http://code.google.com/p/erigone/) which generates graphs that show the model checking algorithm _incrementally_.

# Other projects for teaching concurrency #
DAJ -- interactive execution of distributed algorithms.