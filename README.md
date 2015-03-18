# Assignment 2
In this assignment, you are asked to implement the `Explain` SQL operation.

## Steps
To complete this assignment, you need to

1. Fork the Assignment 2 project
2. Trace the code in the `vanilladb-core-patch` project's `org.vanilladb.core.query` package yourself
3. Modify the current implementation of the query engine to support `Explain` keyword
4. Run a few queries to test your implementation and write a report

## `Explain` Function
`Explain` is a special keyword that shows how a SQL statement is executed by dumping the execution plan chosen by the planner.

### Output

Output should be a table with one record of one field `query-plan` of type `varchar(500)`. A JDBC client can get the result through `RemoteResultSet.getString("query-plan")`

A output includes:
- Each plan the query will pass and the corresponding information
- Number of actual records

The format of each plan:

```
-> ${PLAN_TYPE} [optional information] (#blks=${BLOCKS_ACCESSED}, #recs=${OUTPUT_RECORDS})
```

### Example

Query
```sql
EXPLAIN SELECT d_id FROM district, warehouse WHERE d_w_id = w_id GROUP BY w_id
```

Sample Output
```
query-plan
----------------------------------------------------------------
->ProjectPlan  (#blks=5, #recs=0)
	->GroupByPlan: (#blks=5, #recs=3)
		->SortPlan (#blks=5, #recs=30)
			->SelectPlan pred:(d_w_id=w_id) (#blks=14, #recs=30)
				->ProductPlan  (#blks=14, #recs=90)
					->TablePlan on (district) (#blks=4, #recs=30)
					->TablePlan on (warehouse) (#blks=2, #recs=3)

Actual #recs: 3
```


## The report
- How you implement `Explain` operation
- Please use our benchmark project to load the TPC-C testbed and show the `EXPLAIN` result for the following queries,
  - Single table query
  - Multiple tables query
  - Query with `GROUP BY` or `ORDER BY`
  - Query with at least one aggregation function
- Anything worth mentioning

	Note: There is no strict limitation to the length of your report. Generally, a 2~3 pages report with some figures and tables is fine. **Remember to include all the group members' student IDs in your report.**



## Submission

The procedure of submission is as following:

1. Fork our [Assignment 2](http://shwu10.cs.nthu.edu.tw/2015-cloud-database/assignment-2) on GitLab
2. Clone the repository you forked
3. Finish your work and write the report
4. Commit your work, push to GitLab and then open a merge request to submit. The repository should contain
	- *[Project directory]*
	- *[Team Member 1 ID]_[Team Member 2 ID]*_assignment2_report.pdf (e.g. 102062563_103062528_assignment2_reprot.pdf)

    Note: Each team only need one submission.

## Hints

1. The packages you may modify
  - `org.vanilladb.core.query.algebra`
  - `org.vanilladb.core.query.parse`
  - `org.vanilladb.core.query.planner`
2. Better start from `Parser` and `Lexer`
3. You may want to implement a new plan for `Explain` and modify the existing plans
4. You may also want to implement a new scan for `Explain`

There are currently two kinds of planners in VanillaDB-Core. You just need to modify the `BasicQueryPlanner` and `BasicUpdatePlanner` to support `Explain` operation.

## Deadline
Sumbit your work before **2015/04/01 (Wed.) 23:59:59**.
