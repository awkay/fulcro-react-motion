# React Motion and Fulcro

This is a simple demo project that shows you how to use React
libraries like React Motion to accomplish animations. It uses
shadow-cljs as the compiler, as that makes it much easier to
integrate external NPM dependencies.

The `package.json` file is where we pull in react-motion. The CLJ
dependencies are for running shadow-cljs itself in the JVM.

## Setting Up

The shadow-cljs compiler uses all cljsjs and NPM js dependencies through
NPM. If you use a library that is in cljsjs you will also have to add
it to your `package.json`.

You also cannot compile this project until you install the ones it
depends on already:

```
$ npm install
```

or if you prefer `yarn`:

```
$ yarn install
```

Adding NPM Javascript libraries is as simple as adding them to your
`package.json` file and requiring them! See the
[the Shadow-cljs User's Guide](https://shadow-cljs.github.io/docs/UsersGuide.html#_javascript)
for more information.

## Running the Cards

Running builds:

```
$ npx shadow-cljs watch cards
...
shadow-cljs - HTTP server for ":cards" available at http://localhost:8023
```

The compiler will detect which builds are affected by a change and will minimize
incremental build time.

The URLs for working with cards is: [http://localhost:8023](http://localhost:8023)

