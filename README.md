# repl-reload

A clojure library for reloading changed namespaces on save.

## Usage

I like to use `.lein/profiles.clj` for this  

```
{:user {:dependencies [[komcrad/repl-reload "0.1.0"]]
        :repl-options
        {:init (do (require 'clojure.tools.namespace.repl 'repl-reload.core)
                   (clojure.tools.namespace.repl/refresh)
                   (repl-reload.core/auto-reload)
                   (use 'clojure.repl))}}}
```

I also like to create a blank user.clj in my projects for my repl to use

```
{:repl-options {:init-ns your-project.user}}
```

This way I can def stuff in that ns but when ns are reloaded, since user is a blank ns, my symbols stick around

## Why?

I enjoyed using https://github.com/grampelberg/lein-autoreload.
As Clojure updated and lein updated, the plugin started breaking down.
So I built this to replace lein-autoreload for myself.
