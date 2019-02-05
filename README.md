# Harlock

![Harlock](images/harlock-small.png)

Atom package for using Arcadia

## What Can It Do

* Connect to Arcadia's socket REPL

* Send things to Arcadia (e.g. line, selection, etc.)

* Use Arcadia's debugger

* May be some other things :)

## Setup

### Required

* The [Atom IDE Terminal](https://github.com/qicrosoft/atom-ide-terminal) package

* For a REPL connection, an external program such as [nc](https://en.wikipedia.org/wiki/Netcat) or [telnet](https://en.wikipedia.org/wiki/Telnet)

### Optional

A wrapper for convenient line-editing, such as [rlwrap](https://github.com/hanslub42/rlwrap)

## Usage

### REPL Connection

* In Unity:

  * Start up an Arcadia project

* In Atom:

  * Open the Arcadia project's directory

  * Invoke the command _Connect To Arcadia Socket Repl_ (via the command
    palette, package submenu, etc.).

  * After a terminal window appears, verify the displayed command string
    and press the Enter key with the terminal in focus.

  * Perform magic by typing things at Arcadia

### Sending

With an appropriate .clj file open in Atom:

* the current line,
* selection,
* file, or
* block (non-empty lines between empty lines)

may be sent to the REPL via the context-sensitive menu.  These should
appear via the Harlock submenu.

### Other

There may be other commands available via the context-sensitive menu
too :)

## Keybindings

This package does not register any keybindings.  Some suggestions to
be placed in keymap.cson are:

```cson
'atom-text-editor[data-grammar="source clojure"]':
  'ctrl-\' c':       'harlock:connect-to-arcadia-socket-repl'
  'ctrl-\' d':       'harlock:disconnect'
  'ctrl-\' l':       'harlock:send-line'
  'ctrl-\' s':       'harlock:send-selection'
  'ctrl-\' b':       'harlock:send-block'
  'ctrl-\' f':       'harlock:load-file'
  'ctrl-\' F':       'harlock:send-file'
  'ctrl-\' w':       'harlock:switch-to-file-ns'
```

## Credits:

This package was heavily influenced by [Atom Chlorine](https://github.com/mauricioszabo/atom-chlorine) (@mauricioszabo)
and [VSCode Arcadia](https://github.com/worrel/vscode-arcadia) (@worrel).  Thanks to both authors (and
contributors).
