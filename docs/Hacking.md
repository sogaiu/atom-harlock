# Developing

Clone this repo and run the following commands:

```shell
cd atom-harlock # or the name you gave when cloning
yarn
yarn shadow-cljs watch dev
```

This will start up a compiler for ClojureScript.  If you symlink the directory under `~/.atom/packages/`, you can use it while developing itself!

Please note that Harlock only activates via its "connect-to-arcadia-socket-repl" command. You don't **need** to connect into a Clojure REPL, just invoke the command.

Once Harlock is activated, connect to a cljs repl by:

```shell
yarn shadow-cljs cljs-repl dev
```

Atom's devtools console may be helpful from time to time as well.
