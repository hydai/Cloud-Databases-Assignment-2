package org.vanilladb.core.query.algebra;

import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.storage.metadata.statistics.Histogram;

public class ExplainPlan implements Plan {
	public static Histogram explainHistogram(Histogram hist) {
		Histogram expHist = new Histogram(hist.fields());
		return expHist;
	}

	private Plan p;
	private Schema schema = new Schema();
	private Histogram hist;
	private String queryPlan = "";

	public ExplainPlan(Plan p, boolean isEQ) {
		this.p = p;
		schema.addAll(p.schema());
		hist = explainHistogram(p.histogram());
	}

	public void setQueryPlan(String qP) {
		this.queryPlan = qP;
	}
	
	@Override
	public Scan open() {
		Scan s = p.open();
		ExplainScan ret = new ExplainScan(s, schema.fields());
		ret.setExplainResult(this.queryPlan);
		return ret;
	}

	@Override
	public long blocksAccessed() {
		return p.blocksAccessed();
	}

	@Override
	public Schema schema() {
		return schema;
	}

	@Override
	public Histogram histogram() {
		return hist;
	}

	@Override
	public long recordsOutput() {
		return (long) histogram().recordsOutput();
	}
}
