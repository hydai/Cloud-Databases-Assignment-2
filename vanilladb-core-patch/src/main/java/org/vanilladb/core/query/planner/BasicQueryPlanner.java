package org.vanilladb.core.query.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vanilladb.core.query.algebra.ExplainPlan;
import org.vanilladb.core.query.algebra.Plan;
import org.vanilladb.core.query.algebra.ProductPlan;
import org.vanilladb.core.query.algebra.ProjectPlan;
import org.vanilladb.core.query.algebra.SelectPlan;
import org.vanilladb.core.query.algebra.TablePlan;
import org.vanilladb.core.query.algebra.materialize.GroupByPlan;
import org.vanilladb.core.query.algebra.materialize.SortPlan;
import org.vanilladb.core.query.parse.QueryData;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.storage.tx.Transaction;

/**
 * The simplest, most naive query planner possible.
 */
public class BasicQueryPlanner implements QueryPlanner {

	/**
	 * Creates a query plan as follows. It first takes the product of all tables
	 * and views; it then selects on the predicate; and finally it projects on
	 * the field list.
	 */
	@Override
	public Plan createPlan(QueryData data, Transaction tx) {
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		List<String> ret = new ArrayList<String>();
		Integer lv = 1;
		// Step 1: Create a plan for each mentioned table or view
		List<Plan> plans = new ArrayList<Plan>();
		int i = 0;
		for (String tblname : data.tables()) {
			String viewdef = VanillaDb.catalogMgr().getViewDef(tblname, tx);
			if (viewdef != null)
				plans.add(VanillaDb.newPlanner().createQueryPlan(viewdef, tx));
			else
				plans.add(new TablePlan(tblname, tx));
			Plan p = plans.get(i++);
			ret.add("TablePlan on (" + tblname + ") (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
			System.out.println("TablePlan on (" + tblname + ") (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
		}
		map.put(lv++, ret);
		ret = new ArrayList<String>();
		// Step 2: Create the product of all table plans
		Plan p = plans.remove(0);

		for (Plan nextplan : plans) {
			p = new ProductPlan(p, nextplan);
			ret.add("ProductPlan (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
			System.out.println("ProductPlan (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
		}
		map.put(lv++, ret);
		ret = new ArrayList<String>();

		// Step 3: Add a selection plan for the predicate
		p = new SelectPlan(p, data.pred());
		ret.add("SelectPlan (#blks=" + p.blocksAccessed()
				+ ", #recs="+p.histogram().recordsOutput() + ")");
		System.out.println("SelectPlan (#blks=" + p.blocksAccessed()
				+ ", #recs="+p.histogram().recordsOutput() + ")");
		map.put(lv++, ret);
		ret = new ArrayList<String>();

		// Step 4: Add a group-by plan if specified
		if (data.groupFields() != null) {
			p = new GroupByPlan(p, data.groupFields(), data.aggregationFn(), tx);
			if (!((GroupByPlan)p).isGroupFldsEmpty()) {
				Plan sp = ((GroupByPlan)p).getSp();
				ret.add("SortPlan (#blks=" + sp.blocksAccessed()
						+ ", #recs="+sp.histogram().recordsOutput() + ")");
				System.out.println("SortPlan (#blks=" + sp.blocksAccessed()
						+ ", #recs="+sp.histogram().recordsOutput() + ")");
				map.put(lv++, ret);
				ret = new ArrayList<String>();
			}
			ret.add("GroupByPlan (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
			System.out.println("GroupByPlan (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
		}
		map.put(lv++, ret);
		ret = new ArrayList<String>();

		// Step 5: Project onto the specified fields
		p = new ProjectPlan(p, data.projectFields());
		ret.add("ProjectPlan (#blks=" + p.blocksAccessed()
				+ ", #recs="+p.histogram().recordsOutput() + ")");
		System.out.println("ProjectPlan (#blks=" + p.blocksAccessed()
				+ ", #recs="+p.histogram().recordsOutput() + ")");
		map.put(lv++, ret);
		ret = new ArrayList<String>();

		// Step 6: Add a sort plan if specified
		if (data.sortFields() != null) {
			p = new SortPlan(p, data.sortFields(), data.sortDirections(), tx);
			ret.add("SortPlan (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
			System.out.println("SortPlan (#blks=" + p.blocksAccessed()
					+ ", #recs="+p.histogram().recordsOutput() + ")");
		}
		map.put(lv++, ret);

		if (data.getIsExplainQuery()) {
			p = new ExplainPlan(p, data.getIsExplainQuery());
			System.out.println(map2str(map, lv));
			((ExplainPlan)p).setQueryPlan(map2str(map, lv));
		}
		return p;
	}

	private String map2str(Map<Integer, List<String>> map, Integer lv) {
		String ret = "query-plan\n"
					+"----------------------------------------------------------\n"
					+"\n";
		int tabspace = 0;
		String arr = "->";
		lv--;
		while(lv > 0) {
			for (String s : map.get(lv)) {
				for (int i = 0; i < tabspace; i++) {
					ret += " ";
				}
				ret += arr;
				ret += s + "\n";
			}
			tabspace += 4;
			lv--;
		}
		return ret;
	}
}
