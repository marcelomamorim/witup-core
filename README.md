# WITUp
WITUp is a static analyser that determines the conditions that can cause Java methods to throw.

## Exception path generation
WITUp generates exception paths in two steps:

1. **Local expaths (CFG-only)**: for each method, a CFG is extracted from SootUp and converted to a
   `WITUpGraph`. The `ExPathGenerator` performs a DFS from all entry nodes (nodes with in-degree 0)
   to each `ThrowStatementNode`, collecting every acyclic path it finds. These are the local
   expaths, because they are constrained to the method CFG itself.
2. **Global expaths (per class)**: `SootUpAnalyser#analyseGlobalExPaths` iterates over all throwing
   methods in a class, invokes the local expath generator for each method, and aggregates the
   results into a map keyed by method signature. This keeps the solution minimal while providing a
   class-level view of all method-local exception paths.

During generation, the analyser logs the local expaths and the aggregated global results to the
console to make each step explicit.
