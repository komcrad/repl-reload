# repl-cud

Reloads changed namespaces automatically.

## Usage

I like to us `.lein/profiles.clj` for this  

```
{:user {:dependencies [[repl-cud "0.1.0"]]
        :repl-options
        {:init (do (require 'clojure.tools.namespace.repl 'repl-cud.core)
                   (clojure.tools.namespace.repl/refresh)
                   (repl-cud.core/auto-reload)
                   (use 'clojure.repl))}}}
```

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
