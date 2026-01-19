# SWE 303: Software Testing & Quality Assurance
## Individual Testing Report

**Name:** Ersi Majkaj
**Role:** Member 4 - System QA & File Operations
**Classes Under Test:** ModifyEmployeeController, LoginController, RestockItemView

---

## 1. Static Testing (25%)

### 1.1 Static Analysis Report (SpotBugs / SonarQube)

The following classes were analyzed and issues were identified:

#### ModifyEmployeeController.java
| Issue | Description | Severity |
|-------|-------------|----------|
| String comparison using `!=` | Lines 135-137, 153-155, 181-183 use `!=` instead of `.isEmpty()` | Medium |
| System.err.println usage | Should use proper logging framework | Low |
| Cognitive Complexity | Method `setButtonAction()` has high complexity | Medium |

#### LoginController.java
| Issue | Description | Severity |
|-------|-------------|----------|
| EI_EXPOSE_REP2 | Stage reference stored directly | Medium |
| Unused field | `loginScene` created but controller doesn't show it | Low |
| Missing break statement | Manager login block missing `break` | Medium |

#### RestockItemView.java
| Issue | Description | Severity |
|-------|-------------|----------|
| Constructor throws exception | Partially initialized object risk | Medium |
| EI_EXPOSE_REP2 | Manager and FileHandler stored directly | Medium |

### 1.2 GitHub Workflow
- **Branch:** `test/member4-ersi`
- **Commits:** Systematic commits for each test file and fix
- **Evidence:** Screenshots in Appendix

---

## 2. Individual Testing Analysis (25%)

### 2.1 Boundary Value Testing (BVT): RestockItemView Quantity Validation

**Method Under Test:** `RestockItemView.getViewContent()` - quantity input handling (lines 87-93)

**Rationale:** The restock quantity field accepts integer input parsed from TextField. The business rule requires `quantity > 0`. BVT ensures boundary conditions at and around zero are properly handled.

**Input Domain:** quantity ∈ {x | x is parseable integer}
**Valid Range:** [1, Integer.MAX_VALUE]

#### Test Cases

| Test ID | Input | Expected Result | Actual Result | Status | Reason for Test |
|---------|-------|-----------------|---------------|--------|-----------------|
| BVT-E01 | 0 | Error: "positive number" | Error shown | PASS | Exact boundary (invalid) |
| BVT-E02 | 1 | Restock success | Item restocked | PASS | Minimum valid boundary |
| BVT-E03 | -1 | Error: "positive number" | Error shown | PASS | Below minimum boundary |
| BVT-E04 | 50 | Restock success | Item restocked | PASS | Nominal middle value |
| BVT-E05 | Integer.MAX_VALUE | Restock success | Item restocked | PASS | Maximum boundary |
| BVT-E06 | "" (empty) | Error: "cannot be empty" | Error shown | PASS | Empty input handling |
| BVT-E07 | "abc" | Error: "valid number" | NumberFormatException caught | PASS | Non-numeric boundary |

#### Analysis
- **Boundary Points:** 0 (invalid), 1 (min valid), Integer.MAX_VALUE (max valid)
- **Coverage:** All boundary transitions tested
- **Findings:** Code correctly handles all boundary cases with appropriate error messages

---

### 2.2 Equivalence Class Partitioning: ModifyEmployeeController.parseSalary(String)

**Method Under Test:** `parseSalary(String salaryString)` at line 125-131

**Rationale:** This method converts String input to double for salary. Equivalence Class testing groups inputs into classes that should behave identically, reducing test cases while maintaining coverage.

#### Equivalence Classes Identified

| Class ID | Input Group | Example Values | Expected Behavior | Type |
|----------|-------------|----------------|-------------------|------|
| EC1 | Valid Integer String | "5000", "100", "99999" | Returns parsed double | Valid |
| EC2 | Valid Decimal String | "5000.50", "100.99" | Returns parsed double | Valid |
| EC3 | Empty String | "" | Handled by caller (not passed) | Invalid |
| EC4 | Alphabetic String | "abc", "salary" | IllegalArgumentException | Invalid |
| EC5 | Mixed Alphanumeric | "50abc", "12.5x" | IllegalArgumentException | Invalid |
| EC6 | Special Characters | "@#$", "!!" | IllegalArgumentException | Invalid |
| EC7 | Negative Number String | "-5000" | Returns negative double | Valid* |
| EC8 | Whitespace | "  ", " 500 " | IllegalArgumentException / Trimmed | Invalid |

*Note: Negative salary may be logically invalid but method parses it successfully.

#### Test Cases

| Test ID | Input | EC Class | Expected Result | Actual Result | Status |
|---------|-------|----------|-----------------|---------------|--------|
| EC-E01 | "5000" | EC1 | 5000.0 | 5000.0 | PASS |
| EC-E02 | "5000.50" | EC2 | 5000.5 | 5000.5 | PASS |
| EC-E03 | "abc" | EC4 | IllegalArgumentException | Exception thrown | PASS |
| EC-E04 | "50abc" | EC5 | IllegalArgumentException | Exception thrown | PASS |
| EC-E05 | "@#$" | EC6 | IllegalArgumentException | Exception thrown | PASS |
| EC-E06 | "-100" | EC7 | -100.0 | -100.0 | PASS |
| EC-E07 | "   " | EC8 | IllegalArgumentException | Exception thrown | PASS |

#### Analysis
- **Valid Classes:** EC1, EC2, EC7 (3 classes)
- **Invalid Classes:** EC3, EC4, EC5, EC6, EC8 (5 classes)
- **Bug Found:** Method accepts negative salaries - business logic should reject

---

### 2.3 Code Coverage (MC/DC): LoginController.authenticate(String, String)

**Method Under Test:** `authenticate(username, password)` at lines 47-83

**Rationale:** This method contains compound boolean conditions that determine login success. MC/DC (Modified Condition/Decision Coverage) ensures each condition independently affects the outcome.

#### Decision Under Analysis

```java
if (username.equals(usernameInFile) && password.equals(passwordInFile))
```

**Conditions:**
- **A:** `username.equals(usernameInFile)` (Username matches)
- **B:** `password.equals(passwordInFile)` (Password matches)

**Decision:** Login succeeds (navigate to role-specific view)

#### MC/DC Truth Table

| Case | A (Username?) | B (Password?) | Decision (Login?) | Independence Proof |
|------|---------------|---------------|-------------------|-------------------|
| 1 | True | True | True | Baseline success |
| 2 | True | False | False | B independently affects outcome (compare 1↔2) |
| 3 | False | True | False | A independently affects outcome (compare 1↔3) |
| 4 | False | False | False | Both conditions fail |

#### MC/DC Independence Pairs
- **Condition A:** Cases 1 & 3 (A flips T→F, B=T, Decision flips T→F)
- **Condition B:** Cases 1 & 2 (B flips T→F, A=T, Decision flips T→F)

#### Test Cases

| Test ID | Username | Password | User in DB | Expected | Actual | Status |
|---------|----------|----------|------------|----------|--------|--------|
| MCDC-E01 | "admin" | "admin123" | Admin(admin, admin123) | AdminView opens | AdminView | PASS |
| MCDC-E02 | "admin" | "wrongpass" | Admin(admin, admin123) | Error label shown | Error shown | PASS |
| MCDC-E03 | "wronguser" | "admin123" | Admin(admin, admin123) | Error label shown | Error shown | PASS |
| MCDC-E04 | "wronguser" | "wrongpass" | Admin(admin, admin123) | Error label shown | Error shown | PASS |

#### Coverage Analysis

| Coverage Type | Achieved | Notes |
|---------------|----------|-------|
| Statement Coverage | 100% | All statements in authenticate() executed |
| Branch Coverage | 100% | Both true/false branches of each if executed |
| Condition Coverage | 100% | Each condition evaluated to both T and F |
| MC/DC | 100% | Each condition shown to independently affect decision |

---

## 3. Unit, Integration & System Testing (50%)

### 3.1 Unit Tests Summary

#### ModifyEmployeeController Tests

| Test ID | Method | Input/Scenario | Expected Output | Status |
|---------|--------|----------------|-----------------|--------|
| TC-ME01 | parseSalary | "5000" | 5000.0 | PASS |
| TC-ME02 | parseSalary | "invalid" | IllegalArgumentException | PASS |
| TC-ME03 | modifyToAdmin | Valid user data | Admin object created | PASS |
| TC-ME04 | modifyToManager | No sectors selected | CredentialsException | PASS |
| TC-ME05 | modifyToCashier | Valid sector | Cashier object created | PASS |

#### LoginController Tests

| Test ID | Method | Input/Scenario | Expected Output | Status |
|---------|--------|----------------|-----------------|--------|
| TC-LC01 | authenticate | Valid Admin credentials | AdminView navigation | PASS |
| TC-LC02 | authenticate | Valid Manager credentials | ManagerView navigation | PASS |
| TC-LC03 | authenticate | Valid Cashier credentials | CashierController created | PASS |
| TC-LC04 | authenticate | Invalid username | Error label displayed | PASS |
| TC-LC05 | authenticate | Invalid password | Error label displayed | PASS |
| TC-LC06 | authenticate | Empty credentials | No match, error shown | PASS |

#### RestockItemView Tests

| Test ID | Method | Input/Scenario | Expected Output | Status |
|---------|--------|----------------|-----------------|--------|
| TC-RV01 | getViewContent | Manager with low stock items | ComboBox populated | PASS |
| TC-RV02 | getViewContent | Manager with no low stock | "No items need restocking" | PASS |
| TC-RV03 | getViewContent | Null manager | IllegalArgumentException | PASS |
| TC-RV04 | restockButton | Valid item, quantity=5 | Item restocked | PASS |
| TC-RV05 | restockButton | Empty quantity field | Error: "cannot be empty" | PASS |
| TC-RV06 | restockButton | quantity=0 | Error: "positive number" | PASS |
| TC-RV07 | restockButton | quantity=-1 | Error: "positive number" | PASS |
| TC-RV08 | restockButton | quantity="abc" | Error: "valid number" | PASS |

### 3.2 Integration Tests

| Test ID | Components | Scenario | Expected | Status |
|---------|------------|----------|----------|--------|
| TC-INT01 | LoginController → AdminController | Admin login success | AdminView shown | PASS |
| TC-INT02 | LoginController → ManagerController | Manager login success | ManagerView shown | PASS |
| TC-INT03 | RestockItemController → FileHandler | Restock and save | File updated | PASS |
| TC-INT04 | ModifyEmployeeController → EmployeeFileHandler | Modify and save | Employee file updated | PASS |

### 3.3 System Tests

| Test ID | Scenario | Steps | Expected | Status |
|---------|----------|-------|----------|--------|
| TC-S01 | Full Sale Cycle | Manager adds item → Cashier sells | Inventory reduced | PASS |
| TC-S02 | Login Security | 3 invalid login attempts | Error shown each time, no lockout | PASS |
| TC-S03 | Employee Modification Flow | Admin modifies Manager salary | Updated in file, persists after restart | PASS |
| TC-S04 | Restock Workflow | Manager restocks low-stock item | Stock increased, saved to file | PASS |
| TC-S05 | Role-Based Access | Manager tries admin functions | Access denied / Not available | PASS |

---

## 4. Refactoring & Implementation Notes

### 4.1 Code Changes for Testability

| File | Change | Reason |
|------|--------|--------|
| LoginController.java | Made `authenticate()` package-private | Allow direct testing without UI |
| ModifyEmployeeController.java | Extracted `parseSalary()` as separate method | Isolate parsing logic for unit testing |
| RestockItemView.java | Constructor validation added | Prevent null pointer exceptions |

### 4.2 Bugs Discovered

| Bug ID | Location | Description | Severity |
|--------|----------|-------------|----------|
| BUG-01 | LoginController:64-72 | Missing `break` in Manager login block | Medium |
| BUG-02 | ModifyEmployeeController:135 | String comparison uses `!=` instead of `.isEmpty()` | Low |
| BUG-03 | ModifyEmployeeController:125 | Accepts negative salary values | Medium |
| BUG-04 | RestockItemView:103 | ComboBox refresh shows all items, not filtered low-stock | Low |

---

## Appendix: Evidence

### A. SpotBugs Screenshots
- Screenshot 1: Initial analysis results
- Screenshot 2: After fixes applied

### B. SonarQube Report
- Code smells identified and resolved
- Cognitive complexity improvements

### C. GitHub Activity
- Branch: test/member4-ersi
- Commit history showing systematic progress
- Pull Request with review comments

### D. Test Execution Results
- JUnit test run summary
- All tests passing confirmation
