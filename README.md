# FinalProject – Creative Problem-Solving with Arrays and ArrayLists

**Problem Chosen:**  
A simple **Task Manager** that lets a user add tasks, complete them, search, and persist data.  
- **Pending tasks** are stored in an `ArrayList<String>`.  
- **Completed tasks** are stored in a plain **array** (`String[]`) with a dynamic resize when full.  
- On **program start**, data loads from files. On **exit**, data is saved back to files.

## Features (Rubric Mapping)
- **Arrays & ArrayLists:**  
  - `ArrayList<String> pending` for active tasks  
  - `String[] completed` for finished tasks
- **Menu-driven interface:** 9 options (add/list/complete/remove/search/clear/export/save+exit)
- **Recursive error checking:**  
  - `getIntInRange()` and `getNonEmptyString()` recursively retry on invalid input
- **Exception handling (try-catch):**  
  - Parsing integers, file I/O, and index safety
- **File persistence:**  
  - Loads from `data/pending.txt` and `data/completed.txt` on start  
  - Saves to the same files on exit  
  - Maintains order and integrity

## File Structure
