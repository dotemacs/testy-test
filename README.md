# Multis test

## Setup

Clone this repo and then in it:

```
npm install
M-x cider-jack-in-cljs
```

Which will start the REPL and leave you at its prompt.


## Explanation

The most relevant part is the function `crypto.main/transact`, which
creates the stream. The rest is pretty much standard re-frame.

The UI is pretty plain, because I figured getting the streaming going
was the hard part. If you'd like me to spend time on the UI, maybe we
can chat about it?
