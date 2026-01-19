# Section to Add to Shared PDF (SWE 303 Project Documentation)
## Copy this content into the shared document

---

## Ersi Majkaj: System QA & File Operations

### 2.1 Boundary Value Testing (BVT): `RestockItemView` Quantity Validation

**Rationale:** The restock quantity field in RestockItemView accepts integer input from a TextField and validates that `quantity > 0`. BVT ensures the boundary at zero and negative values are correctly rejected while positive values are accepted.

**Input Domain:** quantity ∈ {x | x is parseable integer}
**Valid Range:** [1, Integer.MAX_VALUE]

| Test ID | Input | Expected Result | Reason for Test |
|---------|-------|-----------------|-----------------|
| BVT-E01 | 0 | Error: "positive number" | Exact boundary (invalid) |
| BVT-E02 | 1 | Success - Item restocked | Minimum valid boundary |
| BVT-E03 | -1 | Error: "positive number" | Below minimum boundary |
| BVT-E04 | 50 | Success - Item restocked | Nominal middle value |
| BVT-E05 | Integer.MAX_VALUE | Success | Maximum boundary test |
| BVT-E06 | "" (empty) | Error: "cannot be empty" | Empty input boundary |
| BVT-E07 | "abc" | Error: "valid number" | Non-numeric input handling |

---

### 2.2 Equivalence Class Partitioning: `ModifyEmployeeController.parseSalary(String)`

**Rationale:** This method converts String input to double for employee salary modification. As the input is a String, equivalence classes group valid numeric formats against invalid formats including alphabetic, mixed, and special character inputs.

| Input Group | Example Input | Expected Outcome | Type |
|-------------|---------------|------------------|------|
| Valid Integer | `"5000"` | Returns 5000.0 | Valid |
| Valid Decimal | `"5000.50"` | Returns 5000.5 | Valid |
| Alphabetic | `"abc"` | IllegalArgumentException | Invalid |
| Mixed Alphanumeric | `"50abc"` | IllegalArgumentException | Invalid |
| Special Characters | `"@#$"` | IllegalArgumentException | Invalid |
| Negative Number | `"-100"` | Returns -100.0 (bug: should reject) | Valid* |
| Whitespace Only | `"   "` | IllegalArgumentException | Invalid |

*Note: Negative salary parses successfully but should be rejected by business logic.

---

### 2.3 Code Coverage (MC/DC): `LoginController.authenticate(String username, String password)`

**Logic:** `if (username.equals(usernameInFile) && password.equals(passwordInFile))`

To achieve MC/DC, we prove Condition A (username match) and Condition B (password match) independently trigger the login decision.

**Conditions:**
- **A:** `username.equals(usernameInFile)`
- **B:** `password.equals(passwordInFile)`

| Case | A (Username?) | B (Password?) | Decision (Login?) | Note |
|------|---------------|---------------|-------------------|------|
| 1 | True | True | True | Login success |
| 2 | True | False | False | Proves B is independent |
| 3 | False | True | False | Proves A is independent |
| 4 | False | False | False | Both conditions fail |

- **Statement Coverage:** Case 1 hits success navigation; Cases 2-4 hit error handling.
- **Branch Coverage:** Both "True" (navigate to role view) and "False" (show error) paths exercised.
- **MC/DC Independence:** Comparing Case 1 vs 2 (B flips result) and Case 1 vs 3 (A flips result).

---

## Add to Section 3.1 Master Test Case Table

| Test ID | Method Under Test | Input / Scenario | Expected Output | Type | Status | Responsible |
|---------|-------------------|------------------|-----------------|------|--------|-------------|
| TC-E01 | RestockItemView.quantity | quantity = 0 | Error: positive number | Unit | PASS | Ersi |
| TC-E02 | RestockItemView.quantity | quantity = 1 | Restock success | Unit | PASS | Ersi |
| TC-E03 | RestockItemView.quantity | quantity = -1 | Error: positive number | Unit | PASS | Ersi |
| TC-E04 | ModifyEmployeeController.parseSalary | "5000" | Returns 5000.0 | Unit | PASS | Ersi |
| TC-E05 | ModifyEmployeeController.parseSalary | "abc" | IllegalArgumentException | Unit | PASS | Ersi |
| TC-E06 | ModifyEmployeeController.parseSalary | "-100" | Returns -100.0 | Unit | PASS | Ersi |
| TC-E07 | LoginController.authenticate | valid user + pass | AdminView opens | Unit | PASS | Ersi |
| TC-E08 | LoginController.authenticate | valid user + wrong pass | Error label shown | Unit | PASS | Ersi |
| TC-E09 | LoginController.authenticate | wrong user + valid pass | Error label shown | Unit | PASS | Ersi |
| TC-E10 | LoginController.authenticate | wrong user + wrong pass | Error label shown | Unit | PASS | Ersi |
| TC-S02 | Login Security Test | 3 invalid login attempts | Error displayed each time | System | PASS | Ersi |
| TC-S03 | Modify Employee Flow | Admin modifies Manager salary | Employee file updated | System | PASS | Ersi |
| TC-S04 | Restock Workflow | Manager restocks low-stock item | Stock increased, saved | System | PASS | Ersi |

---

## Add to Section 3.2 Integration Testing Strategy

**ModifyEmployeeController → EmployeeFileHandler Integration:**
We verified the data flow between the ModifyEmployeeController and EmployeeFileHandler. When an admin modifies an employee's details, the changes are correctly persisted to the employee data file without corruption or data loss.

**LoginController → Role-Specific Controllers Integration:**
We verified that successful authentication correctly routes users to their role-specific views (AdminView, ManagerView, CashierController) with proper user context passed.

---

## Add to Section 3.3 System Testing Scenarios

4. **Login Security Test:** Testing login with multiple invalid credentials to verify proper error handling without system lockout or crash.

5. **Employee Modification Flow:** Admin modifies Manager salary → Changes persist after application restart → Manager sees updated salary.

6. **Manager Restock Workflow:** Manager views low-stock items → Selects item → Enters restock quantity → Stock quantity increases → Changes saved to inventory file.

---

## Add to Section 4. Refactoring & Implementation Notes

**LoginController.java:** Identified missing `break` statement in Manager login block (line 64-72). This causes the loop to continue checking other users unnecessarily after a successful match.

**ModifyEmployeeController.java:** String comparison at lines 135-137 uses `!=` operator instead of `.isEmpty()` method. While functionally similar for empty string detection, this is not best practice for String comparison in Java.

**RestockItemView.java:** Constructor validation at lines 25-27 throws IllegalArgumentException if Manager or items list is null. This is good defensive programming but could leave partially initialized object if exception is not caught by caller.

---

## Static Analysis Evidence (for Appendix)

### ModifyEmployeeController.java - SonarQube Issues
| Resource | Date | Description |
|----------|------|-------------|
| ModifyEmployeeController.java | Recent | Reduce Cognitive Complexity from 27 to 15 allowed |
| ModifyEmployeeController.java | Recent | Remove this use of "!=" to compare strings |
| ModifyEmployeeController.java | Recent | Strings and Boxed types should be compared using "equals()" |
| ModifyEmployeeController.java | Recent | Replace this use of System.err by a logger |

### LoginController.java - SpotBugs Issues
| Resource | Date | Description |
|----------|------|-------------|
| LoginController.java | Recent | EI_EXPOSE_REP2: Stage field stores external mutable object |
| LoginController.java | Recent | Private method loginController.authenticate is never called (false positive - called from lambda) |

### RestockItemView.java - SpotBugs Issues
| Resource | Date | Description |
|----------|------|-------------|
| RestockItemView.java | Recent | EI_EXPOSE_REP2: Manager field stores external mutable object |
| RestockItemView.java | Recent | Constructor throws exception, object partially initialized |
