# receptacle-12100
这部分作为发包界面在 1.21 上的代码实现，提供 1.21 以及更新版本的支持。

由于 Spigot 在 1.21 中对 InventoryView 的破坏性改动 (`abstract class` -> `interface`)，以分包来做 1.21 及以上版本的实现，这个方式最为合理。